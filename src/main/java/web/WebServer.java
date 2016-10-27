package main.java.web;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

@ServerEndpoint("/socket") 
public class WebServer {
	/** The sessions of all players, mapped to each player's chosen name. */
	private static final Map<Session, String> sessions = Collections.synchronizedMap(new HashMap<Session, String>());
	
	/** When a new client makes a connection to the server. */
	@OnOpen
	public void onOpen(Session session) {
		System.out.println(session.getId() + " has opened a connection.");
		try {
			session.getBasicRemote().sendText("Connection established.");
			sessions.put(session, null);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	 
	/** When a client sends a message to the server. */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("Message from " + session.getId() + ": " + message);
		
		Gson g = new Gson();
		ClientInput input = g.fromJson(message, ClientInput.class);
		if (input.getName() != null && input.getName() != "") {
			synchronized(sessions) {
				sessions.put(session, input.getName());
			}
		}
		
		if (input.getMessage() != null && !input.getMessage().isEmpty()) {
			broadcast(input.getMessage(), session);
		}
		
		if (input.getDirection() != null && !input.getDirection().isEmpty()) {
			broadcast("moved " + input.getDirection(), session);
		}
		
		if (input.isClicked()) {
			broadcast("clicked on " + input.getPoint() + " at " +
				input.getClickAngle() + "\u00b0", session);
		}
	}
	
	private void broadcast(String message) {
		broadcast(message, null);
	}

	/** Broadcast text to all connected clients. */
	private void broadcast(String message, Session sourceSession) {
		String sourceName = sessions.get(sourceSession);
		if (sourceName != null && !sourceName.isEmpty()) {
			message = sourceName + ": " + message;
		}
		for (Session s: sessions.keySet()) {
			if (s.isOpen()) {
				try {
					s.getBasicRemote().sendText(message);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
 
	/** When a client closes their connection. */
	@OnClose
	public void onClose(Session session) {
		System.out.println("Session " + session.getId() + " has ended.");
		sessions.remove(session);
	}
}