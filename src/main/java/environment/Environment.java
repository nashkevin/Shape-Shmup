package main.java.environment;

import java.awt.Point;
import java.util.*;

import main.java.agent.*;
import main.java.misc.*;
import main.java.projectile.*;


public class Environment {
  
  private double radius;
  private HashSet<PlayerAgent> activePlayerAgents;
  private HashSet<NPCAgent> activeNPCAgents;
  private HashSet<Projectile> activeProjectiles;
  
  public Environment(double radius, Point origin) {
    this.radius = radius;
  }

  public double getRadius(){
    return this.radius;
  }

  public HashSet<PlayerAgent> getActivePlayerAgents(){
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

  public void spawnPlayer(String name, String id){
    PlayerAgent player = new PlayerAgent(randomPlayerSpawn(), 0, 0, 0, 0, 0, 0/*TODO insert appropriate constructor variables*/);
    activePlayerAgents.add(player);

    //TODO send update message to server
  }

  public void spawnNPC(){
    NPCAgent agent = new TestEnemyAgent(randomNPCSpawn(), 0/*this level won't be necessary eventually*/);
    activeNPCAgents.add(agent);
    
    //TODO send update message to server
  }

  private Point randomPlayerSpawn(){
    double angle = Math.random() * 360;
    return polarToCartesian(angle, getRadius());
  }

  private Point randomNPCSpawn(){
    double angle = Math.random() * 360;
    double distance = Math.random() * radius;
    return polarToCartesian(angle, distance);
  }
  
  private static Point polarToCartesian(double angle, double distance){
    Point p = new Point();
    double x = Math.cos(angle) * distance;
    double y = Math.sin(angle) * distance;
    p.setLocation(x, y);
    return p;
  }
  
  private double checkRadius(Point p){
    return Math.sqrt(Math.abs(p.getX()) * Math.abs(p.getX()) + Math.abs(p.getY()) * Math.abs(p.getY()));
  }
  
  private boolean checkCollision(Agent agent, Projectile p){
    boolean collision = false;
    //TODO - Collision is not necessarily defined by same position
    if (p.getPosition() == agent.getPosition() && agent.getTeam() != p.getOwner().getTeam())
      collision = true;
    return collision;
  }
    
}
