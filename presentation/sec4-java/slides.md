!SLIDE subsection
# Where We Are In Java Land

!SLIDE incremental bullets
# [Tomcat](http://tomcat.apache.org/tomcat-7.0-doc/web-socket-howto.html)
* Since 7.0.27 (03/2012)
* Backport to 6.0.35 [Issue 52918](https://issues.apache.org/bugzilla/show_bug.cgi?id=52918)
* Fairly minimal, server-side only

!SLIDE small incremental bullets
# [Jetty](http://download.eclipse.org/jetty/stable-7/apidocs/org/eclipse/jetty/websocket/package-summary.html)
* Since Jetty 7.x (early adoption, complex)
* [Revised in Jetty 9](http://webtide.intalio.com/2012/10/jetty-9-updated-websocket-api/)
* Builds on Java 7, messages not frames, annotations

!SLIDE small incremental bullets
# [Glassfish](http://antwerkz.com/glassfish-web-sockets-sample/)
* Since 3.1 (02/2011)
* Exposes frames, server-side only
* Like with earlier Jetty versions, a major revision is likely

!SLIDE small incremental bullets
# Other Implementations
* Atmosphere (more on that shortly)
* jWebSocket
* Netty
* vert.x ([bitcoint-rt sample](https://github.com/cbeams/bitcoin-rt/tree/master/java-vertx))
* Grizzly

!SLIDE small incremental bullets
# Client Side
* [AsyncHttpClient](https://github.com/sonatype/async-http-client)
* Jetty
* Netty
* vert.x
* Grizzly

!SLIDE small incremental bullets




