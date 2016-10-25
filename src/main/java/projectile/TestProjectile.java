package java.projectile;

import java.agent.Agent;
import java.awt.Point;
import java.misc.Vector2D;
import java.util.List;

/**
 * @ Zach Janice
 */

public final class TestProjectile extends Projectile {
	public TestProjectile(Agent owner, Point position, Vector2D velocity) {
		super(owner, position, velocity);
	}
	
	protected final void onCollision(List<Agent> agents) {
		if (agents != null && !agents.isEmpty()) {
			Agent firstHit = agents.get(0);
			firstHit.adjustHealth(getOwner().getDamage());
		}
	}
}
