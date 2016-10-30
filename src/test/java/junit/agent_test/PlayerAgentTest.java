package test.java.junit.agent_test;
import main.java.agent.PlayerAgent;
import main.java.environment.Environment;
import main.java.web.ClientInput;
import main.java.misc.Vector2D;

import junit.framework.Assert;
import org.junit.Test;
import java.awt.Point;
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
		event.setDirection("left");

		player.addPlayerEvent(event);

		Queue<ClientInput> queue = player.getPlayerEvents();

		Assert.assertEquals(1, queue.size());

		ClientInput result = queue.poll();
		Assert.assertEquals(event, result);
		Assert.assertTrue(queue.isEmpty());
	}

	@Test
	public void testUpdate() {
		Environment environment = new Environment(20);
		PlayerAgent player = new PlayerAgent(UUID.randomUUID(), environment, new Point(10, 10), 1, 1, 100, 10, 1, 1);
		ClientInput eventRight = new ClientInput();
		eventRight.setDirection("right");
		ClientInput eventUp = new ClientInput();
		eventUp.setDirection("up");
		player.addPlayerEvent(eventRight);
		player.addPlayerEvent(eventUp);

		player.update();
		
		Assert.assertEquals(null, player.getFiringVector());

		Vector2D expectedAcceleration = new Vector2D(Math.sqrt(2), Math.PI/4);
		Vector2D playerAcceleration = player.getAcceleration();
		Assert.assertEquals(expectedAcceleration.getMagnitude(), player.getAcceleration().getMagnitude(), 0.001);
		Assert.assertEquals(expectedAcceleration.getAngle(), player.getAcceleration().getAngle(), 0.001);
	



	}


}
