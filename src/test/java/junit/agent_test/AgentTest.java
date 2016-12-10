package test.java.junit.agent_test;

import main.java.agent.Agent;
import main.java.agent.PlayerAgent;
import main.java.environment.Environment;

import java.awt.geom.Point2D;
import java.lang.Math;
import org.junit.Test;
import org.junit.Assert;

public class AgentTest {
	private static final double ERROR_MARGIN = 0.0001;

	/** Tests that getPosition() returns a defensive copy */
	@Test
	public void testGetPosition() {
		Environment environment = new Environment(false);
		Agent agent = new PlayerAgent(environment, new Point2D.Double(0, 0), "Agent");
		Point2D.Double point = agent.getPosition();
		point.setLocation(1, 1); // moves the point in both x and y directions

		// Moving the point should not have moved the agent
		Assert.assertNotEquals(point, agent.getPosition());
	}
	
	public void testSetPosition() {
		Environment environment = new Environment(false);
		Agent agent = new PlayerAgent(environment, new Point2D.Double(0, 0), "Agent");
		Point2D.Double point = agent.getPosition();
		agent.setPosition(0, environment.getRadius() + 10); //set outside of radius
		Assert.assertEquals(new Point2D.Double(0, environment.getRadius()), agent.getPosition());
		
	}

	/** Tests that setHealth() does not set health above maxHealth */
	@Test
	public void testSetHealth() {
		Environment environment = new Environment(false);
		Agent agent = new PlayerAgent(environment, new Point2D.Double(0, 0), "Agent");
		agent.setMaxHealth(100);
		agent.setHealth(999);

		Assert.assertEquals(100, agent.getHealth()); // should not be 999
	}

	/** Tests that reducing maxHealth also reduces health if above maxHealth */
	@Test
	public void testSetMaxHealth() {
		Environment environment = new Environment(false);
		Agent agent = new PlayerAgent(environment, new Point2D.Double(0, 0), "Agent");
		agent.setMaxHealth(100);
		agent.setHealth(100);
		agent.setMaxHealth(50);

		Assert.assertEquals(50, agent.getHealth()); // should not be 100
	}

	/** Tests that maxHealth cannot be set to a non-positive value */
	@Test(expected = IllegalArgumentException.class)
	public void testSetMaxHealthInvalid() {
		Environment environment = new Environment(false);
		Agent agent = new PlayerAgent(environment, new Point2D.Double(0, 0), "Agent");
		agent.setMaxHealth(0);
	}

	@Test
	public void testApplyDamage() {
		Environment environment = new Environment(false);
		Agent agent = new PlayerAgent(environment, new Point2D.Double(0, 0), "Agent");
		int initialHealth = 100;
		agent.setHealth(initialHealth);
		agent.applyDamage(5);

		Assert.assertEquals(95, agent.getHealth());
	}

	@Test
	public void testGetAngleTo() {
		Environment environment = new Environment(false);
		Agent agent1 = new PlayerAgent(environment, new Point2D.Double(0, 0), "Agent1");
		Agent agent2 = new PlayerAgent(environment, new Point2D.Double(0, 5), "Agent2");

		Assert.assertEquals(-Math.PI / 2, agent1.getAngleTo(agent2), ERROR_MARGIN);
		Assert.assertEquals(-Math.PI / 2, agent1.getAngleTo(new Point2D.Double(0, 1)), ERROR_MARGIN);
		Assert.assertEquals(-Math.PI / 2, agent1.getAngleTo(0, 10), ERROR_MARGIN);
	}
}
