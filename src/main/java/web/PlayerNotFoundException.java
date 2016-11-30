package main.java.web;

public class PlayerNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public PlayerNotFoundException(String message) {
		super(message);
	}
	
	public PlayerNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
