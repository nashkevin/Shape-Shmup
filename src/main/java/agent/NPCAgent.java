package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;

import java.awt.geom.Point2D;

import java.lang.Math;


public abstract class NPCAgent extends Agent {

	private Agent target;
	/** NPCAgent will notice PlayerAgents within this range */
	private double aggroRange;
	/** NPCAgent will try to maintain this distance from its target */
	private double DESIRED_SPACING = 150 + Math.random() * 100;
	/** NPCAgent's speed will not exceed its haste times this multiple */
	private int MAX_SPEED_MULTIPLE = 10;

	public NPCAgent(
		Environment environment, Point2D.Double position, ProjectileFactory gun,
		double size, int health, double haste, double aggroRange
	) {
		super(environment, position, gun, Agent.Team.ENEMY, size, health,
			haste + Math.random() / 100);
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

	public void awardPoints(int pointsAwarded) {
		// do nothing
	}

	@Override
	public void update() {
		// if targetting no one or target moves out of range
		if (target == null || target.getHealth() < 1 ||
			getPosition().distance(target.getPosition()) > getAggroRange()) {
			target = findNewTarget();
		}
		// if finding new target was successful
		if (target != null) {
			setAngle(getAngleTo(target.getPosition()));
			approachPoint(target.getPosition());
			getGun().fireProjectile();
		}
	}

	@Override
	public final void despawn() {
		getEnvironment().despawnNPCAgent(this);
	}

	protected void approachPoint(Point2D.Double p) {
		double angleToPoint = getAngleTo(p);

		// if the NPCAgent is too far away from its target
		if (getPosition().distance(p) > DESIRED_SPACING) {
			double x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
			double y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());

			x += getHaste() * Math.cos(angleToPoint);
			y += getHaste() * Math.sin(-angleToPoint);

			getVelocity().setAngle(Math.atan2(y, x));

			getVelocity().setMagnitude(Math.sqrt(x * x + y * y));
			if (getVelocity().getMagnitude() >= getHaste() * MAX_SPEED_MULTIPLE) {
				getVelocity().setMagnitude(getHaste() * MAX_SPEED_MULTIPLE);
			}

			x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
			y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());
			
			x += getPosition().x;
			y += getPosition().y;

			setPosition(x, y);
		} else {
			getVelocity().setMagnitude(getVelocity().getMagnitude() - getHaste() / 3.0);

			if (getVelocity().getMagnitude() > 0.0) {
				double x = getVelocity().getMagnitude() * Math.cos(getVelocity().getAngle());
				double y = getVelocity().getMagnitude() * Math.sin(getVelocity().getAngle());

				x += getPosition().getX();
				y += getPosition().getY();

				setPosition(x, y);
			} else {
				getVelocity().setMagnitude(0.0);
			}
		}
	}

	protected Agent findNewTarget() {
		return getEnvironment().getNearestPlayer(this, getAggroRange());
	}
}
