package agent;

import java.awt.Point;
import java.util.Vector;

/**
 * @ Zach Janice
 */

public abstract class NPCAgent extends Agent {
	private Point spawnPoint;
	private Agent target;
	
	public MPCAgent(Point spawnPoint, int level, int team, int health, int damage, int projectileSpeed, int baseMovementSpeed) {
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
	
	public abstract getExperienceValue();
	
	protected final preUpdateCall() {
		// TODO
	{
	
	protected abstract determineTarget();
	protected abstract determineMove();
	protected abstract determineProjectileFire();
}
