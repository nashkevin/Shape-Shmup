package web;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/socket") 
public class WebServer {
	/** The sessions of all players */
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
	 
	/** When the client sends a message to the server. */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("Message from " + session.getId() + ": " + message);
		broadcast(message);
	}

	/** Broadcast text to all connected clients. */
	private void broadcast(String message) {
		synchronized(sessions) {
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
	}
 
	/** When the client closes their connection. */
	@OnClose
	public void onClose(Session session) {
		System.out.println("Session " + session.getId() + " has ended.");
		sessions.remove(session);
	}
}