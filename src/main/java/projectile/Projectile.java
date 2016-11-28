package main.java.projectile;

import main.java.agent.Agent;
import main.java.environment.Environment;

import java.awt.geom.Point2D;
import main.java.misc.Vector2D;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/*****************************************************************************
 * To-do:                                                                    *
 *   Award shooter points when collision results in kill                     *
 *   Check that onCollision() actually works                                 *
 *****************************************************************************/

public class Projectile {

	private final long TIME_TO_LIVE = 4000;

	private UUID id = UUID.randomUUID();
	private transient Environment environment;
	private Agent owner;
	private Point2D.Double position;
	private Vector2D velocity;
	private int damage;
	private double size;

	private Timer timer = new Timer("Projectile Timer");

	public Projectile(
		Environment environment, Agent owner, Point2D.Double position,
		Vector2D velocity, int damage, double size
	) {	
		this.environment = environment;
		this.owner = owner;
		this.position = position;
		this.velocity = velocity;
		this.damage = damage;
		this.size = size;

		// despawn when TIME_TO_LIVE is up
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				despawn(); // despawn the projectile
				timer.cancel(); // terminate the timer
			}
		}, TIME_TO_LIVE);
	}
	
	public final UUID getID() {
		return id;
	}

	protected final Environment getEnvironment() {
		return environment;
	}

	public final Agent getOwner() {
		return owner;
	}

	public final Point2D.Double getPosition() {
		return (Point2D.Double) position.clone();
	}
	
	public final Vector2D getVelocity() {
		return new Vector2D(velocity);
	}

	public final void update() {
		double oldX = position.getX();
		double oldY = position.getY();

		double newX = oldX + (velocity.getMagnitude() * Math.cos(velocity.getAngle()));
		double newY = oldY + (velocity.getMagnitude() * -Math.sin(velocity.getAngle()));

		position.setLocation(newX, newY);

		onCollision(environment.checkCollision(this));
	}

	public final void despawn() {
		environment.despawnProjectile(this);
	}

	protected final void onCollision(List<Agent> agents) {
		if (agents != null) {
			for (Agent agent : agents) {
				agent.applyDamage(damage);
				getOwner().awardPoints(damage);
				despawn();
				return;
			}
		}
	}
	
	public String getHexColor() {
		return getOwner().getHexColor();
	}
	
	public double getSize() {
		return size;
	}
}
