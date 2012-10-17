!SLIDE subsection
# How Did We Get Here?

!SLIDE small incremental bullets
# Some history...

* 1996 - Java Applets/Netscape 2.0
* 1999/2000 - XMLHttpRequest (XHR)
* 2003 - Macromedia/Adobe Flash (RTMP Protocol)

!SLIDE center
![comet](comet-bleach.jpeg)

!SLIDE small incremental bullets
# Comet

* March 2006 - **Comet** - Alex Russell
* event-driven, server-push data streaming
* e.g. in GMail's GTalk interface

!SLIDE small incremental bullets
# Comet

* XHR long-polling / XHR multipart-replace / XHR Streaming
* htmlfile ActiveX Object
* Server-sent events (SSE) - Part of HTML5/W3C (EventSource)

!SLIDE small incremental bullets
# Path to Websockets

* 2007 - TCPConnection API and protocol (Ian Hickson)
* WebSocket - First public draft January 2008

!SLIDE small bullets
# IETF Standardization
## (Network Working Group)

* 2009-Jan - hixie-00
* 2010-Feb - hixie-75 - Chrome 4
* 2010-May - hixie-76 - Disabled in FF/Opera 

!SLIDE small bullets
# IETF Standardization
## (HyBi Working Group)

* 2010-May - hybi-00 - Same as hixie-76
* 2011-April - hybi-07 - Firefox 6
* 2011-Dec - **RFC6455**

!SLIDE small bullets
# RFC 6455 - The WebSocket Protocol
* Final Version: Dec 2011
* http://tools.ietf.org/html/rfc6455

!SLIDE small incremental bullets
# Websocket Protocol Details

* TCP-based protocol
* HTTP used solely for upgrade request (Status Code *101*)
* Bi-directional, full-duplex
* Data Frames can be **Text** (UTF-8) or arbitrary **Binary** data

!SLIDE small incremental bullets
# WebSocket Schemes

* Unencrypted: ws://
* Encrypted: wss://
* Use encrypted scheme

!SLIDE small incremental bullets
# Handshake

* Request: **Sec-WebSocket-Key** Header
* Response - 258EAFA5-E914-47DA-95CA-C5AB0DC85B11
* Appended to key + SHA-1 + base64
* **Sec-WebSocket-Accept** Header

!SLIDE smaller
# Websocket Protocol Details

	 0                   1                   2                   3
	 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	+-+-+-+-+-------+-+-------------+-------------------------------+
	|F|R|R|R| opcode|M| Payload len |    Extended payload length    |
	|I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
	|N|V|V|V|       |S|             |   (if payload len==126/127)   |
	| |1|2|3|       |K|             |                               |
	+-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
	|     Extended payload length continued, if payload len == 127  |
	+ - - - - - - - - - - - - - - - +-------------------------------+
	|                               |Masking-key, if MASK set to 1  |
	+-------------------------------+-------------------------------+
	| Masking-key (continued)       |          Payload Data         |
	+-------------------------------- - - - - - - - - - - - - - - - +
	:                     Payload Data continued ...                :
	+ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
	|                     Payload Data continued ...                |
	+---------------------------------------------------------------+

!SLIDE small bullets
# Websocket Protocol Details

* **FIN** (1 bit) - Final fragment in a message
* **RSV**1-3 (1 bit each) - Reserved for extensions

!SLIDE small bullets
# Websocket Protocol Details

* **Opcode** (4 bits) - Which type of payload
* Text frame, binary frame, control frames
* Continuation frame indicates data belongs to previous frame

!SLIDE small bullets
# Websocket Protocol Details

* **Mask** (1 bit)
* Clients must mask
* Minimize data sniffing + Proxy cache-poisoning
* **Masking-key** (32bit) - Random (XOR) for each frame

!SLIDE small bullets
# Websocket Protocol Details

* **Payload length** (7, 16 or 64 bit) in bytes
* **Extension data** + **Application data**

!SLIDE center 
# Websocket Control Frames

![pong](pong.png)

!SLIDE small incremental bullets
# Websocket Control Frames

* Communicate state about the WebSocket

* Close (0x8)
* Ping (0x9)
* Pong (0xA)

* More possible in future
* 125 bytes or less

!SLIDE small incremental bullets
# Close Frame

* Terminates WebSocket connection
* Can contain a body (UTF-8 encoded)
* Defines a set of Status Codes, e.g:
* 1000 = normal closure
* 1001 = endpoint is "going away"

!SLIDE small incremental bullets
# Ping + Pong Frame

* Serves as **keepalive** (Ping followed by Pong)
* Check whether the remote endpoint is still responsive
* Can be sent at any time (Websocket established, before close)
* Just Pongs (unsolicited) = unidirectional **heartbeat**

!SLIDE small incremental bullets
# Extensions

* WebSocket Per-frame Compression (Draft)
* Multiplexing Extension (Draft)
* Extensions Header: Sec-WebSocket-Extensions
* Used in the opening handshake (HTTP)

!SLIDE small bullets
# Multiplexing Extension (MUX) for WebSockets
* [http://tools.ietf.org/html/draft-ietf-hybi-websocket-multiplexing-08](http://tools.ietf.org/html/draft-ietf-hybi-websocket-multiplexing-08)
* separate logical connections over underlying transport connection

!SLIDE small incremental bullets

# Sub-Protocols
* Sub-Protocol Header: Sec-WebSocket-Protocol
* IANA Registry:
* [http://www.iana.org/assignments/websocket/websocket.xml](http://www.iana.org/assignments/websocket/websocket.xml)

!SLIDE small bullets

# HTML5 WebSockets = 
# W3C API + IETF Protocol

!SLIDE small bullets
# The WebSocket API
* W3C Candidate Recommendation 20 Sep 2012
* [http://www.w3.org/TR/websockets/](http://www.w3.org/TR/websockets/)
* Browser client-side API

!SLIDE small incremental bullets
# The WebSocket API

* Binary data supported: **Blob** or **ArrayBuffer** format
* Can inspect extensions (read-only)
* No support for ping/pong frames

!SLIDE small incremental bullets
# The *readyState* attribute

* CONNECTING (0) - Connection not yet established
* OPEN (1) - Connection is established + communication possible
* CLOSING (2) - Connection going through closing handshake / close() method called
* CLOSED (3) - Connection is closed / could not be opened

!SLIDE small incremental bullets
# Event Handlers
* onopen
* onmessage
* onerror
* onclose

!SLIDE
# Code Sample

    @@@ java
    var socket = new WebSocket(
      'ws://localhost:8080/bitcoin-java-servlet/tomcat');
    ...
    socket.onmessage = function(event) {
          console.log(event.data);
          var trade = JSON.parse(event.data);
          ...
        };
    ...

!SLIDE small bullets
# JSR 356: Java API for WebSocket
* Early Draft Review, Sep 2012
* [http://jcp.org/en/jsr/detail?id=356](http://jcp.org/en/jsr/detail?id=356)
