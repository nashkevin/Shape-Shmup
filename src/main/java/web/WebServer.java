package main.java.web;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
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
	private static final Map<Session, String> sessions = Collections.synchronizedMap(new HashMap<Session, String>());
	private Environment environment = new Environment(RADIUS);
	
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
		
		// Add new player.
		if (input.getName() != null && input.getName() != "") {
			synchronized(sessions) {
				sessions.put(session, input.getName());
				environment.spawnPlayer(input.getName(), session.getId());
			}
		}
		
		// Send chat message
		if (input.getMessage() != null && !input.getMessage().isEmpty()) {
			if (input.getMessage().charAt(0) == '/') {
				String[] args =
					input.getMessage().substring(1).toLowerCase().trim().split("\\s++");
				String sourceName = sessions.get(session);
				String selfText = "<strong>" + sourceName + ":</strong> ";
				selfText += input.getMessage();
				unicast(selfText, session);
				parseCommand(args, session);
			}
			else
				broadcast(input.getMessage(), session);
		}
		
		// Send client's update to the relevant agent entity.
		PlayerAgent agent = environment.getActivePlayerAgents().get(session.getId());
		agent.addPlayerEvent(input);
	}

	/** Broadcast text to all connected clients. */
	private void broadcast(String message, Session sourceSession) {
		String sourceName = sessions.get(sourceSession);
		if (sourceName != null && !sourceName.isEmpty()) {
			message = "<strong>" + sourceName + ":</strong> " + message;
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

	/** Send text to a single client */
	private void unicast(String message, Session session) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void parseCommand(String[] args, Session session) {
		switch (args[0]) {
			case "help":
				if (args.length == 1)
					commandHelp(session);
				else if (args.length == 2)
					commandHelp(args[1], session);
				else
					commandHelp("help", session);
				break;
			case "commands":
				commandListCommands(session);
				break;
			case "exit":
				commandClose(session);
				break;
			case "quit":
				commandClose(session);
				break;
			default:
				unicast("Unrecognized command.", session);
		}
	}

	private void commandHelp(Session session) {
		String help = "/commands for a list of commands";
		help += "<br>/help (command) for description and syntax";
		unicast(help, session);
	}

	private void commandHelp(String command, Session session) {
		String msg = new String();
		switch (command) {
			case "help":
				msg = "Syntax: /help [command]";
				break;
			case "commands":
				msg = "Lists all available commands.<br>Syntax: /commands";
				break;
			case "exit":
				msg = "Disconnects you from the server.<br>Syntax: /exit";
				msg += "<br>Aliases: /quit";
				break;
			case "quit":
				msg = "Disconnects you from the server.<br>Syntax: /quit";
				msg += "<br>Aliases: /exit";
				break;
			default:
				msg = "Unrecognized command.";
		}
		unicast(msg, session);
	}

	private void commandListCommands(Session session) {
		String commands = "/help, /exit";
		unicast(commands, session);
	}	

	private void commandClose(Session session) {
		String reasonPhrase = "user closed session via command";
		CloseReason.CloseCode code = CloseReason.CloseCodes.NORMAL_CLOSURE;
		try {
			session.close(new CloseReason(code, reasonPhrase));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
 
	/** When a client closes their connection. */
	@OnClose
	public void onClose(Session session) {
		System.out.println("Session " + session.getId() + " has ended.");
		sessions.remove(session);
	}
}
