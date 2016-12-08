package test.java.junit.projectile_test;

import main.java.projectile.Projectile;
import main.java.environment.Environment;
import main.java.misc.Vector2D;

import java.awt.geom.Point2D;
import java.lang.Math;
import org.junit.Test;
import org.junit.Assert;

public class ProjectileTest {
	private static final double ERROR_MARGIN = 0.0001;

	/** Tests that getPosition() returns a defensive copy */
	@Test
	public void testGetPosition() {
		Environment environment = new Environment(false);
		Projectile projectile = new Projectile(environment, null, new Point2D.Double(0, 0), new Vector2D(0, 0), 1, 1);
		Point2D.Double point = projectile.getPosition();
		point.setLocation(1, 1); // moves the point in both x and y directions

		// Moving the point should not have moved the projectile
		Assert.assertNotEquals(point, projectile.getPosition());
	}
	
	/** Tests that getVelocity() returns a defensive copy */
	@Test
	public void testGetVelocity() {
		Environment environment = new Environment(false);
		Projectile projectile = new Projectile(environment, null, new Point2D.Double(0, 0), new Vector2D(0, 0), 1, 1);
		Vector2D vector = projectile.getVelocity();
		vector.setMagnitude(1); // change the magnitude of the vector

		// Changing the vector's magnitude should not have changed the projectile's velocity magnitude
		Assert.assertNotEquals(vector, projectile.getVelocity());
	}
	
	/** Tests that update() changes the position in the presence of a velocity, and does not otherwise **/
	@Test
	public void testUpdate() {
		Environment environment = new Environment(false);
		
		// Projectile starts with zero velocity, should not move
		Projectile projectile = new Projectile(environment, null, new Point2D.Double(0, 0), new Vector2D(0, 0), 1, 1);
		Point2D oldPosition = projectile.getPosition();
		projectile.update();
		Assert.assertEquals(oldPosition, projectile.getPosition());
		
		// Projectile should move for non-zero velocity
		projectile = new Projectile(environment, null, new Point2D.Double(0, 0), new Vector2D(1, 0), 1, 1);
		oldPosition = projectile.getPosition();
		projectile.update();
		Assert.assertNotEquals(oldPosition, projectile.getPosition());
	}
}
