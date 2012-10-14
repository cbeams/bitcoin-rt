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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.DefaultWebSocketListener;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

/**
 * Sonatype Async HTTP Client implementation.
 *
 * @see https://github.com/sonatype/async-http-client
 */
public class AsyncHttpClientMtgoxClient extends AbstractMtgoxClient {

	private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();


	@PostConstruct
	public void start() {
		WebSocketUpgradeHandler.Builder builder = new WebSocketUpgradeHandler.Builder();
		builder.addWebSocketListener(new MtgoxWebSocketListener());
		WebSocketUpgradeHandler handler = builder.build();
		try {
			this.asyncHttpClient.prepareGet(MTGOX_URL).execute(handler).get();
		}
		catch (Exception ex) {
			logger.error("Failed to execute WebSocketUpgradeHandler", ex);
		}
	}

	@PreDestroy
	public void stop() {
		if (this.asyncHttpClient != null) {
			this.asyncHttpClient.close();
		}
	}

	private class MtgoxWebSocketListener extends DefaultWebSocketListener {

		@Override
		public void onOpen(WebSocket ws) {
			logger.debug("Connected to {}", MTGOX_URL);
			logger.debug("Unsubscribing...");
			ws.sendTextMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}");
			ws.sendTextMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}");
			logger.debug("Waiting for messages...");
		}

		@Override
		public void onMessage(String message) {
			AsyncHttpClientMtgoxClient.this.onMessage(message);
		}

		@Override
		public void onClose(WebSocket ws) {
			logger.debug("Disconnected from {}", MTGOX_URL);
		}

		@Override
		public void onError(Throwable t) {
			logger.debug("Error from {}: {}", MTGOX_URL, t.getMessage());
		}
	}

}
