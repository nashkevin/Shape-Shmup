package agent;

import java.awt.Point;
import java.util.Vector;

/**
 * @ Zach Janice
 */

public final class TestEnemyAgent extends NPCAgent {
	public TestEnemyAgent(Point spawnPoint, int level) {
		if (level < 1)
			level = 1;
		
		int team = 2;
		int health = 80 + (20 * level);
		int damage = 10 * level;
		int projectileSpeed = 50;
		int baseMovementSpeed = 10;
		
		super(spawnPoint, level, team, health, damage, projectileSpeed, baseMovementSpeed);
	}
	
	public final getExperienceValue() {
		return level * 10;
	}
	
	protected final determineTarget() {
		// TODO
	}
	
	protected final determineMove() {
		// TODO
	}
	
	protected final determineProjectileFire() {
		// TODO
	}
}
