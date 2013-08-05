## Spring bitcoint-rt

An implementation backed by the [Spring 4.0 Websocket][] and [SockJS][] support.

The only transaction data coming through at the moment are trades from the
[MtGox] Bitcoin exchange.

# Running the Sample

The sample uses [Gradle][] to build the project.

1. Build the application: `./gradlew clean war`
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

