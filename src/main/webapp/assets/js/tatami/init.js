function initTimeline()
{
	$('#mainTab').tab('show');	
	$('#tweetsList').load('fragments/'+DEFAULT_TWEET_LIST_SIZE+'/timeline.html .lineContent tr',function()
	{
		bindListeners($('#tweetsList'));
	});
}

function initFavoritesline()
{
	$('#favTweetsList').load('fragments/'+DEFAULT_FAVORITE_LIST_SIZE+'/favline.html .lineContent tr',function()
	{
		bindListeners($('#favTweetsList'));
	});	
}

google.load("visualization", "1", {packages:["corechart"]});

var clickFromLink = false;

!function ( $ ) {
	
	// left panel
	loadHome();
	loadProfile();
	loadWhoToFollow();
	
    // auto-refresh
    $('a[data-toggle="pill"]').on('show', function(e) {
    	if (e.target.hash == '#homeTabContent') {
    		updateUserCounters();
    	} 
    });

    
    
    // auto-refresh
    $('a[data-toggle="tab"]').on('show', function(e) {
    	if (e.target.hash == '#timeLinePanel' || e.target.hash == '#userLinePanel' || e.target.hash == '#tagLinePanel') {
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
		$('#picture').click(function()
		{
			var login = $('#picture').attr('data-user');
			showUserProfile(login);
			return false;
		});
	});

}( window.jQuery );


