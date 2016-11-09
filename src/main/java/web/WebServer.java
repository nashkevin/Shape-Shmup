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
		
		// handle client chat input
		if (input.getMessage() != null && !input.getMessage().isEmpty()) {
			// chat input is a command
			if (input.getMessage().charAt(0) == '/') {
				// split commands into arguments
				String[] args =
					input.getMessage().substring(1).toLowerCase().trim().split("\\s++");
				
				// echo the command to the client's chatbox
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
		for (Session s : sessions.keySet()) {
			if (s.isOpen()) {
				try {
					s.getBasicRemote().sendText(message);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public void broadcast(String message) {
		broadcast(message, null);
	}

	/** Send text to a single client */
	private void unicast(String message, Session session) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/** iterates over sessions and returns the session if usernames match */
	private Session findUser(String username) {
		for (Map.Entry<Session, String> entry : sessions.entrySet()) {
			if (entry.getValue().replaceAll("\\s+","").equalsIgnoreCase(username))
				return entry.getKey();
		}
		return null;
	}

	/** call the appropriate method for the given command */
	private void parseCommand(String[] args, Session session) {
		switch (args[0]) {
			case "help":
				if (args.length == 1)		// /help
					commandHelp(session);
				else if (args.length == 2)	// /help [command name]
					commandHelp(args[1], session);
				else						// command used incorrectly
					commandHelp(args[0], session);
				break;
			case "commands":
				if (args.length == 1)
					commandListCommands(session);
				else
					commandHelp(args[0], session);
				break;
			case "exit":
				if (args.length == 1)
					commandClose(session);
				else
					commandHelp(args[0], session);
				break;
			case "quit":
				if (args.length == 1)
					commandClose(session);
				else
					commandHelp(args[0], session);
				break;
			case "kick":
				if (args.length != 2)
					commandHelp(args[0], session);
				else
					commandKick(args[1], session);
				break;
			case "players":
				if (args.length == 1)
					commandListPlayers(session);
				else
					commandHelp(args[0], session);
				break;
			default:
				unicast("Unrecognized command.", session);
		}
	}

	/** method for the command to display generic help message */
	private void commandHelp(Session session) {
		String help = "/commands for a list of commands";
		help += "<br>/help [command] for description and syntax";
		unicast(help, session);
	}

	/** method for the command to display information about a command */
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
			case "kick":
				msg = "Disconnects another user from the server.";
				msg += "<br>Syntax: /kick (username)";
				break;
			case "players":
				msg = "Lists the usernames of everyone playing.";
				msg += "<br>Syntax: /players";
				break;
			default:
				msg = "No documentation found for that command.";
		}
		unicast(msg, session);
	}

	/** method for the command to list all available commands */
	private void commandListCommands(Session session) {
		String commands = "/exit, /help, /kick, /players";
		unicast(commands, session);
	}

	/** method for the command to list all players */
	private void commandListPlayers(Session session) {
		String players = "";
		System.out.println("sessions.size() = " + sessions.size());
		if (sessions.size() > 1) {
			for (Map.Entry<Session, String> entry : sessions.entrySet())
				players += entry.getValue() + ", ";
		} else {
			players = "You're the only one in here. How lonely!";
		}
		unicast(players, session);
	}	

	/** method for the command to close another user's session */
	private void commandKick(String username, Session sourceSession) {
		Session session = findUser(username);
		if (sessions.get(sourceSession).replaceAll("\\s+","").equalsIgnoreCase(username))
			unicast("You can't kick yourself. Use /exit instead.", sourceSession);
		else if (findUser(username) != null) {
			String reasonPhrase = "User '" + username + "' was kicked";
			System.out.println(reasonPhrase);
			CloseReason.CloseCode code = CloseReason.CloseCodes.NORMAL_CLOSURE;
			try {
				session.close(new CloseReason(code, reasonPhrase));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			broadcast(reasonPhrase, sourceSession);
		}
		else
			unicast("User '" + username + "' not found.", sourceSession);
	}

	/** method for the command to close the session */
	private void commandClose(Session session) {
		String reasonPhrase = "User closed session via command";
		System.out.println(reasonPhrase);
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
		
		if (sessions.size() == 0) {
			gameThread.setGameplayOccurring(false);
		}
	}
}
