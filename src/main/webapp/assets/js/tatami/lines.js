function refreshCurrentLine()
{
	var restURL = $('#tweetsPanel div.tab-pane.active td[data-url]').attr('data-url');
	var tweetsNb = $('#tweetsPanel div.tab-pane.active tbody tr.data').size();
	var $targetTable = $('#tweetsPanel div.tab-pane.active .lineContent');
	
	restURL = restURL.replace(tweetsNbRegExp,tweetsNb)+' .lineContent tr';
	
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
        	$('#favTab').tab('show');
        	refreshCurrentLine();
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
        	$('#favTab').tab('show');
        	refreshCurrentLine();
        }
    });
	
	return false;
}

function loadUserline(targetUserLogin)
{
	if(targetUserLogin != null)
	{
		$('#userTweetsList').empty();
		$('#userTab').tab('show');	
		$('#userTweetsList').load('fragments/'+targetUserLogin+'/userline.html .lineContent tr',function()
		{
			bindListeners($('#userTweetsList'));
		});
	}
}

function loadTagsline(tag)
{
	
	if(tag != null)
	{
		$('#tagTweetsList').empty();
		$('#tagTab').tab('show');	
		$('#tagTweetsList').load('fragments/'+tag+'/'+defaultNbTags+'/tagline.html .lineContent tr',function()
		{
			bindListeners($('#tagTweetsList'));
		});
	}	
	
}
