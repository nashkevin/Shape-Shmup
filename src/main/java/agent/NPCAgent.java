package main.java.agent;

import main.java.environment.Environment;

import java.awt.Point;
import java.lang.Math;
import java.util.UUID;


public abstract class NPCAgent extends Agent {

	private PlayerAgent target;
	private EnemyType type;
	private double aggroRange;

	public NPCAgent(Environment environment, Point position, ) {
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
				setFiringDelay(1000);
				setAggroRange(100);
				break;
			case HEAVY:
				setSize(1 + 2 * Math.log(level));
				setMaxHealth(level);
				setHealth(getMaxHealth());
				setMovementSpeed(2);
				setProjectileDamage(10 + level / 3);
				setProjectileSpeed(7);
				setProjectileSpread(Math.toRadians(10));
				setFiringDelay(1500);
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

	@Override
	public void update() {
		if (target == null || getPosition().distance(target.getPosition()) > getAggroRange()) {
			target = findNewTarget();
		}
		if (target != null) {
			fireAtTarget
		}
	}

	@Override
	public final void despawn() {
		getEnvironment().despawnNPCAgent(this);
	}

	private PlayerAgent findNewTarget() {
		return getEnvironment().getNearestPlayer(this, getAggroRange());
	}

	private void fireAtTarget() {

	}
}
