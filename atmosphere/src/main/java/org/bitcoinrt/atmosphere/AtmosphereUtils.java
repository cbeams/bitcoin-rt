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
package org.bitcoinrt.atmosphere;

import java.util.concurrent.CountDownLatch;
import javax.servlet.http.HttpServletRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gunnar Hillert
 *
 */
public final class AtmosphereUtils {

	public static final Logger LOG = LoggerFactory.getLogger(AtmosphereUtils.class);

	public static AtmosphereResource getAtmosphereResource(HttpServletRequest request) {
		return getMeteor(request).getAtmosphereResource();
	}
	public static Meteor getMeteor(HttpServletRequest request) {
		return Meteor.build(request);
	}
	public static void suspend(final AtmosphereResource resource) {

		final CountDownLatch countDownLatch = new CountDownLatch(1);
		resource.addEventListener(new AtmosphereResourceEventListenerAdapter() {
			@Override
			public void onSuspend(AtmosphereResourceEvent event) {
				countDownLatch.countDown();
				LOG.info("Suspending Client..." + resource.uuid());
				resource.removeEventListener(this);
			}

			@Override
			public void onDisconnect(AtmosphereResourceEvent event) {
				LOG.info("Disconnecting Client..." + resource.uuid());
				super.onDisconnect(event);
			}

			@Override
			public void onBroadcast(AtmosphereResourceEvent event) {
				LOG.info("Client is broadcasting..." + resource.uuid());
				super.onBroadcast(event);
			}

		});

		AtmosphereUtils.lookupBroadcaster().addAtmosphereResource(resource);

		if (AtmosphereResource.TRANSPORT.LONG_POLLING.equals(resource.transport())) {
			resource.resumeOnBroadcast(true).suspend(-1, false);
		} else {
			resource.suspend(-1);
		}

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			LOG.error("Interrupted while trying to suspend resource {}", resource);
		}
	}

	public static Broadcaster lookupBroadcaster() {
		Broadcaster b = BroadcasterFactory.getDefault().get();
		return b;
	}

}
