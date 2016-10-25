package agent;

import java.awt.Point;
import java.util.Vector;

/**
 * @ Zach Janice
 */

public final class TestProjectile extends Projectile {
	public TestProjectile(Agent owner, Point position, Vector velocity) {
		super(owner, position, velocity);
	}
	
	protected final onCollision(List<Agent> agents) {
		// TODO
	}
}
