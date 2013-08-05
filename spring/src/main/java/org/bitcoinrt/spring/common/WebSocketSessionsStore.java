/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.bitcoinrt.spring.common;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;


/**
 * Holder for WebSocket Sessions.
 *
 * @author Gunnar Hillert
 *
 */
public class WebSocketSessionsStore {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionsStore.class);

	private volatile Map<String, WebSocketSession> sessiondata = new ConcurrentHashMap<String, WebSocketSession>();

	public void put(String key, WebSocketSession value) {
		this.sessiondata.put(key, value);
	}

	public WebSocketSession get(String key) {
		return this.sessiondata.get(key);
	}

	public void remove(String key) {
		this.sessiondata.remove(key);
	}

	public int size() {
		return this.sessiondata.size();
	}

	public synchronized void sendToAll(WebSocketMessage<?> message) {

		if (sessiondata.size() == 0) {
			logger.info(this + ";; No subscribers to broadcast to.");
			return;
		}

		for (WebSocketSession session : sessiondata.values()) {
			try {
				session.sendMessage(message);
				logger.info("Sent message to client '{}'", session.getId());
			}
			catch (IOException e) {
				logger.error("Error broadcasting to client '{}'", session.getId(), e);
			}
		}
	}
}
