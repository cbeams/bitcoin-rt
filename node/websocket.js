var MTGOX_WS_URL = 'ws://websocket.mtgox.com:80/mtgox'

var fs = require('fs');

var http = require('http');
var server = http.createServer(function (req, res) {
    if (req.url === '/') {
        fs.readFile('index.html', 'utf8', function (err, data) {
            if (err) {
                res.writeHead(500);
                res.end();
                return console.log(err);
            }

            res.writeHead(200, {'Content-Type': 'text/html'});
            res.end(data);
        });
    }
});

var WebSocketServer = require('websocket').server;
wsServer = new WebSocketServer({ httpServer: server });

var WebSocketClient = require('websocket').client;
wsClient = new WebSocketClient();

var subscribers = [];

wsServer.on('request', function (request) {
    var connection = request.accept(null, request.origin);

    connection.on('message', function (message) {
        if (message.type === 'utf8') {
            msg = JSON.parse(message.utf8Data);
            if (msg.op === 'subscribe') {
                console.log('received subscribe operation');
                if (subscribers.length == 0) {
                    wsClient.connect(MTGOX_WS_URL);
                }
                subscribers.push(connection);
            }
            else if (msg.op === 'unsubscribe') {
                doUnsubscribe(connection);
            }
        }
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
            wsClient.socket.end();
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
            subscribers.forEach(function (subscriber) {
                subscriber.send(message.utf8Data);
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

server.listen(1337, function () { });
