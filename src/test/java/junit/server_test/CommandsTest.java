package test.java.junit.server_test;

import org.junit.Assert;
import org.junit.Test;

import main.java.web.Command;

/** Test commands that a player can perform through the chat window. */
public class CommandsTest {
	
	@Test
	/** Tests running "/help" without any arguments. */
	public void testHelp() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/help\"}";
		user.sendMessage(message);

		String expectedSubstring = "/help [command]";
		Assert.assertTrue(user.receivedMessage(expectedSubstring));
		
		user.close();
	}
	
	@Test
	/** Tests running "/help exit". */
	public void testHelpWithAliases() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/help exit\"}";
		user.sendMessage(message);

		String expectedResult = Command.EXIT.getHelpText();
		Assert.assertTrue(user.receivedMessage(expectedResult));
		
		Assert.assertTrue(user.receivedMessage("Aliases"));
		
		user.close();
	}
	
	@Test
	/** Tests running "/where" on a non-existent player. */
	public void testInvalidName() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/where Invalid\"}";
		user.sendMessage(message);

		String expectedSubstring = "Player 'Invalid' was not found.";
		Assert.assertTrue(user.receivedMessage(expectedSubstring));
		
		user.close();
	}
	
	@Test
	/** Tests running "/commands". */
	public void testCommands() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/commands\"}";
		user.sendMessage(message);

		for (Command command : Command.values()) {
			Assert.assertTrue(user.receivedMessage(command.getCommand()));
		}
		
		user.close();
	}
	
	@Test
	/** Test sending a private message to oneself. */
	public void testPmSelf() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/pm Test hi\"}";
		user.sendMessage(message);

		String expectedResponse = "You can't private message yourself";
		Assert.assertTrue(user.receivedMessage(expectedResponse));
		
		user.close();
	}
	
	@Test
	/** Test sending a private message. */
	public void testPm() {
		// Create three mock sessions and connect them to the server.
		MockConnection user1 = new MockConnection("A");
		MockConnection user2 = new MockConnection("B");
		MockConnection user3 = new MockConnection("C");
		
		// Send a message from A to B.
		String privateMessage = "Message with multiple words";
		String message = "{\"message\":\"/pm B " + privateMessage + "\"}";
		user1.sendMessage(message);

		// Verify that B received the message but C didn't.
		Assert.assertTrue(user2.receivedMessage(privateMessage));
		Assert.assertFalse(user3.receivedMessage(privateMessage));

		user1.close();
		user2.close();
		user3.close();
	}
	
	@Test
	/** Test trying to send a PM with no recipient or message. */
	public void testPmWithInvalidArguments() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/pm\"}";
		user.sendMessage(message);

		// Verify that the help info for /pm appeared.
		Assert.assertTrue(user.receivedMessage(Command.PM.getHelpText()));

		user.close();
	}
	
	@Test
	/** Test the /stats command without arguments. */
	public void testSelfStats() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/stats\"}";
		user.sendMessage(message);

		// Verify that the info about the player and level appears.
		String expected = "<strong>Test</strong>, Lv. 0";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
	}
	
	@Test
	/** Test using the /stats command to check another player's stats. */
	public void testOtherStats() {
		// Create two mock sessions and connect them to the server.
		MockConnection user = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test2");
		
		String message = "{\"message\":\"/stats Test2\"}";
		user.sendMessage(message);

		// Verify that the info about the player and level appears.
		String expected = "<strong>Test2</strong>, Lv. 0";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
		user2.close();
	}
	
	@Test
	/** Test using the /team command to check the player's team. */
	public void testTeam() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/team\"}";
		user.sendMessage(message);

		// Have the expected string be vague because we don't want to make
		// an assumption about what team the player is spawned to.
		String expected = "You are on the";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
	}
	
	@Test
	/** Test using the /team command to change the player's team. */
	public void testTeamChange() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/team red\"}";
		user.sendMessage(message);

		String expected = "You were placed on the red team.";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
	}
	
	@Test
	/** Test using the /team command to change the player's team. */
	public void testTeamOfPlayer() {
		// Create two mock sessions and connect them to the server.
		MockConnection user = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test2");
		
		String message = "{\"message\":\"/team Test2\"}";
		user.sendMessage(message);

		String expected = "Test2 is on the";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
		user2.close();
	}
	
	@Test
	/** Test using the /team command where the argument is neither a team nor a player. */
	public void testTeamInvalidArg() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/team blah\"}";
		user.sendMessage(message);

		String expected = "Found no teams or players named 'blah'.";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
	}
	
	@Test
	/** Test using the /team command to change another player's team. */
	public void testChangeOtherTeam() {
		// Create two mock sessions and connect them to the server.
		MockConnection user = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test2");
		
		String message = "{\"message\":\"/team Test2 blue\"}";
		user.sendMessage(message);

		String expected = "Test2 was placed on the blue team.";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
		user2.close();
	}
	
	@Test
	/** Test using the /team command to change another player to an invalid team. */
	public void testChangeOtherToInvalidTeam() {
		// Create two mock sessions and connect them to the server.
		MockConnection user = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test2");
		
		String message = "{\"message\":\"/team Test2 invalid\"}";
		user.sendMessage(message);

		String expected = "There is no 'invalid' team.";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
		user2.close();
	}
	
	@Test
	/** Test using the /team command to change the team of a player that doesn't exist. */
	public void testChangeTeamOfInvalidPlayer() {
		// Create one mock sessions and connect it to the server.
		MockConnection user = new MockConnection("Test");
		
		String message = "{\"message\":\"/team no-one blue\"}";
		user.sendMessage(message);

		String expected = "Player 'no-one' not found.";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
	}
	
	@Test
	/** Test using the /where command. */
	public void testWhere() {
		MockConnection user = new MockConnection("Test");
		
		// Set the player's position so we know where they are
		user.sendMessage("{\"message\":\"/goto 0 0\"}");
		
		user.sendMessage("{\"message\":\"/where\"}");

		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user.receivedMessage(expected));

		user.close();
	}
	
	@Test
	/** Test using the /where command to find another player's location. */
	public void testWhereOther() {
		MockConnection user1 = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test2");
		
		// Set the player's position so we know where they are
		user2.sendMessage("{\"message\":\"/goto 0 0\"}");
		
		user1.sendMessage("{\"message\":\"/where Test2\"}");

		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user1.receivedMessage(expected));

		user1.close();
		user2.close();
	}
	
	@Test
	public void testBringSelf() {
		MockConnection user1 = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test2");
		
		// Set the first player's position so we know where they are
		user1.sendMessage("{\"message\":\"/goto 0 0\"}");
		
		// Bring the second player to the first
		user1.sendMessage("{\"message\":\"/bring Test2\"}");
		
		// Check where the second player is
		user1.sendMessage("{\"message\":\"/where Test2\"}");

		// Ensure that the second player is now co-located with the first
		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user1.receivedMessage(expected));

		user1.close();
		user2.close();
	}
	
	@Test
	public void testBringToPlayer() {
		MockConnection user1 = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test2");
		MockConnection user3 = new MockConnection("Test3");
		
		// Set the third player's position so we know where they are
		user3.sendMessage("{\"message\":\"/goto 0 0\"}");
		
		// Have the first player bring the second to the third
		user1.sendMessage("{\"message\":\"/bring Test2 Test3\"}");
		
		// Check where the second player is
		user1.sendMessage("{\"message\":\"/where Test2\"}");

		// Ensure that the third player is where we expect
		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user1.receivedMessage(expected));

		user1.close();
		user2.close();
		user3.close();
	}
	
	@Test
	public void testBringToPoint() {
		MockConnection user1 = new MockConnection("Test");
		MockConnection user2 = new MockConnection("Test2");
		
		// Bring the second player to the point
		user1.sendMessage("{\"message\":\"/bring Test2 0 0\"}");
		
		// Check where the second player is
		user1.sendMessage("{\"message\":\"/where Test2\"}");

		// Ensure that the third player is where we expect
		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user1.receivedMessage(expected));

		user1.close();
		user2.close();
	}
	
	/** Tests /givexp without any arguments. */
	@Test
	public void testGiveExpSelf() {
		MockConnection user = new MockConnection("Test");
		
		// Check that the user starts at level zero
		user.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user.receivedMessage("Lv. 0"));
		
		user.clearOutput();
		
		// Call /givexp
		user.sendMessage("{\"message\":\"/givexp\"}");
		
		// Verify that the user rose to level 1.
		user.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user.receivedMessage("Lv. 1"));

		user.close();
	}
	
	/** Tests /givexp where the argument is the amount of experience. */
	@Test
	public void testGiveExpAmount() {
		MockConnection user = new MockConnection("Test");
		
		// Check that the user starts with zero experience.
		user.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user.receivedMessage("(0/101 exp)"));
		
		user.clearOutput();
		
		// Call /givexp 10
		user.sendMessage("{\"message\":\"/givexp 10\"}");
		
		// Verify that the user rose to level 1.
		user.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user.receivedMessage("(10/101 exp)"));

		user.close();
	}
	
	/** Tests /givexp where the argument is another player. */
	@Test
	public void testGiveExpToOther() {
		MockConnection user1 = new MockConnection("Test1");
		MockConnection user2 = new MockConnection("Test2");
		
		// Check that the user 2 starts with zero experience.
		user2.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user2.receivedMessage("Lv. 0"));
		
		user2.clearOutput();
		
		// Call /givexp Test2 from Test1.
		user1.sendMessage("{\"message\":\"/givexp Test2\"}");
		
		// Verify that Test2 rose to level 1.
		user2.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user2.receivedMessage("Lv. 1"));

		user1.close();
		user2.close();
	}
	
	/** Tests /givexp with two arguments: giving an amount of experience to another player. */
	@Test
	public void testGiveExpAmountToOther() {
		MockConnection user1 = new MockConnection("Test1");
		MockConnection user2 = new MockConnection("Test2");
		
		// Check that the user 2 starts with zero experience.
		user2.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user2.receivedMessage("(0/101 exp)"));
		
		user2.clearOutput();
		
		// Call /givexp Test2 from Test1.
		user1.sendMessage("{\"message\":\"/givexp Test2 10\"}");
		
		user2.clearOutput();
		
		// Verify that Test2 rose to level 1.
		user2.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user2.receivedMessage("(10/101 exp)"));

		user1.close();
		user2.close();
	}
	
	@Test
	public void testHealSelf() {
		MockConnection user = new MockConnection("Test");
		// Apply 10 damage to self.
		user.sendMessage("{\"message\":\"/heal -10\"}");
		
		user.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user.receivedMessage("Health: 90/100"));
		
		user.clearOutput();
		
		// Heal self without any arguments.
		user.sendMessage("{\"message\":\"/heal\"}");
		
		// Verify that the user is back to full health.
		user.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user.receivedMessage("Health: 100/100"));
		
		user.close();
	}
	
	@Test
	public void testHealOther() {
		MockConnection user1 = new MockConnection("Test1");
		MockConnection user2 = new MockConnection("Test2");
		// Apply 10 damage to the second user.
		user1.sendMessage("{\"message\":\"/heal Test2 -10\"}");
		
		user2.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user2.receivedMessage("Health: 90/100"));
		
		user2.clearOutput();
		
		// Heal the second user to max health.
		user1.sendMessage("{\"message\":\"/heal Test2\"}");
		
		// Verify that the user is back to full health.
		user2.sendMessage("{\"message\":\"/status\"}");
		Assert.assertTrue(user2.receivedMessage("Health: 100/100"));

		user1.close();
		user2.close();
	}
	
	@Test
	public void testSay() {
		MockConnection user1 = new MockConnection("Test1");
		MockConnection user2 = new MockConnection("Test2");
		
		// User 1 says something
		user1.sendMessage("{\"message\":\"/say something\"}");

		// Both users received it
		String saidMessage = "something";
		Assert.assertTrue(user1.receivedMessage(saidMessage));
		Assert.assertTrue(user2.receivedMessage(saidMessage));

		user1.close();
		user2.close();
	}
	
	@Test
	public void testPing() {
		MockConnection user = new MockConnection("Test");
		user.sendMessage("{\"message\":\"/ping\"}");
		Assert.assertTrue(user.receivedMessage("PONG"));
		user.close();
	}
	
	@Test
	public void testPlayers() {
		MockConnection user1 = new MockConnection("Test1");
		
		// Only one player present
		user1.sendMessage("{\"message\":\"/players\"}");
		Assert.assertTrue(user1.receivedMessage("1 player: Test1"));
		
		MockConnection user2 = new MockConnection("Test2");
		
		// Two players present
		user1.sendMessage("{\"message\":\"/players\"}");
		Assert.assertTrue(user1.receivedMessage("2 players: Test1, Test2")
				|| user1.receivedMessage("2 players: Test2, Test1"));

		user1.close();
		user2.close();
	}
}
