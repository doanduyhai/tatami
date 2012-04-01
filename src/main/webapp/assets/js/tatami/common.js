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
}

function errorHandler($targetErrorPanel)
{
	return function(jqXHR, textStatus, errorThrown)
	{
		$targetErrorPanel.find('#errorMessage').empty().html(jqXHR.responseText).end().show();
	};
}