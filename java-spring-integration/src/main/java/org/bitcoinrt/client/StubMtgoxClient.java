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

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.integration.MessageChannel;

/**
 * A "stub" MtGox client that produces messages at fixed intervals.
 */
public class StubMtgoxClient extends AbstractMtgoxClient {

	private ExecutorService executor;

	private boolean shuttingDown = false;

	public StubMtgoxClient(MessageChannel outputChannel) {
		super(outputChannel);
	}

	@Override
	public void start() {

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
					onMessage(message);

					try {
						Thread.sleep(7000);
					}
					catch (InterruptedException ex) {
						logger.debug("Stub MtGox service interrupted");
					}
				}
				logger.debug("Stub MtGox service stopped");
			}
		});

	}

	@Override
	public void stop() {
		logger.debug("Shutting down stub MtGox service...");
		this.shuttingDown = true;
		this.executor.shutdownNow();
	}
}
