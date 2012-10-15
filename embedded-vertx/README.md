## Embedded vert.x bitcoint-rt

This application uses an embedded vert.x instance. The sample is otherwise very similar to the **vert.x bitcoint-rt** sample. The view rendering is backed by Spring MVC. The embedded *vert.x* server starts up on **port 7777**.

# Running the Sample

The sample uses [Gradle][] to build the project.

1. Build the application: `./gradlew clean war`
2. Copy the war file from `build/libs` to e.g. Tomcat's webapps folder
3. Start your container
4. Open your browser: `http://localhost:8080/bitcoin-vertx-embedded/`

### Eclipse settings

````
./gradlew cleanEclipse eclipse
````

Then import the project into Eclipse.

[bitcoin-rt]: https://github.com/cbeams/bitcoin-rt
[Atmosphere Framework]: https://github.com/Atmosphere/atmosphere
[Gradle]: http://www.gradle.org/

