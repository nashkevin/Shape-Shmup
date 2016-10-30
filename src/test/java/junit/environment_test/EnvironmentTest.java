package test.java.junit.environment_test;
import main.java.environment.Environment;

import java.awt.Point;
import java.lang.Math;
import org.junit.Test;
import org.junit.Assert;

public class EnvironmentTest {
	private static final double ERROR_MARGIN = 0.001;
	@Test
	public void testCheckRadius() {
		Point p = new Point(0, 0);
		Assert.assertEquals(0.0, Environment.checkRadius(p), ERROR_MARGIN);
		p.setLocation(3, 4);
		Assert.assertEquals(5.0, Environment.checkRadius(p), ERROR_MARGIN);
		p.setLocation(6, 8);
		Assert.assertEquals(10.0, Environment.checkRadius(p), ERROR_MARGIN);
	}

	@Test
	public void testPolarToCartesian() {
		Assert.assertEquals(new Point(0, 0), Environment.polarToCartesian(0, 0));
		Assert.assertEquals(new Point(0, 1), Environment.polarToCartesian(Math.PI / 2, 1));
		Assert.assertEquals(new Point(-2, 0), Environment.polarToCartesian(Math.PI, 2));
		// TODO: fix the below line
		Assert.assertEquals(new Point(4, 3), Environment.polarToCartesian(36.87, 5));
	}

	@Test
	public void testCartesianToPolar() {
		Assert.assertArrayEquals(new double[] {0, 0}, Environment.cartesianToPolar(new Point(0, 0)), ERROR_MARGIN);
		Assert.assertArrayEquals(new double[] {Math.PI / 2, 1}, Environment.cartesianToPolar(new Point(0, 1)), ERROR_MARGIN);
		Assert.assertArrayEquals(new double[] {Math.PI, 2}, Environment.cartesianToPolar(new Point(-2, 0)), ERROR_MARGIN);
		// TODO: fix the below line
		Assert.assertArrayEquals(new double[] {36.87, 5}, Environment.cartesianToPolar(new Point(4, 3)), ERROR_MARGIN);
	}
}
