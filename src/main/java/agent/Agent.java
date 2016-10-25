package agent;

import java.awt.Point;
import java.util.Vector;

/**
 * @ Zach Janice
 */

public abstract class Agent {
	private int level;
	private int team;
	
	private int health;
	private int maxHealth;
	private int damage;
	private int projectileSpeed;
	
	private Point position;
	private Vector velocity;
	
	private int baseMovementSpeed;
	private double movementSpeedFactor;
	private Vector acceleration;
	private Vector firingVector;
	
	public Agent(Point position, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		this.level = level;
		this.team = team;
		
		this.health = health;
		this.maxHealth = health;
		this.damage = damage;
		this.projectileSpeed = projectileSpeed;
		
		this.position = new Point(position);
		this.velocity = null;
		
		this.baseMovementSpeed = baseMovementSpeed;
		this.movementSpeedFactor = 1.0;
		this.acceleration = null;
		this.firingVector = null;
	}
	
	public final int getLevel() {
		return level;
	}
	
	public final int getTeam() {
		return team;
	}
	
	public final int getHealth() {
		return health;
	}
	
	public final int getDamage() {
		return damage;
	}
	
	public final Point getPosition() {
		return new Point(position);
	}
	
	public final int getBaseMovementSpeed() {
		return baseMovementSpeed;
	}
	
	public final double getMovementSpeedFactor() {
		return movementSpeedFactor;
	}
	
	public final Vector getVelocity() {
		return new Vector(velocity);
	}
	
	public final void update() {
		// TODO
	}
	
	public final int adjustHealth(int amount) {
		// TODO
	}
	
	public final void setBaseMovementSpeed(int amount) {
		// TODO
	}
	
	public final void setMovementSpeedFactor(double factor) {
		// TODO
	}
	
	public final void despawn() {
		// TODO
	}
	
	protected final void setMovementVector(Vector vector) {
		// TODO
	}
	
	protected final void setFiringVector(Vector vector) {
		// TODO
	}
	
	protected abstract void preUpdateCall();
	
	private final void move() {
		// TODO
	}
	
	private final void fireProjectile() {
		// 	TODO
	}
}
