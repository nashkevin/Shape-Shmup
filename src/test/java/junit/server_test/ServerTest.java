package test.java.junit.server_test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.web.ClientInput;
import main.java.web.WebServer;

public class ServerTest {
	private static WebServer server;

	@BeforeClass
	public static void beforeClass() {
		server = new WebServer(false);
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
		String message = "{\"message\":\"Message.\"}";
		server.onMessage(message, user1.getSession());

		// Verify that both clients receive the chat message.
		String receivedMessage = "Message.";
		Assert.assertTrue(user1.receivedMessage(receivedMessage));
		Assert.assertTrue(user2.receivedMessage(receivedMessage));
		
		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
	}
	
	@Test
	public void testKickSelf() {
		MockConnection user = new MockConnection(server, "Test");
		
		// Try to kick the player itself.
		String message = "{\"message\":\"/kick Test\"}";
		server.onMessage(message, user.getSession());
		
		// Verify that the user received a message that they can't kick themself.
		String receivedMessage = "You can't kick yourself.";
		Assert.assertTrue(user.receivedMessage(receivedMessage));
				
		server.onClose(user.getSession());
	}
	
	@Test
	public void testKickOther() {
		MockConnection user1 = new MockConnection(server, "Test1");
		new MockConnection(server, "Test2");
		
		// Have user 1 kick user 2.
		String kickMessage = "{\"message\":\"/kick Test2\"}";
		server.onMessage(kickMessage, user1.getSession());
		
		// Now try to PM user 2 to confirm that they are no longer present.
		String pmMessage = "{\"message\":\"/pm Test2 hi\"}";
		server.onMessage(pmMessage, user1.getSession());
		
		// Confirm the above by the chat output.
		String receivedMessage = "Player 'Test2' was not found.";
		Assert.assertTrue(user1.receivedMessage(receivedMessage));
				
		server.onClose(user1.getSession());
	}
	
	@Test
	public void testDuplicateName() {
		// Try connecting two users to the server with the same username.
		MockConnection user1 = new MockConnection(server, "Test");
		new MockConnection(server, "Test");
		
		// Have the first user check what players have connected.
		String playersMessage = "{\"message\":\"/players\"}";
		server.onMessage(playersMessage, user1.getSession());
		
		// Confirm there is still only one player.
		String receivedMessage = "1 player: Test";
		Assert.assertTrue(user1.receivedMessage(receivedMessage));

		server.onClose(user1.getSession());
	}
	
	@Test
	public void testClientInputMoving() {
		ClientInput ci = new ClientInput();
		ci.setDown(true);
		Assert.assertTrue(ci.isMoving());
		ci.setDown(false);
		ci.setUp(true);
		Assert.assertTrue(ci.isMoving());
		ci.setUp(false);
		ci.setLeft(true);
		Assert.assertTrue(ci.isMoving());
		ci.setRight(true);
		Assert.assertTrue(ci.isMoving());
		ci.setRight(false);
		ci.setLeft(false);
		Assert.assertFalse(ci.isMoving());
	}
}