package main.java.agent.test;

import java.awt.Point;
import java.util.UUID;

import main.java.agent.PlayerAgent;
import main.java.environment.Environment;

public class PlayerAgentTestImp extends PlayerAgent {

	public PlayerAgentTestImp(UUID id, Environment env, Point position,
			int level, int team, int health, int damage, int projectileSpeed,
			int baseMovementSpeed) {
		super(id, env, position, level, team, health, damage, projectileSpeed,
				baseMovementSpeed);
		// TODO Auto-generated constructor stub
	}

}
