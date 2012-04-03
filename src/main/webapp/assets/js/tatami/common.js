function bindListeners($target)
{
	$target.find('a[data-follow]').click(function(e)
	{
		var target = jQuery(e.currentTarget).attr('data-follow');
		followUser(target);
		return false;
	});
	
	$target.find('a[data-unfollow]').click(function(e)
	{
		var target = jQuery(e.currentTarget).attr('data-unfollow');
		removeFriend(target);
		return false;
	});
	
	$target.find('a[data-like]').click(function(e)
	{
		var target = jQuery(e.currentTarget).attr('data-like');
		addFavoriteTweet(target);
		return false;
	});

	$target.find('a[data-unlike]').click(function(e)
	{
		var target = jQuery(e.currentTarget).attr('data-unlike');
		removeFavoriteTweet(target);
		return false;
	});

	$target.find('a[data-user],span[data-user]').click(function(e)
	{
		var target = jQuery(e.currentTarget).attr('data-user');
		loadUserline(target);
		return false;
	});
	
	$target.find('a[data-tag]').click(function(e)
	{
		var target = jQuery(e.currentTarget).attr('data-tag');
		loadTagsline(target);
		return false;
	});
	
	// Bind click handler for "Tweet" button
	$target.find('#tweetButton').click(tweet);
	
	//Bind click handler for "Update" button
	$target.find('button[type="submit"]').click(updateProfile);

	
	//Bind 'hover' on user gravatar
	registerUserDetailsPopOver($target);

}

function errorHandler($targetErrorPanel)
{
	return function(jqXHR, textStatus, errorThrown)
	{
		$targetErrorPanel.find('#errorMessage').empty().html(jqXHR.responseText).end().show();
	};
}

function registerUserDetailsPopOver($target)
{
	$target.find('.tweetGravatar').mouseenter(function()
	{
		console.log('register popover for'+$(this).closest('tr').find('article span').html());
		
		if($(this).data('popover') == null)
		{
			console.log('new popover for'+$(this).closest('tr').find('article span').html());
			
			var data_user= $(this).attr('data-user');
			$(this).popover({
				animation: false,
				placement: 'right',
				trigger: 'manual',
				title: 'User details',
				html : true,
				template: $('#popoverTemplate').clone().attr('id','').find('div.popover').attr('data-user',data_user).end().html()
			});
		}
		$(this).delay(200).popover('show');
		$.ajax({
			type: 'GET',
			url: "rest/usersDetails/" + $('.popover.in').attr('data-user'),
			dataType: "json",
	        success: function(data)
	        {
	        	$('.popover.in .userDetailsTitle').empty()
	        	.append('<img class="userDetailsGravatar" src="http://www.gravatar.com/avatar/'+data.gravatar+'?s=24"></img>')
	        	.append('<span>@'+data.login+'</span>');
	        	
	        	$('.popover.in .userDetailsContent').empty();
	        	$('#userDetailsTemplate .row-fluid').clone()
	        	.find('.userDetailsName').html(data.firstName+'&nbsp;'+data.lastName).end()
	        	.find('.userDetailsTweetsCount').html(data.tweetCount).end()
	        	.find('.userDetailsFriendsCount').html(data.friendsCount).end()
	        	.find('.userDetailsFollowersCount').html(data.followersCount).end()
	        	.appendTo('.popover.in .userDetailsContent');
	        	
	        }
	    });
		
	});
	
	$target.find('.tweetGravatar').mouseleave(function()
	{
		$(this).popover('hide');
	});
}

function registerRefreshLineListeners()
{
	$('.refreshLineIcon').click(refreshCurrentLine);
}

function registerFetchTweetHandlers()
{
	$('.tweetPagingIcon').click(function(event)
	{
		var tweetsNb = $(event.target).closest('footer').find('.pageSelector option').filter(':selected').val(); 
		var currentTweetsNb = $(event.target).closest('footer').closest('div').find('.lineContent tr.data').size();
		var data_rest_url = $(event.target).closest('footer').attr('data-rest-url');
		data_rest_url = data_rest_url.replace(START_TWEET_INDEX_PATTERN,currentTweetsNb+1)
						.replace(END_TWEET_INDEX_PATTERN,parseInt(currentTweetsNb)+parseInt(tweetsNb));
		
		$.ajax({
			type: 'GET',
			url: data_rest_url,
			dataType: "json",
	        success: function(data)
	        {
	        	if(data.length>0)
        		{
	        		//Remove last padding line
		        	var $tableBody = $(event.target).closest('div.tab-pane').find('.lineContent');
		        	var $padding_line = $tableBody.find('tr:last-child').detach();
		        	$.each(data,function(index, tweet)
		        	{        		
		        		$tableBody.append(fillTweetTemplate(tweet));
		        	});
		        	$tableBody.append($padding_line);
        		}
	        	
	        }
	    });
		
		return false;
	});
}

function fillTweetTemplate(tweet)
{
	$newTweetLine = $('#tweetTemplate').clone().attr('id','');
	
	$newTweetLine.find('.tweetGravatar').attr('data-user',tweet.login).attr('src','http://www.gravatar.com/avatar/'+tweet.gravatar+'?s=32');
	
	if(login != tweet.login)
	{
		$newTweetLine.find('article strong').empty().html(tweet.firstName+' '+tweet.lastName+' &nbsp;')
		.after('<a href="#" data-user="'+tweet.login+'" title="Show '+tweet.login+' tweets"><em>@'+tweet.login+'</em></a>');
	}
	
	$newTweetLine.find('article span').html(tweet.content);
	$newTweetLine.find('.tweetDate aside').empty().html(tweet.prettyPrintTweetDate);

	bindListeners($newTweetLine);
	return $newTweetLine.find('tr');
	
	
}
