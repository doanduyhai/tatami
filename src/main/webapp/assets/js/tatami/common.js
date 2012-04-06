function bindListeners($target)
{
	$target.find('a[data-follow]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-follow');
		followUser(target);
		return false;
	});
	
	$target.find('a[data-unfollow]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-unfollow');
		removeFriend(target);
		return false;
	});
	
	$target.find('a[data-like]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-like');
		addFavoriteTweet(target);
		return false;
	});

	$target.find('a[data-unlike]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-unlike');
		removeFavoriteTweet(target);
		return false;
	});

	$target.find('a[data-user],span[data-user]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-user');
		loadUserline(target);
		return false;
	});
	
	$target.find('a[data-tag]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-tag');
		loadTagsline(target);
		return false;
	});
	
	// Bind click handler for "Tweet" button
	$target.find('#tweetButton').click(tweet);
	
	//Bind click handler for "Update" button
	$target.find('button[type="submit"]').click(updateProfile);

	
	//Bind 'hover' on user gravatar
	registerUserDetailsPopOver($target);
	
	//Bind click on gravatar to display user profile modal
	$target.find('img.tweetGravatar[data-user]').click(function(e)
	{
		//First hide popover
		if($(e.currentTarget).data('popover') != null)
		{
			$(e.currentTarget).popover('hide');	
		}
		
		var login = $(e.currentTarget).attr('data-user');
		showUserProfile(login);
		return false;
	});


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
		if($(this).data('popover') == null)
		{
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
		$(this).popover('show');
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
		var data_line_type = $(event.target).closest('footer').attr('data-line-type');
		
		if(data_line_type == 'timeline')
		{
			data_rest_url = data_rest_url.replace(START_TWEET_INDEX_REGEXP,currentTweetsNb+1)
			.replace(END_TWEET_INDEX_REGEXP,parseInt(currentTweetsNb)+parseInt(tweetsNb));
		}
		else if(data_line_type == 'favoriteline')
		{
			data_rest_url = data_rest_url.replace(START_TWEET_INDEX_REGEXP,currentTweetsNb+1)
			.replace(END_TWEET_INDEX_REGEXP,parseInt(currentTweetsNb)+parseInt(tweetsNb));
		}		
		else if(data_line_type == 'userline')
		{
			var data_login=$(event.target).closest('div.tab-pane').find('.lineContent').find('tr.data').filter(':last').find('img[data-user]').attr('data-user');
			data_rest_url = data_rest_url.replace(START_TWEET_INDEX_REGEXP,currentTweetsNb+1)
			.replace(END_TWEET_INDEX_REGEXP,parseInt(currentTweetsNb)+parseInt(tweetsNb))
			.replace(USER_LOGIN_REGEXP,data_login);
		}	
		else if(data_line_type == 'tagline')
		{
			var tag_login=$(event.target).closest('div.tab-pane').find('.lineContent').find('tr.data').filter(':last').find('a[data-tag]').attr('data-tag');
			data_rest_url = data_rest_url.replace(START_TWEET_INDEX_REGEXP,currentTweetsNb+1)
			.replace(END_TWEET_INDEX_REGEXP,parseInt(currentTweetsNb)+parseInt(tweetsNb))
			.replace(TAG_REGEXP,tag_login);
		}
		
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
		        		$tableBody.append(fillTweetTemplate(tweet,data_line_type));
		        	});
		        	
		        	//Put back last padding line
		        	$tableBody.append($padding_line);
        		}
	        	
	        }
	    });
		
		return false;
	});
}

function fillTweetTemplate(tweet,data_line_type)
{
	$newTweetLine = $('#tweetTemplate').clone().attr('id','');
	
	$newTweetLine.find('.tweetGravatar').attr('data-user',tweet.login).attr('src','http://www.gravatar.com/avatar/'+tweet.gravatar+'?s=32');
	
	if(data_line_type != 'userline')
	{
		if(login != tweet.login)
		{
			$newTweetLine.find('article strong').empty().html(tweet.firstName+' '+tweet.lastName+' &nbsp;')
			.after('<a href="#" data-user="'+tweet.login+'" title="Show '+tweet.login+' tweets"><em>@'+tweet.login+'</em></a><br/>');
		}
	}	
	else
	{
		$newTweetLine.find('article strong').empty().html(tweet.firstName+' '+tweet.lastName);
	}
	
	$newTweetLine.find('article span').html(tweet.content);
	
	console.log('data_line_type = '+data_line_type+', tweet.authorForget = '+tweet.authorForget);
	
	// Conditional rendering of Follow icon
	if(data_line_type != 'timeline' && tweet.authorFollow)
	{	
		$newTweetLine.find('.tweetFriend').append('<a href="#" title="Follow" data-follow="'+tweet.login+'"><i class="icon-eye-open"></i>&nbsp;</a>');
	}
	
	// Conditional rendering of unfollow icon
	if(tweet.authorForget)
	{
		$newTweetLine.find('.tweetFriend').append('<a href="#" title="Stop following" data-unfollow="'+tweet.login+'"><i class="icon-eye-close"></i>&nbsp;</a>');
	}	
	
	// Conditional rendering for like icon
	if(data_line_type != 'favoriteline' && tweet.addToFavorite)
	{
		$newTweetLine.find('.tweetFriend').append('<a href="#" title="Like" data-like="'+tweet.tweetId+'"><i class="icon-star"></i>&nbsp;</a>');
	}

	// Conditional rendering for unlike icon
	if(data_line_type == 'favoriteline' && !tweet.addToFavorite)
	{
		$newTweetLine.find('.tweetFriend').append('<a href="#" title="Stop liking" data-unlike="'+tweet.tweetId+'"><i class="icon-star-empty"></i>&nbsp;</a>');
	}	
	
	
	$newTweetLine.find('.tweetDate aside').empty().html(tweet.prettyPrintTweetDate);

	bindListeners($newTweetLine);
	return $newTweetLine.find('tr');
}

function showOverlayBox() {

	// set the window background for the overlay. i.e the body becomes darker
	$('.bgCover').css({
		display:'block',
		width: $(window).width(),
		height:$(window).height(),
	});
	
//	$('.overlayBox').css({
//		display:'block',
//		left:( $(window).width() - $('.overlayBox').width() )/2,
//		top:( $(window).height() - $('.overlayBox').height() )/2 -20,
//		position:'absolute'
//	});
	
	$('.bgCover').css({opacity:0}).animate( {opacity:0.5, backgroundColor:'#000'} );
	
	//return false;
}

function doOverlayClose() {

	//$('.overlayBox').css( 'display', 'none' );

	$('.bgCover').animate( {opacity:0}, null, null, function() { $(this).hide(); } );
}

//$(window).bind('resize',showOverlayBox);