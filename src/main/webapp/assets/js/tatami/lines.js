function refreshCurrentLine()
{
	var restURL = $('#tweetsPanel div.tab-pane.active td[data-url]').attr('data-url');
	var tweetsNb = $('#tweetsPanel div.tab-pane.active tbody tr.data').size();
	var $targetTable = $('#tweetsPanel div.tab-pane.active .lineContent');
	
	restURL = restURL.replace(TWEET_NB_REGEXP,tweetsNb)+' .lineContent tr';
	$targetTable.empty();
	$targetTable.load(restURL,function()
	{
		bindListeners($targetTable);
	});	
	

	return false;
}	

function refreshTimeline()
{
	$('#mainTab').tab('show');
	refreshCurrentLine();
}

function addFavoriteTweet(tweet) {
	
	$.ajax({
		type: 'GET',
		url: "rest/likeTweet/" + tweet,
		dataType: "json",
        success: function()
        {
			setTimeout(function()
			{
	        	$('#favTab').tab('show');
	        	refreshCurrentLine();
			},300);	

        }
    });
	
	return false;
}


function removeFavoriteTweet(tweet) {
	$.ajax({
		type: 'GET',
		url: "rest/unlikeTweet/" + tweet,
		dataType: "json",
        success: function()
        {
			setTimeout(function()
			{
	        	$('#favTab').tab('show');
	        	refreshCurrentLine();
			},300);	        	

        }
    });
	
	return false;
}

function loadUserline(targetUserLogin)
{
	if(targetUserLogin != null)
	{
		$('#userTweetsList').empty();
		clickFromLink = true;
		$('#userTab').tab('show');
		jQuery.ajaxSetup({async:false});
		$('#userTweetsList').load('fragments/'+targetUserLogin+'/'+DEFAULT_TWEET_LIST_SIZE+'/userline.html .lineContent tr',function()
		{
			bindListeners($('#userTweetsList'));
			clickFromLink = false;
			jQuery.ajaxSetup({async:true});

		});
	}
}

function loadTagsline(tag)
{
	if(tag != null)
	{
		$('#tagTweetsList').empty();
		clickFromLink = true;
		$('#tagTab').tab('show');
		jQuery.ajaxSetup({async:false});
		$('#tagTweetsList').load('fragments/'+tag+'/'+DEFAULT_TAG_LIST_SIZE+'/tagline.html .lineContent tr',function()
		{
			bindListeners($('#tagTweetsList'));
			clickFromLink = false;
			jQuery.ajaxSetup({async:true});
		});
	}	
	
}
