package test.java.junit.server_test;

import java.util.LinkedList;
import java.util.List;

import javax.websocket.Session;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
		List<String> output = new LinkedList<String>();
		Session session = ServerTest.getMockSession(output);
		server.onOpen(session);
		String name = " {\"name\":\"Test\"}";
		server.onMessage(name, session);
		
		String message = " {\"message\":\"/help\"}";
		server.onMessage(message, session);

		String expectedSubstring = "/help [command]";
		Assert.assertTrue(ServerTest.listContainsSubstring(output, expectedSubstring));
		
		server.onClose(session);
	}
}
