package test.java.junit.server_test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import main.java.web.WebServer;

public class ServerTest {
	private static WebServer server;

	@BeforeClass
	public static void beforeClass() {
		server = new WebServer();
	}
	
	@Test
	public void testSimpleConnection() {
		List<String> output = new LinkedList<String>();
		Session session = getMockSession(output);
		
		server.onOpen(session);
		
		String name = " {\"name\":\"Test\"}";
		server.onMessage(name, session);
		
		server.onClose(session);
	}
	
	@Test
	public void testChatMessage() {
		// Instantiate two mock sessions.
		List<String> output1 = new LinkedList<String>();
		List<String> output2 = new LinkedList<String>();
		Session session1 = getMockSession(output1);
		Session session2 = getMockSession(output2);
		
		// Connect them to the server.
		server.onOpen(session1);
		server.onOpen(session2);
		
		// Name the first session.
		String name = " {\"name\":\"Test1\"}";
		server.onMessage(name, session1);
		
		// Send a chat message from the first.
		String message = " {\"message\":\"Message.\"}";
		server.onMessage(message, session1);

		// Verify that both clients receive the chat message.
		String receivedMessage = "<strong>Test1:</strong> Message.";
		Assert.assertTrue(output1.contains(receivedMessage));
		Assert.assertTrue(output2.contains(receivedMessage));
		
		server.onClose(session1);
		server.onClose(session2);
	}
	
	private Session getMockSession(List<String> output) {
		Session session = Mockito.mock(Session.class);
		String uuid = UUID.randomUUID().toString();
		Mockito.when(session.getId()).thenReturn(uuid);
		
		Mockito.when(session.isOpen()).thenReturn(true);
		
		Mockito.when(session.getBasicRemote()).thenAnswer(new Answer<RemoteEndpoint.Basic>() {
			public RemoteEndpoint.Basic answer(InvocationOnMock invocation) throws Throwable {
				return getMockEndpoint(output);
			}
		});

		return session;
	}
	
	private RemoteEndpoint.Basic getMockEndpoint(List<String> output) throws IOException {
		RemoteEndpoint.Basic endpoint = Mockito.mock(RemoteEndpoint.Basic.class);
		
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				for (Object arg : invocation.getArguments()) {
					output.add(arg.toString());
				}
				return null;
			}
		}).when(endpoint).sendText(Mockito.anyString());
		
		return endpoint;
	}
}