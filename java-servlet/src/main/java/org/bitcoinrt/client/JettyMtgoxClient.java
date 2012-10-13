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

package org.bitcoinrt.client;

import java.io.IOException;
import java.net.URI;

import org.bitcoinrt.server.Broadcaster;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

/**
 * Jetty WebSocket client implementation.
 *
 * @see http://webtide.intalio.com/2011/09/jetty-websocket-client-api-updated/
 * @see http://download.eclipse.org/jetty/stable-7/apidocs/org/eclipse/jetty/websocket/WebSocketClient.html
 */
public class JettyMtgoxClient extends AbstractMtgoxClient {

	private final WebSocketClientFactory factory;


	public JettyMtgoxClient(Broadcaster broadcaster) {
		super(broadcaster);
		this.factory = new WebSocketClientFactory();
	}

	public void start() {
		try {
			this.factory.start();
			WebSocketClient client = this.factory.newWebSocketClient();
			client.open(new URI(MTGOX_URL), new MtgoxWebSocket());
		}
		catch (Exception ex) {
			logger.error("Failed to start WebSocketClientFactory", ex);
		}
	}

	public void stop() {
		try {
			if (this.factory != null) {
				this.factory.stop();
			}
		}
		catch (Exception ex) {
			logger.error("Failed to stop WebSocketClientFactory", ex);
		}
	}

	private class MtgoxWebSocket implements WebSocket.OnTextMessage {

		@Override
		public void onOpen(Connection conn) {
			logger.debug("Connected to {}", MTGOX_URL);
			logger.debug("Unsubscribing...");
			try {
				conn.sendMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}");
				conn.sendMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}");
			}
			catch (IOException ex) {
				logger.error("Unsubscribe failed", ex);
			}
			logger.debug("Waiting for messages...");
		}

		@Override
		public void onMessage(String message) {
			// Delegate to the parent
			onMessage(message);
		}

		@Override
		public void onClose(int closeCode, String message) {
			String log = "Disconnected from {} with closeCode={} and message={}";
			logger.debug(log, new Object[] {MTGOX_URL, closeCode, message});
		}
	}
}
