package main.java.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import main.java.agent.Agent;
import main.java.agent.NPCAgent;
import main.java.agent.PlayerAgent;
import main.java.agent.TestEnemyAgent;

import main.java.projectile.Projectile;

/** TODOs
  Fix constructor calls for NPC and Player agents for integration
  Change check collision to use a set distance
*/
public class Environment extends Thread {
	private boolean gameplayOccurring = true;
  
  private static final int NPCTOPLAYERRATIO = 5;
  private double radius;
  /** Maps from session ID (of WebSocket connection) to agent for each client. */
  private Map<PlayerAgent, Boolean> playerMap = new ConcurrentHashMap();
  private Set<PlayerAgent> activePlayerAgents;
  private Map<NPCAgent, Boolean> npcMap = new ConcurrentHashMap();
  private Set<NPCAgent> activeNPCAgents;
  private Map<Projectile, Boolean> projectileMap = new ConcurrentHashMap();
  private Set<Projectile> activeProjectiles;
  
  public Environment(double radius) {
    this.radius = radius;
    activePlayerAgents = Collections.newSetFromMap(playerMap);
    activeNPCAgents = Collections.newSetFromMap(npcMap);
    activeProjectiles = Collections.newSetFromMap(projectileMap);
  }

  public double getRadius(){
    return this.radius;
  }

  public Set<PlayerAgent> getActivePlayerAgents(){
    return this.activePlayerAgents;
  }

  public Set<NPCAgent> getActiveNPCAgents(){
    return this.activeNPCAgents;
  }

  public Set<Projectile> getActiveProjectiles(){
    return this.activeProjectiles;
  }
  
  public void despawnNPCAgent(NPCAgent agent){
    activeNPCAgents.remove(agent);
    System.out.println(agent.getID() + " npc was despawned.");
  }
  
  public void despawnPlayerAgent(PlayerAgent agent){
    activePlayerAgents.remove(agent);
    System.out.println(agent.getID() + " player was despawned.");
  }

  public void despawnProjectile(Projectile projectile){
    activeProjectiles.remove(projectile);
    System.out.println("Projectile was despawned.");
  }

  /** Spawns a playable character entity. */
  public PlayerAgent spawnPlayer(String name) {
  	UUID id = UUID.randomUUID();
  	PlayerAgent player = new PlayerAgent(name, id, this, randomPlayerSpawn(), 0, 0, 0, 0, 0, 0/*TODO insert appropriate constructor variables*/);
  	activePlayerAgents.add(player);
    System.out.println(player.getID() + " player was spawned.");
  	return player;
  }

  public void spawnNPC(){
    NPCAgent agent = new TestEnemyAgent(UUID.randomUUID(), this, randomNPCSpawn(), 0/*this level won't be necessary eventually*/);
    activeNPCAgents.add(agent);
    System.out.println(agent.getID() + " npc was spawned.");
  }

  public void spawnProjectile(Projectile p){
    activeProjectiles.add(p);
    System.out.println("Projectile was spawned.");
  }
  
  public static Point polarToCartesian(double angle, double radius){
    Point p = new Point();
    double x = Math.cos(angle) * radius;
    double y = Math.sin(angle) * radius;
    p.setLocation(x, y);
    return p;
  }

  //Returns an array where the first value is the angle and the second is the radius
  public static double[] cartesianToPolar(Point p){
    double[] polar = new double[2];
    polar[0] = Math.atan2(p.getY(), p.getX());
    polar[1] = checkRadius(p);
    return polar;
  }
  
  public static double checkRadius(Point p){
    return Math.sqrt(Math.abs(p.getX()) * Math.abs(p.getX()) + Math.abs(p.getY()) * Math.abs(p.getY()));
  }
  
  public ArrayList<Agent> checkCollision(Projectile p){
    ArrayList<Agent> collisions = new ArrayList<Agent>();
    for (Agent a : getActivePlayerAgents()){
      if (p.getPosition() == a.getPosition() && a.getTeam() != p.getOwner().getTeam())
        collisions.add(a);
    }
    for (Agent a : getActiveNPCAgents()){
      if (p.getPosition() == a.getPosition() && a.getTeam() != p.getOwner().getTeam())
        collisions.add(a);
    }
    return collisions;
  }

  private Point randomPlayerSpawn(){
    double angle = Math.random() * 2 * Math.PI;
    return polarToCartesian(angle, getRadius());
  }

  private Point randomNPCSpawn(){
    double angle = Math.random() * 2 * Math.PI;
    double distance = Math.random() * getRadius();
    return polarToCartesian(angle, distance);
  }

  private void update(){
    for(PlayerAgent agent : getActivePlayerAgents())
      agent.update();
    for(NPCAgent agent : getActiveNPCAgents())
      agent.update();
    for(Projectile p : getActiveProjectiles())
      p.update();
    while(getActiveNPCAgents().size() < (NPCTOPLAYERRATIO * getActivePlayerAgents().size()))
      spawnNPC();
  }

	public void run() {
		while(gameplayOccurring)
			update();
	}

	public boolean isGameplayOccurring() {
		return gameplayOccurring;
	}

	public void setGameplayOccurring(boolean gameplayOccurring) {
		this.gameplayOccurring = gameplayOccurring;
	}
}
