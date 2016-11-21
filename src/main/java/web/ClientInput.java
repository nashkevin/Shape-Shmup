package main.java.web;

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
	private boolean isFiring = false;	
	private Double angle;

	
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

	public boolean isFiring() {
		return isFiring;
	}

	public void setIsFiring(boolean isFiring) {
		this.isFiring = isFiring;
	}

	public boolean isMoving() {
		return left || right || up || down;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public Double getAngle() {
		return angle;
	}
}
