package main.java.projectile;

import main.java.agent.Agent;
import java.awt.Point;
import main.java.misc.Vector2D;
import java.util.HashMap;

public class ProjectileFactory {
	public static enum Type {
		NONE,
		TEST
	}
	
	@SuppressWarnings("serial")
	public static HashMap<Type, FIProjectileCreator> factory = new HashMap<Type, FIProjectileCreator>() {{
		put(Type.TEST, (owner, position, velocity) -> { return new TestProjectile(owner, position, velocity); });
	}};
	
	public static Projectile makeProjectile(Type type, Agent owner, Point position, Vector2D velocity) {
		FIProjectileCreator creator = factory.get(type);
		
		if (creator == null)
			return null;
		
		return creator.createProjectile(owner, position, velocity);
	}
}
