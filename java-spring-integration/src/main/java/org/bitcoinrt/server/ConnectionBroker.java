/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.integration.Message;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.support.MessageBuilder;

/**
 * Serves as a registry for connected clients.
 *
 * @author Gunnar Hillert
 * @since 1.0
 *
 */
public class ConnectionBroker {

	private final Map<String, AtomicInteger> clients = new ConcurrentHashMap<String, AtomicInteger>();

	public ConnectionBroker() {
		super();
	}

	public void subscribe(Message<?> message) {
		final String connectionId = message.getHeaders().get(IpHeaders.CONNECTION_ID, String.class);
		clients.put(connectionId, new AtomicInteger());
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

}
