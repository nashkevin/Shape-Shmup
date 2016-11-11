package test.java.junit.server_test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import main.java.web.WebServer;

public class ServerTest {
	private static WebServer server;

	@BeforeClass
	public static void beforeClass() {
		server = new WebServer();
	}
	
	@Test
	public void testSimpleConnection() {
		MockConnection player = new MockConnection(server, "Test");
		server.onClose(player.getSession());
	}
	
	@Test
	public void testChatMessage() {
		MockConnection user1 = new MockConnection(server, "Test1");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		// Send a chat message from the first.
		String message = " {\"message\":\"Message.\"}";
		server.onMessage(message, user1.getSession());

		// Verify that both clients receive the chat message.
		String receivedMessage = "Message.";
		Assert.assertTrue(user1.receivedMessage(receivedMessage));
		Assert.assertTrue(user2.receivedMessage(receivedMessage));
		
		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
	}
}