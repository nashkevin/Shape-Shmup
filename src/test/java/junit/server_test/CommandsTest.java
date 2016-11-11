package test.java.junit.server_test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.web.Command;
import main.java.web.WebServer;

/** Test commands that a player can perform through the chat window. */
public class CommandsTest {
	private static WebServer server;

	@BeforeClass
	public static void beforeClass() {
		server = new WebServer();
	}
	
	@Test
	/** Tests running "/help" without any arguments. */
	public void testHelp() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = " {\"message\":\"/help\"}";
		server.onMessage(message, user.getSession());

		String expectedSubstring = "/help [command]";
		Assert.assertTrue(user.receivedMessage(expectedSubstring));
		
		server.onClose(user.getSession());
	}
	
	@Test
	/** Tests running "/help commands". */
	public void testSpecificHelpInfo() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = " {\"message\":\"/help commands\"}";
		server.onMessage(message, user.getSession());

		String expectedResult = Command.COMMANDS.getHelpText();
		Assert.assertTrue(user.receivedMessage(expectedResult));
		
		server.onClose(user.getSession());
	}
	
	@Test
	/** Tests running "/commands". */
	public void testCommands() {
		// Create a mock session and connect it to the server.
				MockConnection user = new MockConnection(server, "Test");
				
				String message = " {\"message\":\"/commands\"}";
				server.onMessage(message, user.getSession());

				for (Command command : Command.values()) {
					Assert.assertTrue(user.receivedMessage(command.getCommand()));
				}
				
				server.onClose(user.getSession());
	}
}
