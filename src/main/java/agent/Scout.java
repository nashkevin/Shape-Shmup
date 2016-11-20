package main.java.agent;

import main.java.environment.Environment;
import main.java.projectile.ProjectileFactory;

import java.awt.Point;
import java.lang.Math;


/*****************************************************************************
 * To-do:                                                                    *
 *  Nothing here...                                                          *
 *****************************************************************************/

public class Scout extends NPCAgent {

	public Scout(Environment environment, Point position, int level) {
		super(
			environment,
			position,
			new ProjectileFactory(environment, null, 1 + level / 3, 5,
				Math.toRadians(10), 1000, 0.5),
			1 + Math.log(level),
			1 + level / 2,
			5,
			100
		);
		this.getGun().setOwner(this);
	}
}
