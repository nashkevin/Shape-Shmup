package test.java.junit.server_test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import main.java.web.WebServer;

/** Represents a mock client that connects to the web server. */
public class MockConnection {
	private WebServer server;
	private List<String> output = new LinkedList<>();
	private Session session;
	
	public MockConnection(WebServer server, String name) {
		session = getMockSession(output);
		
		server.onOpen(session);
		this.server = server;
		
		String nameJSON = " {\"name\":\"" + name + "\"}";
		server.onMessage(nameJSON, session);
	}
	
	public List<String> getOutput() {
		return output;
	}
	
	public Session getSession() {
		return session;
	}
	
	protected WebServer getServer() {
		return server;
	}
	
	public boolean receivedMessage(String string) {
		synchronized(output) {
			for (String listItem : output) {
				if (listItem.contains(string)) {
					return true;
				}
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
		
		try {
			Mockito.doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					getServer().onClose(session);
					return null;
				}
			}).when(session).close(Mockito.any());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return session;
	}

	private static RemoteEndpoint.Basic getMockBasicEndpoint(List<String> output) throws IOException {
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
	private static RemoteEndpoint.Async getMockAsyncEndpoint(List<String> output) throws IOException {
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

