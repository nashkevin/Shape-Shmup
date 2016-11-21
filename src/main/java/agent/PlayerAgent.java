package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;
import main.java.web.ClientInput;

import java.awt.Point;

import java.util.Queue;
import java.util.LinkedList;


/*****************************************************************************
 * To-do:                                                                    *
 *   Implement points gain and levelling up system                           *
 *   Implement levelling up to increase stats                                *
 *****************************************************************************/

public class PlayerAgent extends Agent {

	/** PlayerAgent's speed will not exceed its haste times this multiple */
	private int MAX_SPEED_MULTIPLE = 5;

	private String name = "An Unnamed Hero";
	private int level = 1;
	private int points = 0;

	Queue<ClientInput> eventInbox; 

	public PlayerAgent(
		Environment environment, Point position, String name
	) {
		super(
			environment,
			position,
			new ProjectileFactory(environment, null, 1, 10,
				Math.toRadians(5.0), 750, 1.0),
			Agent.Team.RED,
			5,
			100,
			20
		);

		this.name = name;
		this.eventInbox = new LinkedList<ClientInput>();

		this.getGun().setOwner(this);
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void despawn() {
		getEnvironment().despawnPlayerAgent(this);
	}
	
	public void addPlayerEvent(ClientInput event) {
		eventInbox.add(event);
	}

	public Queue<ClientInput> getPlayerEvents() {
		return new LinkedList<ClientInput>(eventInbox);
	}

	@Override
	public void update() {
		/** number of inputs for the left direction */
		int countLeft = 0;
		/** number of inputs for the right direction */
		int countRight = 0;
		/** number of inputs for the up direction */
		int countUp = 0;
		/** number of inputs for the down direction */
		int countDown = 0;

		/** combined horizontal inputs */
		int horizontalDrive = 0;
		/** combined vertical inputs */
		int verticalDrive = 0;

		if (eventInbox.isEmpty()) {
			return; // don't change state of the agent if there are no inputs to be queued
		}
		while (!eventInbox.isEmpty()) {
			ClientInput event = eventInbox.poll();
			
			if (event.getAngle() != null) {
				setRotation(event.getAngle());
			}

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
				getGun.fireProjectile();
			}

			horizontalDrive = countRight - countLeft;
			verticalDrive = countUp - countDown;

			if (event.isMoving()) {
				double x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
				double y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());

				x += horizontalDrive * getHaste() * Math.cos(getRotation());
				y += verticalDrive * getHaste() * Math.sin(getRotation());

				getVelocity.setAngle(Math.atan2(y, x));

<<<<<<< HEAD
			String name = "tester";
			UUID id = UUID.randomUUID();
			Environment env = new EnvironmentMock();
			Point position = new Point();
			position.setLocation(random.nextDouble(), random.nextDouble());
			int level = random.nextInt(10);
			int team = random.nextInt(5);
			int health = random.nextInt(490) + 10;
			int damage = random.nextInt(500) + 1;
			int projectileSpeed = random.nextInt(100) + 1;
			int baseMovementSpeed = random.nextInt(100) + 1;
			
			//TODO: make player test agent implementation class
			testInstance = new PlayerAgentTestImp(name, id, env, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
		}

		public static PlayerAgent getTestInstance() {
			return testInstance;
		}
=======
				getVelocity.setMagnitude(Math.sqrt(x * x + y * y));
				if (getVelocity().getMagnitude() >= getHaste() * MAX_SPEED_MULTIPLE) {
					getVelocity.setMagnitude(getHaste() * MAX_SPEED_MULTIPLE);
				}
>>>>>>> ExtremeAgentOverhaul

				x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
				y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());
				
				getPosition().translate(x, y);
			}
		}
	}
}
