package main.java.projectile;

import main.java.environment.Environment;

import main.java.agent.Agent;
import java.awt.Point;
import main.java.misc.Vector2D;
import java.util.List;


public abstract class Projectile {

	private transient Environment environment;
	private Agent owner;
	private Point position;
	private Vector2D velocity;

	public Projectile(
		Environment environment, Agent owner, Point position,
		Vector2D velocity
	) {	
		this.environment = environment;
		this.owner = owner;
		this.position = position;
		this.velocity = velocity;
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

	protected abstract void onCollision(List<Agent> agents);
}
