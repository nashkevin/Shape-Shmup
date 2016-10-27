package test.java;
import main.java.agent.PlayerAgent;
import main.java.environment.Environment;
import junit.framework.Assert;
import org.junit.Test;
import java.awt.Point;
import java.util.UUID;
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
		ClientInput event = new ClientInput();
		event.setDirection("left");

		player.addPlayerEvent(event);

	}



}
