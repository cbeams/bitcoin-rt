## vert.x bitcoint-rt

An implementation backed by vert.x on the server side;
the only transaction data coming through at the moment are trades from the
[MtGox] Bitcoin exchange.

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


[mtgox]: https://mtgox.com

