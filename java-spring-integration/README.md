## Spring Integration bitcoint-rt

An implementation backed by Spring Integration using the IP Extensions on the server side. This sample is using AsyncHttpClient or Jetty WebSocket clients (pluggable) to receive
trade messages from MtGox; the only transaction data coming through at the moment are trades from the [MtGox] Bitcoin exchange.

### Running the sample

	$ ./gradlew run

Once running, please open in your web browser:

	file:///.../src/main/webapp/index.html

### Eclipse settings

````
./gradlew cleanEclipse eclipse
````

Then import the project into Eclipse.

[mtgox]: https://mtgox.com

