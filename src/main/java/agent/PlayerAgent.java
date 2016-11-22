package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;
import main.java.web.ClientInput;

import java.awt.geom.Point2D;

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
	private int pointsUntilLevelUp;

	Queue<ClientInput> eventInbox;

	public PlayerAgent(
		Environment environment, Point2D.Double position, String name
	) {
		super(
			environment,
			position,
			new ProjectileFactory(environment, null, 1, 10,
				Math.toRadians(5.0), 750, 1.0),
			Agent.Team.RED,
			1,
			100,
			1
		);

		this.name = name;
		this.pointsUntilLevelUp = levelToPoints(level + 1);
		this.eventInbox = new LinkedList<ClientInput>();

		this.getGun().setOwner(this);
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public void awardPoints(int pointsAwarded) {
		if (pointsAwarded >= pointsUntilLevelUp) {
			this.points = pointsAwarded - pointsUntilLevelUp;
			level++; // Level up!
			upgrade(level);
			while (this.points > levelToPoints(level)) {
				this.points -= levelToPoints(level);
				level++;
				upgrade(level);
			}
			pointsUntilLevelUp = levelToPoints(level) - this.points;
		} else {
			this.points += pointsAwarded;
		}
	}

	private void upgrade(int level) {
		// To-do: modify attributes based on level
	}

	/** Returns the number of points needed to reach the given level */
	public static int levelToPoints(int level) {
		return level * level + 100;
	}

	/** Returns the level that would be reached by earning
	 *  the given number of points */
	public static int pointsToLevel(int points) {
		return (points < 100) ? 0 : (int)Math.sqrt(points - 100);
	}	

	@Override
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
		int horizontalInput = 0;
		/** combined vertical inputs */
		int verticalInput = 0;

		while (!eventInbox.isEmpty()) {
			ClientInput event = eventInbox.poll();
			
			if (event.getAngle() != null) {
				setAngle(event.getAngle());
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
				getGun().fireProjectile();
			}

			horizontalInput = countRight - countLeft;
			verticalInput = countUp - countDown;

			if (verticalInput != 0 || horizontalInput != 0) {
				move(Math.atan2(verticalInput, horizontalInput));
			} else {
				move();
			}
		}
	}

	private void move(double inputAngle) {
		// x component of velocity
		double x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
		// y component of velocity
		double y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());

		// increase velocity componentwise by haste
		x += getHaste() * Math.cos(inputAngle);
		y += getHaste() * Math.sin(inputAngle);

		// update velocity angle following the above increase
		getVelocity().setAngle(Math.atan2(y, x));

		getVelocity().setMagnitude(Math.sqrt(x * x + y * y));

		if (getVelocity().getMagnitude() >= getHaste() * MAX_SPEED_MULTIPLE) {
			getVelocity().setMagnitude(getHaste() * MAX_SPEED_MULTIPLE);
		}

		x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
		y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());

		x += getPosition().getX();
		y += getPosition().getY();

		setPosition((int)(x + 0.5), (int)(y + 0.5));
	}

	private void move() {
		if (getVelocity().getMagnitude() > 0.0) {
			getVelocity().setMagnitude(getVelocity().getMagnitude() - getHaste());

			double x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
			double y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());
			
			x += getPosition().getX();
			y += getPosition().getY();

			setPosition((int)(x + 0.5), (int)(y + 0.5));
		} else {
			getVelocity().setMagnitude(0.0);
		}
	}
}
