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

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.deploy.Verticle;

public class BitcoinVerticle extends Verticle {

	protected static final String MTGOX_TRADES_CHANNEL = "dbf1dee9-4f2e-4a08-8cb7-748919a71b21";
	protected static final String MTGOX_TICKER_CHANNEL = "d5f06780-30a8-4a48-a2f8-7ed181b4a13f";
	protected static final String MTGOX_DEPTH_CHANNEL = "24e67e0d-1cad-4cc0-9e7a-f8523ef460fe";

	public static void main(String[] args) {
		JsonObject o = new JsonObject("{\"channel\":\"dbf1dee9-4f2e-4a08-8cb7-748919a71b21\",\"op\":\"private\",\"origin\":\"broadcast\",\"private\":\"trade\",\"trade\":{\"amount\":5,\"amount_int\":\"500000000\",\"date\":1343180077,\"item\":\"BTC\",\"price\":8.57619,\"price_currency\":\"USD\",\"price_int\":\"857619\",\"primary\":\"Y\",\"properties\":\"limit\",\"tid\":\"1343180077270684\",\"trade_type\":\"bid\",\"type\":\"trade\"}}");
		System.out.println(o.getObject("trade").toString());
	}

	@Override
	public void start() throws Exception {

		// HTTP server
		RouteMatcher routeMatcher = new RouteMatcher();
		routeMatcher.get("/", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				request.response.sendFile("webroot/index.html");
			}
		});
		routeMatcher.getWithRegEx("(\\/.*\\.(js|css|png))", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				request.response.sendFile("webroot" + request.params().get("param0"));
			}
		});
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(routeMatcher);


		// SockJS server
		JsonArray permitted = new JsonArray();
	    permitted.add(new JsonObject()); // Let everything through
	    SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);
	    sockJSServer.bridge(new JsonObject().putString("prefix", "/bitcoin"), permitted, permitted);

		httpServer.listen(8080);


		// MTGOX client
		final String host = "websocket.mtgox.com";
		HttpClient client = vertx.createHttpClient().setPort(80).setHost(host);
		client.connectWebsocket("/mtgox", new Handler<WebSocket>() {
		    public void handle(final WebSocket ws) {
				container.getLogger().info("Connected to: " + host);
				ws.writeBuffer(new Buffer("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}"));
				ws.writeBuffer(new Buffer("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}"));
				ws.dataHandler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer buffer) {
						container.getLogger().info("Message received: " + buffer);
						JsonObject trade = new JsonObject(buffer.toString()).getObject("trade");
						container.getLogger().info("Trade details: " + trade);
						vertx.eventBus().publish("bitcoin.trades", trade);
					}
				});
				ws.endHandler(new Handler<Void>() {
					@Override
					public void handle(Void event) {
						container.getLogger().info("Lost connection to: " + host);
					}
				});
				container.getLogger().info("Waiting for messages...");
		    }
		});
	}

}
