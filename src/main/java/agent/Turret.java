package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;

import java.awt.geom.Point2D;

import java.lang.Math;


public class Turret extends NPCAgent {

	ProjectileFactory rearGun;
	ProjectileFactory leftGun;
	ProjectileFactory rightGun;

	public Turret(Environment environment, Point2D.Double position, int level) {
		super(
			environment,
			position,
			new ProjectileFactory(environment, null, level, 3,
				0, 250, 0.5),
			1 + level * 0.01,
			100 * level,
			0.075,
			750
		);

		this.getGun().setOwner(this);
		rearGun = new ProjectileFactory(environment, this,
			level, 3, 0, 250, 0.5);
		leftGun = new ProjectileFactory(environment, this,
			level, 3, 0, 250, 0.5);
		rightGun = new ProjectileFactory(environment, this,
			level, 3, 0, 250, 0.5);
	}

	@Override
	public void update() {
		setAngle(getAngle() + 0.02);
		// if targetting no one or target moves out of range
		if (getTarget() == null || getTarget().getHealth() < 1 ||
			getPosition().distance(getTarget().getPosition()) > getAggroRange()) {
			setTarget(findNewTarget());
		}
		// if finding new target was successful
		if (getTarget() != null) {
			approachPoint(getTarget().getPosition());
		}
		getGun().fireProjectile();
		rearGun.fireProjectile(getAngle() + Math.PI);
		leftGun.fireProjectile(getAngle() - Math.PI / 2);
		rightGun.fireProjectile(getAngle() + Math.PI / 2);
	}
}
