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

import org.bitcoinrt.spring.common.WebSocketSessionsStore;
import org.bitcoinrt.spring.websocket.client.ClientMtgoxWebSocketHandler;
import org.bitcoinrt.spring.websocket.client.CustomWebSocketConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.endpoint.StandardWebSocketClient;

/**
 * Configuration for the Spring 4.0 WebSocket Support.
 *
 * @author Gunar Hillert
 *
 */
@Configuration
public class ClientWebSocketConfig {

	private static final Logger logger = LoggerFactory.getLogger(ClientWebSocketConfig.class);

	@Bean
	public ClientMtgoxWebSocketHandler clientWebSocketHandler() {
		return new ClientMtgoxWebSocketHandler();
	}

	@Bean
	public WebSocketClient webSocketClient() {
		return new StandardWebSocketClient();
	}

	@Bean
	public CustomWebSocketConnectionManager connectionManager() {
		CustomWebSocketConnectionManager cm = new CustomWebSocketConnectionManager(webSocketClient(), clientWebSocketHandler(), "ws://websocket.mtgox.com:80/mtgox");
		cm.setAutoStartup(true);
		return cm;
	}

	@Bean
	public WebSocketSessionsStore webSocketSessionsStore() {
		return new WebSocketSessionsStore();
	}
}
