package main.java.web;

import java.awt.Point;
import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import main.java.agent.Agent;
import main.java.agent.NPCAgent;
import main.java.agent.PlayerAgent;
import main.java.environment.Environment;
import main.java.projectile.Projectile;

public class GameThread extends Thread {
	private static final int FRAME_RATE = 20;
	
	private WebServer server;
	private Environment environment;
	private boolean gameplayOccurring = true;
	
	private Gson gson;
	
	public GameThread(WebServer server, Environment environment) {
		this.server = server;
		this.environment = environment;
		
		gson = new GsonBuilder()
				.registerTypeAdapter(Agent.class, new AgentSerializer())
				.registerTypeAdapter(PlayerAgent.class, new AgentSerializer())
				.registerTypeAdapter(NPCAgent.class, new AgentSerializer())
				.create();
	}
	
	
	@Override
	public void run() {
		while (gameplayOccurring) {
			Collection<PlayerAgent> playerAgents = environment.getActivePlayerAgents().values();
			Collection<NPCAgent> npcAgents = environment.getActiveNPCAgents();
			Collection<Projectile> projectiles = environment.getActiveProjectiles();
			
			GameState state = new GameState(playerAgents, npcAgents, projectiles);
			server.broadcast(gson.toJson(state));
			
			try {
				sleep(1000 / FRAME_RATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isGameplayOccurring() {
		return gameplayOccurring;
	}

	public void setGameplayOccurring(boolean gameplayOccurring) {
		this.gameplayOccurring = gameplayOccurring;
	}

	public static class AgentSerializer implements JsonSerializer<Agent> {
		@Override
		public JsonElement serialize(Agent src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject element = new JsonObject();
			element.add("id", new JsonPrimitive(src.getID().toString()));
			element.add("health", new JsonPrimitive(src.getHealth()));
			Point point = src.getPosition();
			element.add("x", new JsonPrimitive(point.getX()));
			element.add("y", new JsonPrimitive(point.getY()));
			return element;
		}
	}
}
