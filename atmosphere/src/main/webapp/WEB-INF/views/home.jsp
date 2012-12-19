<%@ include file="/WEB-INF/views/includes/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>bitcoin-rt - Atmosphere</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="description" content="">
		<meta name="author" content="">

		<!-- Le styles -->

		<link href="<c:url value='/assets/css/bootstrap.css'/>" rel="stylesheet" />
		<link href="<c:url value='/assets/css/custom.css'/>" rel="stylesheet" />
		<link href="<c:url value='/assets/css/bootstrap-responsive.css'/>" rel="stylesheet" />
		<link href="<c:url value='/assets/css/scales.css'/>" rel="stylesheet" />

		<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
		<!--[if lt IE 9]>
			<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->

		<!-- Le fav and touch icons -->
		<link rel="shortcut icon" href="<c:url value='/assets/ico/favicon.ico'/>">
		<link rel="apple-touch-icon-precomposed" sizes="144x144" href="<c:url value='/assets/ico/apple-touch-icon-144-precomposed.png'/>">
		<link rel="apple-touch-icon-precomposed" sizes="114x114" href="<c:url value='/assets/ico/apple-touch-icon-114-precomposed.png'/>">
		<link rel="apple-touch-icon-precomposed" sizes="72x72" href="<c:url value='/assets/ico/apple-touch-icon-72-precomposed.png'/>">
		<link rel="apple-touch-icon-precomposed" href="<c:url value='/assets/ico/apple-touch-icon-57-precomposed.png'/>">
	</head>

	<body>

		<div class="navbar navbar-fixed-top">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</a>
					<a class="brand" href="#">bitcoin-rt</a>
					<div class="nav-collapse">
						<ul class="nav">
							<li class="active"><a href="#">Home</a></li>
							<li><a href="#aboutModal" data-toggle="modal">About</a></li>
							<li class="dropdown" id="menu-resources">
								<a class="dropdown-toggle" data-toggle="dropdown" href="#menu-resources">
									Resources
									<b class="caret"></b>
								</a>
								<ul class="dropdown-menu">
									<li><a href="https://github.com/cbeams/bitcoin-rt"><i class="icon-home"></i> bitcoin-rt Project</a></li>
									<li><a href="https://github.com/Atmosphere/atmosphere"><i class="icon-home"></i> Atmosphere</a></li>
								</ul>
							</li>
						</ul>
					</div><!--/.nav-collapse -->
				</div>
			</div>
		</div>

		<div class="container">
			<div class="page-header">
				<h1>bitcoin-rt <small>Atmosphere</small></h1>
			</div>

			<div class="row">
				<div class="span8" id="tweets">
					<div id="chart"></div>
				</div>
				<div class="span2">
					<div>
						Bitcoin Control
					</div>
					<button id="control" class="btn btn-warning disabled"><i class="icon-stop"></i> <span>Pause</span></button>
				</div>
			</div>

		</div> <!-- /container -->

		<div class="navbar navbar-fixed-bottom">
			<div class="navbar-inner">
				<div class="container">
					<a class="brand" href="http://www.springsource.org/"><img alt="SpringSource"
							title="SpringSource" src="${ctx}/assets/img/spring/SpringSource-logo.png"></a>
				</div>
			</div>
		</div>

		<div id="aboutModal" class="modal" style="display: none;">
			<div class="modal-header">
				<button class="close" data-dismiss="modal" type="button">×</button>
				<h3>About...</h3>
			</div>
			<div class="modal-body">
				<h4>bitcoin-rt for Atmosphere</h4>
				<p>
					This examples is a clone of
					<a href="http://bitcoinmonitor.com">http://bitcoinmonitor.com</a>
					using WebSockets instead of long polling and d3 instead of
					jQuery UI. The idea is to visualize all transactions in the
					global Bitcoin network.
				</p>
				<p>
					If you don't know what Bitcoin is, or don't know much about it,
					you should watch this <a href="http://www.weusecoins.com/">two
					minute video</a>.
				</p>
				<p>
					This implementation uses the
					<a href="https://github.com/Atmosphere/atmosphere">Atmosphere
					Framework</a> to implement client-server communication via
					WebSockets (incl. fallbacks).
				</p>
				<hr>
			</div>
			<div class="modal-footer">
				<a class="btn" data-dismiss="modal" href="#">Close</a>
			</div>
		</div>

		<!-- Le javascript
		=================================================================== -->

		<%-- <script src="<c:url value='/wro/g1.js'/>"></script> --%>

		<script src="<c:url value='/assets/js/jquery.js'/>"></script>
		<script src="<c:url value='/assets/js/bootstrap.js'/>"></script>
		<script src="<c:url value='/assets/js/jquery.atmosphere.js'/>"></script>
		<script src="<c:url value='/assets/js/custom.js'/>"></script>

		<script type="text/javascript" src="<c:url value='/assets/js/d3.js'/>"></script>
		<script src="<c:url value='/assets/js/custom-bitcoin-chart.js'/>"></script>

		<script type="text/javascript">

			$(function() {

				if (!window.console) {
					console = {log: function() {}};
				}

				$.mynamespace = {
					bitcoinChart : null,
					trades: []
				};

				var socket = $.atmosphere;
				var transport = 'websocket';
				var websocketUrl = "${fn:replace(r.requestURL, r.requestURI, '')}${r.contextPath}/websockets/";

				console.log('websocketUrl: ' + websocketUrl);

				var request = {
						url: websocketUrl,
						contentType : "application/json",
						logLevel : 'debug',
						transport : transport ,
						fallbackTransport: 'long-polling',
						onMessage: onMessage,
						onOpen: function(response) {
							console.log('Atmosphere onOpen: Atmosphere connected using ' + response.transport);
							transport = response.transport;
						},
						onReconnect: function (request, response) {
							console.log("Atmosphere onReconnect: Reconnecting");
						},
						onClose: function(response) {
							console.log('Atmosphere onClose executed');
						},

						onError: function(response) {
							console.log('Atmosphere onError: Sorry, but there is some problem with your '
								+ 'socket or the server is down');
						}
				};

				var subSocket = socket.subscribe(request);

				function onMessage(response) {
					var message = response.responseBody;
					console.log('message: ' + message);

					var result;

					try {
						result =  $.parseJSON(message);
					} catch (e) {
						console.log("An error ocurred while parsing the JSON Data: " + message.data + "; Error: " + e);
						return;
					}

					var trade = result.trade;

				    trade.date = trade.date * 1000;
				    console.log(trade);

					$.mynamespace.trades.push(trade);

				}

				$.mynamespace.bitcoinChart = BitcoinChart();
				$.mynamespace.bitcoinChart.start();
				$("#control").removeClass("disabled");
			});
		</script>
	</body>
</html>
