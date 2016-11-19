package main.java.agent;

import main.java.environment.Environment;

import java.awt.Point;

import main.java.web.ClientInput;

import java.util.Queue;
import java.util.LinkedList;


public class PlayerAgent extends Agent {

	private String name = "An Unnamed Hero";
	private int level = 1;
	private int points = 0;

	Queue<ClientInput> eventInbox; 

	public PlayerAgent(
		Environment environment, Point position, Team team, String name
	) {
		super(environment, position, team);

		this.name = name;
		this.eventInbox = new LinkedList<ClientInput>();


		setSize(10);
		setMaxHealth(100);
		setHealth(getMaxHealth());
		setMovementSpeed(10);
		setProjectileDamage(1);
		setProjectileSpeed(20);
		setProjectileSpread(Math.toRadians(5));
		setFiringDelay(500);
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
