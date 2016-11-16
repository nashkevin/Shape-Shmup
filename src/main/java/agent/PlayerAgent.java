package main.java.agent;
import main.java.agent.test.AgentTestImp;
import main.java.agent.test.EnvironmentMock;
import main.java.agent.test.PlayerAgentTestImp;
import main.java.environment.Environment;

import java.awt.Point;

import main.java.misc.Vector2D;
import main.java.projectile.ProjectileFactory;
import main.java.web.ClientInput;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
public class PlayerAgent extends Agent {

	Queue<ClientInput> eventInbox; 

	public PlayerAgent(UUID id, Environment env, Point position, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		super(id, env, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
        this.eventInbox = new LinkedList<ClientInput>();
	}

	public final void despawn() {
		getEnvironment().despawnPlayerAgent(this);
	}
	/***************************************************************
	 * TODO: make preparations given angle rather than coordinates *
	 ***************************************************************/
	protected void preUpdateCall() {
		final Point ORIGIN = new Point(); // (0, 0), used for reference in calculating vectors
		int countLeft = 0;
		int countRight = 0;
		int countUp = 0;
		int countDown = 0;
		Vector2D firingVector = null;
		
		while (!eventInbox.isEmpty()) {
			ClientInput event = eventInbox.poll();
			
			if (event.isLeft()) {
				countLeft++;
			}
			if (event.isRight()) {
				countRight++;
			}
			if (event.isUp()) {
				countUp++;
			}
			if (event.isDown()) {
				countDown++;
			}
			
			if (event.isFiring()) {
				double angle = event.getAngle();
				firingVector = new Vector2D(1, angle);
			}
		}

		//calculate acceleration vector
		int horizontalDistance = countRight - countLeft;
		int verticalDistance = countUp - countDown;

		Point accelerationPoint = new Point(horizontalDistance, verticalDistance);

		double accelerationAngle = calculateAngle(ORIGIN, accelerationPoint);
		double accelerationMagnitude = calculateMagnitude(ORIGIN, accelerationPoint);

		Vector2D accelVector = new Vector2D(accelerationMagnitude, accelerationAngle);
		
		//set vectors
		setAcceleration(accelVector);
		setFiringVector(firingVector);
	}

	private static double calculateAngle(Point p1, Point p2) {
		double verticalDistance = p2.y - p1.y;
		double horizontalDistance = p2.x - p1.x;
		return Math.atan2(verticalDistance, horizontalDistance);
	}

	private static double calculateMagnitude(Point p1, Point p2) {
		double verticalDistance = p2.y - p1.y;
		double horizontalDistance = p2.x - p1.x;
		return Math.sqrt(verticalDistance * verticalDistance + horizontalDistance * horizontalDistance);
	}

	public void addPlayerEvent(ClientInput event) {
        eventInbox.add(event);
	}

	public Queue<ClientInput> getPlayerEvents() {
		return new LinkedList<ClientInput>(eventInbox);
	}


	public static final class PlayerAgentTester {
		private static PlayerAgent testInstance;
		
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
			
			//TODO: make player test agent implementation class
			testInstance = new PlayerAgentTestImp(id, env, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
		}
		
		public static PlayerAgent getTestInstance() {
			return testInstance;
		}
		
		public static void call_preUpdateCall() {
			testInstance.preUpdateCall();
		}

		public static void call_addPlayerEvent(ClientInput event) {
			testInstance.addPlayerEvent(event);
			
		}
	}
}
