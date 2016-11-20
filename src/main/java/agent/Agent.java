package main.java.agent;

import main.java.environment.Environment;
import main.java.misc.Vector2D;
import main.java.projectile.ProjectileFactory;

import java.awt.Point;
import java.util.UUID;


/*****************************************************************************
 * To-do:                                                                    *
 *   Nothing in this one...                                                  *
 *****************************************************************************/

public abstract class Agent {
	
	public static enum Team {
		ENEMY, RED, BLUE
	}

	private UUID id = UUID.randomUUID();
	private double rotation = 0;
	private transient Environment environment;
	private Point position;
	private Team team;
	private double size;

	private ProjectileFactory gun;

	/** gameplay attributes */
	private int health;
	private int maxHealth;
	private int speed;
	private int firingDelay; // must wait this duration (in ms) before firing

	private boolean isReadyToFire;

	public Agent(
		Environment environment, Point position, ProjectileFactory gun,
		Team team, double size, int health, int speed
	) {
		this.environment = environment;
		this.position = new Point(position);
		this.team = team;
		this.size = size;
		this.health = health;
		this.maxHealth = health;
		this.speed = speed;
		this.gun = gun;
	}

	/******************************
	 * start of getters and setters *
	 ******************************/
	public final UUID getID() {
		return id;
	}

	public final double getRotation() {
		return rotation;
	}

	public final void setRotation(double rotation) {
		this.rotation = rotation;
	}

	protected final Environment getEnvironment() {
		return environment;
	}

	public final Point getPosition() {
		return (position == null) ? null : new Point(position);
	}

	public final void setPosition(int x, int y) {
		this.position.setLocation(x, y);
	}

	public final void setPosition(Point p) {
		this.position.setLocation(p);
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
	}

	public final int getMaxHealth() {
		return maxHealth;
	}

	public final void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;

		// if current health exceeds maxHealth, reduce to maxHealth
		this.health = (health > maxHealth) ? maxHealth : health;
	}

	public final int getSpeed() {
		return speed;
	}

	public final void setSpeed(int speed) {
		this.speed = speed;
	}
	/******************************
	 * end of getters and setters *
	 ******************************/

	/** reduces health by an amount */
	public final void applyDamage(int amount) {
		health -= amount;
		if (health <= 0) {
			despawn();
		}
	}

	public final double getAngleTo(Agent other) {
		return Math.atan2(other.getPosition().getY() - this.getPosition().getY(),
			other.getPosition().getX() - this.getPosition().getX());
	}

	public abstract void update();
	public abstract void despawn();
}
