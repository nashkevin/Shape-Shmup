package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;
import main.java.web.ClientInput;

import java.awt.Point;

import java.util.Queue;
import java.util.LinkedList;


public class PlayerAgent extends Agent {

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
			10
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
		;
	}
}
