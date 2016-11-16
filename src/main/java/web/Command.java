package main.java.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.Session;

/** Manages commands that a player can perform by typing in the chat window. */
public enum Command {
	// Pro tip: the order in which the commands are defined
	// is the order they will appear when using "/commands".
	HELP("help",
			null,
			"Syntax: /help [command]"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length == 1) {
					// Display generic help info.
					server.unicast("/commands for a list of commands" +
							"<br>/help [command] for description and syntax", session);
				} else if (args.length == 2) {
					// Display help for the specified command.
					Command command = commands.get(args[1]);
					if (command == null) {
						server.unicast("No documentation found for that command.", session);
					} else {
						server.unicast(command.getHelpText(), session);
					}
				} else {
					throw new IllegalArgumentException();
				}
			}
		},
	COMMANDS("commands",
			null,
			"Lists all available commands.<br>Syntax: /commands"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length > 1) {
					throw new IllegalArgumentException();
				}
				
				StringBuilder sb = new StringBuilder();
				boolean firstInList = true;
				for (Command command : Command.values()) {
					// Put a comma before every command except the first.
					if (firstInList) {
						firstInList = false;
					} else {
						sb.append(", ");
					}
					
					// Prepend each command with a slash.
					sb.append("/").append(command.getCommand());
					
				}
				server.unicast(sb.toString(), session);
			}
		},
	EXIT("exit",
			new String[] {"quit"},
			"Disconnects you from the server.<br>Syntax: /exit"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length > 1) {
					throw new IllegalArgumentException();
				}
				String user = server.getNameBySession(session);
				String reasonPhrase = "User '" + user + "' closed session via command.";
				CloseReason.CloseCode code = CloseReason.CloseCodes.NORMAL_CLOSURE;
				try {
					session.close(new CloseReason(code, reasonPhrase));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		},
	KICK("kick",
			null,
			"Disconnects another user from the server.<br>Syntax: /kick (username)"
		) {
			@Override
			protected void perform(String[] args, Session sourceSession, WebServer server) {
				if (args.length != 2) {
					throw new IllegalArgumentException();
				}
				
				String targetName = args[1];
				Session targetSession = server.getSessionByUser(targetName);
				if (sourceSession.equals(targetSession)) {
					server.unicast("You can't kick yourself. Use /exit instead.", sourceSession);
				} else if (targetSession != null) {
					String reasonPhrase = "User '" + targetName + "' was kicked.";
					System.out.println(reasonPhrase);
					CloseReason.CloseCode code = CloseReason.CloseCodes.NORMAL_CLOSURE;
					try {
						targetSession.close(new CloseReason(code, reasonPhrase));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					server.broadcast(reasonPhrase, sourceSession);
				}
				else
					server.unicast("User '" + targetName + "' not found.", sourceSession);
			}
		},
	PLAYERS("players",
			null,
			"Lists the usernames of everyone playing.<br>Syntax: /players"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length > 1) {
					throw new IllegalArgumentException();
				}
				StringBuilder sb = new StringBuilder();
				sb.append(server.getNames().size());
				sb.append(" players: ");
				sb.append(String.join(", ", server.getNames()));
				server.unicast(sb.toString(), session);
			}
		},
	PING("ping",
			null,
			"Asks for a response from the server for timing.<br>Syntax: /ping"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length > 1) {
					throw new IllegalArgumentException();
				}
				server.unicast("PONG", session);
			}
		},
	PM("pm",
			new String[] {"tell"},
			"Sends a private message to a player.<br>Syntax: /pm (username) (message)"
		) {
			@Override
			protected void perform(String[] args, Session sourceSession, WebServer server) {
				if (args.length <= 2) {
					throw new IllegalArgumentException();
				}
				String sourceUser = server.getNameBySession(sourceSession);
				String destinationUser = args[1];
				Session destinationSession = server.getSessionByUser(destinationUser);
				if (sourceSession.equals(destinationSession)) {
					server.unicast("You can't private message yourself.", sourceSession);
				} else if (destinationSession != null) {
					StringBuilder msg = new StringBuilder();
					msg.append("From " + sourceUser + ":");
					for (int i = 2; i < args.length; i++)
						msg.append(" " + args[i]);
					server.unicast(msg.toString(), destinationSession);
				}
				else {
					server.unicast("User '" + destinationUser + "' not found.", sourceSession);
				}
			}
		},
	SAY("say",
			new String[] {"announce", "broadcast"},
			"Sends a message to all connected players. Useful for server announcements.<br>Syntax: /say (message)"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length < 2) {
					throw new IllegalArgumentException();
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("<b><i>");
					for (int i = 1; i < args.length; i++)
						sb.append(args[i] + " ");
					sb.append("</b></i>");
					server.broadcast(sb.toString());
				}
			}
		};
	
	
	private String command;
	private String[] aliases;
	private String helpText;
	/**
	 * @param command the primary way to call the command.
	 * @param aliases alternative ways to call the command (can be null).
	 * @param helpText a description of how to use the command. Aliases are automatically appended.
	 */
	private Command(String command, String[] aliases, String helpText) {
		this.command = command;
		this.aliases = aliases;
		this.helpText = helpText;
	}
	
	/** Returns alternate ways to call the command. Can be null. */
	public String[] getAliases() {
		return aliases;
	}
	
	/** Returns the primary way to call the command. */
	public String getCommand() {
		return command;
	}
	
	/** Returns the description of the command, including any aliases. */
	public String getHelpText() {
		if (aliases != null) {
			// If there are aliases, append all command names to the description.
			StringBuilder sb = new StringBuilder();
			sb.append(helpText);
			sb.append("<br>Aliases: ");
			sb.append("/").append(command);
			for (String alias : aliases) {
				sb.append(", /").append(alias);
			}
			return sb.toString();
		} else {
			return helpText;
		}
	}
	
	/** Performs the command. Each command must implement this function. */
	protected abstract void perform(String[] args, Session session, WebServer server);
	
	
	
	/** Map of names + aliases to commands. */
	private static Map<String, Command> commands = new HashMap<>();
	
	static {
		// Populate the commands map.
		for (Command command : Command.values()) {
			commands.put(command.getCommand(), command);
			if (command.getAliases() != null) {
				for (String alias : command.getAliases()) {
					commands.put(alias, command);
				}
			}
		}
	}
	
	public static void handleInput(String input, Session session, WebServer server) {
		// split commands into arguments
		String[] args = input.substring(1).trim().split("\\s++");
		
		// echo the command to the client's chatbox.
		String selfText = "<strong>" + server.getNameBySession(session) + "</strong>: " + input;
		server.unicast(selfText, session);

		parseCommand(args, session, server);
	}
	
	private static void parseCommand(String[] args, Session session, WebServer server) {
		Command command = commands.get(args[0].toLowerCase());
		if (command != null) {
			try {
				command.perform(args, session, server);
			} catch (IllegalArgumentException e) {
				// Incorrect parameters for command
				server.unicast(command.getHelpText(), session);
			}
		} else {
			// Command not found
			server.unicast("Unrecognized command.", session);
		}
	}
}
