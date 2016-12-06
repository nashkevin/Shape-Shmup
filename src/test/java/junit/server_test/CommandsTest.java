package test.java.junit.server_test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import main.java.web.Command;
import main.java.web.WebServer;

/** Test commands that a player can perform through the chat window. */
public class CommandsTest {
	private WebServer server;

	@Before
	public void beforeTest() {
		server = new WebServer(false);
	}
	
	@Test
	/** Tests running "/help" without any arguments. */
	public void testHelp() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/help\"}";
		server.onMessage(message, user.getSession());

		String expectedSubstring = "/help [command]";
		Assert.assertTrue(user.receivedMessage(expectedSubstring));
		
		server.onClose(user.getSession());
	}
	
	@Test
	/** Tests running "/help exit". */
	public void testHelpWithAliases() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/help exit\"}";
		server.onMessage(message, user.getSession());

		String expectedResult = Command.EXIT.getHelpText();
		Assert.assertTrue(user.receivedMessage(expectedResult));
		
		Assert.assertTrue(user.receivedMessage("Aliases"));
		
		server.onClose(user.getSession());
	}
	
	@Test
	/** Tests running "/where" on a non-existent player. */
	public void testInvalidName() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/where Invalid\"}";
		server.onMessage(message, user.getSession());

		String expectedSubstring = "Player 'Invalid' was not found.";
		Assert.assertTrue(user.receivedMessage(expectedSubstring));
		
		server.onClose(user.getSession());
	}
	
	@Test
	/** Tests running "/commands". */
	public void testCommands() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/commands\"}";
		server.onMessage(message, user.getSession());

		for (Command command : Command.values()) {
			Assert.assertTrue(user.receivedMessage(command.getCommand()));
		}
		
		server.onClose(user.getSession());
	}
	
	@Test
	/** Test sending a private message to oneself. */
	public void testPmSelf() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/pm Test hi\"}";
		server.onMessage(message, user.getSession());

		String expectedResponse = "You can't private message yourself";
		Assert.assertTrue(user.receivedMessage(expectedResponse));
		
		server.onClose(user.getSession());
	}
	
	@Test
	/** Test sending a private message. */
	public void testPm() {
		// Create three mock sessions and connect them to the server.
		MockConnection user1 = new MockConnection(server, "A");
		MockConnection user2 = new MockConnection(server, "B");
		MockConnection user3 = new MockConnection(server, "C");
		
		// Send a message from A to B.
		String privateMessage = "Message with multiple words";
		String message = "{\"message\":\"/pm B " + privateMessage + "\"}";
		server.onMessage(message, user1.getSession());

		// Verify that B received the message but C didn't.
		Assert.assertTrue(user2.receivedMessage(privateMessage));
		Assert.assertFalse(user3.receivedMessage(privateMessage));

		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
		server.onClose(user3.getSession());
	}
	
	@Test
	/** Test trying to send a PM with no recipient or message. */
	public void testPmWithInvalidArguments() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/pm\"}";
		server.onMessage(message, user.getSession());

		// Verify that the help info for /pm appeared.
		Assert.assertTrue(user.receivedMessage(Command.PM.getHelpText()));

		server.onClose(user.getSession());
	}
	
	@Test
	/** Test the /stats command without arguments. */
	public void testSelfStats() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/stats\"}";
		server.onMessage(message, user.getSession());

		// Verify that the info about the player and level appears.
		String expected = "<strong>Test</strong>, Lv. 0";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
	}
	
	@Test
	/** Test using the /stats command to check another player's stats. */
	public void testOtherStats() {
		// Create two mock sessions and connect them to the server.
		MockConnection user = new MockConnection(server, "Test");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		String message = "{\"message\":\"/stats Test2\"}";
		server.onMessage(message, user.getSession());

		// Verify that the info about the player and level appears.
		String expected = "<strong>Test2</strong>, Lv. 0";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
		server.onClose(user2.getSession());
	}
	
	@Test
	/** Test using the /team command to check the player's team. */
	public void testTeam() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/team\"}";
		server.onMessage(message, user.getSession());

		// Have the expected string be vague because we don't want to make
		// an assumption about what team the player is spawned to.
		String expected = "You are on the";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
	}
	
	@Test
	/** Test using the /team command to change the player's team. */
	public void testTeamChange() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/team red\"}";
		server.onMessage(message, user.getSession());

		String expected = "You were placed on the red team.";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
	}
	
	@Test
	/** Test using the /team command to change the player's team. */
	public void testTeamOfPlayer() {
		// Create two mock sessions and connect them to the server.
		MockConnection user = new MockConnection(server, "Test");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		String message = "{\"message\":\"/team Test2\"}";
		server.onMessage(message, user.getSession());

		String expected = "Test2 is on the";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
		server.onClose(user2.getSession());
	}
	
	@Test
	/** Test using the /team command where the argument is neither a team nor a player. */
	public void testTeamInvalidArg() {
		// Create a mock session and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/team blah\"}";
		server.onMessage(message, user.getSession());

		String expected = "Found no teams or players named 'blah'.";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
	}
	
	@Test
	/** Test using the /team command to change another player's team. */
	public void testChangeOtherTeam() {
		// Create two mock sessions and connect them to the server.
		MockConnection user = new MockConnection(server, "Test");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		String message = "{\"message\":\"/team Test2 blue\"}";
		server.onMessage(message, user.getSession());

		String expected = "Test2 was placed on the blue team.";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
		server.onClose(user2.getSession());
	}
	
	@Test
	/** Test using the /team command to change another player to an invalid team. */
	public void testChangeOtherToInvalidTeam() {
		// Create two mock sessions and connect them to the server.
		MockConnection user = new MockConnection(server, "Test");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		String message = "{\"message\":\"/team Test2 invalid\"}";
		server.onMessage(message, user.getSession());

		String expected = "There is no 'invalid' team.";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
		server.onClose(user2.getSession());
	}
	
	@Test
	/** Test using the /team command to change the team of a player that doesn't exist. */
	public void testChangeTeamOfInvalidPlayer() {
		// Create one mock sessions and connect it to the server.
		MockConnection user = new MockConnection(server, "Test");
		
		String message = "{\"message\":\"/team no-one blue\"}";
		server.onMessage(message, user.getSession());

		String expected = "Player 'no-one' not found.";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
	}
	
	@Test
	/** Test using the /where command. */
	public void testWhere() {
		MockConnection user = new MockConnection(server, "Test");
		
		// Set the player's position so we know where they are
		String goTo = "{\"message\":\"/goto 0 0\"}";
		server.onMessage(goTo, user.getSession());
		
		String where = "{\"message\":\"/where\"}";
		server.onMessage(where, user.getSession());

		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user.receivedMessage(expected));

		server.onClose(user.getSession());
	}
	
	@Test
	/** Test using the /where command to find another player's location. */
	public void testWhereOther() {
		MockConnection user1 = new MockConnection(server, "Test");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		// Set the player's position so we know where they are
		String goTo = "{\"message\":\"/goto 0 0\"}";
		server.onMessage(goTo, user2.getSession());
		
		String where = "{\"message\":\"/where Test2\"}";
		server.onMessage(where, user1.getSession());

		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user1.receivedMessage(expected));

		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
	}
	
	@Test
	public void testBringSelf() {
		MockConnection user1 = new MockConnection(server, "Test");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		// Set the first player's position so we know where they are
		String goTo = "{\"message\":\"/goto 0 0\"}";
		server.onMessage(goTo, user1.getSession());
		
		// Bring the second player to the first
		String bring = "{\"message\":\"/bring Test2\"}";
		server.onMessage(bring, user1.getSession());
		
		// Check where the second player is
		String where = "{\"message\":\"/where Test2\"}";
		server.onMessage(where, user1.getSession());

		// Ensure that the second player is now co-located with the first
		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user1.receivedMessage(expected));

		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
	}
	
	@Test
	public void testBringToPlayer() {
		MockConnection user1 = new MockConnection(server, "Test");
		MockConnection user2 = new MockConnection(server, "Test2");
		MockConnection user3 = new MockConnection(server, "Test3");
		
		// Set the third player's position so we know where they are
		String goTo = "{\"message\":\"/goto 0 0\"}";
		server.onMessage(goTo, user3.getSession());
		
		// Have the first player bring the second to the third
		String bring = "{\"message\":\"/bring Test2 Test3\"}";
		server.onMessage(bring, user1.getSession());
		
		// Check where the second player is
		String where = "{\"message\":\"/where Test2\"}";
		server.onMessage(where, user1.getSession());

		// Ensure that the third player is where we expect
		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user1.receivedMessage(expected));

		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
		server.onClose(user3.getSession());
	}
	
	@Test
	public void testBringToPoint() {
		MockConnection user1 = new MockConnection(server, "Test");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		// Bring the second player to the point
		String bring = "{\"message\":\"/bring Test2 0 0\"}";
		server.onMessage(bring, user1.getSession());
		
		// Check where the second player is
		String where = "{\"message\":\"/where Test2\"}";
		server.onMessage(where, user1.getSession());

		// Ensure that the third player is where we expect
		String expected = "(0.00, 0.00)";
		Assert.assertTrue(user1.receivedMessage(expected));

		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
	}
	
	/** Tests /givexp without any arguments. */
	@Test
	public void testGiveExpSelf() {
		MockConnection user = new MockConnection(server, "Test");
		
		// Check that the user starts at level zero
		server.onMessage("{\"message\":\"/status\"}", user.getSession());
		Assert.assertTrue(user.receivedMessage("Lv. 0"));
		
		user.clearOutput();
		
		// Call /givexp
		server.onMessage("{\"message\":\"/givexp\"}", user.getSession());
		
		// Verify that the user rose to level 1.
		server.onMessage("{\"message\":\"/status\"}", user.getSession());
		Assert.assertTrue(user.receivedMessage("Lv. 1"));

		server.onClose(user.getSession());
	}
	
	/** Tests /givexp where the argument is the amount of experience. */
	@Test
	public void testGiveExpAmount() {
		MockConnection user = new MockConnection(server, "Test");
		
		// Check that the user starts with zero experience.
		server.onMessage("{\"message\":\"/status\"}", user.getSession());
		Assert.assertTrue(user.receivedMessage("(0/101 exp)"));
		
		user.clearOutput();
		
		// Call /givexp 10
		server.onMessage("{\"message\":\"/givexp 10\"}", user.getSession());
		
		// Verify that the user rose to level 1.
		server.onMessage("{\"message\":\"/status\"}", user.getSession());
		Assert.assertTrue(user.receivedMessage("(10/101 exp)"));

		server.onClose(user.getSession());
	}
	
	/** Tests /givexp where the argument is another player. */
	@Test
	public void testGiveExpToOther() {
		MockConnection user1 = new MockConnection(server, "Test1");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		// Check that the user 2 starts with zero experience.
		server.onMessage("{\"message\":\"/status\"}", user2.getSession());
		Assert.assertTrue(user2.receivedMessage("Lv. 0"));
		
		user2.clearOutput();
		
		// Call /givexp Test2 from Test1.
		server.onMessage("{\"message\":\"/givexp Test2\"}", user1.getSession());
		
		// Verify that Test2 rose to level 1.
		server.onMessage("{\"message\":\"/status\"}", user2.getSession());
		Assert.assertTrue(user2.receivedMessage("Lv. 1"));

		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
	}
	
	/** Tests /givexp with two arguments: giving an amount of experience to another player. */
	@Test
	public void testGiveExpAmountToOther() {
		MockConnection user1 = new MockConnection(server, "Test1");
		MockConnection user2 = new MockConnection(server, "Test2");
		
		// Check that the user 2 starts with zero experience.
		server.onMessage("{\"message\":\"/status\"}", user2.getSession());
		Assert.assertTrue(user2.receivedMessage("(0/101 exp)"));
		
		user2.clearOutput();
		
		// Call /givexp Test2 from Test1.
		server.onMessage("{\"message\":\"/givexp Test2 10\"}", user1.getSession());
		
		user2.clearOutput();
		
		// Verify that Test2 rose to level 1.
		server.onMessage("{\"message\":\"/status\"}", user2.getSession());
		Assert.assertTrue(user2.receivedMessage("(10/101 exp)"));

		server.onClose(user1.getSession());
		server.onClose(user2.getSession());
	}
}
