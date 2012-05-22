
// Store retrieved trade data to MongoDB

var mongo = require('mongodb');

// connect and ensure mongo is up
var server = new mongo.Server('localhost', 27017, {auto_reconnect: true});
var db = new mongo.Db('bitcoin', server);

db.open(function(err, db) {
    if(err) { throw err; }
});

// save trade data to mongo
// see http://mongodb.github.com/node-mongodb-native/api-articles/nodekoarticle1.html#and-then-there-was-crud
exports.saveTrade = function(trade) {
    db.collection('trades', function (err, collection) {
        collection.insert(trade);
    });
}

// iterate all saved trades and invoke the given callback
// TODO: limit to a date range communicated by the client
// see http://mongodb.github.com/node-mongodb-native/api-articles/nodekoarticle1.html#time-to-query
exports.forEachTrade = function(callback) {
    db.collection('trades', function(err, collection) {
        var stream = collection.find({}).streamRecords();
        stream.on('data', function(trade) {
            callback(trade);
        });
    });
}

