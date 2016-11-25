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
			new ProjectileFactory(environment, null, 1 + level / 3, 5,
				Math.toRadians(10), 1000, 0.5),
			0.5 + level * 0.005,
			(int)(1 + level * 0.7),
			0.007 * level + 0.3,
			1000
		);

		this.getGun().setOwner(this);
	}
}
