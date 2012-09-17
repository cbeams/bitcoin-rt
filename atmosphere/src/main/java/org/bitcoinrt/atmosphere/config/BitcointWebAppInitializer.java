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
package org.bitcoinrt.atmosphere.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.MeteorServlet;
import org.atmosphere.handler.ReflectorServletProcessor;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class BitcointWebAppInitializer implements WebApplicationInitializer {

	public void onStartup(ServletContext servletContext) throws ServletException {

		AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
        webAppContext.register(WebConfig.class);

        final DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);

		@SuppressWarnings("serial")
		MeteorServlet meteorServlet = new MeteorServlet() {
			@Override
			public void init(ServletConfig sc) throws ServletException {
				super.init(sc);

				// MeteorServlet only support init parameters but in Java config it's easier
				// to register instances. So we re-register the default Atmosphere handler.

				BroadcasterFactory.getDefault().remove("/*");
				framework.addAtmosphereHandler("/*", new ReflectorServletProcessor(dispatcherServlet));
				framework.initAtmosphereHandler(sc);
			}
		};

		servletContext.addServlet("meteor", meteorServlet).addMapping("/");
	}

}
