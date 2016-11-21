package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;

import java.awt.Point;


public abstract class NPCAgent extends Agent {

	private Agent target;
	/** NPCAgent will notice PlayerAgents within this range */
	private double aggroRange;
	/** NPCAgent will try to maintain this distance from its target */
	private double DESIRED_SPACING = 300;
	/** NPCAgent's speed will not exceed its haste times this multiple */
	private int MAX_SPEED_MULTIPLE = 5;


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
		// if targetting no one or target moves out of range
		if (target == null || getPosition().distance(target.getPosition()) > getAggroRange()) {
			target = findNewTarget();
		}
		// if finding new target was successful
		if (target != null) {
			approachPoint(target.getPosition());
			getGun().fireProjectile();
		}
	}

	@Override
	public final void despawn() {
		getEnvironment().despawnNPCAgent(this);
	}

	private void approachPoint(Point p) {
		double angleToPoint = getAngleTo(p);		

		// update to face the point
		setRotation(angleToPoint);

		// if the NPCAgent is too far away from its target
		if (getPosition().distance(p) > DESIRED_SPACING) {
			double x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
			double y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());

			x += getHaste() * Math.cos(angleToPoint);
			y += getHaste() * Math.sin(angleToPoint);

			getVelocity().setAngle(Math.atan2(y, x));

			getVelocity().setMagnitude(Math.sqrt(x * x + y * y));
			if (getVelocity().getMagnitude() >= getHaste() * MAX_SPEED_MULTIPLE) {
				getVelocity().setMagnitude(getHaste() * MAX_SPEED_MULTIPLE);
			}

			x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
			y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());
			
			getPosition().translate((int)(x + 0.5), (int)(y + 0.5));
		}
	}

	private Agent findNewTarget() {
		return getEnvironment().getNearestPlayer(this, getAggroRange());
	}
}
