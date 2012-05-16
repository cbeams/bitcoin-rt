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

package org.bitcoinrt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.DefaultWebSocketListener;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class AsyncHttpClientBitcoinClient {

	private static final String MTGOX_URL = "ws://websocket.mtgox.com:80/mtgox";

	private Logger logger = LoggerFactory.getLogger(AsyncHttpClientBitcoinClient.class);

	private final Broadcaster broadcaster;

	private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public AsyncHttpClientBitcoinClient(Broadcaster broadcaster) {
		this.broadcaster = broadcaster;
	}

	public void connect() throws IOException {
		MtgoxWebSocketListener listener = new MtgoxWebSocketListener();
		WebSocketUpgradeHandler handler = new WebSocketUpgradeHandler.Builder().addWebSocketListener(listener).build();
		this.asyncHttpClient.prepareGet(MTGOX_URL).execute(handler);
	}

	private class MtgoxWebSocketListener extends DefaultWebSocketListener {

		private static final String MTGOX_TRADES_CHANNEL = "dbf1dee9-4f2e-4a08-8cb7-748919a71b21";
		private static final String MTGOX_TICKER_CHANNEL = "d5f06780-30a8-4a48-a2f8-7ed181b4a13f";
		private static final String MTGOX_DEPTH_CHANNEL = "24e67e0d-1cad-4cc0-9e7a-f8523ef460fe";

		@Override
		public void onOpen(WebSocket websocket) {
			logger.debug("Connected to " + MTGOX_URL);
			logger.debug("Waiting for messages...");
			websocket.sendTextMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}");
			websocket.sendTextMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}");
		}

		@Override
		public void onMessage(String message) {
			logger.debug("New message: " + message);

			String channel = JsonPath.compile("$.channel").read(message);
			String primary = JsonPath.compile("$.trade.primary").read(message);
			if (!MTGOX_TRADES_CHANNEL.equals(channel) || !"Y".equals(primary)) {
	            // ignore any non-trade messages that might slip in before our
	            // op:unsubscribes sent above have been handled; also ignore any
	            // 'non-primary' trades. see
	            // https://en.bitcoin.it/wiki/MtGox/API/HTTP/v1#Multi_currency_trades
				logger.debug("Skipping message: channel:" + channel + ", primary:" + primary);
				return;
			}

			Object trade = JsonPath.compile("$.trade").read(message);
			logger.debug("Sending trade: " + trade);
			try {
				AsyncHttpClientBitcoinClient.this.broadcaster.broadcast(trade.toString());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onClose(WebSocket websocket) {
			logger.debug("Disconnected from " + MTGOX_URL);
		}

		@Override
		public void onError(Throwable t) {
			logger.debug("Error from " + MTGOX_URL + ": " + t.getMessage());
		}
	}

}
