package main.java.projectile;

import main.java.agent.Agent;
import java.awt.Point;
import main.java.misc.Vector2D;
import java.util.List;

/**
 * @ Zach Janice
 */

public final class TestProjectile extends Projectile {
	public TestProjectile(Environment env, Agent owner, Point position, Vector2D velocity) {
		super(env, owner, position, velocity);
	}
	
	protected final void onCollision(List<Agent> agents) {
		if (agents != null && !agents.isEmpty()) {
			Agent firstHit = agents.get(0);
			firstHit.adjustHealth(getOwner().getDamage());
		}
	}
}
