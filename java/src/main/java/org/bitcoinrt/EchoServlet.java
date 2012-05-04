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

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

@SuppressWarnings("serial")
public class EchoServlet extends WebSocketServlet {

	@Override
	protected StreamInbound createWebSocketInbound(String arg0) {
		return new MessageInbound() {
			@Override
			protected void onTextMessage(CharBuffer arg0) throws IOException {
				System.out.println("onTextMessage(): " + arg0);
				this.getWsOutbound().writeTextMessage(arg0);
			}

			@Override
			protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
				System.out.println("onBinaryMessage(): " + arg0);
				this.getWsOutbound().writeBinaryMessage(arg0);
			}
		};
	}

}
