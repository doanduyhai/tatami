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

	$target.find('a[data-quote]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-quote');
		quoteUser(target);
		return false;
	});
	
	$target.find('a[data-remove]').click(function(e)
	{
		var target = $(e.currentTarget).attr('data-remove');
		removeTweet(target);
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


function sessionTimeOutPopup()
{
	$('#sessionTimeOutModal').modal('show');
	$('#sessionTimeOutModal').css('z-index',6000);
}


function errorHandler($targetErrorPanel)
{
	return function(jqXHR, textStatus, errorThrown)
	{
		if(errorThrown != 901)
		{
			$targetErrorPanel.find('.errorMessage').empty().html(jqXHR.responseText).end().show();
		}
		
	};
}

function registerLoginRedirectListener()
{
	$('.redirectToLogin').click(function()
	{
		window.location.replace("./login");

	});
}

var idRegExp = new RegExp("\{id\}", "g");

function replaceIdInURL(url,id)
{
	var newUrl = url.replace(idRegExp,id);
	
	return newUrl;
}

function registerTweetCounter()
{
	$('#tweetContent').jqEasyCounter({
	    maxChars: MAX_CHARACTERS_PER_TWEET,
	    maxCharsWarning: MAX_CHARACTERS_PER_TWEET-10,
	    msgFontSize: '12px',
	    msgFontColor: '#000',
	    msgFontFamily: 'Arial',
	    msgTextAlign: 'left',
		msgWarningClass: 'badge-warning',
		msgErrorClass: 'badge-error',
		msgShortUrl: true,
		msgShortUrlLength: LINK_SHORT_LENGTH,
		msgUrlPattern : LINK_REGEXP,		    
	    msgAppendMethod: 'insertAfter'              
	});
}

function registerEnterKeypress()
{
	$('#tweetContent').bind('keypress', function(e) 
	{
		// 13 is keycode for 'Enter'
		if ((e.keyCode || e.which) == 13)
		{
			$('#tweetButton').trigger('click');
		}	
		
	});
	
	$('#userSearchInput').bind('keypress', function(e) 
	{
		// 13 is keycode for 'Enter'
		if ((e.keyCode || e.which) == 13)
		{
			$('#userSearchButton').trigger('click');
		}	
		
	});
}