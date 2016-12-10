package test.java.junit.environment_test;

import main.java.agent.Agent;
import main.java.agent.PlayerAgent;
import main.java.agent.Pulsar;
import main.java.agent.Scout;
import main.java.environment.Environment;
import main.java.misc.Vector2D;
import main.java.projectile.Projectile;

import java.util.ArrayList;

import java.awt.geom.Point2D;

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

	/** Tests that the environment can spawn and despawn Scout-type NPCs */
	@Test
	public void testSpawnDespawnScoutNPC() {
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

	/** Tests that the environment can spawn and despawn Pulsar-type NPCs */
	@Test
	public void testSpawnDespawnPulsarNPC() {
		Environment environment = new Environment(false);
		int initialCount = environment.getActiveNPCAgents().size();
		Pulsar npc1 = environment.spawnPulsar();
		Pulsar npc2 = environment.spawnPulsar();
		Pulsar npc3 = environment.spawnPulsar();
		int spawnedCount = environment.getActiveNPCAgents().size();
		environment.despawnNPCAgent(npc1);
		environment.despawnNPCAgent(npc2);
		environment.despawnNPCAgent(npc3);
		int despawnedCount = environment.getActiveNPCAgents().size();

		Assert.assertNotEquals(initialCount, spawnedCount);
		Assert.assertEquals(initialCount, despawnedCount);
	}

	/** Tests that Projectiles can be added to the environment */
	@Test
	public void testAddDespawnProjectlie() {
		Environment environment = new Environment(false);
		int initialCount = environment.getActiveProjectiles().size();
		Projectile projectile1 = new Projectile(environment, null,
			new Point2D.Double(), new Vector2D(0.0, 0.0), 1, 1);
		environment.addProjectile(projectile1);
		Projectile projectile2 = new Projectile(environment, null,
			new Point2D.Double(), new Vector2D(0.0, 0.0), 1, 1);
		environment.addProjectile(projectile2);
		Projectile projectile3 = new Projectile(environment, null,
			new Point2D.Double(), new Vector2D(0.0, 0.0), 1, 1);
		environment.addProjectile(projectile3);
		int spawnedCount = environment.getActiveProjectiles().size();
		environment.despawnProjectile(projectile1);
		environment.despawnProjectile(projectile2);
		environment.despawnProjectile(projectile3);
		int despawnedCount = environment.getActiveProjectiles().size();

		Assert.assertNotEquals(initialCount, spawnedCount);
		Assert.assertEquals(initialCount, despawnedCount);
	}

	/** Tests that checkCollision correctly returns all collisions detected */
	@Test
	public void testCheckCollision() {
		Environment environment = new Environment(false);
		/** Note that the teams of the players being spawned is determined by the 
			* order of spawning. By spawning the firing player first we assure that 
			* this player will be on the same team as player 2, while player 1 will
			* be on the opposing team.
			*/
		PlayerAgent firingPlayer = environment.spawnPlayer(new Point2D.Double(100, 100));
		PlayerAgent player1 = environment.spawnPlayer(new Point2D.Double(0, 0));
		PlayerAgent player2 = environment.spawnPlayer(new Point2D.Double(0, 0));
		Scout firingNPC = environment.spawnScout(new Point2D.Double(100, 100));
		Scout npc1 = environment.spawnScout(new Point2D.Double(0, 0));
		Scout npc2 = environment.spawnScout(new Point2D.Double(0, 0));

		Assert.assertNotEquals(player1.getTeam(), player2.getTeam());

		Projectile playerProjectile = new Projectile(environment, firingPlayer,
			new Point2D.Double(0, 0), new Vector2D(0.0, 0.0), 1, 1);

		ArrayList<Agent> collisions = environment.checkCollision(playerProjectile);

		Assert.assertTrue(collisions.contains(player1));
		Assert.assertFalse(collisions.contains(player2));
		Assert.assertTrue(collisions.contains(npc1));
		Assert.assertTrue(collisions.contains(npc2));

		Projectile npcProjectile = new Projectile(environment, firingNPC,
			new Point2D.Double(0, 0), new Vector2D(0.0, 0.0), 1, 1);

		collisions = environment.checkCollision(npcProjectile);

		Assert.assertTrue(collisions.contains(player1));
		Assert.assertTrue(collisions.contains(player2));
		Assert.assertFalse(collisions.contains(npc1));
		Assert.assertFalse(collisions.contains(npc2));
	}

	/** Tests that checkRadius correctly returns distance from origin */
	@Test
	public void testCheckRadius() {
		Point2D.Double p = new Point2D.Double(0, 0);
		Assert.assertEquals(0.0, Environment.checkRadius(p), ERROR_MARGIN);
		p.setLocation(3, 4);
		Assert.assertEquals(5.0, Environment.checkRadius(p), ERROR_MARGIN);
		p.setLocation(6, 8);
		Assert.assertEquals(10.0, Environment.checkRadius(p), ERROR_MARGIN);
	}

	/** Tests that polarToCartesian correctly converts between coordinate formats */
	@Test
	public void testPolarToCartesian() {
		Point2D.Double p1 = new Point2D.Double(0, 0);
		Point2D.Double p2 = Environment.polarToCartesian(0, 0);
		Assert.assertEquals(p1.getX(), p2.getX(), ERROR_MARGIN);
		Assert.assertEquals(p1.getY(), p2.getY(), ERROR_MARGIN);

		p1 = new Point2D.Double(0, 1);
		p2 = Environment.polarToCartesian(Math.PI / 2, 1);
		Assert.assertEquals(p1.getX(), p2.getX(), ERROR_MARGIN);
		Assert.assertEquals(p1.getY(), p2.getY(), ERROR_MARGIN);

		p1 = new Point2D.Double(-2, 0);
		p2 = Environment.polarToCartesian(Math.PI, 2);
		Assert.assertEquals(p1.getX(), p2.getX(), ERROR_MARGIN);
		Assert.assertEquals(p1.getY(), p2.getY(), ERROR_MARGIN);

		p1 = new Point2D.Double(4, 3);
		p2 = Environment.polarToCartesian(Math.asin(0.6) , 5);
		Assert.assertEquals(p1.getX(), p2.getX(), ERROR_MARGIN);
		Assert.assertEquals(p1.getY(), p2.getY(), ERROR_MARGIN);
	}

	/** Tests that cartesianToPolar correctly converts between coordinate formats */
	@Test
	public void testCartesianToPolar() {
		Point2D.Double p1 = new Point2D.Double(0, 0);
		Point2D.Double p2 = Environment.cartesianToPolar(new Point2D.Double(0, 0));
		Assert.assertEquals(p1.getX(), p2.getX(), ERROR_MARGIN);
		Assert.assertEquals(p1.getY(), p2.getY(), ERROR_MARGIN);

		p1 = new Point2D.Double(Math.PI / 2, 1);
		p2 = Environment.cartesianToPolar(new Point2D.Double(0, 1));
		Assert.assertEquals(p1.getX(), p2.getX(), ERROR_MARGIN);
		Assert.assertEquals(p1.getY(), p2.getY(), ERROR_MARGIN);

		p1 = new Point2D.Double(Math.PI, 2);
		p2 = Environment.cartesianToPolar(new Point2D.Double(-2, 0));
		Assert.assertEquals(p1.getX(), p2.getX(), ERROR_MARGIN);
		Assert.assertEquals(p1.getY(), p2.getY(), ERROR_MARGIN);

		p1 = new Point2D.Double(Math.asin(0.6), 5);
		p2 = Environment.cartesianToPolar(new Point2D.Double(4, 3));
		Assert.assertEquals(p1.getX(), p2.getX(), ERROR_MARGIN);
		Assert.assertEquals(p1.getY(), p2.getY(), ERROR_MARGIN);
	}
}
