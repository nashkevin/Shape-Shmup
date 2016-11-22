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
	
	private Collection<PlayerAgent> despawnedPlayerAgents;
	private Collection<NPCAgent> despawnedNPCAgents;
	private Collection<Projectile> despawnedProjectiles;

	public GameState(Collection<PlayerAgent> playerAgents, Collection<NPCAgent> npcAgents,
			Collection<Projectile> projectiles, Collection<PlayerAgent> despawnedPlayerAgents,
			Collection<NPCAgent> despawnedNPCAgents, Collection<Projectile> despawnedProjectiles) {
		this.playerAgents = playerAgents;
		this.npcAgents = npcAgents;
		this.projectiles = projectiles;
		this.despawnedPlayerAgents = despawnedPlayerAgents;
		this.despawnedNPCAgents = despawnedNPCAgents;
		this.despawnedProjectiles = despawnedProjectiles;
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
	public Collection<PlayerAgent> getDespawnedPlayerAgents() {
		return despawnedPlayerAgents;
	}
	public void setDespawnedPlayerAgents(Collection<PlayerAgent> despawnedPlayerAgents) {
		this.despawnedPlayerAgents = despawnedPlayerAgents;
	}
	public Collection<NPCAgent> getDespawnedNPCAgents() {
		return despawnedNPCAgents;
	}
	public void setDespawnedNPCAgents(Collection<NPCAgent> despawnedNPCAgents) {
		this.despawnedNPCAgents = despawnedNPCAgents;
	}
	public Collection<Projectile> getDespawnedProjectiles() {
		return despawnedProjectiles;
	}
	public void setDespawnedProjectiles(Collection<Projectile> despawnedProjectiles) {
		this.despawnedProjectiles = despawnedProjectiles;
	}
}
