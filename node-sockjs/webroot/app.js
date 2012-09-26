(function() {

  var trades = [];

  console.log("opening sockjs socket");

  var socket = new SockJS('/bitcoin');

  socket.onopen = function(event) {
    // no-op
  };

  socket.onmessage = function(event) {
    var trade = JSON.parse(event.data);
    trades.push(trade);
  };

  socket.onerror = function(event) {
    console.log("A WebSocket error occured");
    console.log(event);
  };

  socket.onclose = function(event) {
    console.log("Remote host closed or refused WebSocket connection");
    console.log(event);
  };

  function BitcoinChart() {

    var refreshFrequency = 750,
        timespan = 5 * 60 * 1000;

    var chartElement, timeScale, timeAxis, timeline, amountScale;

    var keepDrawing = true;

    function init() {
      var margin = {top: 6, right: 0, bottom: 20, left: 40},
          width = 960 - margin.right,
          height = 200 - margin.top - margin.bottom;

      timeScale = d3.time.scale().range([0, width]);
      timeAxis = d3.svg.axis().scale(timeScale).orient("bottom");
      amountScale = d3.scale.log().domain([0.01, 10000]).range([height, 0]);

      chartElement = d3.select("div#chart")
            .append("svg")
            .attr("width", 1020)
            .attr("height", 200)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

      timeline = chartElement.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(timeAxis);

      chartElement.append("g")
          .attr("class", "y axis")
          .call(d3.svg.axis().scale(amountScale).orient("left"));
    }

    function redraw() {

      if (!keepDrawing) {
        return;
      }

      // remove trades as they approach the origin of the time-axis
      while (trades.length > 0 && timeScale(trades[0].date - refreshFrequency) < 0) {
        trades.shift();
      }

      // join trade data to points on the graph
      // see http://bost.ocks.org/mike/join
      var circle = chartElement.selectAll("circle")
          .data(trades, function(d, i) { return d._id });

      circle.enter().append("circle")
          .style("stroke", "gray")
          .style("fill", "red")
          .attr("cx", function(d, i) { return timeScale(d.date) })
          .attr("cy", function(d, i) { return amountScale(d.amount) })
          .attr("r", 0)
          .transition().duration(refreshFrequency)
          .attr("r", 5);

      circle.transition()
          .duration(refreshFrequency)
          .ease("linear")
          .attr("cx", function(d) { return timeScale(d.date) });

      circle.exit().transition()
          .duration(refreshFrequency)
          .attr("r", 0)
          .ease("linear")
          .attr("cx", function(d) { return timeScale(d.date) })
          .remove();

      // update the timeline range
      var now = Date.now();
      timeScale.domain([now - timespan, now - refreshFrequency]);

      // slide the time-axis left
      timeline.transition()
            .duration(refreshFrequency)
            .ease("linear")
            .call(timeAxis)
            .each("end", redraw);
    }

    init();

    return {
      start : function() {
        keepDrawing = true;
        redraw();
      },
      stop : function() {
        keepDrawing = false;
      }
    }

  }

  var bitcoinChart = BitcoinChart();
  
  var button = d3.select("#control");
  button.on("click", function() {
    if (button.text() === "Resume") {
      bitcoinChart.start();
      button.text("Pause");
    }
    else {
      bitcoinChart.stop();
      button.text("Resume");
    }
  });

  bitcoinChart.start();

})();


