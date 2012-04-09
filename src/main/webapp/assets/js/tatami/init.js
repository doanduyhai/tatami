function initTimeline()
{
	refreshLine('timelinePanel',1,DEFAULT_TWEET_LIST_SIZE,true,null,null);
}

function initFavoritesline()
{
	refreshLine('favlinePanel',1,DEFAULT_TAG_LIST_SIZE,true,null,null);	
}

google.load("visualization", "1", {packages:["corechart"]});

var clickFromLink = false;

!function ( $ ) {

	// left panel
	loadHome();
	loadProfile();
	refreshUserSuggestions();
	
    // auto-refresh
    $('a[data-toggle="pill"]').on('show', function(e) {
    	if (e.target.hash == '#homeTabContent') {
    		updateUserCounters();
    	} 
    });

    
    
    // auto-refresh
    $('a[data-toggle="tab"]').on('show', function(e) {
    	if (e.target.hash == '#timelinePanel' || e.target.hash == '#userlinePanel' || e.target.hash == '#taglinePanel') {
    		if(!clickFromLink)
    		{	
    			setTimeout(refreshCurrentLine,10);
    		}	
    	}
    	else if (e.target.hash == '#piechartPanel') {
			refreshPieChart();
    	} 
    	else if (e.target.hash == '#punchchartPanel') {
			refreshPunchChart();
        }
    });
    
    // browser's refresh shortcut override
	shortcut.add("Ctrl+R", function() {
		refreshCurrentLine();
	});
	
	$(function() {

	    // right panel
	    initFavoritesline();
	    initTimeline();

		// Register refresh handler for all lines
		registerRefreshLineListeners();
		registerUserDetailsPopOver($('#userSuggestions'));
		registerFetchTweetHandlers();
		registerUserSearchListener();
		$('#picture').click(function()
		{
			var login = $('#picture').attr('data-user');
			showUserProfile(login);
			return false;
		});
	});

}( window.jQuery );



