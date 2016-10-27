package main.java.agent;

import java.awt.Point;
import java.util.UUID;
/**
 * @ Zach Janice
 */

public final class TestEnemyAgent extends NPCAgent {
	public TestEnemyAgent(UUID id, Point spawnPoint, int level) {
		super(
				id,
				spawnPoint,
				Math.min(level, 1),
				2,
				80 + (20 * level),
				10 * level,
				50,
				10
		);
	}
	
	@Override
	public final int getExperienceValue() {
		return getLevel() * 10;
	}
	
	@Override
	protected final void determineTarget() {
		return;
	}
	
	@Override
	protected final void determineMove() {
		return;
	}
	
	@Override
	protected final void determineProjectileFire() {
		return;
	}
}
