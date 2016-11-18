package test.java.junit.projectile;

import java.awt.Point;
import java.util.Random;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import main.java.agent.Agent;
import main.java.agent.test.AgentTestImp;
import main.java.agent.test.EnvironmentMock;
import main.java.environment.Environment;
import main.java.misc.Vector2D;
import main.java.projectile.Projectile;
import main.java.projectile.ProjectileFactory;
import main.java.projectile.test.ProjectileTestImp;

public class ProjectileTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		return;
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		return;
	}
	
	@Test @Ignore
	public void test_constructor() {
		Random random = new Random();
		
		Environment env = new EnvironmentMock(random.nextDouble() * 100);
		Agent.AgentTester.generateTestInstance();
		Agent owner = Agent.AgentTester.getTestInstance();
		Point position = new Point();
		position.setLocation(random.nextDouble(), random.nextDouble());
		Vector2D velocity = new Vector2D(random.nextDouble() * 5, random.nextDouble() * 5);
		
		// TEST CASE: Non-null values, assert values initialized properly
		Projectile projectile = new ProjectileTestImp(env, owner, position, velocity);
		Assert.assertTrue(owner.equals(projectile.getOwner()));
		Assert.assertTrue(position.equals(projectile.getPosition()));
		Assert.assertTrue(velocity.equals(projectile.getVelocity()));
		
		// TEST CASE: Null environment
		try {
			projectile = new ProjectileTestImp(null, owner, position, velocity);
			Assert.fail();
		} catch (Exception e) {}
		
		// TEST CASE: Null owner
		try {
			projectile = new ProjectileTestImp(env, null, position, velocity);
			Assert.fail();
		} catch (Exception e) {}
		
		// TEST CASE: Null position
		try {
			projectile = new ProjectileTestImp(env, owner, null, velocity);
			Assert.fail();
		} catch (Exception e) {}
		
		// TEST CASE: Null velocity
		try {
			projectile = new ProjectileTestImp(env, owner, position, null);
			Assert.fail();
		} catch (Exception e) {}
	}
	
	@Test
	public void test_update() {
		
	}
}
