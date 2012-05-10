var clickFromLink = false;
var directContatTabClick = true;

!function ( $ ) {

	// left panel
	loadProfile();
	loadSuggestions();
	
	
    // Tweet lines refresh
    $('a[data-toggle="tab"]').on('shown', function(e) {
    	if (e.target.hash == '#timelinePanel' || e.target.hash == '#userlinePanel' 
    		|| e.target.hash == '#taglinePanel' || e.target.hash == '#favlinePanel') {
    		if(!clickFromLink)
    		{	
    			refreshCurrentLine();
    		}	
    	}
    	
    });
    
    
	$(function() {
		
		$.ajaxSetup({
			statusCode: 
			{
				901 : function () {
						window.location.replace("./login");
					}
				
			}
		});
		// Bind click handler for "Tweet" button
		$('#tweetButton').click(tweet);
		
		//Load tweet lines
		loadEmptyLines();
		
		//Load user lines
		loadEmptyUserLines();
		
		$('#picture').click(function()
		{
			var user = $('#picture').attr('data-user');
			showUserProfile(user);
			return false;
		});

		registerUserProfileModalListeners();
		registerHomePanelListeners();
	});
	

}( window.jQuery );


