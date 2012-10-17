!SLIDE subsection
# Where We Are In Java Land

!SLIDE incremental bullets
# [Tomcat](http://tomcat.apache.org/tomcat-7.0-doc/web-socket-howto.html)
* `WebSocketServlet`
* Since 7.0.27 (03/2012)
* Backport to 6.0.35 [Issue 52918](https://issues.apache.org/bugzilla/show_bug.cgi?id=52918)
* Fairly minimal, server-side only

!SLIDE subsection
# `bitcoin-rt: Tomcat demo`
.notes :
* show mongod running
* show client code

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
* [Atmosphere](https://github.com/Atmosphere/atmosphere)
* [jWebSocket](http://jwebsocket.org)
* [Netty](https://netty.io/)
* [vert.x](http://vertx.io/)
* [Grizzly](http://grizzly.java.net/)

!SLIDE small incremental bullets
# Client Side
* [AsyncHttpClient](https://github.com/sonatype/async-http-client)
* Jetty
* Netty
* vert.x
* Grizzly

!SLIDE small incremental bullets
# Java API for WebSocket (JSR-356)
* Original discussion started in JSR-340 (Servlet 3.1)
* Later split out into separate spec
* Servlet spec will have an upgrade option
* JSR-356 will likely not depend on Servlet API

!SLIDE small incremental bullets
# What's under discussion
* Client and server-side API
* Use of annotations
* Support for extensions
* Security considerations
* Thread model

!SLIDE small incremental bullets
# Resources
* [All drafts so far](http://java.net/projects/websocket-spec/downloads/directory/Spec%20javadoc%20Drafts)
* The latest [v006 early draft review](http://java.net/projects/websocket-spec/downloads/directory/Spec%20javadoc%20Drafts/v006-EDR)
* [Mailing list archives](http://java.net/projects/websocket-spec/lists)


