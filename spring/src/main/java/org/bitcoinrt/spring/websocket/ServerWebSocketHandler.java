package org.bitcoinrt.spring.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.TextWebSocketHandlerAdapter;

/**
 * Echo messages by implementing a Spring {@link WebSocketHandler} abstraction.
 */
public class ServerWebSocketHandler extends TextWebSocketHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(ServerWebSocketHandler.class);

	@Autowired
	private WebSocketSessionsStore webSocketSessionsStore;

	//@Autowired
	public ServerWebSocketHandler() {
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		webSocketSessionsStore.put(session.getId(), session);
		logger.debug("Opened new session (Id: {}, Total subscribers: {}) in instance {}",
			new Object[] {session.getId(), webSocketSessionsStore.size(), this});
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		logger.debug("Server Disconnected from {}", session.getId());
		webSocketSessionsStore.remove(session.getId());
		super.afterConnectionClosed(session, status);

	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		logger.debug("Reveiving Text Message from client {}", session.getId());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.close(CloseStatus.SERVER_ERROR);
		logger.error("Error: {}", exception);
		webSocketSessionsStore.remove(session.getId());
	}

}
