package main.java.agent;

import java.awt.Point;
import main.java.misc.Vector2D;
import main.java.web.ClientInput;
import java.util.Queue;
import java.util.LinkedList;
import java.util.UUID;
public class PlayerAgent extends Agent {

	private Vector2D velocity;
	Vector2D acceleration;
	private Vector2D firingVector;

	Queue<ClientInput> eventInbox; 

	public PlayerAgent(UUID id, Point position, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		super(id, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
        this.eventInbox = new LinkedList<ClientInput>();
	}

	protected void preUpdateCall() {
		final Point ORIGIN = new Point(); // (0, 0), used for reference in calculating vectors
		int countLeft = 0;
		int countRight = 0;
		int countUp = 0;
		int countDown = 0;
		Integer clickX = null;
		Integer clickY = null;
		//get all events
		while (!eventInbox.isEmpty()) {
			ClientInput event = eventInbox.poll();
			if (event.getDirection().equals("left")) {
				countLeft++;
			}
			if (event.getDirection().equals("right")) {
				countRight++;
			}
			if (event.getDirection().equals("up")) {
				countUp++;
			}
			if (event.getDirection().equals("down")) {
				countDown++;
			}
			if (event.isClicked()) {
				clickX = event.getClickX();
				clickY = event.getClickY();
			}
		}

		//calculate acceleration vector
		int horizontalDistance = countRight - countLeft;
		int verticalDistance = countUp - countDown;

		Point accelerationPoint = new Point(horizontalDistance, verticalDistance);

		double accelerationAngle = calculateAngle(ORIGIN, accelerationPoint);
		double accelerationMagnitude = calculateMagnitude(ORIGIN, accelerationPoint);

		Vector2D accelVector = new Vector2D(accelerationAngle, accelerationMagnitude);
		
		//calculate firing vector
		Vector2D fireVector = null;
		if (clickX != null && clickY != null) {
			fireVector = new Vector2D(clickX, clickY);
		}

		//set vectors
		setAcceleration(accelVector);
		setFiringVector(fireVector);
	}

	private static double calculateAngle(Point p1, Point p2) {
		double verticalDistance = p2.y - p1.y;
		double horizontalDistance = p2.x - p1.x;
		return Math.tan(verticalDistance/horizontalDistance);
	}

	private static double calculateMagnitude(Point p1, Point p2) {
		double verticalDistance = p2.y - p1.y;
		double horizontalDistance = p2.x - p1.x;
		return Math.sqrt(verticalDistance * verticalDistance + horizontalDistance * horizontalDistance);
	}

	public void addPlayerEvent(ClientInput event) {
        eventInbox.add(event);
	}


}
