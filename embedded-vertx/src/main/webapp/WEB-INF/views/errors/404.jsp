<%@ include file="/WEB-INF/views/includes/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>Spring Integration Template</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="description" content="">
		<meta name="author" content="">

		<!-- Le styles -->
		<link href="<c:url value='/assets/css/bootstrap.css'/>" rel="stylesheet" />
		<link href="<c:url value='/assets/css/custom.css'/>" rel="stylesheet" />
		<link href="<c:url value='/assets/css/bootstrap-responsive.css'/>" rel="stylesheet" />

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
					<a class="brand" href="#">Twitter Template</a>
					<div class="nav-collapse">
						<ul class="nav">
							<li class="active"><a href='<c:url value="/"/>'>Home</a></li>
						</ul>
					</div><!--/.nav-collapse -->
				</div>
			</div>
		</div>

		<div class="container">
			<div class="page-header">
				<h1>Unresolved View!</h1>
			</div>
			<p>
				We were unable to render the requested view. Other representations
				of the data may however exist.
			</p>

		</div> <!-- /container -->
	</body>
</html>
