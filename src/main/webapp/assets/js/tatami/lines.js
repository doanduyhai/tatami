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
//function loadFavoritesline()
//{
//	$('#favTweetsList').empty();
//	$('#favTab').tab('show');	
//	
//	$('#favTweetsList').load('fragments/favline.html .lineContent tr',function()
//	{
//		bindListeners($('#favTweetsList'));
//	});	
//}


//function loadTimeline(nbTweets)
//{
//	if(nbTweets == null)
//	{
//		nbTweets = defaultNbTweets;
//	}
//
//	$('#timeLinePanel').empty();
//	$('#mainTab').tab('show');	
//	$('#timeLinePanel').load('fragments/'+nbTweets+'/timeline.html #timeline',function()
//	{
//		$('#refreshTimeline').click(function()
//		{
//			loadTimeline();
//			return false;
//		});
//		bindListeners($('#timeline'));		
//	});
//}

	

//function loadUserline(targetUserLogin)
//{
//	$('#userLinePanel').empty();
//
//	
//	if(targetUserLogin != null)
//	{
//		$('#userTab').tab('show');	
//		$('#userLinePanel').load('fragments/'+targetUserLogin+'/userline.html #userline',function()
//		{
//			bindListeners($('#userline'));
//		});
//	}
//	else
//	{
//		$('#userLinePanel').load('fragments/userline.html #userline');
//	}
//}






//function loadTagsline(tag)
//{
//	$('#tagLinePanel').empty();
//	$('#tagTab').tab('show');	
//	
//	if(tag != null)
//	{
//		$('#tagLinePanel').load('fragments/'+tag+'/'+defaultNbTags+'/tagline.html #tagline',function()
//		{
//			bindListeners($('#tagline'));
//		});
//	}	
//	else
//	{
//		$('#tagLinePanel').load('fragments/'+defaultNbTags+'/tagline.html #tagline',function()
//		{
//			bindListeners($('#tagline'));
//		});		
//	}	
//}
