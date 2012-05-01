$.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push($.trim(this.value) || '');
        } else {
            o[this.name] = $.trim(this.value) || '';
        }
    });
    return o;
};


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

	$target.find('span[data-friends]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-friends');
		refreshFriendsline(target);
		return false;
	});
	
	$target.find('span[data-followers]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-followers');
		refreshFollowersline(target);
		return false;
	});	
	
	$target.find('a[data-tag]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-tag');
		loadTagsline(target);
		return false;
	});

	$target.find('[data-modal-hide]').click(function(e)
	{
		var modal = $(e.currentTarget).attr('data-modal-hide');
		if(modal!= null)
		{
			$(''+modal).modal('hide');
		}
		
	});
	
	//Bind click on gravatar to display user profile modal
	$target.find('img.tweetGravatar[data-user],#picture').click(function(e)
	{
		//First hide popover
		if($(e.currentTarget).data('popover') != null)
		{
			$(e.currentTarget).popover('hide');	
		}
		var modal = $(e.currentTarget).attr('data-modal-highlight');
		var user = $(e.currentTarget).attr('data-user');
		showUserProfile(user);
		if(modal!= null)
		{
			$(modal).css('z-index',5000);
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


