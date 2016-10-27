package main.java.web;

import java.awt.Point;

/**
 * POJO representing information that each client can send to the server.
 */
public class ClientInput {
	private String name;
	private String message;
	private String direction;
	private int clickX = -1;
	private int clickY = -1;
	
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

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
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
}
