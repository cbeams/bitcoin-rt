package org.bitcoinrt.spring.websocket.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.SmartLifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.ConnectionManagerSupport;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.support.LoggingWebSocketHandlerDecorator;

/**
 * Workaround for https://jira.springsource.org/browse/SPR-10796
 *
 * @author Gunnar Hillert
 *
 */
public class CustomWebSocketConnectionManager extends ConnectionManagerSupport {

	private final WebSocketClient client;

	private final WebSocketHandler webSocketHandler;

	private WebSocketSession webSocketSession;

	private final List<String> protocols = new ArrayList<String>();

	private final boolean syncClientLifecycle;

	public CustomWebSocketConnectionManager(WebSocketClient client,
			WebSocketHandler webSocketHandler, String uriTemplate, Object... uriVariables) {

		super(uriTemplate, uriVariables);
		this.client = client;
		this.webSocketHandler = decorateWebSocketHandler(webSocketHandler);
		this.syncClientLifecycle = ((client instanceof SmartLifecycle) && !((SmartLifecycle) client).isRunning());
	}


	/**
	 * Decorate the WebSocketHandler provided to the class constructor.
	 *
	 * <p>By default {@link LoggingWebSocketHandlerDecorator} is added.
	 */
	protected WebSocketHandler decorateWebSocketHandler(WebSocketHandler handler) {
		return new LoggingWebSocketHandlerDecorator(handler);
	}

	public void setSupportedProtocols(List<String> protocols) {
		this.protocols.clear();
		if (!CollectionUtils.isEmpty(protocols)) {
			this.protocols.addAll(protocols);
		}
	}

	public List<String> getSupportedProtocols() {
		return this.protocols;
	}

	@Override
	public void startInternal() {
		if (this.syncClientLifecycle) {
			((SmartLifecycle) this.client).start();
		}
		super.startInternal();
	}

	@Override
	public void stopInternal() throws Exception {
		if (this.syncClientLifecycle) {
			((SmartLifecycle) this.client).stop();
		}
		super.stopInternal();
	}

	@Override
	protected void openConnection() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setSecWebSocketProtocol(this.protocols);
		headers.setOrigin("http://websocket.mtgox.com");
		this.webSocketSession = this.client.doHandshake(this.webSocketHandler, headers, getUri());
	}

	@Override
	protected void closeConnection() throws Exception {
		this.webSocketSession.close();
	}

	@Override
	protected boolean isConnected() {
		return ((this.webSocketSession != null) && (this.webSocketSession.isOpen()));
	}
}