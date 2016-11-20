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
	private static final double EPSILON = 0.00001; //Allowed error of floating point values for testing purposes
	@Test
	public void testConstructor() {
		Environment environment = new Environment();
		PlayerAgent player = new PlayerAgent("Tester", UUID.randomUUID(), environment, new Point(10, 10), 1, 1, 100, 10, 1, 1);

		Assert.assertEquals(new Point(10,10), player.getPosition());
		Assert.assertEquals(1, player.getLevel());
		Assert.assertEquals(1, player.getTeam());
		Assert.assertEquals(100, player.getHealth());
		Assert.assertEquals(10, player.getDamage());


	}
	@Test
	public void testAddPlayerEvent() {
		Environment environment = new Environment();
		PlayerAgent player = new PlayerAgent("Tester", UUID.randomUUID(), environment, new Point(10, 10), 1, 1, 100, 10, 1, 1);

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
		
		/*Environment environment = new Environment();
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
		
		ClientInput eventLeft = new ClientInput();
		eventLeft.setLeft(true);
		
		ClientInput eventUp = new ClientInput();
		eventUp.setUp(true);
		
		ClientInput eventDown = new ClientInput();
		eventDown.setDown(true);
		
		PlayerAgent.PlayerAgentTester.generateTestInstance();
		
		//Test 0: Test where the summation of events sets the vectors to be zero vectors
		
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventRight);
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventUp);
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventDown);
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventLeft);
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		
		

		Assert.assertEquals(0, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getMagnitude(), EPSILON);
		Assert.assertEquals(0, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getAngle(), EPSILON);
		
		
		//Test 1: Test the unit vector in each direction
		
		
		Assert.assertTrue(PlayerAgent.PlayerAgentTester.getTestInstance().getPlayerEvents().isEmpty());
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventRight);
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		Assert.assertEquals(1, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getMagnitude(), EPSILON);
		Assert.assertEquals(0, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getAngle(), EPSILON);
		
		
		Assert.assertTrue(PlayerAgent.PlayerAgentTester.getTestInstance().getPlayerEvents().isEmpty());
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventUp);
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		Assert.assertEquals(1, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getMagnitude(), EPSILON);
		Assert.assertEquals(Math.PI/2.0, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getAngle(), EPSILON);
		
		
		Assert.assertTrue(PlayerAgent.PlayerAgentTester.getTestInstance().getPlayerEvents().isEmpty());
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventLeft);
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		Assert.assertEquals(1, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getMagnitude(), EPSILON);
		Assert.assertEquals(Math.PI, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getAngle(), EPSILON);
		
		Assert.assertTrue(PlayerAgent.PlayerAgentTester.getTestInstance().getPlayerEvents().isEmpty());
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventDown);
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		Assert.assertEquals(1, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getMagnitude(), EPSILON);
		Assert.assertEquals(-Math.PI/2.0, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getAngle(), EPSILON);
		
		
		
		//Test many: Test with various test cases of multiple events
		
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventRight);
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventUp);
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		

		PlayerAgent testAgent = PlayerAgent.PlayerAgentTester.getTestInstance();
		
		Vector2D expectedAcceleration = new Vector2D(Math.sqrt(2), Math.PI/4);
		Assert.assertEquals(expectedAcceleration.getMagnitude(), testAgent.getAcceleration().getMagnitude(), EPSILON);
		Assert.assertEquals(expectedAcceleration.getAngle(), testAgent.getAcceleration().getAngle(), EPSILON);
		
		//Test boundary case: There are no events to be queued
		
		//Unit test in up direction just as a precondition
		Assert.assertTrue(PlayerAgent.PlayerAgentTester.getTestInstance().getPlayerEvents().isEmpty());
		PlayerAgent.PlayerAgentTester.call_addPlayerEvent(eventRight);
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		Assert.assertEquals(1, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getMagnitude(), EPSILON);
		Assert.assertEquals(0, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getAngle(), EPSILON);
		
		//call preupdate call again with no more inputs, there should be no change 
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		Assert.assertEquals(1, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getMagnitude(), EPSILON);
		Assert.assertEquals(0, PlayerAgent.PlayerAgentTester.getTestInstance().getAcceleration().getAngle(), EPSILON);
		
		
		
		//Test firing: There is a firing vector
		
		/*
		ClientInput firingEvent = new ClientInput();
		firingEvent.setIsFiring(true);
		firingEvent.setAngle(Math.PI / 2.0);
		
		
		PlayerAgent.PlayerAgentTester.call_preUpdateCall();
		Assert.assertEquals(1, PlayerAgent.PlayerAgentTester.getTestInstance().getFiringVector().getMagnitude(), EPSILON);
		Assert.assertEquals(Math.PI / 2.0, PlayerAgent.PlayerAgentTester.getTestInstance().getFiringVector().getAngle(), EPSILON);
		*/



	}


}
