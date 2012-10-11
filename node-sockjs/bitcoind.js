
var http = require('http'),
    static = require('node-static'),
    sockjs = require('sockjs'),
    trades_db = require('./trades-db.js');

// switch between stub and "live" MtGox
var mtgox = require('./mtgox-stub.js');
// var mtgox = require('./mtgox.js');

// serve static resources
var webroot = new static.Server('./webroot');
var httpServer = http.createServer(function (req, res) {
    webroot.serve(req, res);
}).listen(1337, function () {});

console.log("Started listening on port 1337");

// holds client connections
var clients = [];

var sockjs_options = {
  sockjs_url: "http://cdn.sockjs.org/sockjs-0.3.min.js"
};

sockjs.createServer()
    .on('connection', function(conn) {
        clients.push(conn);
        var trades = trades_db.forEachTrade(function (trade) {
            conn.write(JSON.stringify(trade));
        });
        conn.on('close', function() {
          removeDisconnectedClients();
        });
    }).installHandlers(httpServer, {prefix : '/bitcoin'});

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
        conn.write(JSON.stringify(trade));
    });
});

