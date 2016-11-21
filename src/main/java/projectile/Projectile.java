package main.java.projectile;

import main.java.environment.Environment;

import main.java.agent.Agent;
import main.java.agent.test.EnvironmentMock;

import java.awt.Point;
import main.java.misc.Vector2D;

import java.util.List;
import java.util.Random;

/*****************************************************************************
 * To-do:                                                                    *
 *   Award shooter points when collision results in kill                     *
 *   Check that onCollision() actually works                                 *
 *****************************************************************************/

public class Projectile {

	private transient Environment environment;
	private Agent owner;
	private Point position;
	private Vector2D velocity;
	private int damage;

	public Projectile(
		Environment environment, Agent owner, Point position,
		Vector2D velocity, int damage
	) {	
		this.environment = environment;
		this.owner = owner;
		this.position = position;
		this.velocity = velocity;
		this.damage = damage;
	}

	protected final Environment getEnvironment() {
		return environment;
	}

	public final Agent getOwner() {
		return owner;
	}

	public final Point getPosition() {
		return new Point(position);
	}
	
	public final Vector2D getVelocity() {
		return new Vector2D(velocity);
	}

	public final void update() {
		double oldX = position.getX();
		double oldY = position.getY();

		double newX = oldX + (velocity.getMagnitude() * Math.cos(velocity.getAngle()));
		double newY = oldY + (velocity.getMagnitude() * Math.sin(velocity.getAngle()));

		position.setLocation(newX, newY);

		onCollision(environment.checkCollision(this));
	}

	public final void despawn() {
		environment.despawnProjectile(this);
	}

	protected final void onCollision(List<Agent> agents) {
		if (agents != null && !agents.isEmpty()) {
			Agent firstHit = agents.get(0);
			firstHit.applyDamage(damage);
		}
	}
}
