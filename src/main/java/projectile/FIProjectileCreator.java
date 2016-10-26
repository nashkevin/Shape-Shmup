package main.java.projectile;

import main.java.agent.Agent;
import java.awt.Point;
import main.java.misc.Vector2D;

public interface FIProjectileCreator {
	public Projectile createProjectile(Agent owner, Point position, Vector2D velocity);
}
