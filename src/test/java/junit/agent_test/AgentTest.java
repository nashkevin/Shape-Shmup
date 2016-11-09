package test.java.junit.agent_test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.Random;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.agent.Agent;
import main.java.agent.test.AgentTestImp;
import main.java.agent.test.EnvironmentMock;
import main.java.environment.Environment;
import main.java.misc.Vector2D;
import main.java.projectile.ProjectileFactory;

public class AgentTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		return;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		return;
	}
	
	@Test
	public void test_constructor() {
		Random random = new Random();
		
		UUID id = UUID.randomUUID();
		Environment env = new EnvironmentMock(random.nextDouble() * 100);
		Point position = new Point();
		position.setLocation(random.nextDouble(), random.nextDouble());
		int level = random.nextInt(10);
		int team = random.nextInt(5);
		int health = random.nextInt(500) + 1;
		int damage = random.nextInt(500);
		int projectileSpeed = random.nextInt(100);
		int baseMovementSpeed = random.nextInt(100);
		
		// TEST CASE: Non-null values, assert values initialized properly
		Agent agent = new AgentTestImp(id, env, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
		Assert.assertTrue(id.equals(agent.getID()));
		Assert.assertTrue(position.equals(agent.getPosition()));
		Assert.assertTrue(level == agent.getLevel());
		Assert.assertTrue(team == agent.getTeam());
		Assert.assertTrue(health == agent.getHealth());
		Assert.assertTrue(health == agent.getMaxHealth());
		Assert.assertTrue(damage == agent.getDamage());
		Assert.assertTrue(baseMovementSpeed == agent.getBaseMovementSpeed());
		Assert.assertTrue(1.0 == agent.getMovementSpeedFactor());
		Assert.assertTrue(null == agent.getAcceleration());
		Assert.assertTrue(null == agent.getFiringVector());
		Assert.assertTrue(ProjectileFactory.Type.NONE == agent.getProjectileType());
		
		// TEST CASE: Null id
		try {
			agent = new AgentTestImp(null, env, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
			Assert.fail();
		} catch (Exception e) {}
		
		// TEST CASE: Null environment
		try {
			agent = new AgentTestImp(id, null, position, level, team, health, damage, projectileSpeed, baseMovementSpeed);
			Assert.fail();
		} catch (Exception e) {}
		
		// TEST CASE: Null position
		try {
			agent = new AgentTestImp(id, env, null, level, team, health, damage, projectileSpeed, baseMovementSpeed);
			Assert.fail();
		} catch (Exception e) {}
		
		// TEST CASE: Negative level
		int badLevel = (level >= 0) ? -level : level;
		agent = new AgentTestImp(id, env, position, badLevel, team, health, damage, projectileSpeed, baseMovementSpeed);
		Assert.assertTrue(0 == agent.getLevel());
		
		// TEST CASE: Zero or negative health
		int badHealth = (health > 0) ? -health : health;
		try {
			agent = new AgentTestImp(id, env, position, level, team, badHealth, damage, projectileSpeed, baseMovementSpeed);
			Assert.fail();
		} catch (Exception e) {}
		
		// TEST CASE: Negative damage
		int badDamage = (damage >= 0) ? -damage : damage;
		agent = new AgentTestImp(id, env, position, badLevel, team, health, badDamage, projectileSpeed, baseMovementSpeed);
		Assert.assertTrue(0 == agent.getDamage());
		
		// TEST CASE: Negative projectile speed
		int badProjectileSpeed = (projectileSpeed >= 0) ? -projectileSpeed : projectileSpeed;
		agent = new AgentTestImp(id, env, position, badLevel, team, health, damage, badProjectileSpeed, baseMovementSpeed);
		Assert.assertTrue(0 == agent.getProjectileSpeed());
		
		// TEST CASE: Negative base movement speed
		int badBaseMovementSpeed = (baseMovementSpeed >= 0) ? -baseMovementSpeed : baseMovementSpeed;
		agent = new AgentTestImp(id, env, position, badLevel, team, health, damage, projectileSpeed, badBaseMovementSpeed);
		Assert.assertTrue(0 == agent.getBaseMovementSpeed());
	}
	
	@Test
	public void test_setAcceleration() {
		Random random = new Random();
		Agent.AgentTester.generateTestInstance();
		
		// TEST CASE: Non-null value
		Vector2D vector = new Vector2D(random.nextDouble(), random.nextDouble());
		Agent.AgentTester.call_setAcceleration(vector);
		Assert.assertTrue(vector.equals(Agent.AgentTester.getTestInstance().getAcceleration()));
		
		// TEST CASE: Null value
		Agent.AgentTester.call_setAcceleration(null);
		Assert.assertTrue(null == Agent.AgentTester.getTestInstance().getAcceleration());
	}
	
	@Test
	public void test_setFiringVector() {
		Random random = new Random();
		Agent.AgentTester.generateTestInstance();
		
		// TEST CASE: Non-null value
		Vector2D vector = new Vector2D(random.nextDouble(), random.nextDouble());
		Agent.AgentTester.call_setFiringVector(vector);
		Assert.assertTrue(vector.equals(Agent.AgentTester.getTestInstance().getFiringVector()));
		
		// TEST CASE: Null value
		Agent.AgentTester.call_setFiringVector(null);
		Assert.assertTrue(null == Agent.AgentTester.getTestInstance().getFiringVector());
	}
	
	@Test
	public void test_setProjectileType() {
		Agent.AgentTester.generateTestInstance();
		
		// TEST CASE: Default value
		Assert.assertTrue(ProjectileFactory.Type.NONE == Agent.AgentTester.getTestInstance().getProjectileType());
		
		// TEST CASE: Each possible enum value
		for (ProjectileFactory.Type type : ProjectileFactory.Type.values()) {
			Agent.AgentTester.call_setProjectileType(type);
			Assert.assertTrue(type == Agent.AgentTester.getTestInstance().getProjectileType());
		}
		
		// TEST CASE: Set to null
		Agent.AgentTester.call_setProjectileType(null);
		Assert.assertTrue(ProjectileFactory.Type.NONE == Agent.AgentTester.getTestInstance().getProjectileType());
	}
	
	@Test
	public void test_setBaseMovementSpeed() {
		Random random = new Random();
		Agent.AgentTester.generateTestInstance();
		int amount;
		
		// TEST CASE: Value > 0
		amount = random.nextInt(100) + 1;
		Agent.AgentTester.getTestInstance().setBaseMovementSpeed(amount);
		Assert.assertTrue(amount == Agent.AgentTester.getTestInstance().getBaseMovementSpeed());
		
		// TEST CASE: Value = 0
		amount = 0;
		Agent.AgentTester.getTestInstance().setBaseMovementSpeed(amount);
		Assert.assertTrue(amount == Agent.AgentTester.getTestInstance().getBaseMovementSpeed());
		
		// TEST CASE: Value < 0
		amount = -1;
		Agent.AgentTester.getTestInstance().setBaseMovementSpeed(amount);
		Assert.assertTrue(0 == Agent.AgentTester.getTestInstance().getBaseMovementSpeed());
	}
	
	@Test
	public void test_setMovementSpeedFactor() {
		Random random = new Random();
		Agent.AgentTester.generateTestInstance();
		double amount;
		
		// TEST CASE: Value > 0
		amount = random.nextDouble() + 1;
		Agent.AgentTester.getTestInstance().setMovementSpeedFactor(amount);
		Assert.assertTrue(amount == Agent.AgentTester.getTestInstance().getMovementSpeedFactor());
		
		// TEST CASE: Value = 0
		amount = 0;
		Agent.AgentTester.getTestInstance().setMovementSpeedFactor(amount);
		Assert.assertTrue(amount == Agent.AgentTester.getTestInstance().getMovementSpeedFactor());
		
		// TEST CASE: Value < 0
		amount = -1;
		Agent.AgentTester.getTestInstance().setMovementSpeedFactor(amount);
		Assert.assertTrue(0 == Agent.AgentTester.getTestInstance().getMovementSpeedFactor());
	}
	
	@Test
	public void test_update() {
		
	}
	
	@Test
	public void test_adjustHealth() {
		Agent.AgentTester.generateTestInstance();
		int originalHealth = Agent.AgentTester.getTestInstance().getHealth();
		int halfHealth = Agent.AgentTester.getTestInstance().getHealth() / 2;
		
		// TEST CASE: Subtract health, result > 0
		((AgentTestImp) Agent.AgentTester.getTestInstance()).setDespawnFlag(false);
		Assert.assertFalse(((AgentTestImp) Agent.AgentTester.getTestInstance()).getDespawnFlag());
		Agent.AgentTester.getTestInstance().adjustHealth(-halfHealth);
		Assert.assertTrue(originalHealth - halfHealth == Agent.AgentTester.getTestInstance().getHealth());
		Assert.assertFalse(((AgentTestImp) Agent.AgentTester.getTestInstance()).getDespawnFlag());
		
		// TEST CASE: Add health, result within max health
		((AgentTestImp) Agent.AgentTester.getTestInstance()).setDespawnFlag(false);
		Assert.assertFalse(((AgentTestImp) Agent.AgentTester.getTestInstance()).getDespawnFlag());
		Agent.AgentTester.getTestInstance().adjustHealth(halfHealth / 2);
		Assert.assertTrue((originalHealth - halfHealth) + (halfHealth / 2) == Agent.AgentTester.getTestInstance().getHealth());
		Assert.assertFalse(((AgentTestImp) Agent.AgentTester.getTestInstance()).getDespawnFlag());
		
		// TEST CASE: Add health, result above max health
		((AgentTestImp) Agent.AgentTester.getTestInstance()).setDespawnFlag(false);
		Assert.assertFalse(((AgentTestImp) Agent.AgentTester.getTestInstance()).getDespawnFlag());
		Agent.AgentTester.getTestInstance().adjustHealth(halfHealth);
		Assert.assertTrue(originalHealth == Agent.AgentTester.getTestInstance().getHealth());
		Assert.assertFalse(((AgentTestImp) Agent.AgentTester.getTestInstance()).getDespawnFlag());
		
		// TEST CASE: Subtract health, result <= 0
		((AgentTestImp) Agent.AgentTester.getTestInstance()).setDespawnFlag(false);
		Assert.assertFalse(((AgentTestImp) Agent.AgentTester.getTestInstance()).getDespawnFlag());
		Agent.AgentTester.getTestInstance().adjustHealth(-originalHealth * 2);
		Assert.assertTrue(0 == Agent.AgentTester.getTestInstance().getHealth());
		Assert.assertTrue(((AgentTestImp) Agent.AgentTester.getTestInstance()).getDespawnFlag());
	}
	
	@Test
	public void test_move() {
		
	}
	
	@Test
	public void test_fireProjectile() {
		
	}
}
