package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;

import java.awt.geom.Point2D;

import java.lang.Math;


public class Scout extends NPCAgent {

	public Scout(Environment environment, Point2D.Double position, int level) {
		super(
			environment,
			position,
			new ProjectileFactory(environment, null, 10 + (int) Math.round(level * 0.2),
				5 + level * 0.08, Math.toRadians(10), 1000 - level * 5, 0.5),
			0.5 + level * 0.005,
			(int)(30 + level * 0.7),
			0.3 + level * 0.007,
			1000
		);

		getGun().setOwner(this);
		getGun().setSize(getSize());
	}
}
