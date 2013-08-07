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
package org.bitcoinrt.spring.config;

import org.bitcoinrt.spring.websocket.ClientMtgoxWebSocketHandler;
import org.bitcoinrt.spring.websocket.WebSocketSessionsStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.endpoint.StandardWebSocketClient;

/**
 * Configuration for the Spring 4.0 WebSocket Support.
 *
 * @author Gunar Hillert
 *
 */
@Configuration
public class ClientConfig {

	private static final String WS_URL = "ws://websocket.mtgox.com:80/mtgox";


	@Bean
	public WebSocketConnectionManager connectionManager() {

		HttpHeaders headers = new HttpHeaders();
		headers.setOrigin("http://websocket.mtgox.com");

		WebSocketConnectionManager cm = new WebSocketConnectionManager(wsClient(), wsHandler(), WS_URL);
		cm.setHeaders(headers);
		cm.setAutoStartup(true);
		return cm;
	}

	@Bean
	public WebSocketClient wsClient() {
		return new StandardWebSocketClient();
	}

	@Bean
	public WebSocketHandler wsHandler() {
		return new ClientMtgoxWebSocketHandler();
	}

	@Bean
	public WebSocketSessionsStore webSocketSessionsStore() {
		return new WebSocketSessionsStore();
	}
}
