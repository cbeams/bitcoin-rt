## bitcoin-rt

A clone of http://bitcoinmonitor.com using WebSockets instead of long polling
and [d3][] instead of jQuery UI. The idea is to visualize all transactions
in the global [Bitcoin][] network. If you don't know what Bitcoin is, or don't
know much about it, you should watch this [two minute video][video].

The current implementation is backed by Node and Mongo on the server side;
the only transaction data coming through at the moment are trades from the
[MtGox] Bitcoin exchange. The UI is simple but functional; polish can come later.

The intention is to render trade data from all major exchanges as well as all
account-to-account transfers and block creation events from the blockchain.
The UI should eventually allow users to determine via a slider the date range
they wish to see, hover for detailed transaction data, and ideally provide a
[Google-finance][] style set of checkboxes allowing the user to include/exclude
which kinds of transactions they wish to see, which exchanges, etc.

This is primarily a testing ground for different WebSocket APIs and potential
WebSocket support in Spring. While the implementation is Node-based at the
moment, the goal is to implement it in Tomcat's [native WebSocket API][tomcat]
as well as in [vert.x][], which should provide for interesting comparisons.

Below are setup instructions for OS X (with [homebrew][] installed) and
for Linux. If you're on another platform, you'll easily figure it out.

## OS X Setup

### install and run mongodb
see http://www.mongodb.org/display/DOCS/Quickstart+OS+X
```
brew install mongodb # (~50MB)
sudo mkdir -p /data/db/
sudo chown `id -u` /data/db
mongod
```

### install npm/node
```
brew install npm
brew install node
```

## Linux Setup

### mongodb
see http://www.mongodb.org/downloads#packages

### npm/node
see https://github.com/joyent/node/wiki/Installing-Node.js-via-package-manager

## run the app
```
git clone git://github.com/cbeams/bitcoin-rt.git
cd bitcoin-rt/node
npm install websocket
npm install node-static
npm install mongodb
node bitcoind.js
open http://localhost:1337
```

If you don't see any trades on the graph, just wait. Trades on MtGox are
usually pretty frequent, but can sometimes be spaced minutes apart. As a simple
test that things are working, bring up http://bitcoinmonitor.com in a second
window and watch for the red dots. Hover to see the exchange name. When you see
new mtgox trades coming through there, you should also see them in your
bitcoin-rt window.

Oh, and bitcoinmonitor is a CPU hog. If you hear your fans spinning up, it's
not bitcoin-rt's fault. Close the bitcoinmonitor window at that point. Data
should be seen and not heard.

[d3]: http://d3js.org
[bitcoin]: http://bitcoin.org
[video]: http://www.weusecoins.com
[mtgox]: https://mtgox.com
[google-finance]: http://www.google.com/finance
[tomcat]: http://tomcat.apache.org/tomcat-7.0-doc/web-socket-howto.html
[vert.x]: http://vertx.io
[homebrew]: http://mxcl.github.com/homebrew
