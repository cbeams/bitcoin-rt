
### install vert.x runtime
e.g.:
```
wget http://vertx.io/downloads/vert.x-1.2.3.final.zip
unzip vert.x-1.2.3.final.zip
export VERTX_HOME=$PWD/vert.x-1.2.3.final
export PATH=$PATH:$VERTX_HOME/bin
vertx version
```
See http://vertx.io/install.html

### build
```
./gradlew build
```

### run
```
vertx run org.bitcoin.vertx.MainVerticle -cp build/classes/main
```

Open http://localhost:8080/
