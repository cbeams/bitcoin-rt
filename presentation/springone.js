$(function() {
	$("#preso").bind("showoff:loaded", function (event) {

		$("body").append('<div id="copyright"><span>&copy; 2012 SpringOne 2GX. All rights reserved.</span></div>')
		if($(".cover").is(':visible')) {
			$("#footer").hide();
		} else {
			$("#copyright").hide();
		}
		
		$(".cover").bind("showoff:show", function (event) {
			$("#footer").hide();
			$("#copyright").show();
		});

		$(".cover").bind("showoff:next", function (event) {
			$("#footer").show();
			$("#copyright").hide();
		});
	});
});

function toggleFooter() {
	$("#footer").toggle();
	if($(".cover").is(':visible')) {
		$("#copyright").toggle();
	}
}