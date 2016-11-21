package test.java.junit.environment_test;

import main.java.agent.Agent;
import main.java.agent.NPCAgent;
import main.java.agent.PlayerAgent;
import main.java.agent.Scout;
import main.java.environment.Environment;

import java.awt.Point;
import java.lang.Math;
import org.junit.Test;
import org.junit.Assert;

public class EnvironmentTest {
	private static final double ERROR_MARGIN = 0.0001;

	/** Tests that the environment can spawn and despawn players */
	@Test
	public void testSpawnDespawnPlayer() {
		Environment environment = new Environment(false);
		int initialCount = environment.getActivePlayerAgents().size();
		PlayerAgent player1 = environment.spawnPlayer("Player 1");
		PlayerAgent player2 = environment.spawnPlayer("Player 2");
		PlayerAgent player3 = environment.spawnPlayer("Player 3");
		int spawnedCount = environment.getActivePlayerAgents().size();
		environment.despawnPlayerAgent(player1);
		environment.despawnPlayerAgent(player2);
		environment.despawnPlayerAgent(player3);
		int despawnedCount = environment.getActivePlayerAgents().size();

		Assert.assertNotEquals(initialCount, spawnedCount);
		Assert.assertEquals(initialCount, despawnedCount);
	}

	/** Tests that the environment can spawn and despawn NPCs */
	@Test
	public void testSpawnDespawnNPC() {
		Environment environment = new Environment(false);
		int initialCount = environment.getActiveNPCAgents().size();
		Scout npc1 = environment.spawnScout();
		Scout npc2 = environment.spawnScout();
		Scout npc3 = environment.spawnScout();
		int spawnedCount = environment.getActiveNPCAgents().size();
		environment.despawnNPCAgent(npc1);
		environment.despawnNPCAgent(npc2);
		environment.despawnNPCAgent(npc3);
		int despawnedCount = environment.getActiveNPCAgents().size();

		Assert.assertNotEquals(initialCount, spawnedCount);
		Assert.assertEquals(initialCount, despawnedCount);
	}

	@Test
	public void testCheckRadius() {
		Point p = new Point(0, 0);
		Assert.assertEquals(0.0, Environment.checkRadius(p), ERROR_MARGIN);
		p.setLocation(3, 4);
		Assert.assertEquals(5.0, Environment.checkRadius(p), ERROR_MARGIN);
		p.setLocation(6, 8);
		Assert.assertEquals(10.0, Environment.checkRadius(p), ERROR_MARGIN);
	}

	@Test
	public void testPolarToCartesian() {
		Assert.assertEquals(new Point(0, 0), Environment.polarToCartesian(0, 0));
		Assert.assertEquals(new Point(0, 1), Environment.polarToCartesian(Math.PI / 2, 1));
		Assert.assertEquals(new Point(-2, 0), Environment.polarToCartesian(Math.PI, 2));
		Assert.assertEquals(new Point(4, 3), Environment.polarToCartesian(Math.asin(0.6), 5));
	}

	@Test
	public void testCartesianToPolar() {
		Assert.assertArrayEquals(new double[] {0, 0}, Environment.cartesianToPolar(new Point(0, 0)), ERROR_MARGIN);
		Assert.assertArrayEquals(new double[] {Math.PI / 2, 1}, Environment.cartesianToPolar(new Point(0, 1)), ERROR_MARGIN);
		Assert.assertArrayEquals(new double[] {Math.PI, 2}, Environment.cartesianToPolar(new Point(-2, 0)), ERROR_MARGIN);
		Assert.assertArrayEquals(new double[] {Math.asin(0.6), 5}, Environment.cartesianToPolar(new Point(4, 3)), ERROR_MARGIN);
	}
}
