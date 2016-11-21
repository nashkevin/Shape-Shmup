package test.java.junit.agent_test;

import main.java.agent.Agent;
import main.java.agent.PlayerAgent;
import main.java.environment.Environment;

import java.awt.Point;
import java.lang.Math;
import org.junit.Test;
import org.junit.Assert;

public class AgentTest {
	private static final double ERROR_MARGIN = 0.001;

	@Test
	public void testGetAngleTo() {
		Environment environment = new Environment();
		Agent agent1 = new PlayerAgent(environment, new Point(0, 0), "Agent1");
		Agent agent2 = new PlayerAgent(environment, new Point(0, 5), "Agent2");

		Assert.assertEquals(Math.PI / 2, agent1.getAngleTo(agent2), ERROR_MARGIN);
		Assert.assertEquals(Math.PI / 2, agent1.getAngleTo(new Point(0, 1)), ERROR_MARGIN);
		Assert.assertEquals(Math.PI / 2, agent1.getAngleTo(0, 10), ERROR_MARGIN);
	}
}
