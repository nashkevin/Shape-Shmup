package web;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/socket") 
public class WebServer {
	/** The sessions of all players */
	private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	
	/** When a new client makes a connection to the server. */
	@OnOpen
	public void onOpen(Session session) {
		System.out.println(session.getId() + " has opened a connection.");
		try {
			session.getBasicRemote().sendText("Connection established.");
			sessions.add(session);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	 
	/** When the client sends a message to the server. */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("Message from " + session.getId() + ": " + message);
		broadcast(message);
	}

	/** Broadcast text to all connected clients. */
	private void broadcast(String message) {
		synchronized(sessions) {
			for (Session s: sessions) {
				if (s.isOpen()) {
					try {
						s.getBasicRemote().sendText(message);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
 
	/** When the client closes their connection. */
	@OnClose
	public void onClose(Session session) {
		System.out.println("Session " + session.getId() + " has ended.");
		sessions.remove(session);
	}
}