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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;

public abstract class AbstractMtgoxClient implements MtgoxSource {

	protected static final String MTGOX_URL = "ws://websocket.mtgox.com:80/mtgox";
	protected static final String MTGOX_TRADES_CHANNEL = "dbf1dee9-4f2e-4a08-8cb7-748919a71b21";
	protected static final String MTGOX_TICKER_CHANNEL = "d5f06780-30a8-4a48-a2f8-7ed181b4a13f";
	protected static final String MTGOX_DEPTH_CHANNEL = "24e67e0d-1cad-4cc0-9e7a-f8523ef460fe";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final List<MtgoxMessageListener> listeners = new ArrayList<MtgoxMessageListener>();

	@Override
	public void registerListener(MtgoxMessageListener listener) {
		this.listeners.add(listener);
	}

	public abstract void start() throws Exception;

	protected void onMessage(String message) {
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

		// Example message:
		// {"channel":"dbf1dee9-4f2e-4a08-8cb7-748919a71b21","op":"private","origin":"broadcast","private":"trade","trade":{"amount":0.01,"amount_int":"1000000","date":1342989115,"item":"BTC","price":8.50097,"price_currency":"USD","price_int":"850097","primary":"Y","properties":"limit","tid":"1342989115044532","trade_type":"bid","type":"trade"}}
		logger.debug("New Trade message: " + message);

		JSONObject trade = JsonPath.compile("$.trade").read(message);
		notifyMessageListeners(trade.toString());
	}

	private void notifyMessageListeners(String message) {
		try {
			for (MtgoxMessageListener listener : this.listeners) {
				listener.onMessage(message);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}