package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;

import java.awt.geom.Point2D;

import java.lang.Math;


public class Pulsar extends NPCAgent {

	ProjectileFactory[] guns = new ProjectileFactory[8];

	public Pulsar(Environment environment, Point2D.Double position, int level) {
		super(
			environment,
			position,
			new ProjectileFactory(environment, null, level, 3,
				0, 1, 1 + level * 0.01),
			1 + level * 0.01,
			100 * level,
			0.075,
			750
		);

		getGun().setOwner(this);
		for (int i = 0; i < guns.length; i++) {
			guns[i] = new ProjectileFactory(environment, this, getGun().getDamage(),
				getGun().getSpeed(), getGun().getSpread(),
				getGun().getFiringDelay(), getGun().getSize());
			guns[i].fireProjectile();
		}
	}

	@Override
	public void update() {
		setAngle(getAngle() + 0.005);
		// if targetting no one or target moves out of range
		if (getTarget() == null || getTarget().getHealth() < 1 ||
			getPosition().distance(getTarget().getPosition()) > getAggroRange()) {
			setTarget(findNewTarget());
		}
		// if finding new target was successful
		if (getTarget() != null) {
			approachPoint(getTarget().getPosition());
		}
		double firingAngle = getAngle();
		for (ProjectileFactory gun : guns) {
			gun.fireProjectile(firingAngle);
			firingAngle += Math.PI / 4;
		}
	}
}
