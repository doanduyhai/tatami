function bindListeners($target)
{
	$target.find('a[data-follow]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-follow');
		var modal = $(e.currentTarget).attr('data-modal');
		if(modal!= null)
		{
			$('#'+modal).modal('hide');
		}	
		followUser(target);
		return false;
	});
	
	$target.find('a[data-unfollow]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-unfollow');
		var modal = $(e.currentTarget).attr('data-modal');
		if(modal!= null)
		{
			$('#'+modal).modal('hide');
		}			
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
		var modal = $(e.currentTarget).attr('data-modal');
		if(modal!= null)
		{
			$('#'+modal).modal('hide');
		}	
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
	$target.find('img.tweetGravatar[data-user],#picture').click(function(e)
	{
		//First hide popover
		if($(e.currentTarget).data('popover') != null)
		{
			$(e.currentTarget).popover('hide');	
		}
		var modal = $(e.currentTarget).attr('data-highlight');
		var login = $(e.currentTarget).attr('data-user');
		showUserProfile(login);
		if(modal!= null)
		{
			$('#'+modal).css('z-index',5000);
		}
		return false;
	});


}

function errorHandler($targetErrorPanel)
{
	return function(jqXHR, textStatus, errorThrown)
	{
		$targetErrorPanel.find('.errorMessage').empty().html(jqXHR.responseText).end().show();
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
			type: HTTP_GET,
			url: "rest/usersDetails/" + $('.popover.in').attr('data-user'),
			dataType: JSON_DATA,
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
	$('.tweetPagingButton').click(function(event)
	{
		var tweetsNb = $(event.target).closest('footer').find('.pageSelector option').filter(':selected').val(); 
		var currentTweetsNb = $(event.target).closest('footer').closest('div').find('.lineContent tr.data').size();
		var targetLine =  $(event.target).closest('div.tab-pane.active').attr('id');
		
		refreshLine(targetLine,currentTweetsNb+1,parseInt(currentTweetsNb)+parseInt(tweetsNb),false);
				
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

function fillUserTemplate(user)
{
	$newUserLine = $('#fullUserTemplate').clone().attr('id','');
	
	$newUserLine
	.find('.tweetGravatar').attr('data-user',user.login).attr('src','http://www.gravatar.com/avatar/'+user.gravatar+'?s=32').attr('data-highlight','userProfileModal').end()
	.find('#userLink').attr('id','').attr('data-user',user.login).attr('title','Show '+user.login+' tweets').attr('data-modal','userSearchModal').end()
	.find('em').html('@'+user.login).end()
	.find('.userDetailsName').html(user.firstName+' '+user.lastName);
	
	if(user.follow)
	{
		$newUserLine.find('.tweetFriend a').attr('data-follow',user.login).attr('title','Follow '+user.login).attr('data-modal','userSearchModal');
	}
	else
	{
		$newUserLine.find('.tweetFriend a').removeAttr('data-follow').attr('data-unfollow',user.login)
		.attr('title','Stop following '+user.login).attr('data-modal','userSearchModal')
		.find('i').removeClass().addClass('icon-eye-close');
	}
	bindListeners($newUserLine);
	
	return $newUserLine;
	
}
