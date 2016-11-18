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
	private double aggroRange;

	public NPCAgent(Environment environment, Point position, EnemyType type, int level) {
		super(environment, position, Agent.Team.ENEMY);

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
				setAggroRange(50);
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
				setAggroRange(50);
				break;
		}
	}

	public final Agent getTarget() {
		return target;
	}

	public final void setTarget(Agent target) {
		this.target = target;
	}

	public final double getAggroRange() {
		return aggroRange;
	}

	public final void setAggroRange(double range) {
		this.aggroRange = range;
	}

	public final void findNewTarget() {
		target = getEnvironment().getNearestPlayer(this, getAggroRange());
	}
	
	@Override
	public final void despawn() {
		getEnvironment().despawnNPCAgent(this);
	}
}
