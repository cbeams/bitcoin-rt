// https://en.bitcoin.it/wiki/MtGox/API/HTTP/v1
// https://en.bitcoin.it/wiki/MtGox/API/Streaming

var MTGOX_WS_URL = 'ws://websocket.mtgox.com:80/mtgox'
var MTGOX_TRADES_CHANNEL = 'dbf1dee9-4f2e-4a08-8cb7-748919a71b21'

var fs = require('fs');
var path = require('path');
var http = require('http');
var mongo = require('mongodb'),
    server = new mongo.Server('localhost', 27017, {auto_reconnect: true}),
    db = new mongo.Db('bitcoin', server);

db.open(function(err, db) {
  if(err) {
      console.log('Could not connect to MongoDB! Is the server running?');
      throw err;
  }
});

var server = http.createServer(function (req, res) {
    var p = req.url;

    if (p === '/') {
        p = '/index.html'
    }

    p = 'webroot' + p;

    var contentType;
    if (p.match('\.html$')) {
        contentType = 'text/html';
    }
    else if (p.match('\.js$')) {
        contentType = 'application/javascript';
    }
    else {
        contentType = 'application/unknown';
    }

    path.exists(p, function(exists) {
        if (!exists) {
            res.writeHead(404);
            res.end();
            return;
        }
        fs.readFile(p, 'utf8', function (err, data) {
            if (err) {
                res.writeHead(500);
                res.end(JSON.stringify(err));
                return console.log(err);
            }
            res.writeHead(200, { 'Content-Type': contentType });
            res.end(data);
        });
    });
});

var WebSocketServer = require('websocket').server;
wsServer = new WebSocketServer({ httpServer: server });

var WebSocketClient = require('websocket').client;
wsClient = new WebSocketClient();

// client (browser) websocket connections
var subscribers = [];

wsServer.on('request', function (request) {
    var connection = request.accept(null, request.origin);

    if (subscribers.length == 0) {
        wsClient.connect(MTGOX_WS_URL);
    }
    subscribers.push(connection);

    db.collection('trades', function(err, collection) {
      var stream = collection.find({}).streamRecords();
      stream.on('data', function(trade) {
          connection.send(JSON.stringify(trade));
      });
    });

    connection.on('close', function (connection) {
        var unsubscribe = subscribers.filter(function (subscriber) {
            return subscriber.connected == false
        });
        unsubscribe.forEach(function (subscriber) {
            doUnsubscribe(subscriber);
        });
    });
});

function doUnsubscribe(connection) {
    var index = subscribers.indexOf(connection);
    if (index >= 0) {
        subscriber = subscribers.splice(index, 1);
        if (subscribers.length == 0) {
            console.log('no more subscribers! disconnecting from mtgox.');
            if (wsClient.socket != null) {
                wsClient.socket.end();
            }
        }
    }
}


wsClient.on('connect', function (connection) {
    console.log('connected to mtgox');
    console.log('unsubscribing from ticker and depth channels');
    connection.send('{"op":"unsubscribe","channel":"d5f06780-30a8-4a48-a2f8-7ed181b4a13f"}');
    connection.send('{"op":"unsubscribe","channel":"24e67e0d-1cad-4cc0-9e7a-f8523ef460fe"}');

    connection.on('message', function (message) {
        if (message.type === 'utf8') {
            console.log(message.utf8Data);
            var m = JSON.parse(message.utf8Data);
            if (m.channel != MTGOX_TRADES_CHANNEL || !m.trade.primary) {
                return;
            }
            var t = m.trade;
            var trade = {
                "type"           : "trade",
                "exchange"       : "mtgox" + t.price_currency,
                "date"           : t.date * 1000, // for easy ms dates in js
                "btc_amount"     : t.amount,
                "price"          : t.price,
                "price_currency" : t.price_currency,
                "txid"           : t.tid
            };
            db.collection('trades', function(err, collection) {
                collection.insert(trade, {safe:true}, function(err, result) {
                    subscribers.forEach(function (subscriber) {
                        subscriber.send(JSON.stringify(result[0]));
                    });
                });
            });

        }
    });

    connection.on('error', function (error) {
        console.log(error);
    });
});

wsClient.on('close', function (connection) {
    console.log('disconnected by server from mtgox');
});


function sendTestTrades(connection) {
    // for dev purposes: seed a few trades for display
    var now = Date.now();
    connection.send(
        JSON.stringify({
            "type"           : "trade",
            "exchange"       : "mtgoxUSD",
            "date"           : now-2000,
            "btc_amount"     : 3,
            "price"          : 5,
            "price_currency" : "USD",
            "txid"           : 9876543210
        })
    );
    connection.send(
        JSON.stringify({
            "type"           : "trade",
            "exchange"       : "mtgoxUSD",
            "date"           : now-1000,
            "btc_amount"     : 30,
            "price"          : 5,
            "price_currency" : "USD",
            "txid"           : 9876543211
        })
    );
    connection.send(
        JSON.stringify({
            "type"           : "trade",
            "exchange"       : "mtgoxUSD",
            "date"           : now,
            "btc_amount"     : 300,
            "price"          : 5,
            "price_currency" : "USD",
            "txid"           : 9876543211
        })
    );
}

server.listen(1337, function () { });
