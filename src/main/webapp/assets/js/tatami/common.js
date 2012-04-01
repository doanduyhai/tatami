var nbTweets = defaultNbTweets;

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

	$target.find('a[data-user]').click(function(e)
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
	registerUserDetailsPopOver();

}

function errorHandler($targetErrorPanel)
{
	return function(jqXHR, textStatus, errorThrown)
	{
		$targetErrorPanel.find('#errorMessage').empty().html(jqXHR.responseText).end().show();
	};
}

function registerUserDetailsPopOver()
{
	$('.tweetGravatar').mouseenter(function()
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
		$(this).delay(200).popover('show');
		$.ajax({
			type: 'GET',
			url: "rest/usersDetails/" + $('.popover.in').attr('data-user'),
			dataType: "json",
	        success: function(data)
	        {
	        	$('.popover.in .userDetailsTitle').empty().html('<span>'+data.firstName+'</span>&nbsp;<span>'+data.lastName+'</span>');
	        	$('.popover.in .userDetailsContent').empty();
	        	$('#userDetailsTemplate .row-fluid').clone()
	        	.find('.userDetailsTweetsCount').html(data.tweetCount).end()
	        	.find('.userDetailsFriendsCount').html(data.friendsCount).end()
	        	.find('.userDetailsFollowersCount').html(data.followersCount).end()
	        	.appendTo('.popover.in .userDetailsContent');
	        	
	        }
	    });
		
	});
	
	$('.tweetGravatar').mouseleave(function()
	{
		$(this).popover('hide');
	});
}