
var http = require('http'),
    static = require('node-static'),
    sockjs = require('sockjs'),
//    mtgox = require('./mtgox.js'),
    mtgox = require('./mtgox-stub.js'),
    trades_db = require('./trades-db.js');

// serve static resources
var webroot = new static.Server('./webroot');
var httpServer = http.createServer(function (req, res) {
    webroot.serve(req, res);
}).listen(1337, function () {});

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

