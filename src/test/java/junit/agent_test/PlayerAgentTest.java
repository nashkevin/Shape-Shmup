package test.java.junit.agent_test;
import main.java.agent.Agent;
import main.java.agent.PlayerAgent;
import main.java.environment.Environment;
import main.java.web.ClientInput;
import main.java.misc.Vector2D;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Point;
import java.util.Random;
import java.util.UUID;
import java.util.Queue;

public class PlayerAgentTest {
	@Test
	public void testConstructor() {
		Environment environment = new Environment(20);
		PlayerAgent player = new PlayerAgent(UUID.randomUUID(), environment, new Point(10, 10), 1, 1, 100, 10, 1, 1);

		Assert.assertEquals(new Point(10,10), player.getPosition());
		Assert.assertEquals(1, player.getLevel());
		Assert.assertEquals(1, player.getTeam());
		Assert.assertEquals(100, player.getHealth());
		Assert.assertEquals(10, player.getDamage());


	}
	@Test
	public void testAddPlayerEvent() {
		Environment environment = new Environment(20);
		PlayerAgent player = new PlayerAgent(UUID.randomUUID(), environment, new Point(10, 10), 1, 1, 100, 10, 1, 1);

		ClientInput event = new ClientInput();
		event.setLeft(true);

		player.addPlayerEvent(event);

		Queue<ClientInput> queue = player.getPlayerEvents();

		Assert.assertEquals(1, queue.size());

		ClientInput result = queue.poll();
		Assert.assertEquals(event, result);
		Assert.assertTrue(queue.isEmpty());
	}

	@Test
	public void testPreUpdateCall() {
		
		/*Environment environment = new Environment(20);
		PlayerAgent player = new PlayerAgent(UUID.randomUUID(), environment, new Point(10, 10), 1, 1, 100, 10, 1, 1);
		ClientInput eventRight = new ClientInput();
		eventRight.setRight(true);
		ClientInput eventUp = new ClientInput();
		eventUp.setUp(true);
		player.addPlayerEvent(eventRight);
		player.addPlayerEvent(eventUp);
		*/
		ClientInput eventRight = new ClientInput();
		eventRight.setRight(true);
		ClientInput eventUp = new ClientInput();
		eventUp.setUp(true);
		
		PlayerAgent.PlayerAgentTester.generateTestInstance();
		
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventRight);
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventUp);
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();

		PlayerAgent testAgent = PlayerAgent.PlayerAgentTester.getTestInstance();
		
		Vector2D expectedAcceleration = new Vector2D(Math.sqrt(2), Math.PI/4);
		Assert.assertEquals(expectedAcceleration.getMagnitude(), testAgent.getAcceleration().getMagnitude(), 0.001);
		Assert.assertEquals(expectedAcceleration.getAngle(), testAgent.getAcceleration().getAngle(), 0.001);
	



	}


}
