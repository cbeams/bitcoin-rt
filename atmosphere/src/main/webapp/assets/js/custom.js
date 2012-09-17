/** Contains custom scripts */

$(document).ready(function() {

	$("#control").click(function () {

		var buttonText = $(this).children('span');
		var buttonIcon = $(this).children('i');
		console.log('clicked: ' + buttonText.html());

		if (buttonText.html() === "Resume") {
			$.mynamespace.bitcoinChart.start();
			buttonText.html("Pause");
			$(this).removeClass("btn-success").addClass("btn-warning");
			buttonIcon.removeClass("icon-play").addClass("icon-stop");
		}
		else {
			$.mynamespace.bitcoinChart.stop();
			buttonText.html("Resume");
			$(this).removeClass("btn-warning").addClass("btn-success");
			buttonIcon.removeClass("icon-stop").addClass("icon-play");
		}

	});

});


(function($) {

})(jQuery);
