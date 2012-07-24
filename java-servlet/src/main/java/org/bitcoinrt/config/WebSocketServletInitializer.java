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
import org.bitcoinrt.client.JettyMtgoxClient;
import org.bitcoinrt.server.TomcatBitcoinServlet;
import org.springframework.web.WebApplicationInitializer;

public class WebSocketServletInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		AbstractMtgoxClient mtgoxClient = new JettyMtgoxClient();
//		AbstractMtgoxClient mtgoxClient = new AsyncHttpClientMtgoxClient();

		Dynamic servlet = servletContext.addServlet("ws", new TomcatBitcoinServlet(mtgoxClient));
		servlet.addMapping("/tomcat");
		servlet.setLoadOnStartup(1);

		try {
			mtgoxClient.start();
		}
		catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

}