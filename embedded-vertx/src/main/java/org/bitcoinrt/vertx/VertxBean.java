package org.bitcoinrt.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

public class VertxBean {

	private final Vertx vertx;
	private static final Logger LOGGER = LoggerFactory.getLogger(VertxBean.class);

	private static final String MTGOX_HOST_NAME = "websocket.mtgox.com";
	private static final String MTGOX_TRADES_CHANNEL = "dbf1dee9-4f2e-4a08-8cb7-748919a71b21";
	private static final String MTGOX_TICKER_CHANNEL = "d5f06780-30a8-4a48-a2f8-7ed181b4a13f";
	private static final String MTGOX_DEPTH_CHANNEL = "24e67e0d-1cad-4cc0-9e7a-f8523ef460fe";

	public VertxBean() {
		super();
		vertx = Vertx.newVertx();
		start();
		startClient();
	}

	public void start() {

		RouteMatcher routeMatcher = new RouteMatcher();

		// HTTP server
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(routeMatcher);

		// SockJS server
		JsonArray permitted = new JsonArray();
		permitted.add(new JsonObject()); // Let everything through
		SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);
		sockJSServer.bridge(new JsonObject().putString("prefix", "/bitcoin"), permitted, permitted);

		httpServer.listen(7777);
	}

	public void startClient() {

			HttpClient client = vertx.createHttpClient().setPort(80).setHost(MTGOX_HOST_NAME);

			client.connectWebsocket("/mtgox", new Handler<WebSocket>() {
				@Override
				public void handle(final WebSocket ws) {
					LOGGER.info("Connected to: " + MTGOX_HOST_NAME);

					ws.writeBuffer(new Buffer("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}"));
					ws.writeBuffer(new Buffer("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}"));

					ws.dataHandler(new Handler<Buffer>() {
						@Override
						public void handle(Buffer buffer) {
							LOGGER.info("Message received: " + buffer);

							JsonObject message = new JsonObject(buffer.toString());
							JsonObject trade = message.getObject("trade");
							String channel = message.getString("channel");
							String primary = trade.getString("primary");

							if (MTGOX_TRADES_CHANNEL.equals(channel) && "Y".equals(primary)) {
								vertx.eventBus().publish("bitcoin.trades", trade);
								LOGGER.info("Published trade: " + trade);
							}
							else {
								// Ignore any non-trade messages that might slip in before our
								// op:unsubscribes sent above have been handled; also ignore any
								// 'non-primary' trades. see
								// https://en.bitcoin.it/wiki/MtGox/API/HTTP/v1#Multi_currency_trades
								LOGGER.info("Message ignored");
							}
						}
					});

					ws.endHandler(new Handler<Void>() {
						@Override
						public void handle(Void event) {
							LOGGER.info("Lost connection to: " + MTGOX_HOST_NAME);
						}
					});

					LOGGER.info("Waiting for messages...");
				}
			});
	}

}
