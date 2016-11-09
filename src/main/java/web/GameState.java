package main.java.web;

import java.util.Collection;

import main.java.agent.NPCAgent;
import main.java.agent.PlayerAgent;
import main.java.projectile.Projectile;

/**
 * POJO representing the state of the game, which is broadcast to each client.
 */
public class GameState {
	private Collection<PlayerAgent> playerAgents;
	private Collection<NPCAgent> npcAgents;
	private Collection<Projectile> projectiles;
	
	public GameState(Collection<PlayerAgent> playerAgents, Collection<NPCAgent> npcAgents, 
			Collection<Projectile> projectiles) {
		this.playerAgents = playerAgents;
		this.npcAgents = npcAgents;
		this.projectiles = projectiles;
	}
	
	public Collection<PlayerAgent> getPlayerAgents() {
		return playerAgents;
	}
	public void setPlayerAgents(Collection<PlayerAgent> playerAgents) {
		this.playerAgents = playerAgents;
	}
	public Collection<NPCAgent> getNpcAgents() {
		return npcAgents;
	}
	public void setNpcAgents(Collection<NPCAgent> npcAgents) {
		this.npcAgents = npcAgents;
	}
	public Collection<Projectile> getProjectiles() {
		return projectiles;
	}
	public void setProjectiles(Collection<Projectile> projectiles) {
		this.projectiles = projectiles;
	}
}
