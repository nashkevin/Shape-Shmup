package agent;

import java.awt.Point;
import java.util.Vector;

/**
 * @ Zach Janice
 */

public abstract class Projectile {
	private Agent owner;
	private Point position;
	private Vector velocity;
	
	public Projectile(Agent owner, Point position, Vector velocity) {
		this.owner = owner;
		this.position = position;
		this.velocity = velocity;
	}
	
	public final void update() {
		// TODO
	}
	
	public final void despawn() {
		// TODO
	}
	
	protected abstract onCollision(List<Agent> agents);
}
