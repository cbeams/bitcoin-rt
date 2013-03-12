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

package org.bitcoinrt.client;

import java.io.IOException;
import java.net.URI;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.springframework.integration.MessageChannel;

/**
 * Jetty 9 WebSocket client implementation.
 *
 * @see http://webtide.intalio.com/2012/10/jetty-9-updated-websocket-api/
 * @see http://download.eclipse.org/jetty/9.0.0.v20130308/apidocs/org/eclipse/jetty/websocket/client/WebSocketClient.html
 */
public class JettyMtgoxClient extends AbstractMtgoxClient {

	private final WebSocketClient webSocketClient;

	public JettyMtgoxClient(MessageChannel outputChannel) {
		super(outputChannel);
		this.webSocketClient = new WebSocketClient();
	}

	@Override
	public void start() {
		try {
			webSocketClient.setConnectTimeout(10000);
			webSocketClient.start();
			webSocketClient.connect(new MtgoxWebSocket(), new URI(MTGOX_URL));
		}
		catch (Exception ex) {
			logger.error("Failed to start WebSocketClientFactory", ex);
		}
	}

	@Override
	public void stop() {
		try {
			if (this.webSocketClient != null) {
				this.webSocketClient.stop();
			}
		}
		catch (Exception ex) {
			logger.error("Failed to stop WebSocketClientFactory", ex);
		}
	}

	private class MtgoxWebSocket implements WebSocketListener {

		@Override
		public void onWebSocketBinary(byte[] payload, int offset, int len) {
			throw new IllegalStateException("Not implemented.");
		}

		@Override
		public void onWebSocketClose(int statusCode, String reason) {
			String log = "Disconnected from {} with closeCode={} and message={}";
			logger.debug(log, new Object[] {MTGOX_URL, statusCode, reason});
		}

		@Override
		public void onWebSocketConnect(Session session) {
			logger.debug("Connected to {}", MTGOX_URL);
			logger.debug("Unsubscribing...");
			try {
				session.getRemote().sendString("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}");
				session.getRemote().sendString("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}");
			}
			catch (IOException ex) {
				logger.error("Unsubscribe failed", ex);
			}
			logger.debug("Waiting for messages...");
		}

		@Override
		public void onWebSocketError(Throwable cause) {
			throw new IllegalStateException(cause);
		}

		@Override
		public void onWebSocketText(String message) {
			// Delegate to the parent
			onMessage(message);
		}
	}
}
