
var http = require('http'),
    static = require('node-static'),
    sockjs = require('sockjs'),
    mtgox = require('./mtgox.js'),
    trades_db = require('./trades-db.js');

// serve static resources
var webroot = new static.Server('./webroot');
var httpServer = http.createServer(function (req, res) {
    webroot.serve(req, res);
}).listen(1337, function () { });

// holds client, websocket connections
var clients = [];

var sockjs_options = {
  sockjs_url: "http://cdn.sockjs.org/sockjs-0.3.min.js", 
  prefix:'/bitcoin'
};

sockjs.createServer(sockjs_options)
    .on('connection', function(conn) {
        clients.push(conn);

        var trades = trades_db.forEachTrade(function (trade) {
            conn.write(JSON.stringify(trade));
        });

        // TODO: register 'close' handler

    }).installHandlers(httpServer);

function removeDisconnectedClients() {
    clients.filter(function (conn) { 
        return conn.connected == false 
    }).forEach(function (conn) {
        clients.splice(clients.indexOf(conn), 1);
    });
}

mtgox.addNewTradeCallback(function(trade) {
    trades_db.saveTrade(trade);
    clients.forEach(function (conn) {
        conn.write(JSON.stringify(trade));
    });
});

