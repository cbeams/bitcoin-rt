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

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

/**
 * Jetty WebSocket client implementation
 *
 * @see http://webtide.intalio.com/2011/09/jetty-websocket-client-api-updated/
 * @see http://download.eclipse.org/jetty/stable-7/apidocs/org/eclipse/jetty/websocket/WebSocketClient.html
 */
public class JettyBitcoinClient extends AbstractBitcoinClient {

	private final WebSocketClient webSocketClient;

	public JettyBitcoinClient() {
		this.webSocketClient = createWebSocketClient();
	}

	private WebSocketClient createWebSocketClient() {
		WebSocketClientFactory factory = new WebSocketClientFactory();
		try {
			factory.start();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		return factory.newWebSocketClient();
	}

	@Override
	public void start() throws Exception {
		this.webSocketClient.open(new URI(MTGOX_URL), new MtgoxWebSocket()).get();
	}

	private class MtgoxWebSocket implements WebSocket.OnTextMessage {

		@Override
		public void onOpen(Connection connection) {
			logger.debug("Connected to {}", MTGOX_URL);
			logger.debug("Unsubscribing...");
			try {
				connection.sendMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}");
				connection.sendMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}");
			}
			catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
			logger.debug("Waiting for messages...");
		}

		@Override
		public void onMessage(String message) {
			JettyBitcoinClient.this.onMessage(message);
		}

		@Override
		public void onClose(int closeCode, String message) {
			logger.debug("Disconnected from {} with closeCode={} and message={}",
					new Object[] {MTGOX_URL, closeCode, message});
		}
	}

}
