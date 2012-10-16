!SLIDE subsection
# Building a Non-Trivial Application
.notes : 15 min

!SLIDE
# Short-term considerations
.notes :
We've already mentioned the issues with raw websockets, therefore the immediate
concern is fallback options, both of which we've already talked about. It's
possible you can build an application with just the right constrains but it's a
very narrow path, pretty unlikely. So you'll need fallback options and this
helps to explain why there are commercial options as well. A slide on Kaazing at
least for sure (fallback options?, jms/xmpp/amqp) and any others just listed
(pusher?) Pusher is more than just websockets, it's also native notifications
.. go to Jeremy's talk

!SLIDE
# Advice
.notes :
advice for what to use today .. decision tree
- if building internal applications with websocket browser support start with
  your servlet container
- if you need fallback options, it rules out most servlet containers (except
  Jetty) .. leaves vert.x, atmosphere, cometd/jetty and non-java solution
  connected via messaging

!SLIDE
# Long-term considerations
.notes :
[long-term] Starting with JSON, imagine exchanging messages, there is one
connection (it's all about efficiency) and many kinds of messages, how do you
distinguish the type of message? Jackson has 'Jackson Polymorphic Deserialization'
- markers that could be used but would you really do it that way?
With remoting (like Flash/Flex) you're targetting a server-side object and that's
provided as a service to you by the remoting technology; here we are on a lower
level; with REST you have a protocol (HTTP method, headers, etc) clear use case
that demonstrates the need for protocol; one some level the fact that Rabbit and
ActiveMQ have provided stomp; Kaazing provides multiple protocols; it is quite
predictable, this is a very low level mechanism; buffering of messages (client
sends messages faster than server can process, client and server are temporarily
disconnected, etc) transport vs application protocol .. lightweight pub-sub
mechanism one protocol to rule them all or many? without any protocol the kind
of interrop enabled by the WWW is unimaginable; the choice of websockets should
be very conscious, understanding that you loose the HTTP verbs the Kaazing
products are a good example to bring up .. the fact that they recommend using
something on top of WebSockets; probably X number of years from now we won't be
using WebSockets direclty
CQRS
