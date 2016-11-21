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

<<<<<<< HEAD
	public Projectile(Environment env, Agent owner, Point position,
		Vector2D velocity) {
		
		//if (env == null)
		//	throw new Exception("Cannot have null environment parameter.");
		//if (owner == null)
		//	throw new Exception("Cannot have null owner.");
		//if (position == null)
		//	throw new Exception("Cannot have null position parameter.");
		//if (velocity == null)
		//	throw new Exception("Cannot have null velocity.");
		
		this.env = env;
=======
	public Projectile(
		Environment environment, Agent owner, Point position,
		Vector2D velocity, int damage
	) {	
		this.environment = environment;
>>>>>>> ExtremeAgentOverhaul
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

<<<<<<< HEAD
	protected abstract void onCollision(List<Agent> agents);
	
	public static final class ProjectileTester {
		private static Projectile testInstance;
		
		public static void generateTestInstance() {
			Random random = new Random();
			
			Environment env = new EnvironmentMock();
			Agent.AgentTester.generateTestInstance();
			Agent owner = Agent.AgentTester.getTestInstance();
			Point position = new Point();
			position.setLocation(random.nextDouble(), random.nextDouble());
			Vector2D velocity = new Vector2D(random.nextDouble() * 5, random.nextDouble() * 5);
			
			//testInstance = new ProjectileTestImp(env, owner, position, velocity);
		}
		
		public static final Projectile getTestInstance() {
			return testInstance;
		}
		
		public static final Environment call_getEnvironment() {
			return testInstance.getEnvironment();
=======
	protected final void onCollision(List<Agent> agents) {
		if (agents != null && !agents.isEmpty()) {
			Agent firstHit = agents.get(0);
			firstHit.applyDamage(damage);
>>>>>>> ExtremeAgentOverhaul
		}
	}
}
