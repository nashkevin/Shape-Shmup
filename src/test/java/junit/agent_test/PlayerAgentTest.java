package test.java.junit.agent_test;

import main.java.agent.Agent;
import main.java.agent.PlayerAgent;
import main.java.environment.Environment;
import main.java.misc.Vector2D;
import main.java.web.ClientInput;

import java.awt.geom.Point2D;
import java.lang.Math;

import org.junit.Test;
import org.junit.Assert;

import java.lang.reflect.*;

public class PlayerAgentTest {
	
	private static final double MARGIN = 0.001;

	@Test
	public void testMove() {
		
		//Test 0: Initial magnitude of 0
		Environment env = new Environment(false);
		PlayerAgent testPlayer = new PlayerAgent(env,new Point2D.Double(0, 0) , "Player");
		
		testPlayer.getVelocity().setMagnitude(0);
		testPlayer.getVelocity().setAngle(0);
		System.out.println(testPlayer.getVelocity().getMagnitude() + testPlayer.getVelocity().getAngle());
		
		//reflection to invoke private method
		 try {
			Method move = PlayerAgent.class.getDeclaredMethod("move");
			move.setAccessible(true);
			move.invoke(testPlayer);
			move.setAccessible(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 Assert.assertEquals(0, testPlayer.getVelocity().getMagnitude(), MARGIN);
		 Assert.assertEquals(0, testPlayer.getVelocity().getAngle(), MARGIN);
		 Assert.assertEquals(new Point2D.Double(0, 0), testPlayer.getPosition());
		 
		 
		 //Test nominal: Initial magnitude 1
		 
		testPlayer.getVelocity().setMagnitude(1);
		testPlayer.getVelocity().setAngle(0);
		 
		 try {
				Method move = PlayerAgent.class.getDeclaredMethod("move");
				move.setAccessible(true);
				move.invoke(testPlayer);
				move.setAccessible(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 Assert.assertEquals(1.0 - 0.3/3, testPlayer.getVelocity().getMagnitude(), MARGIN); //0.3 is default margin
			 Assert.assertEquals(0, testPlayer.getVelocity().getAngle(), MARGIN);
	}
	
	

}
