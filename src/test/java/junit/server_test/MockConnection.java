package test.java.junit.server_test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import main.java.web.GameSocket;

/** Represents a mock client that connects to the web server. */
public class MockConnection {
	private GameSocket socket = new GameSocket(false);
	private List<String> output = new LinkedList<>();
	private Session session;
	private boolean connected = true;
	
	public MockConnection(String name) {
		
		session = getMockSession(output);
		socket.onWebSocketConnect(session);
		
		String nameJSON = " {\"name\":\"" + name + "\"}";
		socket.onWebSocketText(nameJSON);
	}
	
	public List<String> getOutput() {
		return output;
	}
	
	public Session getSession() {
		return session;
	}
	
	protected GameSocket getSocket() {
		return socket;
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
	
	public void close() {
		close(0, null);
	}
	
	public void sendMessage(String message) {
		socket.onWebSocketText(message);
	}
	
	public void close(int reason, String message) {
		socket.onWebSocketClose(reason, message);
		connected = false;
	}
	
	public void clearOutput() {
		synchronized(output) {
			output.clear();
		}
	}
	
	private Session getMockSession(List<String> output) {
		Session session = Mockito.mock(Session.class);
		InetSocketAddress mockAddress = new InetSocketAddress(0);
		Mockito.when(session.getRemoteAddress()).thenReturn(mockAddress);
		
		Mockito.when(session.isOpen()).thenReturn(connected);
		
		Mockito.when(session.getRemote()).thenAnswer(new Answer<RemoteEndpoint>() {
			public RemoteEndpoint answer(InvocationOnMock invocation) throws Throwable {
				return getMockEndpoint(output);
			}
		});
		
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				getSocket().onWebSocketClose(0, null);
				connected = false;
				return null;
			}
		}).when(session).close();

		return session;
	}

	private static RemoteEndpoint getMockEndpoint(List<String> output) throws IOException {
		RemoteEndpoint endpoint = Mockito.mock(RemoteEndpoint.class);
		
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				for (Object arg : invocation.getArguments()) {
					if (!arg.toString().startsWith("{")) {
						synchronized(output) {
							output.add(arg.toString());
						}
					}
				}
				return null;
			}
		}).when(endpoint).sendString(Mockito.anyString());
		
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				for (Object arg : invocation.getArguments()) {
					if (!arg.toString().startsWith("{")) {
						synchronized(output) {
							output.add(arg.toString());
						}
					}
				}
				return null;
			}
		}).when(endpoint).sendStringByFuture(Mockito.anyString());
		
		return endpoint;
	}
}

