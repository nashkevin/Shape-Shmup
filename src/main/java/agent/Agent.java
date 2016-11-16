package main.java.agent;
import main.java.agent.test.AgentTestImp;
import main.java.agent.test.EnvironmentMock;
import main.java.environment.Environment;

import java.awt.Point;
import java.util.Random;
import java.util.UUID;

import main.java.misc.Vector2D;
import main.java.projectile.Projectile;
import main.java.projectile.ProjectileFactory;

/**
 * @ Zach Janice
 */

public abstract class Agent {
	private UUID id;
	private transient Environment env;
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
	
	public Agent(UUID id, Environment env, Point position, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		//if (id == null)
		//	throw new Exception("Cannot have null ID parameter.");
		//if (env == null)
		//	throw new Exception("Cannot have null environment parameter.");
		//if (position == null)
		//	throw new Exception("Cannot have null position parameter.");
		//if (health <= 0)
		//	throw new Exception("Cannot have zero or negative health parameter.");
		
		this.id = id;
		this.env = env;
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
	
	protected final Environment getEnvironment() {
		return env;
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
	
	public final int getMaxHealth() {
		return maxHealth;
	}
	
	public final int getDamage() {
		return damage;
	}
	
	public final int getProjectileSpeed() {
		return projectileSpeed;
	}
	
	public final Point getPosition() {
		if (position == null) {
			return null;
		}
		else {
			return new Point(position);
		}
	}
	
	public final Vector2D getVelocity() {
		if (velocity == null) {
			return null;
		}
		else {
			return new Vector2D(velocity);
		}
	}

	public final Vector2D getAcceleration() {
		if (acceleration == null) {
			return null;
		}
		else {
			return new Vector2D(acceleration);
		}
	}

	public final Vector2D getFiringVector() {
		if (firingVector == null) {
			return null;
		}
		else {
			return new Vector2D(firingVector);
		}
	}
	
	public final ProjectileFactory.Type getProjectileType() {
		return projectileType;
	}
	
	public final int getBaseMovementSpeed() {
		return baseMovementSpeed;
	}
	
	public final double getMovementSpeedFactor() {
		return movementSpeedFactor;
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

	public void setPosition(int x, int y) {
		this.position.setLocation(x, y);
	}

	public void setPosition(Point p) {
		this.position.setLocation(p);
	}
	
	protected final void setAcceleration(Vector2D vector) {
		if (vector == null) {
			acceleration = null;
		}
		else {
			acceleration = new Vector2D(vector);
		}
	}
	
	protected final void setFiringVector(Vector2D vector) {
		if (vector == null) {
			firingVector = null;
		}
		else {
			firingVector = new Vector2D(vector);
		}
	}
	
	protected final void setProjectileType(ProjectileFactory.Type type) {
		projectileType = type == null ? ProjectileFactory.Type.NONE : type;
	}
	
	protected abstract void preUpdateCall();
	
	private final void move(Vector2D vector) {
		// Create a new velocity
		// TODO: Create new velocity from given vector as acceleration
		if (vector == null)
			velocity = new Vector2D(0, 0);
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
		Projectile projectile = ProjectileFactory.makeProjectile(projectileType, env, this, position, projVelocity);
		
		return projectile;
	}
	
	public static final class AgentTester {
		private static Agent testInstance;
		
		public static void generateTestInstance() {
			Random random = new Random();
			
			UUID id = UUID.randomUUID();
			Environment env = new EnvironmentMock(random.nextDouble() * 100);
			Point position = new Point();
			position.setLocation(random.nextDouble(), random.nextDouble());
			int level = random.nextInt(10);
			int team = random.nextInt(5);
			int health = random.nextInt(490) + 10;
			int damage = random.nextInt(500) + 1;
			int projectileSpeed = random.nextInt(100) + 1;
			int baseMovementSpeed = random.nextInt(100) + 1;
			
			testInstance = new AgentTestImp(id, env, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
		}
		
		public static Agent getTestInstance() {
			return testInstance;
		}
		
		public static void call_setAcceleration(Vector2D vector) {
			testInstance.setAcceleration(vector);
		}
		
		public static void call_setFiringVector(Vector2D vector) {
			testInstance.setFiringVector(vector);
		}
		
		public static void call_setProjectileType(ProjectileFactory.Type type) {
			testInstance.setProjectileType(type);
		}
		
		public static void call_move(Vector2D vector) {
			testInstance.move(vector);
		}
		
		public static void call_fireProjectile(Vector2D vector) {
			testInstance.fireProjectile(vector);
		}
	}
}
