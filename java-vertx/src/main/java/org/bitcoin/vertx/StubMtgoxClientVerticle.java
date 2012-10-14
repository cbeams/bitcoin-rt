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

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * A "stub" MtGox client that produces messages at fixed intervals.
 */
public class StubMtgoxClientVerticle extends Verticle {

	private ExecutorService executor;

	private boolean shuttingDown = false;


	@Override
	public void start() throws Exception {
		this.executor = Executors.newSingleThreadExecutor();

		this.executor.submit(new Runnable() {
			@Override
			public void run() {
				while (!shuttingDown) {

					String expression =
							"{\"channel\":\"dbf1dee9-4f2e-4a08-8cb7-748919a71b21\",\"op\":\"private\"," +
							"\"origin\":\"broadcast\",\"private\":\"trade\",\"trade\":{\"amount\":[%f]," +
							"\"date\":[%d],\"price\":12.239,\"exchange\":\"mtgoxUSD\"," +
							"\"price_currency\":\"USD\",\"primary\":\"Y\"," +
							"\"txid\":\"1348679989121772\",\"type\":\"trade\"}}";

					long timestamp = (long) (Math.floor(new Date().getTime() / 1000));
					double amount = Math.floor(Math.random() * 20);
					String message = String.format(expression, amount, timestamp);

					JsonObject jsonObject = new JsonObject(message);
					JsonObject trade = jsonObject.getObject("trade");

					vertx.eventBus().publish("bitcoin.trades", trade);

					try {
						Thread.sleep(7000);
					}
					catch (InterruptedException ex) {
						container.getLogger().debug("Stub MtGox service interrupted");
					}
				}
				container.getLogger().debug("Stub MtGox service stopped");
			}
		});
	}

	@Override
	public void stop() throws Exception {
		container.getLogger().debug("Shutting down stub MtGox service...");
		this.shuttingDown = true;
		this.executor.shutdownNow();
	}

}
