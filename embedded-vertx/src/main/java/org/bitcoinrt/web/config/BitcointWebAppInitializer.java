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
package org.bitcoinrt.web.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;

public class BitcointWebAppInitializer implements WebApplicationInitializer {

	public void onStartup(ServletContext servletContext) throws ServletException {

		AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
        webAppContext.register(WebConfig.class);

        final DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/site/*");

		UrlRewriteFilter urlRewriteFilter = new UrlRewriteFilter();

		servletContext.addFilter("UrlRewriteFilter", urlRewriteFilter)
			.addMappingForUrlPatterns(
					EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD),
					false, "/*");

	}

}
