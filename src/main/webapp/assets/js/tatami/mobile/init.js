google.load("visualization", "1", {packages:["corechart"]});

var clickFromLink = false;

!function ( $ ) {
    	
	$(function() {

		// left panel
		loadProfile();
		loadWhoToFollow();
		loadEmptyLines();	
	});

}( window.jQuery );


