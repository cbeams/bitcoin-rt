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

package org.bitcoinrt.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.bitcoinrt.client.AbstractMtgoxClient;
import org.bitcoinrt.client.StubMtgoxClient;
import org.bitcoinrt.server.TomcatBitcoinServlet;
import org.springframework.web.WebApplicationInitializer;

public class WebSocketServletInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		final AbstractMtgoxClient mtgoxClient = new StubMtgoxClient();
//		final AbstractMtgoxClient mtgoxClient = new JettyMtgoxClient();
//		final AbstractMtgoxClient mtgoxClient = new AsyncHttpClientMtgoxClient();

		Dynamic servlet = servletContext.addServlet("ws", new TomcatBitcoinServlet(mtgoxClient));
		servlet.addMapping("/tomcat");
		servlet.setLoadOnStartup(1);

		servletContext.addListener(new ServletContextListener() {

			@Override
			public void contextInitialized(ServletContextEvent sce) {
				try {
					mtgoxClient.start();
				}
				catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}

			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				try {
					mtgoxClient.stop();
				}
				catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
	}

}