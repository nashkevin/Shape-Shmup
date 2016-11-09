package main.java.agent.test;

import java.awt.Point;
import java.util.UUID;

import main.java.agent.Agent;
import main.java.environment.Environment;

public class AgentTestImp extends Agent {
	private boolean despawnFlag;
	private boolean preUpdateFlag;
	
	public AgentTestImp(UUID id, Environment env, Point position, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		super(id, env, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
		
		despawnFlag = false;
		preUpdateFlag = false;
	}
	
	public final boolean getDespawnFlag() {
		return despawnFlag;
	}
	
	public final boolean getPreUpdateFlag() {
		return preUpdateFlag;
	}
	
	public final void setDespawnFlag(boolean flag) {
		despawnFlag = flag;
	}
	
	public final void setPreUpdateFlag(boolean flag) {
		preUpdateFlag = flag;
	}
	
	@Override
	public final void despawn() {
		despawnFlag = true;
	}

	@Override
	protected final void preUpdateCall() {
		preUpdateFlag = true;
	}
}
