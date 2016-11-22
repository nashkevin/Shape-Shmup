package main.java.environment;

import main.java.agent.Agent;
import main.java.agent.NPCAgent;
import main.java.agent.PlayerAgent;
import main.java.agent.Scout;

import main.java.projectile.Projectile;

import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/** TODOs
	Fix constructor calls for NPC and Player agents for integration
	Change check collision to use range instead of exact coords
	Limit update frequency
*/
public class Environment {
	/** Radius of the environment, in pixels. Value is arbitrarily chosen. */
	private static final double RADIUS = 10000;
	/** The ideal ratio of NPCAgents to PlayerAgents */
	private static final int NPC_PLAYER_RATIO = 5;
	/** The frame rate, in Hz */
	private static final int FRAME_RATE = 30;
	/** A random number generator **/
	private static final Random random = new Random();
	
	private boolean gameplayOccurring = true;
	private boolean verbose = true;

	private Set<PlayerAgent> activePlayerAgents;
	private Set<NPCAgent> activeNPCAgents;
	private Set<Projectile> activeProjectiles;

	private Timer timer = new Timer("Environment Timer");

	public Environment() {
		this(true);
	}
	
	public Environment(boolean verbose) {
		this.verbose = verbose;

		activePlayerAgents = Collections.newSetFromMap(new ConcurrentHashMap<PlayerAgent, Boolean>());
		activeNPCAgents = Collections.newSetFromMap(new ConcurrentHashMap<NPCAgent, Boolean>());
		activeProjectiles = Collections.newSetFromMap(new ConcurrentHashMap<Projectile, Boolean>());

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

	public void despawnNPCAgent(NPCAgent agent) {
		activeNPCAgents.remove(agent);
		// agent = null;
		if (verbose) {
			System.out.println(agent.getID() + " npc was despawned.");
		}
	}

	public void despawnPlayerAgent(PlayerAgent agent) {
		activePlayerAgents.remove(agent);
		// agent = null;
		if (verbose) {
			System.out.println("\"" + agent.getName() + "\" was despawned.");
		}
	}

	public void despawnProjectile(Projectile projectile) {
		activeProjectiles.remove(projectile);
	}

	/** Spawns a playable character entity. */
	public PlayerAgent spawnPlayer(String name) {
		PlayerAgent player = new PlayerAgent(this, randomPlayerSpawn(), name);
		activePlayerAgents.add(player);
		if (verbose) {
			System.out.println("Player (" + player.getID() + ") was spawned.");
		}
		return player;
	}

	public PlayerAgent spawnPlayer(Point2D.Double point) {
		PlayerAgent player = new PlayerAgent(this, point, "Player" +
			String.format("%04d", random.nextInt(10000)));
		activePlayerAgents.add(player);
		if (verbose) {
			System.out.println("Player (" + player.getID() + ") was spawned.");
		}
		return player;
	}

	public Scout spawnScout() {
		// Randomly pick level from 1 to 15
		int level = random.nextInt(15) + 1;
		return spawnScout(level);
	}

	public Scout spawnScout(Point2D.Double point) {
		// Randomly pick level from 1 to 100
		int level = random.nextInt(100) + 1;
		Scout agent = new Scout(this, point, level);
		activeNPCAgents.add(agent);
		if (verbose) {
			System.out.println("Level " + level + " scout (" +
				agent.getID() + ") was spawned.");
			System.out.println("[LEVEL " + level + " SCOUT]\n" + agent.toString());
		}
		return agent;
	}

	public Scout spawnScout(int level) {
		Scout agent = new Scout(this, randomNPCSpawn(), level);
		activeNPCAgents.add(agent);
		if (verbose) {
			System.out.println("Level " + level + " scout (" +
				agent.getID() + ") was spawned.");
		}
		return agent;
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
		return Math.sqrt(Math.abs(p.getX()) * Math.abs(p.getX()) + Math.abs(p.getY()) * Math.abs(p.getY()));
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
		for (Agent a : getActivePlayerAgents()) {
			if (a.getTeam() != p.getOwner().getTeam() &&
				a.getPosition().distance(p.getPosition()) < 35 * a.getSize()) {
				collisions.add(a);
			}
		}
		for (Agent a : getActiveNPCAgents()) {
			if (a.getTeam() != p.getOwner().getTeam() &&
				a.getPosition().distance(p.getPosition()) < 35 * a.getSize()) {
				collisions.add(a);
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
