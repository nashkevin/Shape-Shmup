package java.misc;

import java.awt.Point;

/**
 * @ Zach Janice
 */

public final class Vector2D {
	private double magnitude;
	private double angle;
	
	public Vector2D(Vector2D original) {
		this(original.magnitude, original.angle);
	}
	
	public Vector2D(double magnitude, double angle) {
		this.magnitude = magnitude;
		this.angle = angle;
	}
	
	public double getMagnitude() {
		return magnitude;
	}
	
	public double getAngle() {
		return angle;
	}
}
