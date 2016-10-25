package java.projectile;

import java.agent.Agent;
import java.awt.Point;
import java.misc.Vector2D;

public interface FIProjectileCreator {
	public Projectile createProjectile(Agent owner, Point position, Vector2D velocity);
}
