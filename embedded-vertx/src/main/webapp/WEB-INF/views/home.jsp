<%@ include file="/WEB-INF/views/includes/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>bitcoin-rt - vert.x</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="${ctx}/css/bootstrap.css" rel="stylesheet" />
    <link href="${ctx}/css/custom.css" rel="stylesheet" />
    <link href="${ctx}/css/bootstrap-responsive.css" rel="stylesheet" />
    <link href="${ctx}/css/scales.css" rel="stylesheet" />

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <link rel="shortcut icon" href="${ctx}/ico/favicon.ico">
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="${ctx}/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="${ctx}/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="${ctx}/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="${ctx}/ico/apple-touch-icon-57-precomposed.png">
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
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container">
      <div class="page-header">
        <h1>bitcoin-rt <small>vert.x</small></h1>
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
              title="SpringSource" src="${ctx}/img/spring/SpringSource-logo.png"></a>
        </div>
      </div>
    </div>

    <div id="aboutModal" class="modal" style="display: none;">
      <div class="modal-header">
        <button class="close" data-dismiss="modal" type="button">×</button>
        <h3>About...</h3>
      </div>
      <div class="modal-body">
        <h4>bitcoin-rt vert.x</h4>
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
          This implementation uses vert.x.
        </p>
        <hr>
      </div>
      <div class="modal-footer">
        <a class="btn" data-dismiss="modal" href="#">Close</a>
      </div>
    </div>

	<script type="text/javascript" src="${ctx}/js/jquery.js"></script>
    <script src="${ctx}/js/bootstrap.js"></script>
    <script src="http://cdn.sockjs.org/sockjs-0.3.min.js"></script>
    <script src="${ctx}/js/vertxbus.js"></script>
    <script type="text/javascript" src="${ctx}/js/d3.js"></script>
    <script type="text/javascript" src="${ctx}/js/app.js"></script>
    <script type="text/javascript" src="${ctx}/js/custom.js"></script>

  </body>
</html>
