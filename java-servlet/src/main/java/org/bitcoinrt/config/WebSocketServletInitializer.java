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
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.bitcoinrt.client.AbstractMtgoxClient;
import org.bitcoinrt.client.StubMtgoxClient;
import org.bitcoinrt.server.Broadcaster;
import org.bitcoinrt.server.TomcatBitcoinServlet;
import org.springframework.web.WebApplicationInitializer;

public class WebSocketServletInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		TomcatBitcoinServlet servlet = new TomcatBitcoinServlet();

		Dynamic registration = servletContext.addServlet("ws", servlet);
		registration.addMapping("/tomcat");
		registration.setLoadOnStartup(1);

		Broadcaster broadcaster = servlet;

		AbstractMtgoxClient mtgoxClient = new StubMtgoxClient(broadcaster);
//		AbstractMtgoxClient mtgoxClient = new JettyMtgoxClient(broadcaster);
//		AbstractMtgoxClient mtgoxClient = new AsyncHttpClientMtgoxClient(broadcaster);

		servletContext.addListener(new MtGoxContextListener(mtgoxClient));
	}

}