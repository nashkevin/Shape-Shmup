package main.java.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import main.java.agent.PlayerAgent;
import main.java.environment.Environment;

@ServerEndpoint("/socket") 
public class WebServer {
	private static final int RADIUS = 200;
	
	/** The sessions of all players, mapped to each player's chosen name. */
	private static final Map<Session, String> sessionToName = Collections.synchronizedMap(new HashMap<>());
	/** The sessions of all players, mapped to each player agent. */
	private static final Map<Session, PlayerAgent> sessionToPlayerAgent = Collections.synchronizedMap(new HashMap<>());
	/** The chosen name of each named player, mapped to session. */
	private static final Map<String, Session> nameToSession = Collections.synchronizedMap(new HashMap<>());
	private static Environment environment = new Environment(RADIUS);
	private static GameThread gameThread;
	
	public WebServer() {
		gameThread = new GameThread(this, environment);
		gameThread.start();
	}
	
	/** When a new client makes a connection to the server. */
	@OnOpen
	public void onOpen(Session session) {
		System.out.println(session.getId() + " has opened a connection.");
		session.getAsyncRemote().sendText("Connection established.");
		synchronized(sessionToName) {
			sessionToName.put(session, null);
		}
	}
	 
	/** When a client sends a message to the server. */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("Message from " + session.getId() + ": " + message);
		
		Gson g = new Gson();
		ClientInput input = g.fromJson(message, ClientInput.class);
		
		// Add new player.
		if (input.getName() != null && input.getName() != "") {
			synchronized(sessionToName) {
				sessionToName.put(session, input.getName());
			}
			synchronized(nameToSession) {
				nameToSession.put(input.getName(), session);
			}
			PlayerAgent agent = environment.spawnPlayer();
			synchronized(sessionToPlayerAgent) {
				sessionToPlayerAgent.put(session, agent);
			}
		}
		
		// handle client chat input
		if (input.getMessage() != null && !input.getMessage().isEmpty()) {
			if (input.getMessage().charAt(0) == '/') {
				// chat input is a command
				Command.handleInput(input.getMessage(), session, this);
			} else {
				broadcast(input.getMessage(), session);
			}
		}
		
		// Send client's update to the relevant agent entity.
		PlayerAgent agent = sessionToPlayerAgent.get(session);
		agent.addPlayerEvent(input);
	}

	/** Broadcast text to all connected clients. */
	void broadcast(String message, Session sourceSession) {
		String sourceName = null;
		synchronized(sessionToName) {
			sourceName = sessionToName.get(sourceSession);
		}
		if (sourceName != null && !sourceName.isEmpty()) {
			message = "<strong>" + sourceName + "</strong>: " + message;
		}
		for (Session s : sessionToName.keySet()) {
			if (s.isOpen()) {
				s.getAsyncRemote().sendText(message);
			}
		}
	}
	
	void broadcast(String message) {
		broadcast(message, null);
	}

	/** Send text to a single client */
	void unicast(String message, Session session) {
		session.getAsyncRemote().sendText(message);
	}

	Session getSessionByUser(String username) {
		return nameToSession.get(username);
	}
	
	String getNameBySession(Session session) {
		return sessionToName.get(session);
	}
	
	Set<String> getNames() {
		return nameToSession.keySet();
	}
 
	/** When a client closes their connection. */
	@OnClose
	public void onClose(Session session) {
		System.out.println("Session " + session.getId() + " has ended.");
		sessionToPlayerAgent.remove(session);
		String name = sessionToName.remove(session);
		if (name != null) {
			nameToSession.remove(name);
		}
		
		synchronized(sessionToName) {
			if (sessionToName.size() == 0) {
				gameThread.setGameplayOccurring(false);
			}
		}
	}
}
