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
package org.bitcoin.vertx;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * Start a WebSocket connection to "websocket.mtgox.com" and publish received trade
 * messages to the vert.x event bus at address "bitcoin.trades".
 *
 */
public class MtgoxClientVerticle extends Verticle {

	private static final String MTGOX_HOST_NAME = "websocket.mtgox.com";
	private static final String MTGOX_TRADES_CHANNEL = "dbf1dee9-4f2e-4a08-8cb7-748919a71b21";
	private static final String MTGOX_TICKER_CHANNEL = "d5f06780-30a8-4a48-a2f8-7ed181b4a13f";
	private static final String MTGOX_DEPTH_CHANNEL = "24e67e0d-1cad-4cc0-9e7a-f8523ef460fe";

	@Override
	public void start() throws Exception {

		HttpClient client = vertx.createHttpClient().setPort(80).setHost(MTGOX_HOST_NAME);

		client.connectWebsocket("/mtgox", new Handler<WebSocket>() {
			@Override
		    public void handle(final WebSocket ws) {
				container.getLogger().info("Connected to: " + MTGOX_HOST_NAME);

				ws.writeBuffer(new Buffer("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}"));
				ws.writeBuffer(new Buffer("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}"));

				ws.dataHandler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer buffer) {
						container.getLogger().info("Message received: " + buffer);

						JsonObject message = new JsonObject(buffer.toString());
						JsonObject trade = message.getObject("trade");
						String channel = message.getString("channel");
						String primary = trade.getString("primary");

						if (MTGOX_TRADES_CHANNEL.equals(channel) && "Y".equals(primary)) {
							vertx.eventBus().publish("bitcoin.trades", trade);
							container.getLogger().info("Published trade: " + trade);
						}
						else {
				            // Ignore any non-trade messages that might slip in before our
				            // op:unsubscribes sent above have been handled; also ignore any
				            // 'non-primary' trades. see
				            // https://en.bitcoin.it/wiki/MtGox/API/HTTP/v1#Multi_currency_trades
							container.getLogger().info("Message ignored");
						}
					}
				});

				ws.endHandler(new Handler<Void>() {
					@Override
					public void handle(Void event) {
						container.getLogger().info("Lost connection to: " + MTGOX_HOST_NAME);
					}
				});

				container.getLogger().info("Waiting for messages...");
		    }
		});
	}

}
