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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class TomcatBitcoinServlet extends WebSocketServlet {

	private Logger logger = LoggerFactory.getLogger(TomcatBitcoinServlet.class);

	private final Set<MessageInbound> connections = new CopyOnWriteArraySet<MessageInbound>();

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			new AsyncHttpClientBitcoinClient(createBroadcaster()).connect();
		}
		catch (IOException ex) {
			throw new ServletException(ex);
		}
	}

	private Broadcaster createBroadcaster() throws IOException {
		return new Broadcaster() {
			@Override
			public void broadcast(String message) throws IOException {
				for (MessageInbound inbound : TomcatBitcoinServlet.this.connections) {
					inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
				}
			}
		};
	}

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol) {
		return new MessageInbound() {
			@Override
			protected void onOpen(WsOutbound outbound) {
				logger.debug("Opened new WS connection");
				TomcatBitcoinServlet.this.connections.add(this);
			}
	        @Override
	        protected void onClose(int status) {
				logger.debug("Closing WS connection");
	        	TomcatBitcoinServlet.this.connections.remove(this);
	        }
			@Override
			protected void onTextMessage(CharBuffer buffer) throws IOException {
				logger.trace("Got text message: " + buffer);
			}
			@Override
			protected void onBinaryMessage(ByteBuffer buffer) throws IOException {
				logger.trace("Got binary message");
			}
		};
	}

}
