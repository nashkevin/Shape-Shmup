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
		String receivedMessage = "Message.";
		Assert.assertTrue(listContainsSubstring(output1, receivedMessage));
		Assert.assertTrue(listContainsSubstring(output2, receivedMessage));
		
		server.onClose(session1);
		server.onClose(session2);
	}
	
	private static boolean listContainsSubstring(List<String> list, String string) {
		for (String listItem : list) {
			if (listItem.contains(string)) {
				return true;
			}
		}
		return false;
	}
	
	private Session getMockSession(List<String> output) {
		Session session = Mockito.mock(Session.class);
		String uuid = UUID.randomUUID().toString();
		Mockito.when(session.getId()).thenReturn(uuid);
		
		Mockito.when(session.isOpen()).thenReturn(true);
		
		Mockito.when(session.getBasicRemote()).thenAnswer(new Answer<RemoteEndpoint.Basic>() {
			public RemoteEndpoint.Basic answer(InvocationOnMock invocation) throws Throwable {
				return getMockBasicEndpoint(output);
			}
		});
		
		Mockito.when(session.getAsyncRemote()).thenAnswer(new Answer<RemoteEndpoint.Async>() {
			public RemoteEndpoint.Async answer(InvocationOnMock invocation) throws Throwable {
				return getMockAsyncEndpoint(output);
			}
		});

		return session;
	}
	
	private RemoteEndpoint.Basic getMockBasicEndpoint(List<String> output) throws IOException {
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
	
	// RemoteEndpoint doesn't specify sendText(), so I need separate methods for basic and async...
	private RemoteEndpoint.Async getMockAsyncEndpoint(List<String> output) throws IOException {
		RemoteEndpoint.Async endpoint = Mockito.mock(RemoteEndpoint.Async.class);
		
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