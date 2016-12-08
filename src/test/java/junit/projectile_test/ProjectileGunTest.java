package test.java.junit.projectile_test;

import main.java.projectile.ProjectileFactory;
import main.java.environment.Environment;

import java.awt.geom.Point2D;
import java.lang.Math;
import org.junit.Test;
import org.junit.Assert;

public class ProjectileGunTest {
	private static final double ERROR_MARGIN = 0.0001;
	
	/** Tests that setFiringDelay() correctly sets the new firing delay. **/
	@Test
	public void testSetFiringDelay() {
		Environment environment = new Environment(false);
		ProjectileFactory factory = new ProjectileFactory(environment, null, 1, 1, 1, 1000, 1);
		
		// Assert the original firing delay
		Assert.assertEquals(1000, factory.getFiringDelay());
		
		// Set and assert the new firing delay
		factory.setFiringDelay(500);
		Assert.assertEquals(500, factory.getFiringDelay());
	}
}
