function refreshLine(targetLine,start,end,clearAll,userLogin,tagWord)
{
	var data_rest_url = $('#'+targetLine+' footer').attr('data-rest-url');
	var data_line_type = $('#'+targetLine+' footer').attr('data-line-type');
	var $tableBody = $('#'+targetLine+' .lineContent');
	
	if(data_line_type == 'timeline')
	{
		data_rest_url = data_rest_url.replace(START_TWEET_INDEX_REGEXP,start)
		.replace(END_TWEET_INDEX_REGEXP,end);
	}
	else if(data_line_type == 'favoriteline')
	{
		data_rest_url = data_rest_url.replace(START_TWEET_INDEX_REGEXP,start)
		.replace(END_TWEET_INDEX_REGEXP,end);
	}		
	else if(data_line_type == 'userline')
	{
		var data_login='';
		if(userLogin != null)
		{
			 data_login=userLogin;
		}	
		else
		{
			data_login=$('#'+targetLine).closest('div.tab-pane').find('.lineContent').find('tr.data').filter(':last').find('img[data-user]').attr('data-user');
		}
		data_rest_url = data_rest_url.replace(START_TWEET_INDEX_REGEXP,start)
		.replace(END_TWEET_INDEX_REGEXP,end)
		.replace(USER_LOGIN_REGEXP,data_login);
	}	
	else if(data_line_type == 'tagline')
	{
		var tag='';
		if(tagWord != null)
		{
			 tag=tagWord;
		}	
		else
		{
			tag=$('#'+targetLine).closest('div.tab-pane').find('.lineContent').find('tr.data').filter(':last').find('a[data-tag]').attr('data-tag');
		}
		data_rest_url = data_rest_url.replace(START_TWEET_INDEX_REGEXP,start)
		.replace(END_TWEET_INDEX_REGEXP,end)
		.replace(TAG_REGEXP,tag);
	}

	 
	$.ajax({
		type: HTTP_GET,
		url: data_rest_url,
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
	        		$tableBody.append(fillTweetTemplate(tweet,data_line_type));
	        	});
	        	
	        	$('#tweetPaddingTemplate tr').clone().css('display','inline').appendTo($tableBody);
    		}
        }
    });	
}

function refreshCurrentLine()
{
	var tweetsNb = $('#tweetsPanel div.tab-pane.active tbody tr.data').size();
	var targetLine = $('#tweetsPanel div.tab-pane.active').attr('id');
	
	refreshLine(targetLine,1,tweetsNb,true,null,null);	

	return false;
}	

function refreshTimeline()
{
	$('#mainTab').tab('show');
	refreshCurrentLine();
}

function addFavoriteTweet(tweet) {
	
	$.ajax({
		type: HTTP_GET,
		url: "rest/likeTweet/" + tweet,
		dataType: JSON_DATA,
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
		type: HTTP_GET,
		url: "rest/unlikeTweet/" + tweet,
		dataType: JSON_DATA,
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
		
		refreshLine('userlinePanel',1,DEFAULT_TWEET_LIST_SIZE,true,targetUserLogin,null);
		clickFromLink = false;
		jQuery.ajaxSetup({async:true});
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
		
		refreshLine('taglinePanel',1,DEFAULT_TAG_LIST_SIZE,true,null,tag);
		clickFromLink = false;
		jQuery.ajaxSetup({async:true});		
	}	
	
}
