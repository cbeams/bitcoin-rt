## Node.js bitcoint-rt

An implementation backed by Node and Mongo on the server side;
the only transaction data coming through at the moment are trades from the
[MtGox] Bitcoin exchange.

By default the sample runs with a "stub" MtGox service. To switch to the
live version, edit bitcoind.js. If you don't see any trades on the graph
with the live version, just wait. Trades on MtGox are usually pretty frequent,
but can sometimes be spaced minutes apart. As a simple test that things are
working, bring up http://bitcoinmonitor.com in a second window and watch
for the red dots. Hover to see the exchange name. When you see
new mtgox trades coming through there, you should also see them in your
bitcoin-rt window.

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
Assuming mongod is already running (see above):

```
git clone git://github.com/cbeams/bitcoin-rt.git
cd bitcoin-rt/node
npm install websocket
npm install node-static
npm install mongodb
node bitcoind.js
open http://localhost:1337
```

Oh, and bitcoinmonitor is a CPU hog. If you hear your fans spinning up, it's
not bitcoin-rt's fault. Close the bitcoinmonitor window at that point. Data
should be seen and not heard.

[mtgox]: https://mtgox.com
[homebrew]: http://mxcl.github.com/homebrew

