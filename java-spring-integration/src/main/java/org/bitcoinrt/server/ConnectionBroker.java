/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinrt.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.integration.Message;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent.EventType;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent.TcpConnectionEventType;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.x.ip.websocket.WebSocketEvent.WebSocketEventType;

/**
 * Serves as a registry for connected clients.
 *
 * @author Gunnar Hillert
 * @since 1.0
 *
 */
public class ConnectionBroker implements ApplicationListener<TcpConnectionEvent>{

	private static final Logger logger = LoggerFactory.getLogger(ConnectionBroker.class);

	private final Map<String, AtomicInteger> clients = new ConcurrentHashMap<String, AtomicInteger>();

	public ConnectionBroker() {
		super();
	}

	public List<Message<?>> createBroadCastMessages(Message<?> message) {

		final List<Message<?>> messages = new ArrayList<Message<?>>();

		for (String connectionId : clients.keySet()) {
			messages.add(MessageBuilder.fromMessage(message).setHeader(IpHeaders.CONNECTION_ID, connectionId).build());
		}

		return messages;

	}

	/**
	 * Returns the number of currently connected clients.
	 */
	public int connectedClients() {
		return clients.size();
	}

	@Override
	public void onApplicationEvent(TcpConnectionEvent event) {

		final EventType eventType = event.getType();

		if (TcpConnectionEventType.OPEN.equals(eventType)) {

		}
		else if (TcpConnectionEventType.CLOSE.equals(eventType)) {
			logger.info("TCP Event: CLOSED - Removing client: {}", event.getConnectionId());
			clients.remove(event.getConnectionId());
		}
		else if (TcpConnectionEventType.EXCEPTION.equals(eventType)) {
			logger.warn("EXCEPTION Event - Removing client: {}", event.getConnectionId());
			clients.remove(event.getConnectionId());
		}
		else if (WebSocketEventType.HANDSHAKE_COMPLETE.equals(eventType)) {
			logger.info("HANDSHAKE_COMPLETE - Adding client: {}", event.getConnectionId());
			clients.put(event.getConnectionId(), new AtomicInteger());
		}
		else if (WebSocketEventType.WEBSOCKET_CLOSED.equals(eventType)) {
			logger.info("WEBSOCKET_CLOSED - Removing client: {}", event.getConnectionId());
			clients.remove(event.getConnectionId());
		}
		else {
			throw new IllegalArgumentException(String.format("EventType '%s' is not supported.", eventType));
		}
	}
}
