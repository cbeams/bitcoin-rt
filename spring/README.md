## Spring bitcoint-rt

An implementation backed by the [Spring 4.0 Websocket][] and [SockJS][] support.

The only transaction data coming through at the moment are trades from the
[MtGox] Bitcoin exchange.

# Building and Running the Sample

The sample uses [Gradle][] to build the project.

Ideally you want to deploy this sample against Servlet containers that support **JSR-356**, such as the forthcoming *Tomcat 8*. Nevertheless,
you can also deploy the sample to slightly older containers such as *Tomcat 7*. In that case *SockJS* will fallback to either Streaming or Long-Polling.

## Build the Sample

### For Containers Supporting JSR-356

`./gradlew clean build`

### For older Containers NOT Supporting JSR-356

`./gradlew clean build -PnoJSR356`

This will include the *Tyrus* libraries to the War.

## Run the Sample

2. Copy the war file from `build/libs` to e.g. Tomcat's webapps folder
3. Start your container
4. Open your browser: `http://localhost:8080/bitcoin-spring/`

### Eclipse settings

````
./gradlew cleanEclipse eclipse
````

Then import the project into Eclipse.

[Spring 4.0 Websocket]: http://static.springsource.org/spring/docs/4.0.x/javadoc-api/index.html?org/springframework/web/socket/package-summary.html
[bitcoin-rt]: https://github.com/cbeams/bitcoin-rt
[SockJS]: http://sockjs.org
[Gradle]: http://www.gradle.org/

