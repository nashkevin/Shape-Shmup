package main.java.agent;

import java.awt.Point;

/**
 * @ Zach Janice
 */

public abstract class NPCAgent extends Agent {
	private Point spawnPoint;
	private Agent target;
	
	public NPCAgent(Point spawnPoint, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
		super(spawnPoint, level, team, health, damage, projectileSpeed, baseMovementSpeed);
		
		this.spawnPoint = new Point(spawnPoint);
		this.target = null;
	}
	
	public final Agent getTarget() {
		return target;
	}
	
	public final Point getSpawnPoint() {
		return new Point(spawnPoint);
	}
	
	public abstract int getExperienceValue();
	
	@Override
	protected final void preUpdateCall() {
		determineTarget();
		determineMove();
		determineProjectileFire();
	}
	
	protected abstract void determineTarget();
	protected abstract void determineMove();
	protected abstract void determineProjectileFire();
}
