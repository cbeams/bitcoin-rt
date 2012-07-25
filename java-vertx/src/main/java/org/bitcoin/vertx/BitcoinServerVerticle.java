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

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.deploy.Verticle;

/**
 * Start HTTP server at port 8080 serving index.html and static files from the "webroot" sub-directory.
 *
 * Layer SockJS server on top of the HTTP server at the URL path "/bitcoin".
 *
 */
public class BitcoinServerVerticle extends Verticle {

	@Override
	public void start() throws Exception {

		RouteMatcher routeMatcher = new RouteMatcher();
		routeMatcher.get("/", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				request.response.sendFile("webroot/index.html");
			}
		});
		routeMatcher.getWithRegEx("(\\/.*\\.(js|css|png))", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				request.response.sendFile("webroot" + request.params().get("param0"));
			}
		});

		// HTTP server
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(routeMatcher);

		// SockJS server
		JsonArray permitted = new JsonArray();
	    permitted.add(new JsonObject()); // Let everything through
	    SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);
	    sockJSServer.bridge(new JsonObject().putString("prefix", "/bitcoin"), permitted, permitted);

		httpServer.listen(8080);
	}

}
