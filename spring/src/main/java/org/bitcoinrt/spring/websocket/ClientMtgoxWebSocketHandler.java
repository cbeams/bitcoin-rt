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

package org.bitcoinrt.spring.websocket;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.TextWebSocketHandlerAdapter;

import com.jayway.jsonpath.JsonPath;

/**
 * Spring 4.0 WebSocket Handler
 *
 * @author Gunnar Hillert
 */
public class ClientMtgoxWebSocketHandler extends TextWebSocketHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ClientMtgoxWebSocketHandler.class);

	public static final String MTGOX_URL = "ws://websocket.mtgox.com:80/mtgox";
	protected static final String MTGOX_TRADES_CHANNEL = "dbf1dee9-4f2e-4a08-8cb7-748919a71b21";
	protected static final String MTGOX_TICKER_CHANNEL = "d5f06780-30a8-4a48-a2f8-7ed181b4a13f";
	protected static final String MTGOX_DEPTH_CHANNEL = "24e67e0d-1cad-4cc0-9e7a-f8523ef460fe";

	@Autowired
	WebSocketSessionsStore serverWebSocketSessionsStore;


	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		final TextMessage tickerMessage = new TextMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_TICKER_CHANNEL + "\"}");
		session.sendMessage(tickerMessage);

		final TextMessage message = new TextMessage("{\"op\":\"unsubscribe\",\"channel\":\"" + MTGOX_DEPTH_CHANNEL + "\"}");
		session.sendMessage(message);

		logger.debug("WebSocket connection to {} established, waiting for messages...", MTGOX_URL);

		session.sendMessage(message);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		logger.debug("New message: " + message);

		String channel = JsonPath.compile("$.channel").read(message.getPayload());

		String primary = null;
		try {
			primary = JsonPath.compile("$.trade.primary").read(message.getPayload());
		}
		catch (Exception ex) {
			// ignore
		}

		if (MTGOX_TRADES_CHANNEL.equals(channel) && "Y".equals(primary)) {
			JSONObject trade = JsonPath.compile("$.trade").read(message.getPayload());
			logger.info("Broadcasting Trade: {}", trade);
			serverWebSocketSessionsStore.sendToAll(new TextMessage(message.getPayload()));
		}
		else {
			// ignore any non-trade messages that might slip in before our
			// op:unsubscribes sent above have been handled; also ignore any
			// 'non-primary' trades. see
			// https://en.bitcoin.it/wiki/MtGox/API/HTTP/v1#Multi_currency_trades
			logger.debug("Ignoring Message: {}", message);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		logger.debug("Disconnected from {}", MTGOX_URL);
		super.afterConnectionClosed(session, status);
	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		logger.error("Error from {}: {}", MTGOX_URL, exception.getMessage());
		super.handleTransportError(session, exception);
	}

}
