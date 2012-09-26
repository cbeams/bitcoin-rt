
// Simulate MtGox data, for offline use

// export function to register listeners of new trades

var listeners = [];
exports.registerListener = function(listener) {
    listeners.push(listener);
}

console.log("Starting mock MtGox service");

setInterval(function() {
  
    var trade = {
        "type" : "trade",
        "exchange" : "mtgoxUSD",
        "date" : Math.round(new Date().getTime() / 1000) * 1000,
        "amount" : Math.floor(Math.random() * 20),
        "price" : 12.239,
        "price_currency" : "USD",
        "txid" : "1348679989121772"
    };
  
    console.log('sending trade ' + JSON.stringify(trade));
    
    listeners.forEach(function(listener) {
      listener(trade);
    });
    
}, 7000);
