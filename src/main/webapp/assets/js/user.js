var nbTweets = defaultNbTweets;

//function resetNbTweets() {
//	nbTweets = defaultNbTweets;
//}
//
//function incrementNbTweets() {
//	nbTweets += 10;
//}

function loadHome(callBack)
{
	$('#homeTabContent').empty();
	$('#homeTabContent').load('fragments/user.html #homeContent',
	function()
	{
		$('#tweetButton').click(tweet);
		if(callBack != null)
		{
			callBack();
		}
	});
}

function updateProfile() {
	$.ajax({
		type: 'POST',
		url: "rest/users/" + login,
		contentType: "application/json",
		data: JSON.stringify($("#updateUserForm").serializeObject()),
		dataType: "json",
		success: function() {
			loadHome(function()
			{
				$('#defaultTab').tab('show');
			});
		}	
	});
	return false;
}

function loadProfile()
{
	$('#profileTabContent').empty();
	$('#profileTabContent').load('fragments/profile.html #profileContent',function()
	{
		$('#profileTabContent').find('button[type="submit"]').click(updateProfile);
	});
	
}

function followUser(loginToFollow) {
	$.ajax({
		type: 'POST',
		url: "rest/users/" + login + "/followUser",
		contentType: "application/json",
		data: loginToFollow,
		dataType: "json",
        success: function(data) {
            $("#followUserInput").val("");
            updateUserCounters();
            $('#followSuccess').find('span').remove();
            $('#followSuccess').fadeIn("fast").append('<span>You are now following '+loginToFollow+'</span>');
            $('#followSuccess').delay(2000).fadeOut(5000);
            refreshFollowableUsers();
        },
    	error: function(xhr, ajaxOptions, thrownError) {
    		$('#followError').find('span').remove();
    		$('#followError').fadeIn("fast").append('<span>'+thrownError+'</span>');
            $('#followError').delay(2000).fadeOut(5000);
    	}
	});

	return false;
}

function removeFriend(friend) {
	
	$.ajax({
		type: 'POST',
		url: "rest/users/" + login + "/removeFriend",
		contentType: "application/json",
		data: friend,
		dataType: "json",
        success: function(data) {
        	updateUserCounters();
        	$('#followSuccess').find('span').remove();
        	$('#followSuccess').fadeIn("fast").append('<span>You no longer follow '+friend+'</span>');
            $('#followSuccess').delay(2000).fadeOut(5000);
            loadFollowableUsers();
        }
	});
}


function addFavoriteTweet(tweet) {
	
	$.ajax({
		type: 'GET',
		url: "rest/likeTweet/" + tweet,
		dataType: "json",
        success: function()
        {
        	$('#favTab').tab('show');
        	loadFavoritesline();
        }
    });	
}

function removeFavoriteTweet(tweet) {
	
}

function bindTweetListeners($target)
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

	$target.find('a[data-remove]').click(function(e)
	{
		var target = jQuery(e.currentTarget).attr('data-remove');
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
}

function loadTimeline(nbTweets)
{
	if(nbTweets == null)
	{
		nbTweets = defaultNbTweets;
	}

	$('#timeLinePanel').empty();
	$('#mainTab').tab('show');	
	$('#timeLinePanel').load('fragments/'+nbTweets+'/timeline.html #timeline',function()
	{
		$('#refreshTimeline').click(function()
		{
			loadTimeline();
			return false;
		});
		bindTweetListeners($('#timeline'));		
	});

}

function loadUserline(targetUserLogin)
{
	$('#userLinePanel').empty();

	
	if(targetUserLogin != null)
	{
		$('#userTab').tab('show');	
		$('#userLinePanel').load('fragments/'+targetUserLogin+'/userline.html #userline',function()
		{
			bindTweetListeners($('#userline'));
		});
	}
	else
	{
		$('#userLinePanel').load('fragments/userline.html #userline');
	}
}


function loadFavoritesline()
{
	$('#favLinePanel').empty();
	$('#favTab').tab('show');	
	
	$('#favLinePanel').load('fragments/favline.html #favline',function()
	{
		bindTweetListeners($('#favline'));
	});	
}

function loadTagsline(tag)
{
	$('#tagLinePanel').empty();
	$('#tagTab').tab('show');	
	
	if(tag != null)
	{
		$('#tagLinePanel').load('fragments/'+tag+'/'+defaultNbTags+'/tagline.html #tagline',function()
		{
			bindTweetListeners($('#tagline'));
		});
	}	
	else
	{
		$('#tagLinePanel').load('fragments/'+defaultNbTags+'/tagline.html #tagline',function()
		{
			bindTweetListeners($('#tagline'));
		});		
	}	
}

function loadWhoToFollow()
{
	$('#followUserContent').empty();
	$('#followUserContent').load('fragments/followUser.html #followline',function()
	{
		bindTweetListeners($('#followline'));
		
//		$('button[type="submit"]').click(function()
//		{
//			followUser($('#followline').val());
//			return false;
//		});
	});
}

function refreshFollowableUsers()
{
	$('#followableUsers').empty();
	$('#followableUsers').load('fragments/followUser.html #followableUsers',function()
	{
		bindTweetListeners($('#followableUsers'));
	});	
}

function updateUserCounters()
{
	$.ajax({
		type: 'GET',
		url: "rest/users/" + login + "/",
		dataType: "json",
		success: function(data) {
			$("#tweetCount").text(data.tweetCount);
			$("#friendsCount").text(data.friendsCount);
			$("#followersCount").text(data.followersCount);
		}
	});
}

function tweet() {
	if ($.trim($('#tweetContent').val()) == "") {
		$('#tweetValidationMessage').show();
		return false;
	}
	else
	{
		$('#tweetValidationMessage').hide();
		$.ajax({
	        type: 'POST',
	        url: "rest/tweets",
	        contentType: "application/json",
	        data: $("#tweetContent").val(),
	        dataType: "json",
	        success: function(data) {
	            $("#tweetContent").slideUp().val("").slideDown('fast');
	            updateUserCounters();
	            loadTimeline();
	            loadWhoToFollow();
	        }
	    });
	}	
	return false;
}

$.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};


