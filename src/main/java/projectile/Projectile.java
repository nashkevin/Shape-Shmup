package main.java.projectile;

import main.java.environment.Environment;

import main.java.agent.Agent;
import main.java.agent.test.EnvironmentMock;

import java.awt.Point;
import main.java.misc.Vector2D;

import java.util.List;
import java.util.Random;

public abstract class Projectile {
	private transient Environment env;
	private Agent owner;
	private Point position;
	private Vector2D velocity;

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
		this.owner = owner;
		this.position = position;
		this.velocity = velocity;
	}

	protected final Environment getEnvironment() {
		return env;
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

		onCollision(env.checkCollision(this));
	}

	public final void despawn() {
		env.despawnProjectile(this);
	}

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
		}
	}
}
