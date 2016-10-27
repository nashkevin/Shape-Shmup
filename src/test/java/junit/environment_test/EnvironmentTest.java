package test.java;

import junit.framework.Assert;
import org.junit.Test;

public class EnvironmentTest {
	private static final double ERROR_MARGIN = 0.0001;
	@Test
	public void checkRadius() {
		
		double radius = 3.0;
		Assert.assertEquals(3.0, radius, ERROR_MARGIN); // (expected, actual, error) EXAMPLE FOR JOEL

		//TODO	
	}
	@Test
	public void testPolarToCartesian() {
		//TODO
		Assert.fail();
	}
}
