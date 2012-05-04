var WebSocketServer = require('websocket').server;
var WebSocketClient = require('websocket').client;
var http = require('http');

var server = http.createServer(function(request, response) { });
server.listen(1337, function() { });

var subscribers = [];

wsServer = new WebSocketServer({ httpServer: server });

wsServer.on('request', function(request) {
    var connection = request.accept(null, request.origin);

    connection.on('message', function(message) {
        if (message.type === 'utf8') {
            msg = JSON.parse(message.utf8Data);
            if (msg.op === 'subscribe') {
                subscribers.push(connection);
            }
            else if (msg.op === 'unsubscribe') {
                var index = subscribers.indexOf(connection);
                if (index >= 0) {
                    subscribers.splice(index, 1);
                }
            }
        }
    });

    connection.on('close', function(connection) {
    });
});

wsClient = new WebSocketClient();

wsClient.connect("ws://websocket.mtgox.com:80/mtgox");
wsClient.on('connect', function(connection) {
    connection.on('message', function(message) {
        if (message.type === 'utf8') {
            subscribers.forEach(function (subscriber) {
                subscriber.send(message.utf8Data);
            });
        }
    });
});
