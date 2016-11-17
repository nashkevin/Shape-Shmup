package main.java.agent;
import main.java.environment.Environment;

import java.awt.Point;
import java.util.UUID;

public final class TestEnemyAgent extends NPCAgent {
	public TestEnemyAgent(UUID id, Environment env, Point spawnPoint, int level) {
		super(
			id,
			env,
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
