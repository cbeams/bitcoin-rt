
var http = require('http'),
    static = require('node-static'),
    WebSocketServer = require('websocket').server,
    trades_db = require('./trades-db.js');

// switch between stub and "live" MtGox
var mtgox = require('./mtgox-stub.js');
// var mtgox = require('./mtgox.js');

// serve static resources
var webroot = new static.Server('./webroot');
var httpServer = http.createServer(function (req, res) {
    webroot.serve(req, res);
}).listen(1337, function () { });

console.log("Started listening on port 1337");

// holds client, websocket connections
var clients = [];

// serve websocket requests
var wsServer = new WebSocketServer({ httpServer: httpServer });

wsServer.on('request', function (request) {
    var conn = request.accept(null, request.origin);
    clients.push(conn);

    var trades = trades_db.forEachTrade(function (trade) {
        conn.send(JSON.stringify(trade));
    });

    conn.on('close', function (conn) {
      removeDisconnectedClients();
    });
});

function removeDisconnectedClients() {
    clients.filter(function (conn) { 
        return conn.connected == false 
    }).forEach(function (conn) {
        clients.splice(clients.indexOf(conn), 1);
    });
}

mtgox.registerListener(function(trade) {
    trades_db.saveTrade(trade);
    clients.forEach(function (conn) {
        conn.send(JSON.stringify(trade));
    });
});

