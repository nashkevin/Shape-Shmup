package main.java.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import java.awt.Point;

import main.java.agent.PlayerAgent;


/** Manages commands that a player can perform by typing in the chat window. */
public enum Command {
	// Pro tip: the order in which the commands are defined
	// is the order they will appear when using "/commands".
	HELP ("help",
			new String[] {"?"},
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
	COMMANDS ("commands",
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
	BRING ("bring",
			new String[] {"fetch", "summon"},
			("Brings a player to you or to another player or to a location." +
				"<br>Syntax: /bring username [username|x y]")
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				PlayerAgent caller = server.getPlayerAgentBySession(session);

				if (args.length == 2) {
					PlayerAgent targetPlayer = server.getPlayerAgentByName(args[1]);
					if (targetPlayer == null) {
						server.unicast("Player '" + args[1] + "' not found.", session);
					} else {
						targetPlayer.setPosition(caller.getPosition());
					}
				}
				else if (args.length == 3) {
					PlayerAgent playerFrom = server.getPlayerAgentByName(args[1]);
					PlayerAgent playerTo = server.getPlayerAgentByName(args[2]);
					if (playerFrom == null) {
						server.unicast("Player '" + args[1] + "' not found.", session);
					}
					else if (playerTo == null) {
						server.unicast("Player '" + args[2] + "' not found.", session);
					}
					else {
						playerFrom.setPosition(playerTo.getPosition());
					}
				}
				else if (args.length == 4) {
					PlayerAgent targetPlayer = server.getPlayerAgentByName(args[1]);
					if (targetPlayer == null) {
						server.unicast("Player '" + args[1] + "' not found.", session);
					} else {
						try {
							int x = Integer.parseInt(args[2]);
							int y = Integer.parseInt(args[3]);
							targetPlayer.setPosition(x, y);
						}
						catch (NumberFormatException ex) {
							server.unicast("Invalid coordinates", session);
						}
					}

				}
			}
		},
	CLEAR ("clear",
			null,
			"Clears your chat window.<br>Syntax: /clear"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length > 1) {
					throw new IllegalArgumentException();
				}
			}
		},
	EXIT ("exit",
			new String[] {"quit"},
			"Disconnects you from the server.<br>Syntax: /exit"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length > 1) {
					throw new IllegalArgumentException();
				}
				String user = server.getNameBySession(session);
				String reasonPhrase = "Player '" + user + "' closed session via command.";
				CloseReason.CloseCode code = CloseReason.CloseCodes.NORMAL_CLOSURE;
				try {
					session.close(new CloseReason(code, reasonPhrase));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		},
	GOTO ("goto",
			new String[] {"tp"},
			"Sends you to a location.<br>Syntax: /goto [player|x y]"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				PlayerAgent caller = server.getPlayerAgentBySession(session);

				if (args.length == 3) {
					try {
						int x = Integer.parseInt(args[1]);
						int y = Integer.parseInt(args[2]);
						caller.setPosition(x, y);
					}
					catch (NumberFormatException ex) {
						server.unicast("Invalid coordinates", session);
					}
				}
				else if (args.length == 2) {
					PlayerAgent targetPlayer = server.getPlayerAgentByName(args[1]);
					if (targetPlayer != null) {
						caller.setPosition(targetPlayer.getPosition());
					} else {
						server.unicast("Player '" + args[1] + "' not found.", session);
					}
				}
				else {
					throw new IllegalArgumentException();
				}
			}
		},
	KICK ("kick",
			null,
			"Disconnects another user from the server.<br>Syntax: /kick username"
		) {
			@Override
			protected void perform(String[] args, Session sourceSession, WebServer server) {
				if (args.length != 2) {
					throw new IllegalArgumentException();
				}

				String targetName = args[1];
				String sourceName = server.getNameBySession(sourceSession);
				Session targetSession = server.getSessionByName(targetName);
				if (sourceSession.equals(targetSession)) {
					server.unicast("You can't kick yourself. Use /exit instead.", sourceSession);
				} else if (targetSession != null) {
					String reasonPhrase = targetName + " was kicked by " + sourceName + ".";
					System.out.println(reasonPhrase);
					CloseReason.CloseCode code = CloseReason.CloseCodes.NORMAL_CLOSURE;
					try {
						targetSession.close(new CloseReason(code, reasonPhrase));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					server.broadcast(reasonPhrase);
				}
				else
					server.unicast("Player '" + targetName + "' not found.", sourceSession);
			}
		},
	PLAYERS ("players",
			new String[] {"who"},
			"Lists the usernames of everyone playing.<br>Syntax: /players"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length > 1) {
					throw new IllegalArgumentException();
				}
				Set<String> players = server.getNames();
				StringBuilder sb = new StringBuilder();
				sb.append(players.size());
				if (players.size() == 1) {
					sb.append(" player: ");
				} else {
					sb.append(" players: ");
				}
				sb.append(String.join(", ", server.getNames()));
				server.unicast(sb.toString(), session);
			}
		},
	PING ("ping",
			null,
			"Requests a timed response from the server.<br>Syntax: /ping"
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				if (args.length > 1) {
					throw new IllegalArgumentException();
				}
				server.unicast("PONG", session);
			}
		},
	PM ("pm",
			new String[] {"tell"},
			"Sends a private message to a player.<br>Syntax: /pm username message"
		) {
			@Override
			protected void perform(String[] args, Session sourceSession, WebServer server) {
				if (args.length <= 2) {
					throw new IllegalArgumentException();
				}

				String sourceName = server.getNameBySession(sourceSession);
				String destinationName = args[1];
				Session destinationSession = server.getSessionByName(destinationName);

				if (sourceSession.equals(destinationSession)) {
					server.unicast("You can't private message yourself.", sourceSession);
				}
				else if (destinationSession != null) {
					StringBuilder msg = new StringBuilder();
					StringBuilder echo = new StringBuilder();
					msg.append("<i>From " + sourceName + ":");
					echo.append("<i>To " + destinationName + ":");
					for (int i = 2; i < args.length; i++) {
						msg.append(" ").append(args[i]);
						echo.append(" ").append(args[i]);
					}
					msg.append("</i>");
					echo.append("</i>");
					server.unicast(msg.toString(), destinationSession);
					server.unicast(echo.toString(), sourceSession);
				}
				else {
					server.unicast("Player '" + destinationName + "' not found.", sourceSession);
				}
			}
		},
	SAY ("say",
			new String[] {"announce", "broadcast"},
			("Sends a message to all connected players. " +
				"Useful for server announcements.<br>Syntax: /say message")
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
		},
	WHERE ("where",
			new String[] {"locate"} ,
			("Gives your location or the location of another player. " +
				"<br>Syntax: /where [username]")
		) {
			@Override
			protected void perform(String[] args, Session session, WebServer server) {
				Point position = new Point();
				if (args.length == 1) {
					position = server.getPlayerAgentBySession(session).getPosition();
					server.unicast("(" + position.getX() + ", " + position.getY() + ")", session);
				}
				else if (args.length == 2) {
					if (server.getPlayerAgentByName(args[1]) != null) {
						position = server.getPlayerAgentByName(args[1]).getPosition();
						server.unicast("(" + position.getX() + ", " + position.getY() + ")", session);
					} else {
						server.unicast("Player '" + args[1] + "' not found.", session);
					}
				}
				else {
					throw new IllegalArgumentException();
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
			sb.append("/").append(String.join(", /", aliases));
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

		if (!args[0].equalsIgnoreCase("clear")) {
			// echo the command to the client's chatbox
			String selfText = "<strong>" + server.getNameBySession(session)
				+ "</strong>: " + input;
			server.unicast(selfText, session);
		}

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
