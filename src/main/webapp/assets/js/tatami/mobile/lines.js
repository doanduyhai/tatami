/*
 * Tweet actions 
 */
function addFavoriteTweet(tweet) {
	
	$.ajax({
		type: HTTP_GET,
		url: "rest/likeTweet/" + tweet,
		dataType: JSON_DATA,
        success: function()
        {
			setTimeout(function()
			{
	        	$('#favoriteTab').tab('show');
	        	refreshCurrentLine();
			},300);	

        }
    });
	
	return false;
}


function removeFavoriteTweet(tweet) {
	$.ajax({
		type: HTTP_GET,
		url: "rest/unlikeTweet/" + tweet,
		dataType: JSON_DATA,
        success: function()
        {
			setTimeout(function()
			{
	        	$('#favoriteTab').tab('show');
	        	refreshCurrentLine();
			},300);	        	

        }
    });
	
	return false;
}

/*
 *  Lines activation
 */
function initTimeline()
{
	refreshLine('timelinePanel',null,DEFAULT_TWEET_LIST_SIZE,true);
}

function initFavoritesline()
{
	refreshLine('favlinePanel',null,DEFAULT_TWEET_LIST_SIZE,true);	
}

function loadEmptyLines()
{
	$('#timelinePanel').load('fragments/mobile/timeline.html #timeline',function()
	{
		initTimeline();
		bindListeners($('#tweetsList'));
		registerRefreshLineListeners($('#timelinePanel'));
		registerFetchTweetHandlers($('#timelinePanel'));
	});
	
	
	$('#favlinePanel').load('fragments/mobile/favline.html #favline',function()
	{
		initFavoritesline();
		bindListeners($('#favTweetsList'));
		registerRefreshLineListeners($('#favlinePanel'));
		registerFetchTweetHandlers($('#favlinePanel'));
	});

	$('#userlinePanel').load('fragments/mobile/userline.html #userline',function()
	{
		registerRefreshLineListeners($('#userlinePanel'));
		registerFetchTweetHandlers($('#userlinePanel'));
	});	

	$('#taglinePanel').load('fragments/mobile/tagline.html #tagline',function()
	{
		registerRefreshLineListeners($('#taglinePanel'));
		registerFetchTweetHandlers($('#taglinePanel'));
	});	
}


function loadUserline(targetUserLogin)
{
	if(targetUserLogin != null)
	{
		$('#userTweetsList').empty();
		$('#userlinePanel footer').attr('data-tweetFetch-key',targetUserLogin);
		$('#userlineTab').tab('show');
	}
}

function loadTagsline(tag)
{
	if(tag != null)
	{
		$('#tagTweetsList').empty();
		$('#taglinePanel footer').attr('data-tweetFetch-key',tag);
		$('#taglineTab').tab('show');		
	}	
	
}

/*
 *  Lines refresh
 */
function refreshTimeline()
{
	$('#timelineTab').tab('show');
	refreshCurrentLine();
}

function refreshCurrentLine()
{
	var tweetsNb = $('#dataContentPanel div.tab-pane.active tbody tr.data').size();
	var targetLine = $('#dataContentPanel div.tab-pane.active').attr('id');
	
	refreshLine(targetLine,null,tweetsNb,true);	

	return false;
}	

function refreshLine(targetLine,startTweetId,count,clearAll)
{
	var data_tweetFetch_url = $('#'+targetLine+' footer').attr('data-tweetFetch-url');
	var data_tweetFetch_type = $('#'+targetLine+' footer').attr('data-tweetFetch-type');
	var data_tweetFetch_key = $('#'+targetLine+' footer').attr('data-tweetFetch-key');
	var $tableBody = $('#'+targetLine+' .lineContent');

	var tweetFetchRangeObject = buildTweetFetchRange(startTweetId,count,data_tweetFetch_key);
	
	$.ajax({
		type: HTTP_POST,
		url: data_tweetFetch_url,
		contentType: JSON_CONTENT,
        data:  JSON.stringify(tweetFetchRangeObject),		
		dataType: JSON_DATA,
        success: function(data)
        {
        	if(data.length>0)
    		{
        		if(clearAll)
        		{
        			$tableBody.empty();
            		$('#tweetPaddingTemplate tr').clone().appendTo($tableBody);
            		$('#tweetPaddingTemplate tr').clone().appendTo($tableBody);
        		}
        		else
        		{
        			$tableBody.find('tr:last-child').remove();
        		}
        		
	        	$.each(data,function(index, tweet)
	        	{        		
	        		$tableBody.append(fillTweetTemplate(tweet,data_tweetFetch_type));
	        	});
	        	
	        	$('#tweetPaddingTemplate tr').clone().appendTo($tableBody).show();
    		}
        	else if(clearAll)
    		{
        		$tableBody.empty();
    		}
        }
    });	
}

function buildTweetFetchRange(startTweetId,count,functionalKey)
{
	return {
		startTweetId: startTweetId,
		count: count,
		functionalKey: functionalKey
	};
}

/*
 * Handlers registration
 */
function registerRefreshLineListeners($target)
{
	$target.find('.refreshLineIcon').click(refreshCurrentLine);
}

function registerFetchTweetHandlers($target)
{
	$target.find('.tweetPagingButton').click(function(event)
	{
		var $target = $(event.target);
		var tweetsNb = $target.closest('footer').find('.pageSelector option').filter(':selected').val(); 
		var targetLine =  $target.closest('div.tab-pane.active').attr('id');
		var startTweetId = $target.closest('footer').closest('div').find('.lineContent tr.data').last().find('article').attr('data-tweetId');
		refreshLine(targetLine,startTweetId,parseInt(tweetsNb),false);
				
		return false;
	});
}

/*
 *  Tweet template handling
 */
function fillTweetTemplate(tweet,data_line_type)
{
	$newTweetLine = $('#tweetTemplate').clone().attr('id','');
	
	$newTweetLine.find('.tweetGravatar').attr('data-user',tweet.login).attr('src','http://www.gravatar.com/avatar/'+tweet.gravatar+'?s=32');
	
	if(data_line_type != 'userline')
	{
		if(login != tweet.login)
		{
			$newTweetLine.find('article strong').empty().html(tweet.firstName+' '+tweet.lastName+' &nbsp;')
			.after('<a class="tweetAuthor" href="#" data-user="'+tweet.login+'" title="Show '+tweet.login+' tweets"><em>@'+tweet.login+'</em></a><br/>');
		}
	}	
	else
	{
		$newTweetLine.find('article strong').empty().html(tweet.firstName+' '+tweet.lastName+'<br/>');
	}
	
	$newTweetLine.find('article span').html(tweet.content);
	
	// Conditional rendering of Follow icon
	if(data_line_type != 'timeline' && tweet.authorFollow)
	{	
		$newTweetLine.find('.tweetFriend').append('<a href="#" title="Follow" data-follow="'+tweet.login+'"><i class="frame icon-eye-open"></i></a>&nbsp;&nbsp;');
	}
	
	// Conditional rendering of unfollow icon
	if(tweet.authorForget)
	{
		$newTweetLine.find('.tweetFriend').append('<a href="#" title="Stop following" data-unfollow="'+tweet.login+'"><i class="frame icon-eye-close"></i></a>&nbsp;&nbsp;');
	}	
	
	// Conditional rendering for like icon
	if(data_line_type != 'favoriteline' && tweet.addToFavorite)
	{
		$newTweetLine.find('.tweetFriend').append('<a href="#" title="Like" data-like="'+tweet.tweetId+'"><i class="frame icon-star"></i></a>&nbsp;&nbsp;');
	}

	// Conditional rendering for unlike icon
	if(data_line_type == 'favoriteline' && !tweet.addToFavorite)
	{
		$newTweetLine.find('.tweetFriend').append('<a href="#" title="Stop liking" data-unlike="'+tweet.tweetId+'"><i class="frame icon-star-empty"></i></a>&nbsp;&nbsp;');
	}	
	
	// Set tweetId
	$newTweetLine.find('article').attr('data-tweetId',tweet.tweetId);
	
	$newTweetLine.find('.tweetDate aside').empty().html(tweet.prettyPrintTweetDate);

	bindListeners($newTweetLine);
	return $newTweetLine.find('tr');
}