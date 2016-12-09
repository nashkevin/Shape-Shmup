package test.java.junit.server_test;

import org.junit.Assert;
import org.junit.Test;

import main.java.web.ClientInput;

public class ServerTest {
	
	@Test
	public void testSimpleConnection() {
		MockConnection player = new MockConnection("Test");
		player.close();
	}
	
	@Test
	public void testChatMessage() {
		MockConnection user1 = new MockConnection("Test1");
		MockConnection user2 = new MockConnection("Test2");
		
		// Send a chat message from the first.
		String message = "{\"message\":\"Message.\"}";
		user1.sendMessage(message);

		// Verify that both clients receive the chat message.
		String receivedMessage = "Message.";
		Assert.assertTrue(user1.receivedMessage(receivedMessage));
		Assert.assertTrue(user2.receivedMessage(receivedMessage));
		
		user1.close();
		user2.close();
	}
	
	@Test
	public void testKickSelf() {
		MockConnection user = new MockConnection("Test");
		
		// Try to kick the player itself.
		String message = "{\"message\":\"/kick Test\"}";
		user.sendMessage(message);
		
		// Verify that the user received a message that they can't kick themself.
		String receivedMessage = "You can't kick yourself.";
		Assert.assertTrue(user.receivedMessage(receivedMessage));
				
		user.close();
	}
	
	@Test
	public void testKickOther() {
		MockConnection user1 = new MockConnection("Test1");
		new MockConnection("Test2");
		
		// Have user 1 kick user 2.
		String kickMessage = "{\"message\":\"/kick Test2\"}";
		user1.sendMessage(kickMessage);
		
		// Now try to PM user 2 to confirm that they are no longer present.
		String pmMessage = "{\"message\":\"/pm Test2 hi\"}";
		user1.sendMessage(pmMessage);
		
		// Confirm the above by the chat output.
		String receivedMessage = "Player 'Test2' was not found.";
		Assert.assertTrue(user1.receivedMessage(receivedMessage));
				
		user1.close();
	}
	
	@Test
	public void testDuplicateName() {
		// Try connecting two users to the server with the same username.
		MockConnection user1 = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test");
		
		// Have the first user check what players have connected.
		String playersMessage = "{\"message\":\"/players\"}";
		user1.sendMessage(playersMessage);
		
		// Confirm there is still only one player.
		String receivedMessage = "1 player: Test";
		Assert.assertTrue(user1.receivedMessage(receivedMessage));

		user1.close();
		user2.close();
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