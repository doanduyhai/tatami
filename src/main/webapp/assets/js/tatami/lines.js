/*
 * Tweet actions 
 */
function addFavoriteTweet(tweetId) {
	
	$.ajax({
		type: HTTP_GET,
		url:  replaceIdInURL(FAVORITE_ADD_REST,tweetId),
		dataType: JSON_DATA,
        success: function()
        {
			setTimeout(function()
			{
	        	//$('#favTab').tab('show');
	        	refreshCurrentLine();
			},100);	

        }
    });
	
	return false;
}


function removeFavoriteTweet(tweetId) {
	$.ajax({
		type: HTTP_GET,
		url: replaceIdInURL(FAVORITE_REMOVE_REST,tweetId),  
		dataType: JSON_DATA,
        success: function()
        {
			setTimeout(function()
			{
	        	//$('#favTab').tab('show');
	        	refreshCurrentLine();
			},100);	        	

        }
    });
	
	return false;
}

function removeTweet(tweetId)
{
	$.ajax({
		type: HTTP_GET,
		url: replaceIdInURL(TWEET_REMOVE_REST,tweetId), 
		dataType: JSON_DATA,
        success: function()
        {
			setTimeout(refreshCurrentLine,300);	        	
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
        	console.log(jqXHR.responseText);
        	console.log(textStatus);
        	console.log(errorThrown);
        	
        }
    });
	
	return false;
}
/*
 * Lines activation & refresh 
 */
function loadUserline(targetUserLogin)
{
	if(targetUserLogin != null)
	{
		$('#userTweetsList').empty();
		$('#userlinePanel footer').attr('data-tweetFetch-key',targetUserLogin);
		if($('#userlinePanel').hasClass('active'))
		{
			refreshCurrentLine();
		}
		else
		{
			$('#userTab').tab('show');
		}	
		
	}
}

function loadTagsline(tag)
{
	if(tag != null)
	{
		$('#tagTweetsList').empty();
		$('#taglinePanel footer').attr('data-tweetFetch-key',tag);
		if($('#taglinePanel').hasClass('active'))
		{
			refreshCurrentLine();
		}
		else
		{
			$('#tagTab').tab('show');
		}	
	}	
	
}

function refreshTimeline()
{
	$('#mainTab').tab('show');
	refreshCurrentLine();
}


function refreshCurrentLine()
{
	var tweetsNb = $('#tweetsPanel div.tab-pane.active tbody tr.data').size();
	var targetLine = $('#tweetsPanel div.tab-pane.active').attr('id');
	
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
function registerRefreshLineListeners()
{
	$('.refreshLineIcon').click(refreshCurrentLine);
}

function registerFetchTweetHandlers()
{
	$('.pageSelector')
	.find('option:eq(0)').html(FIRST_FETCH_SIZE).end()
	.find('option:eq(1)').html(SECOND_FETCH_SIZE).end()
	.find('option:eq(2)').html(THIRD_FETCH_SIZE);
	
	$('.tweetPagingButton').click(function(event)
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
function fillTweetTemplate(tweet,data_tweetFetch_type)
{
	$newTweetLine = $('#tweetTemplate').clone().attr('id','');
	
	$newTweetLine.find('.tweetGravatar').attr('data-user',tweet.login).attr('src','http://www.gravatar.com/avatar/'+tweet.gravatar+'?s=32');
	
	if(data_tweetFetch_type != 'userline')
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
	
	
	if(tweet.deletable)
	{
		$newTweetLine.find('.tweetAction').append('<a href="#" title="Remove" data-remove="'+tweet.tweetId+'"><i class="icon-remove"></i>&nbsp;</a>');
		
	}	
	
	// Conditional rendering for like icon
	if(tweet.addToFavorite)
	{
		$newTweetLine.find('.tweetAction').append('<a href="#" title="Like" data-like="'+tweet.tweetId+'"><i class="icon-star"></i>&nbsp;</a>');
	}
	else
	{
		$newTweetLine.find('.tweetAction')
		.append('<a href="#" title="Stop liking" data-unlike="'+tweet.tweetId+'"><i class="icon-star-empty"></i>&nbsp;</a>');
		
		if(data_tweetFetch_type != 'favoriteline')
		{
			$newTweetLine.find('tr').addClass('favoriteTweet');
		}	
	}	
	
	// Set tweetId
	$newTweetLine.find('article').attr('data-tweetId',tweet.tweetId);
	
	
	$newTweetLine.find('.tweetDate aside').empty().html(tweet.prettyPrintTweetDate);

	bindListeners($newTweetLine);
	return $newTweetLine.find('tr');
}
