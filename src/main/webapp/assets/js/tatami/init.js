function initTimeline()
{
	$('#mainTab').tab('show');	
	$('#tweetsList').load('fragments/'+defaultNbTweets+'/timeline.html .lineContent tr',function()
	{
		bindListeners($('#tweetsList'));		
	});
}

function initFavoritesline()
{
	$('#favTweetsList').load('fragments/favline.html .lineContent tr',function()
	{
		bindListeners($('#favTweetsList'));
	});	
}

google.load("visualization", "1", {packages:["corechart"]});

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

    // right panel
    initFavoritesline();
    initTimeline();		    
    
    // auto-refresh
    $('a[data-toggle="tab"]').on('show', function(e) {
    	if (e.target.hash == '#piechartPanel') {
			refreshPieChart();
    	} else if (e.target.hash == '#punchchartPanel') {
			refreshPunchChart();
        }
    });
    
    // browser's refresh shortcut override
	shortcut.add("Ctrl+R", function() {
		loadTimeline();
	});

    // infinite scroll
	$(window).scroll(function() { 
		if ($('#timeline').is(':visible') && $(window).scrollTop() >= $(document).height() - $(window).height()) {
			listTweets(false);
		}
	});
	
	$(function() {
		
		// Register refresh handler for all lines
		$('.refreshLineIcon').click(refreshCurrentLine);
		registerUserDetailsPopOver();
		
	});
}( window.jQuery );