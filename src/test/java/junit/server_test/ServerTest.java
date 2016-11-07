package test.java.junit.server_test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
	public void testConnectionOpen() {
		LinkedList<String> output = new LinkedList<String>();
		Session session = getMockSession("test", output);
		
		server.onOpen(session);
	}
	
	private Session getMockSession(String id, List<String> output) {
		Session session = Mockito.mock(Session.class);
		Mockito.when(session.getId()).thenReturn(id);
		
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