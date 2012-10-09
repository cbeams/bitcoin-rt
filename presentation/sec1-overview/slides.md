!SLIDE subsection
# Intro to WebSocket
<br><br>
## Chris Beams
## Gunnar Hillert
## Rossen Stoyanchev

!SLIDE subsection
# Objective

## Survey the lay of the land
## Less focus on syntax and mechanics
## Broad, pragmatic perspective
## Special emphasis on Java

!SLIDE bullets
* presentation source [http://cbeams.github.com/bitcoin-rt](http://cbeams.github.com/bitcoin-rt)
* bitcoin-rt demo source [http://github.com/cbeams/bitcoin-rt](http://github.com/cbeams/bitcoin-rt)
* bitcoin-rt live on the web (TODO: deploy to Cloud Foundry?)


!SLIDE small center
# Browser Support
![can-i-use-websockets.png](can-i-use-websockets.png)
<a href="http://caniuse.com/websockets">caniuse.com/websockets</a>

!SLIDE small center
# Browser Share (World-Wide)
![browser-world-wide.jpg](browser-world-wide.jpg)

!SLIDE small center
# Browser Share (China)
![browser-china.jpg](browser-china.jpg)

!SLIDE small center
# Browser Versions
![browser-version.jpg](browser-versions.jpg)

!SLIDE small bullets incremental
# Proxy Traversal
* HTTP Proxies designed for document transfer
* Not for streaming or idle connections
* Indistinguishable from unresponsive HTTP server
* Proxies may also buffer unencrypted responses

!SLIDE small
## "Today, most transparent proxy servers will not yet be familiar with the Web Socket protocol and these proxy servers will be unable to support the Web Socket protocol"
Peter Lubbers
<br>
<a href="http://www.infoq.com/articles/Web-Sockets-Proxy-Servers">
  http://www.infoq.com/articles/Web-Sockets-Proxy-Servers</a>

!SLIDE small bullets incremental
# A Few Rules of Thumb
* `"wss:"` provides a much better chance of success
* Browsers using explicit proxy will likely work
* Transparent proxies can support WebSocket
* but must be configured explicitly

!SLIDE small bullets incremental
# Keeping Connections Alive
* Internet inherently unreliable
* Both server and client can go away
* Wireless connection may fade out
* ...

!SLIDE small bullets incremental
# A New Set of Challenges
* Keep-alive ("ping!")
* Heartbeat ("i'm still here!")
* Message delivery guarantee
* Buffering



