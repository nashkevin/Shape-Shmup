import java.awt.Point;
import java.util.*;

import main.java.agent;
import main.java.misc.*;
import main.java.projectile.*;


public class Environment {
  
  private double radius;
  private Point origin;
  private int activePlayerAgents;
  private int activeNPCAgents;
  private int activeProjectiles;
  
  public Environment(double radius, Point origin) {
    this.radius = radius;
    this.origin = origin;
  }
  
  
  public void setTimer(long time, String msg, agent a){
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run(){
        //TODO 
      }
    }, time);
  }
  
  //TODO - INTEGRATION
  public void despawn(Agent agent){
    //Call agent despawn method
  }
  
  public void spawn(){
    double angle = Math.random() * 360;
    double distance = Math.random() * radius;
    polarToCartesian(angle, distance);
    //TODO NPCAgent spawn method
    
    
    
  }
  
  private static Point polarToCartesian(double angle, double distance){
    Point p = new Point();
    double x = Math.cos(angle) * distance;
    double y = Math.sin(angle) * distance;
    p.setX((int)x);
    p.setY((int)y);
    return p;
  }
  
  private double checkRadius(Point p){
    return Math.sqrt(Math.abs(p.getX()) * Math.abs(p.getX()) + Math.abs(p.getY()) * Math.abs(p.getY()));
  }
  
  public void spawnPlayer(PlayerAgent player){
    //TODO
  }
  
  private Point randomPlayerSpawn(){
    double r = Math.random() * 360;
    return polarToCartesian(radius, r);
  }
  
  //TODO - Integration: Send agent hash to server?
  public void getAgentStatus(){
    
  }
  
  private boolean checkCollision(Agent agent, Projectile p){
    boolean collision = false;
    //TODO - Collision is not necessarily defined by same position
    if (p.getPosition() == agent.getPosition() && agent.getTeam() != p.getOwner().getTeam())
      collision = true;
    return collision;
  }
    
}
