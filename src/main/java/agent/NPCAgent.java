package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;

import java.awt.Point;


public abstract class NPCAgent extends Agent {

	private Agent target;
	private double aggroRange;

	public NPCAgent(
		Environment environment, Point position, ProjectileFactory gun,
		double size, int health, int speed, double aggroRange
	) {
		super(environment, position, gun, Agent.Team.ENEMY, size, health, speed);
		this.aggroRange = aggroRange;
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
			setRotation(getAngleTo(target));
			// To-do: move closer to target, if necessary
			getGun().fireProjectile();
		}
	}

	@Override
	public final void despawn() {
		getEnvironment().despawnNPCAgent(this);
	}

	private Agent findNewTarget() {
		return getEnvironment().getNearestPlayer(this, getAggroRange());
	}
}
