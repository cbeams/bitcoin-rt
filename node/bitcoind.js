var MTGOX_WS_URL = 'ws://websocket.mtgox.com:80/mtgox',
    MTGOX_TRADES_CHANNEL = 'dbf1dee9-4f2e-4a08-8cb7-748919a71b21',
    MTGOX_TICKER_CHANNEL = 'd5f06780-30a8-4a48-a2f8-7ed181b4a13f',
    MTGOX_DEPTH_CHANNEL = '24e67e0d-1cad-4cc0-9e7a-f8523ef460fe';

var MONGO_TRADES_COLL = 'trades';

var fs = require('fs'),
    path = require('path'),
    http = require('http'),
    mongo = require('mongodb'),
    WebSocketServer = require('websocket').server,
    WebSocketClient = require('websocket').client;


// connect and ensure mongo is up
var db = new mongo.Db('bitcoin',
        new mongo.Server('localhost', 27017, {auto_reconnect: true}));

db.open(function(err, db) {
    if(err) { throw err; }
});


// serve html and js resources
var httpServer = http.createServer(function (req, res) {
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
}).listen(1337, function () { });


// holder for client (browser) websocket connections
var clients = [];


// handle websocket requests
var wsServer = new WebSocketServer({ httpServer: httpServer });

wsServer.on('request', function (request) {
    var clientConn = request.accept(null, request.origin);

    clients.push(clientConn);
    sendAllTrades(clientConn);

    clientConn.on('close', function (clientConn) {
        // eliminate references to disconnected clients to avoid memory leak
        clients.filter(function (clientConn) {
            return clientConn.connected == false
        }).forEach(function (clientConn) {
            clients.splice(clients.indexOf(clientConn), 1);
        });
    });
});


// retrieve trade data from the MtGox bitcoin exchange
// see https://en.bitcoin.it/wiki/MtGox/API/HTTP/v1
//     https://en.bitcoin.it/wiki/MtGox/API/Streaming
var mtgoxConn = new WebSocketClient();

mtgoxConn.connect(MTGOX_WS_URL);

mtgoxConn.on('connect', function (mtgoxConn) {
    console.log('connected to mtgox, unsubscribing from ticker and depth channels');
    mtgoxConn.send('{"op":"unsubscribe","channel":"' + MTGOX_TICKER_CHANNEL + '"}');
    mtgoxConn.send('{"op":"unsubscribe","channel":"' + MTGOX_DEPTH_CHANNEL + '"}');

    mtgoxConn.on('message', function (message) {
        if (message.type !== 'utf8') {
            // ignore any binary messages (should never occur from mtgox anyway)
            return;
        }

        var data = JSON.parse(message.utf8Data);

        if (data.channel != MTGOX_TRADES_CHANNEL || !data.trade.primary) {
            // ignore any non-trade messages that might slip in before our
            // op:unsubscribes sent above have been handled; also ignore any
            // 'non-primary' trades. see
            // https://en.bitcoin.it/wiki/MtGox/API/HTTP/v1#Multi_currency_trades
            return;
        }

        // map the mtgox trade data to our more minimal structure that provides
        // only what's necessary for client display
        var trade = {
            "type"           : "trade",
            "exchange"       : "mtgox" + data.trade.price_currency,
            "date"           : data.trade.date * 1000, // for easy ms dates in js
            "btc_amount"     : data.trade.amount,
            "price"          : data.trade.price,
            "price_currency" : data.trade.price_currency,
            "txid"           : data.trade.tid
        };

        sendNewTrade(trade);
    });

    mtgoxConn.on('error', function (error) {
        console.log(error);
    });
});

mtgoxConn.on('close', function (mtgoxConn) {
    console.log('disconnected by server from mtgox');
    // TODO: reconnect!
});


// send all saved trades to the client
// TODO: limit to a date range communicated by the client
// see http://mongodb.github.com/node-mongodb-native/api-articles/nodekoarticle1.html#time-to-query
function sendAllTrades(clientConn) {
    db.collection(MONGO_TRADES_COLL, function(err, collection) {
        var stream = collection.find({}).streamRecords();
        stream.on('data', function(trade) {
            clientConn.send(JSON.stringify(trade));
        });
    });
}

// save trade data to mongo and send trade message to client when complete
// see http://mongodb.github.com/node-mongodb-native/api-articles/nodekoarticle1.html#and-then-there-was-crud
function sendNewTrade(trade) {
    db.collection(MONGO_TRADES_COLL, function (err, collection) {
        collection.insert(trade, function(err, result) {
            clients.forEach(function (clientConn) {
                clientConn.send(JSON.stringify(result[0]));
            });
        });
    });
}
