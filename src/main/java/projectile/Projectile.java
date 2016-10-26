package main.java.projectile;

import main.java.agent.Agent;
import java.awt.Point;
import main.java.misc.Vector2D;
import java.util.List;

/**
 * @ Zach Janice
 */

public abstract class Projectile {
	private Agent owner;
	private Point position;
	private Vector2D velocity;
	
	public Projectile(Agent owner, Point position, Vector2D velocity) {
		this.owner = owner;
		this.position = position;
		this.velocity = velocity;
	}
	
	public Agent getOwner() {
		return owner;
	}
	
	public Point getPosition() {
		return new Point(position);
	}
	
	public Vector2D getVelocity() {
		return new Vector2D(velocity);
	}
	
	public final void update() {
		double oldX = position.getX();
		double oldY = position.getY();
		
		double newX = oldX + (velocity.getMagnitude() * Math.cos(velocity.getAngle()));
		double newY = oldY + (velocity.getMagnitude() * Math.sin(velocity.getAngle()));
		
		position.setLocation(newX, newY);
		
		// TODO: Ask environment about collisions
	}
	
	public final void despawn() {
		// TODO
	}
	
	protected abstract void onCollision(List<Agent> agents);
}
