
### install vert.x runtime
See http://vertx.io/install.html

### install vert.x in local Maven repository
One way to do that is by cloning vert.x:
```
$ git clone https://github.com/vert-x/vert.x.git
$ cd vert.x
$ ./mk install -x javadoc
```

Note that this installs the latest development version of vert.x and that may be ahead of the latest vert.x runtime available for download (currently 1.2.0.final).

### build
```
./gradlew build
```

### run
```
vertx run org.bitcoin.vertx.MainVerticle -cp build/classes/main
```

Open http://localhost:8080/


