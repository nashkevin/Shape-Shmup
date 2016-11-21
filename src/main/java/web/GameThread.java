package main.java.web;

import java.awt.Point;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

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


public class GameThread {
	
	private static final int FRAME_RATE = 30;

	private WebServer server;
	private Environment environment;
	private boolean gameplayOccurring = true;

	private Gson gson;

	private Timer timer = new Timer("GameThread Timer");

	public GameThread(WebServer server, Environment environment) {
		this.server = server;
		this.environment = environment;

		gson = new GsonBuilder()
				.registerTypeAdapter(Agent.class, new AgentSerializer())
				.registerTypeAdapter(PlayerAgent.class, new PlayerAgentSerializer())
				.registerTypeHierarchyAdapter(NPCAgent.class, new AgentSerializer())
				.registerTypeAdapter(Projectile.class, new ProjectileSerializer())
				.create();

		// call update at FRAME_RATE
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (gameplayOccurring) {
					broadcastGameState();
				}
			}
		}, 0, 1000 / FRAME_RATE);
	}

	public void broadcastGameState() {
		// Clone all collections to avoid concurrent modification woes.
		Collection<PlayerAgent> playerAgents = new ArrayList<>(environment.getActivePlayerAgents());
		Collection<NPCAgent> npcAgents = new ArrayList<>(environment.getActiveNPCAgents());
		Collection<Projectile> projectiles = new ArrayList<>(environment.getActiveProjectiles());

		GameState state = new GameState(playerAgents, npcAgents, projectiles);
		
		server.broadcast(gson.toJson(state));
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
			element.add("angle", new JsonPrimitive(src.getAngle()));
			return element;
		}
	}
	
	public static class PlayerAgentSerializer implements JsonSerializer<PlayerAgent> {
		@Override
		public JsonElement serialize(PlayerAgent src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject element = new JsonObject();
			element.add("id", new JsonPrimitive(src.getID().toString()));
			element.add("name", new JsonPrimitive(src.getName()));
			element.add("health", new JsonPrimitive(src.getHealth()));
			Point point = src.getPosition();
			element.add("x", new JsonPrimitive(point.getX()));
			element.add("y", new JsonPrimitive(point.getY()));
			element.add("angle", new JsonPrimitive(src.getAngle()));
			return element;
		}
	}
	
	public static class ProjectileSerializer implements JsonSerializer<Projectile> {
		@Override
		public JsonElement serialize(Projectile src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject element = new JsonObject();
			element.add("id", new JsonPrimitive(src.getID().toString()));
			Point point = src.getPosition();
			element.add("x", new JsonPrimitive(point.getX()));
			element.add("y", new JsonPrimitive(point.getY()));
			return element;
		}
	}
}
