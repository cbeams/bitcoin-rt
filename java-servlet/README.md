## Tomcat (Servlet) bitcoint-rt

An implementation backed by Tomcat on the server side and using
AsyncHttpClient or Jetty WebSocket clients (pluggable) to receive
trade messages from MtGox; the only transaction data coming through
at the moment are trades from the [MtGox] Bitcoin exchange.

### Pre-requisites

This sample must run on Tomcat 7.0.29 (or higher).

### Eclipse settings

````
./gradlew cleanEclipse eclipse
````

Then import the project into Eclipse.

[mtgox]: https://mtgox.com

