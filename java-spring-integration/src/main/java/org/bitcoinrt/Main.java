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
package org.bitcoinrt;

import java.util.Scanner;

import org.bitcoinrt.server.ConnectionBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Starts the Spring Context and will initialize the Spring Integration routes.
 *
 * @author Gunnar Hillert
 * @since 1.0
 *
 */
public final class Main {

	protected static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private Main() { }

	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args - command line arguments
	 */
	public static void main(final String... args) {

		final Scanner scanner = new Scanner(System.in);

		System.out.println("\n========================================================="
						+ "\n                                                         "
						+ "\n    Welcome to the Spring Integration Bitcoin-rt Sample! "
						+ "\n                                                         "
						+ "\n=========================================================" );

		System.out.println("Which WebSocket Client would you like to use? <enter>: ");
		System.out.println("\t1. Use Sonatype's Async HTTP Client implementation");
		System.out.println("\t2. Use Jetty's WebSocket client implementation");
		System.out.println("\t3. Use a Dummy client");

		System.out.println("\tq. Quit the application");
		System.out.print("Enter you choice: ");

		final GenericXmlApplicationContext context = new GenericXmlApplicationContext();

		while (true) {
			final String input = scanner.nextLine();

			if("1".equals(input.trim())) {
				context.getEnvironment().setActiveProfiles("default");
				break;
			} else if("2".equals(input.trim())) {
				context.getEnvironment().setActiveProfiles("jetty");
				break;
			} else if("3".equals(input.trim())) {
				context.getEnvironment().setActiveProfiles("dummy");
				break;
			} else if("q".equals(input.trim())) {
				System.out.println("Exiting application...bye.");
				System.exit(0);
			} else {
				System.out.println("Invalid choice\n\n");
				System.out.print("Enter you choice: ");
			}
		}

		context.load("classpath:META-INF/spring/integration/*-context.xml");
		context.registerShutdownHook();
		context.refresh();

		final ConnectionBroker connectionBroker = context.getBean(ConnectionBroker.class);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("\n========================================================="
					  + "\n                                                         "
					  + "\n    Please press 'q + Enter' to quit the application.    "
					  + "\n    For statistical information press 'i + Enter'.       "
					  + "\n                                                         "
					  + "\n    In your browser open:                                "
					  + "\n    file:///.../src/main/webapp/index.html               "
					  + "\n=========================================================" );
		}

		while (true) {

			final String input = scanner.nextLine();

			if("q".equals(input.trim())) {
				break;
			}
			else if("i".equals(input.trim()))  {
				LOGGER.info("\n========================================================="
						  + "\n                                                         "
						  + "\n Number of connected clients: " + connectionBroker.connectedClients()
						  + "\n                                                         "
						  + "\n=========================================================" );
			}

		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Exiting application...bye.");
		}

		System.exit(0);

	}
}
