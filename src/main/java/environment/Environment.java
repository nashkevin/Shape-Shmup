package main.java.environment;

import java.awt.Point;
import java.util.*;

import main.java.agent.*;
import main.java.projectile.*;


public class Environment {
  
  private double radius;
  /** Maps from session ID (of WebSocket connection) to agent for each client. */
  private HashMap<String, PlayerAgent> activePlayerAgents = new HashMap<String, PlayerAgent>();
  private HashSet<NPCAgent> activeNPCAgents = new HashSet<NPCAgent>();
  private HashSet<Projectile> activeProjectiles = new HashSet<Projectile>();
  
  public Environment(double radius) {
    this.radius = radius;
  }

  public double getRadius(){
    return this.radius;
  }

  public HashMap<String, PlayerAgent> getActivePlayerAgents(){
    return this.activePlayerAgents;
  }

  public HashSet<NPCAgent> getActiveNPCAgents(){
    return this.activeNPCAgents;
  }

  public HashSet<Projectile> getActiveProjectiles(){
    return this.activeProjectiles;
  }
  
  public void despawnNPCAgent(NPCAgent agent){
    activeNPCAgents.remove(agent);
    agent.despawn();
  }
  
  public void despawnPlayerAgent(PlayerAgent agent){
    activePlayerAgents.remove(agent);
    agent.despawn();
  }

  public void despawnProjectile(Projectile projectile){
    activeProjectiles.remove(projectile);
    projectile.despawn();
  }

  /** Spawns a playable character entity with the given display name and WebSocket session ID. */
  public void spawnPlayer(String name, String sessionId) {
	UUID id = UUID.randomUUID();
	PlayerAgent player = new PlayerAgent(id, this, randomPlayerSpawn(), 0, 0, 0, 0, 0, 0/*TODO insert appropriate constructor variables*/);
	activePlayerAgents.put(sessionId, player);
	
	//TODO send update message to server
  }

  public void spawnNPC(){
    NPCAgent agent = new TestEnemyAgent(UUID.randomUUID(), this, randomNPCSpawn(), 0/*this level won't be necessary eventually*/);
    activeNPCAgents.add(agent);
    
    //TODO send update message to server
  }
  
  public static Point polarToCartesian(double angle, double radius){
    Point p = new Point();
    double x = Math.cos(angle * Math.PI / 180) * radius;
    double y = Math.sin(angle * Math.PI / 180) * radius;
    p.setLocation(x, y);
    return p;
  }

  //Returns an array where the first value is the angle and the second is the radius
  public static double[] cartesianToPolar(Point p){
    double[] polar = new double[2];
    polar[0] = Math.atan2(p.getY(), p.getX()) * 180 / Math.PI;
    polar[1] = checkRadius(p);
    return polar;
  }
  
  public static double checkRadius(Point p){
    return Math.sqrt(Math.abs(p.getX()) * Math.abs(p.getX()) + Math.abs(p.getY()) * Math.abs(p.getY()));
  }
  
  public ArrayList<Agent> checkCollision(Projectile p){
    ArrayList<Agent> collisions = new ArrayList<Agent>();
    for (Agent a : getActivePlayerAgents().values()){
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
    double angle = Math.random() * 360;
    return polarToCartesian(angle, getRadius());
  }

  private Point randomNPCSpawn(){
    double angle = Math.random() * 360;
    double distance = Math.random() * getRadius();
    return polarToCartesian(angle, distance);
  }
    
}
