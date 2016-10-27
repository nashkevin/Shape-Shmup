package main.java.agent;

import java.awt.Point;
import java.util.UUID;

import main.java.misc.Vector2D;
import main.java.projectile.Projectile;
import main.java.projectile.ProjectileFactory;

/**
 * @ Zach Janice
 */

public abstract class Agent {
	private UUID id;
	private int level;
	private int team;
	
	private int health;
	private int maxHealth;
	private int damage;
	private int projectileSpeed;
	
	private Point position;
	private Vector2D velocity;
	
	private int baseMovementSpeed;
	private double movementSpeedFactor;
	private Vector2D acceleration;
	private Vector2D firingVector;
	private ProjectileFactory.Type projectileType;
	
	public Agent(UUID id, Point position, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		this.id = id;
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
		this.projectileType = ProjectileFactory.Type.NONE;
	}
	
	public final UUID getID() {
		return this.id;
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
	
	public final Vector2D getVelocity() {
		return new Vector2D(velocity);
	}
	
	public final void update() {
		preUpdateCall();
		
		move(acceleration);
		// TODO: relay?
		
		Projectile firedProjectile = fireProjectile(firingVector);
		if (firedProjectile != null) {
			// TODO: relay?
		}
	}
	
	public final int adjustHealth(int amount) {
		health += amount;
		
		health = Math.min(health, maxHealth);
		health = Math.max(health, 0);
		
		if (health == 0)
			despawn();
		
		return health;
	}
	
	public final void setBaseMovementSpeed(int amount) {
		baseMovementSpeed = Math.max(amount, 0);
	}
	
	public final void setMovementSpeedFactor(double factor) {
		movementSpeedFactor = Math.max(factor, 0.0);
	}
	
	public abstract void despawn();
	
	protected final void setAcceleration(Vector2D vector) {
		if (vector == null)
			acceleration = null;
		acceleration = new Vector2D(vector);
	}
	
	protected final void setFiringVector(Vector2D vector) {
		if (vector == null)
			firingVector = null;
		firingVector = new Vector2D(vector);
	}
	
	protected final void setProjectileType(ProjectileFactory.Type type) {
		projectileType = type;
	}
	
	protected abstract void preUpdateCall();
	
	private final void move(Vector2D vector) {
		// Create a new velocity
		// TODO: Create new velocity from given vector as acceleration
		if (vector == null)
			velocity = null;
		else
			velocity = new Vector2D(vector);
		
		double oldX = position.getX();
		double oldY = position.getY();
		
		double newX = oldX + (velocity.getMagnitude() * Math.cos(velocity.getAngle()));
		double newY = oldY + (velocity.getMagnitude() * Math.sin(velocity.getAngle()));
		
		position.setLocation(newX, newY);
	}
	
	private final Projectile fireProjectile(Vector2D vector) {
		if (vector == null)
			return null;
		
		Vector2D projVelocity = new Vector2D(projectileSpeed, vector.getAngle());
		Projectile projectile = ProjectileFactory.makeProjectile(projectileType, this, position, projVelocity);
		
		return projectile;
	}
}
