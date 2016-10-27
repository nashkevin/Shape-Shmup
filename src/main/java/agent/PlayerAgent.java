package main.java.agent;

import java.awt.Point;
import main.java.misc.Vector2D;
import main.java.misc.PlayerEvent;
import java.util.Queue;
import java.util.LinkedList;

public class PlayerAgent extends Agent {

	private Vector2D velocity;
	Vector2D acceleration;
	private Vector2D firingVector;

	Queue<PlayerEvent> eventInbox; 

	public PlayerAgent(Point position, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		super(position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
        this.eventInbox = new LinkedList<PlayerEvent>();
	}

	protected void preUpdateCall() {
		//TODO
	}
	
	public void addPlayerEvent(PlayerEvent event) {
        eventInbox.add(event);
	}


}
