package main.java.environment;

import main.java.agent.Agent;
import main.java.agent.NPCAgent;
import main.java.agent.PlayerAgent;
import main.java.agent.Scout;
import main.java.agent.Turret;

import main.java.projectile.Projectile;

import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class Environment {
	/** Radius of the environment, in pixels. Value is arbitrarily chosen. */
	private static final double RADIUS = 10000;
	/** The ideal ratio of NPCAgents to PlayerAgents */
	private static final int NPC_PLAYER_RATIO = 50;
	/** The frame rate, in Hz */
	private static final int FRAME_RATE = 40;
	/** A random number generator **/
	private static final Random random = new Random();
	
	private int envionmentLevel = 1;
	private boolean gameplayOccurring = true;
	private boolean verbose = true;

	private Set<PlayerAgent> activePlayerAgents;
	private Set<NPCAgent> activeNPCAgents;
	private Set<Projectile> activeProjectiles;

	private Set<PlayerAgent> recentlyDespawnedPlayerAgents;
	private Set<NPCAgent> recentlyDespawnedNPCAgents;
	private Set<Projectile> recentlyDespawnedProjectiles;

	private Set<PlayerAgent> redPlayers;
	private Set<PlayerAgent> bluePlayers;

	private Timer timer = new Timer("Environment Timer");

	public Environment() {
		this(true);
	}
	
	public Environment(boolean verbose) {
		this.verbose = verbose;

		activePlayerAgents = Collections.newSetFromMap(new ConcurrentHashMap<PlayerAgent, Boolean>());
		activeNPCAgents = Collections.newSetFromMap(new ConcurrentHashMap<NPCAgent, Boolean>());
		activeProjectiles = Collections.newSetFromMap(new ConcurrentHashMap<Projectile, Boolean>());
		
		recentlyDespawnedPlayerAgents = Collections.newSetFromMap(new ConcurrentHashMap<PlayerAgent, Boolean>());
		recentlyDespawnedNPCAgents = Collections.newSetFromMap(new ConcurrentHashMap<NPCAgent, Boolean>());
		recentlyDespawnedProjectiles = Collections.newSetFromMap(new ConcurrentHashMap<Projectile, Boolean>());

		redPlayers = Collections.newSetFromMap(new ConcurrentHashMap<PlayerAgent, Boolean>());
		bluePlayers = Collections.newSetFromMap(new ConcurrentHashMap<PlayerAgent, Boolean>());


		// call update at FRAME_RATE
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (gameplayOccurring) {
					update();
				}
			}
		}, 0, 1000 / FRAME_RATE);
	}

	public double getRadius(){
		return RADIUS;
	}

	public Set<PlayerAgent> getActivePlayerAgents() {
		return this.activePlayerAgents;
	}

	public Set<NPCAgent> getActiveNPCAgents() {
		return this.activeNPCAgents;
	}

	public Set<Projectile> getActiveProjectiles() {
		return this.activeProjectiles;
	}
	
	/** Gets the player agents who were despawned since the last time this method was called. */
	public synchronized Set<PlayerAgent> getRecentlyDespawnedPlayerAgents() {
		Set<PlayerAgent> agents = new HashSet<>(recentlyDespawnedPlayerAgents);
		recentlyDespawnedPlayerAgents.clear();
		return agents;
	}

	/** Gets the NPC agents that were despawned since the last time this method was called. */
	public synchronized Set<NPCAgent> getRecentlyDespawnedNPCAgents() {
		Set<NPCAgent> agents = new HashSet<>(recentlyDespawnedNPCAgents);
		recentlyDespawnedNPCAgents.clear();
		return agents;
	}

	/** Gets the projectiles that were despawned since the last time this method was called. */
	public synchronized Set<Projectile> getRecentlyDespawnedProjectiles() {
		Set<Projectile> projectiles = new HashSet<>(recentlyDespawnedProjectiles);
		recentlyDespawnedProjectiles.clear();
		return projectiles;
	}

	public Agent.Team getSmallestTeam(){
		if (redPlayers.size() < bluePlayers.size()) {
			return Agent.Team.RED;
		} else {
			return Agent.Team.BLUE;
		}
	}

	public void addPlayerToTeam(PlayerAgent player) {
		if (player.getTeam() == Agent.Team.RED) {
			redPlayers.add(player);
		} else if (player.getTeam() == Agent.Team.BLUE) {
			bluePlayers.add(player);
		}
	}

	public void removePlayerFromTeam(PlayerAgent player) {
		if (player.getTeam() == Agent.Team.RED) {
			redPlayers.remove(player);
		} else if (player.getTeam() == Agent.Team.BLUE) {
			bluePlayers.remove(player);
		}
	}

	public void despawnNPCAgent(NPCAgent agent) {
		activeNPCAgents.remove(agent);
		recentlyDespawnedNPCAgents.add(agent);
		// agent = null;
		if (verbose) {
			System.out.println(agent.getID() + " npc was despawned.");
		}
	}

	public void despawnPlayerAgent(PlayerAgent agent) {
		if (agent != null) {
			activePlayerAgents.remove(agent);
			recentlyDespawnedPlayerAgents.add(agent);
			removePlayerFromTeam(agent);
			if (verbose) {
				System.out.println("\"" + agent.getName() + "\" was despawned.");
			}
		}
	}

	public void despawnProjectile(Projectile projectile) {
		activeProjectiles.remove(projectile);
		recentlyDespawnedProjectiles.add(projectile);
	}

	/** Spawns a playable character entity. */
	public PlayerAgent spawnPlayer(String name) {
		PlayerAgent player = new PlayerAgent(this, randomPlayerSpawn(),
			name, getSmallestTeam());
		activePlayerAgents.add(player);
		addPlayerToTeam(player);		
		updateEnvironmentLevel();
		if (verbose) {
			System.out.println("Player (" + player.getName() + ") was spawned.");
		}
		return player;
	}

	public PlayerAgent spawnPlayer(Point2D.Double point) {
		PlayerAgent player = new PlayerAgent(this, point, "Player" +
			String.format("%04d", random.nextInt(10000)), getSmallestTeam());
		activePlayerAgents.add(player);
		addPlayerToTeam(player);		
		if (verbose) {
			System.out.println("Spoofed player (" + player.getName() + ") was spawned.");
		}
		return player;
	}

	public Scout spawnScout() {
		return spawnScout(generateLevel());
	}

	public Scout spawnScout(int level) {
		return spawnScout(randomNPCSpawn(), level);
	}

	public Scout spawnScout(Point2D.Double point) {
		return spawnScout(point, generateLevel());
	}

	public Scout spawnScout(Point2D.Double point, int level) {
		Scout agent = new Scout(this, point, level);
		activeNPCAgents.add(agent);
		if (verbose) {
			System.out.println("Level " + level + " scout (" +
				agent.getID() + ") was spawned.");
		}
		return agent;
	}

	public Turret spawnTurret() {
		return spawnTurret(generateLevel());
	}

	public Turret spawnTurret(int level) {
		return spawnTurret(randomNPCSpawn(), level);
	}

	public Turret spawnTurret(Point2D.Double point) {
		return spawnTurret(point, generateLevel());
	}

	public Turret spawnTurret(Point2D.Double point, int level) {
		Turret agent = new Turret(this, point, level);
		activeNPCAgents.add(agent);
		if (verbose) {
			System.out.println("Level " + level + " scout (" +
				agent.getID() + ") was spawned.");
		}
		return agent;
	}

	public int generateLevel() {
		int level = (int) Math.round(random.nextGaussian() * 2 + envionmentLevel);
		if (level < 1) {
			level = 1;
		}
		else if (level > 100) {
			level = 100;
		}
		return level;
	}

	public void updateEnvironmentLevel() {
		double avgPlayerLevel = 0;
		for (PlayerAgent a : getActivePlayerAgents()) {
			avgPlayerLevel += a.getLevel();
		}
		avgPlayerLevel /= getActivePlayerAgents().size();
		envionmentLevel = (int) Math.round(avgPlayerLevel);
	}

	public void addProjectile(Projectile p) {
		activeProjectiles.add(p);
	}

	public static Point2D.Double polarToCartesian(double angle, double radius) {
		Point2D.Double p = new Point2D.Double();
		double x = Math.cos(angle) * radius;
		double y = Math.sin(angle) * radius;
		p.setLocation(x, y);
		return p;
	}

	/** Returns a point where the first value is the angle and the second is the radius */
	public static Point2D.Double cartesianToPolar(Point2D.Double p) {
		Point2D.Double polar = new Point2D.Double();
		polar.x = Math.atan2(p.getY(), p.getX());
		polar.y = checkRadius(p);
		return polar;
	}

	public static double checkRadius(Point2D.Double p){
		return Math.sqrt(Math.abs(p.getX() * p.getX()) + Math.abs(p.getY() * p.getY()));
	}

	/** returns the PlayerAgent nearest to the given Agent */
	public PlayerAgent getNearestPlayer(Agent source) {
		return getNearestPlayer(source, Double.MAX_VALUE);
	}

	/** returns the PlayerAgent nearest to the given Agent,
	 *  if there is one within the given range */
	public PlayerAgent getNearestPlayer(Agent source, double range) {
		PlayerAgent nearestPlayer = null;
		double minDistance = range;
		for (PlayerAgent player : activePlayerAgents) {
			double distance = player.getPosition().distance(source.getPosition());
			if (distance <= minDistance && !source.equals(player)) {
				minDistance = distance;
				nearestPlayer = player;
			}
		}
		return nearestPlayer;
	}

	public ArrayList<Agent> checkCollision(Projectile p) {
		ArrayList<Agent> collisions = new ArrayList<Agent>();

		// NPCAgents can't damage each other
		if (p.getOwner() instanceof PlayerAgent) {
			for (Agent a : getActiveNPCAgents()) {
				if (a.getTeam() != p.getOwner().getTeam() &&
					a.getPosition().distance(p.getPosition()) < 33 * a.getSize() * p.getSize()) {
					collisions.add(a);
				}
			}
		}

		// unaffiliated PlayerAgents can't damage other PlayerAgents
		if (p.getOwner().getTeam() != Agent.Team.NONE) {
			for (Agent a : getActivePlayerAgents()) {
				// target is not on a PvP team and shooter is
				boolean neutralTarget = (p.getOwner().getTeam() !=
					Agent.Team.ENEMY && a.getTeam() == Agent.Team.NONE);
				// target and shooter are on different teams
				boolean teamsDiffer = (a.getTeam() != p.getOwner().getTeam());
				// the shot actually hits
				boolean overlapping = a.getPosition().distance(p.getPosition()) <
					33 * a.getSize() * p.getSize();
				if (!neutralTarget && teamsDiffer && overlapping) {
					collisions.add(a);
				}
			}
		}

		return collisions;
	}

	private Point2D.Double randomPlayerSpawn() {
		double angle = Math.random() * 2 * Math.PI;
		return polarToCartesian(angle, getRadius());
	}

	private Point2D.Double randomNPCSpawn() {
		double angle = Math.random() * 2 * Math.PI;
		double distance = Math.random() * getRadius();
		return polarToCartesian(angle, distance);
	}

	private void update() {
		for(PlayerAgent agent : getActivePlayerAgents())
			agent.update();
		for(NPCAgent agent : getActiveNPCAgents())
			agent.update();
		for(Projectile p : getActiveProjectiles())
			p.update();
		while(getActiveNPCAgents().size() < (NPC_PLAYER_RATIO * getActivePlayerAgents().size()))
			spawnScout();
	}

	public boolean isGameplayOccurring() {
		return gameplayOccurring;
	}

	public void setGameplayOccurring(boolean gameplayOccurring) {
		this.gameplayOccurring = gameplayOccurring;
	}
}
