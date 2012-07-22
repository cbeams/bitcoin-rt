
// Retrieve trade data from the MtGox bitcoin exchange
//
//   https://en.bitcoin.it/wiki/MtGox/API/HTTP/v1
//   https://en.bitcoin.it/wiki/MtGox/API/Streaming

var MTGOX_TRADES_CHANNEL = 'dbf1dee9-4f2e-4a08-8cb7-748919a71b21',
    MTGOX_TICKER_CHANNEL = 'd5f06780-30a8-4a48-a2f8-7ed181b4a13f',
    MTGOX_DEPTH_CHANNEL = '24e67e0d-1cad-4cc0-9e7a-f8523ef460fe';

// Export a function to register 'New Trade' callbacks
var tradeCallbacks = [];
exports.addNewTradeCallback = function(callback) {
    tradeCallbacks.push(callback);
}

var WebSocketClient = require('websocket').client;
var conn = new WebSocketClient();
conn.connect('ws://websocket.mtgox.com:80/mtgox');

conn.on('connect', function (conn) {
    console.log('connected to mtgox, unsubscribing from ticker and depth channels');
    conn.send('{"op":"unsubscribe","channel":"' + MTGOX_TICKER_CHANNEL + '"}');
    conn.send('{"op":"unsubscribe","channel":"' + MTGOX_DEPTH_CHANNEL + '"}');

    conn.on('message', function (message) {
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

        tradeCallbacks.forEach(function(callback) {
            callback(trade);
        });
    });

    conn.on('error', function (error) {
        console.log(error);
    });
});

conn.on('close', function (conn) {
    console.log('disconnected by server from mtgox');
    // TODO: reconnect!
});

