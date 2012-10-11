## Atmosphere bitcoint-rt

An implementation backed by the [Atmosphere Framework][] on the server side;
the only transaction data coming through at the moment are trades from the
[MtGox] Bitcoin exchange.

# Running the Sample

The sample uses [Gradle][] to build the project.

1. Build the application: `./gradlew clean war`
2. Copy the war file from `build/libs` to e.g. Tomcat's webapps folder
3. Start your container
4. Open your browser: `http://localhost:8080/bitcoin-atmosphere/`

### Eclipse settings

````
./gradlew cleanEclipse eclipse
````

Then import the project into Eclipse.

[bitcoin-rt]: https://github.com/cbeams/bitcoin-rt
[Atmosphere Framework]: https://github.com/Atmosphere/atmosphere
[Gradle]: http://www.gradle.org/

