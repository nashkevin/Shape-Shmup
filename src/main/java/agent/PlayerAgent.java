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

	public PlayerAgent(UUID id, Environment env, Point position, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		super(id, env, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
        this.eventInbox = new LinkedList<ClientInput>();
	}

	public final void despawn() {
		getEnvironment().despawnPlayerAgent(this);
	}
	
	protected void preUpdateCall() {
		//TODO
	}
	
	public void addPlayerEvent(ClientInput event) {
        eventInbox.add(event);
	}


}
