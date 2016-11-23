package main.java.agent;

import main.java.environment.Environment;
import main.java.misc.Vector2D;
import main.java.projectile.ProjectileFactory;

import java.awt.geom.Point2D;
import java.util.UUID;


public abstract class Agent {

	public static enum Team {
		ENEMY("0xF8CECC"), RED("0xD4E9D3"), BLUE("0x0000FF");
		
		private String hexColor;
		private Team(String hexColor) {
			this.hexColor = hexColor;
		}
		public String getColor() {
			return hexColor;
		}
	}

	private UUID id = UUID.randomUUID();
	private double angle = 0;
	private Vector2D velocity = new Vector2D(0.0, 0.0);
	private transient Environment environment;
	private Point2D.Double position;
	private Team team;
	private double size;

	private ProjectileFactory gun;

	/* Gameplay Attributes */
	/** current health value, reaching zero will trigger despawning */
	private int health;
	/** the maximum value that health can reach */
	private int maxHealth;
	/** how speedy the Agent is, affects acceleration and max speed */
	private double haste;
	/** Agent must wait this duration (in milliseconds) before firing */
	private int firingDelay;

	private boolean isReadyToFire;

	public Agent(
		Environment environment, Point2D.Double position, ProjectileFactory gun,
		Team team, double size, int health, double haste
	) {
		this.environment = environment;
		this.position = (Point2D.Double) position.clone();
		this.team = team;
		this.size = size;
		this.health = health;
		this.maxHealth = health;
		this.haste = haste;
		this.gun = gun;
	}

	/******************************
	 * start of getters and setters *
	 ******************************/
	public final UUID getID() {
		return id;
	}

	public final double getAngle() {
		return angle;
	}

	public final void setAngle(double angle) {
		this.angle = angle;
	}

	public final Vector2D getVelocity() {
		return velocity;
	}

	public final void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	protected final Environment getEnvironment() {
		return environment;
	}

	public final Point2D.Double getPosition() {
		return (position == null) ? null : (Point2D.Double) position.clone();
	}

	public final void setPosition(Point2D.Double p) {
		setPosition(p.getX(), p.getY());
	}

	public final void setPosition(double x, double y) {
		position.setLocation(x, y);
	}

	public final ProjectileFactory getGun() {
		return gun;
	}

	public final void setGun(ProjectileFactory gun) {
		this.gun = gun;
	}

	public final Team getTeam() {
		return team;
	}

	public final void setTeam(Team team) {
		this.team = team;
	}

	public final double getSize() {
		return size;
	}

	public final void setSize(double size) {
		this.size = size;
	}

	public final int getHealth() {
		return health;
	}

	public final void setHealth(int health) {
		this.health = health;

		// if given health exceeds maxHealth, increase to maxHealth
		this.health = (health > maxHealth) ? maxHealth : health;

		if (health <= 0) {
			despawn();
		}
	}

	public final int getMaxHealth() {
		return maxHealth;
	}

	public final void setMaxHealth(int maxHealth) {
		if (maxHealth <= 0) {
			throw new IllegalArgumentException("maxHealth must have a positive value");
		}
		this.maxHealth = maxHealth;

		// if current health exceeds maxHealth, reduce to maxHealth
		this.health = (health > maxHealth) ? maxHealth : health;
	}

	public final double getHaste() {
		return haste;
	}

	public final void setHaste(double haste) {
		this.haste = haste;
	}
	/** Override if you want an agent to display with a different color. */
	public String getHexColor() {
		return getTeam().getColor();
	};
	/******************************
	 * end of getters and setters *
	 ******************************/

	/** Give experience points to agent. If the agent is a player agent, it should
	 * accumulate the points. If it is an NPC, it should not do anything. */
	public abstract void awardPoints(int pointsAwarded);

	/** reduces health by an amount */
	public final void applyDamage(int amount) {
		setHealth(getHealth() - amount);
	}

	/** increases health by an amount */
	public final void applyHealing(int amount) {
		setHealth(getHealth() + amount);
	}

	public final double getAngleTo(Agent other) {
		return getAngleTo(other.getPosition());
	}

	public final double getAngleTo(Point2D.Double p) {
		return getAngleTo(p.getX(), p.getY());
	}

	public final double getAngleTo(double x, double y) {
		return -Math.atan2(y - this.getPosition().getY(),
			x - this.getPosition().getX());
	}

	public abstract void despawn();
	public abstract void update();
}
