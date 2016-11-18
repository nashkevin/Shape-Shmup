package main.java.agent;

import main.java.environment.Environment;

import java.awt.Point;
import java.lang.Math;
import java.util.UUID;


public class NPCAgent extends Agent {
	
	public static enum EnemyType {
		SCOUT, HEAVY
	}

	private PlayerAgent target;
	private EnemyType type;

	public NPCAgent(
		Environment environment, Point position,
		Agent.Team team, EnemyType type, int level
	) {
		super(environment, position, team);

		this.type = type;

		switch (type) {
			case SCOUT:
				setSize(1 + Math.log(level));
				setMaxHealth((1 + level) / 2);
				setHealth(getMaxHealth());
				setMovementSpeed(5);
				setProjectileDamage(1 + level / 3);
				setProjectileSpeed(10);
				setProjectileSpread(Math.toRadians(10));
				setFireRate(1.0);
				break;
			case HEAVY:
				setSize(1 + 2 * Math.log(level));
				setMaxHealth(level);
				setHealth(getMaxHealth());
				setMovementSpeed(2);
				setProjectileDamage(10 + level / 3);
				setProjectileSpeed(7);
				setProjectileSpread(Math.toRadians(10));
				setFireRate(0.75);
				break;
		}
	}
	
	public final Agent getTarget() {
		return target;
	}
	
	@Override
	public final void despawn() {
		getEnvironment().despawnNPCAgent(this);
	}
}
