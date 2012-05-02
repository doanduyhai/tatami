function initTimeline()
{
	refreshLine('timelinePanel',null,DEFAULT_TWEET_LIST_SIZE,true);
}

function initFavoritesline()
{
	refreshLine('favlinePanel',null,DEFAULT_TWEET_LIST_SIZE,true);	
}

google.load("visualization", "1", {packages:["corechart"]});

var clickFromLink = false;
var directContatTabClick = true;

!function ( $ ) {

	// left panel
	loadHome();
	loadProfile();
	refreshUserSuggestions();
	
    // auto-refresh
    $('a[data-toggle="pill"]').on('shown', function(e) {
    	if (e.target.hash == '#homeTabContent') {
    		updateUserCounters();
    	} 
    });

    
    
    // auto-refresh
    $('a[data-toggle="tab"]').on('shown', function(e) {
    	if (e.target.hash == '#timelinePanel' || e.target.hash == '#userlinePanel' || e.target.hash == '#taglinePanel') {
    		setTimeout(refreshCurrentLine,10);
    	}
    	else if (e.target.hash == '#piechartPanel') {
			refreshPieChart();
    	} 
    	else if (e.target.hash == '#punchchartPanel') {
			refreshPunchChart();
        }
    });
    
    $('a[data-toggle="pill"]').on('shown', function(e) {
    	if (e.target.hash == '#friendsLine') 
    	{
    		if(directContatTabClick)
    		{
    			$('#friendsLine h2').html("My friends").removeClass('red');
    			$('#friendsLine footer').attr('data-userFetch-key',login);
    		}
    		refreshCurrentUserLine();
    	}
    	else if (e.target.hash == '#followersLine')
    	{
    		if(directContatTabClick)
    		{
    			$('#followersLine h2').html("My followers").removeClass('red');
    			$('#followersLine footer').attr('data-userFetch-key',login);
    		}
    		refreshCurrentUserLine();
    	}	
    });
    
    
    // browser's refresh shortcut override
	shortcut.add("Ctrl+R", function() {
		refreshCurrentLine();
		refreshCurrentUserLine();
	});
	
	$(function() {

		$.ajaxSetup({
			statusCode: 
			{
				901 : sessionTimeOutPopup
			}
		});
		
	    // right panel
	    initFavoritesline();
	    initTimeline();

		// Register refresh handler for all tweet lines
		registerRefreshLineListeners();
		registerFetchTweetHandlers();
		
		// Register refresh handler for all user lines
		registerRefreshUserLineListeners();
		registerFetchUserHandlers();
		
		registerUserDetailsPopOver($('#userSuggestions'));
		registerUserSearchListener();
		registerUserProfileModalListeners();
		registerLoginRedirectListener();
		
		
		
		$('#picture').click(function()
		{
			var user = $('#picture').attr('data-user');
			showUserProfile(user);
			return false;
		});
	});

}( window.jQuery );



