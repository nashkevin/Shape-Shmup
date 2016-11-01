package main.java.web;

import java.awt.Point;

/**
 * POJO representing information that each client can send to the server.
 */


public class ClientInput {
	
	private String name;
	private String message;
	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;
	private int clickX = -1;
	private int clickY = -1;
	private double clickAngle = 0;
	
	public ClientInput() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isMoving() {
		return left || right || up || down;
	}

	public boolean isClicked() {
		return clickX >= 0 && clickY >= 0;
	}

	public int getClickX() {
		return clickX;
	}

	public void setClickX(int clickX) {
		this.clickX = clickX;
	}

	public int getClickY() {
		return clickY;
	}

	public void setClickY(int clickY) {
		this.clickY = clickY;
	}
	
	public Point getPoint() {
		return new Point(this.clickX, this.clickY);
	}

	public void setClickAngle(double clickAngle) {
		this.clickAngle = clickAngle;
	}

	public double getClickAngle() {
		return clickAngle;
	}
}
