package main.java.misc;


public final class Vector2D {
	
	private double magnitude;
	private double angle;

	public Vector2D(Vector2D original) {
		this(original.magnitude, original.angle);
	}
	
	public Vector2D(double angle) {
		this(1.0, angle);
	}

	public Vector2D(double magnitude, double angle) {
		this.magnitude = magnitude;
		this.angle = angle;
	}
	
	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}
	
	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public static Vector2D normalize(Vector2D input) {
		//TODO, return a vector of the same angle as input but of unit length
		return null;
	}
}
