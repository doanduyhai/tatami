/*
 *  User actions
 */

function followUser(loginToFollow) {
	
	$('#followErrorPanel').hide();
	
	$.ajax({
		type: HTTP_GET,
		url: replaceIdInURL(FRIEND_ADD_REST,loginToFollow),
		dataType: JSON_DATA,
        success: function(data) {

			setTimeout(function()
			{
	            $("#followUserInput").val("");
	            updateUserCounters();
	            refreshUserSuggestions();
	            refreshFriendsline(login);
			},300);

        },
    	error: errorHandler($('#followErrorPanel'))
	});

	return false;
}

function removeFriend(loginToRemove) {
	
	$('#followErrorPanel').hide();
	
	$.ajax({
		type: HTTP_GET,
		url: replaceIdInURL(FRIEND_REMOVE_REST,loginToRemove),
		dataType: JSON_DATA,
        success: function(data) {

			setTimeout(function()
			{
	        	updateUserCounters();	
	        	refreshUserSuggestions();
	        	refreshFriendsline(login);
			},300);

        },
    	error: errorHandler($('#followErrorPanel'))
	});
}

function updateProfile() {
	
	$('#userProfileErrorPanel').hide();
	
	$.ajax({
		type: HTTP_POST,
		url: USER_UPDATE_REST,
		contentType: JSON_CONTENT,
		data: JSON.stringify($("#updateUserForm").serializeObject()),
		dataType: JSON_DATA,
		success: function(data) {
			$('#defaultTab').tab('show');
			setTimeout(function()
			{
				refreshHome();
				updateUserCounters();
			},300);		
		},
		error: errorHandler($('#userProfileErrorPanel'))
	});
	return false;
}

function tweet() {

	$('#tweetErrorPanel').hide();
	$.ajax({
        type: HTTP_POST,
        url: TWEET_POST_REST,
        async: false,
        contentType: JSON_CONTENT,
        data:  JSON.stringify({content: $.trim($("#tweetContent").val())}),
        dataType: JSON_DATA,
        success: function(data) {
			setTimeout(function()
			{
				$("#tweetContent").slideUp().val("").slideDown('fast');
				updateUserCounters();
				refreshTimeline();
				refreshUserSuggestions();
			},300);	
        },
        error: errorHandler($('#tweetErrorPanel'))
    });
		
	return false;
}

function updateUserCounters()
{
	$.ajax({
		type: HTTP_GET,
		url: replaceIdInURL(USER_STATS_REST,login),
		dataType: JSON_DATA,
		success: function(data) {
			$("#tweetCount").text(data.tweetCount);
			$("#friendsCount").text(data.friendsCount);
			$("#followersCount").text(data.followersCount);
			
		}
	});
}

function showUserProfile(userLogin)
{
	$.ajax({
		type: HTTP_GET,
		url: replaceIdInURL(USER_SHOW_REST,userLogin),
		dataType: JSON_DATA,
		success: function(data) {
			
			updateUserProfileModal(data);
			$('#userProfileModal').modal('show');
		}
	});
	
}

function quoteUser(userToQuote)
{
	$('#defaultTab').tab('show');
	$('#tweetContent').empty().trigger('focus').html('@'+userToQuote);
}

function updateUserProfileModal(user)
{
	
	var avartarSize = $(window).width() * 2/12;
	
	$('#userProfileModal')
	.find('#userProfileLogin').html('@'+user.login).end()
	.find('#userProfileGravatar .tweetGravatar').attr('src','http://www.gravatar.com/avatar/'+user.gravatar+'?s='+avartarSize).end()
	.find('#userProfileName').html(user.firstName+'&nbsp;'+user.lastName).end()
	.find('#userProfileLocation span:nth-child(2)').html(user.location).end()
	.find('#userProfileWebsite a').html(user.website).attr('href',user.website).end()
	.find('#userProfileBio').html(user.biography).end()
	.find('#userProfileTweetsCount').attr('data-user',user.login).html(user.tweetCount).end()
	.find('#userProfileFriendsCount').attr('data-friends',user.login).html(user.friendsCount).end()
	.find('#userProfileFollowersCount').attr('data-followers',user.login).html(user.followersCount).end()
	.find('.btn').hide();
	
	if(user.login != login)
	{
		$('#userProfileModal')
		.find('#userProfileDoQuote').attr('data-quote',user.login).show().end()
		.find(user.directMessage ? '#userProfileDoWrite':'#userProfileDoQuote').attr('data-direct-message',user.login).show().end()
		.find(user.follow ? '#userProfileDoFollow':'#userProfileDoForget').attr(user.follow ? 'data-follow':'data-unfollow',user.login).show().end()
		.find('#userProfileDoBlock').attr('data-block',user.login).show();
		
	}

}

/*
 *  Lines activation
 */


function loadSuggestions()
{
	$('#suggestionsPanel').empty();
	$('#suggestionsPanel').load('fragments/mobile/suggestions.html #suggestionsline',function()
	{
		refreshUserSuggestions();
		registerUserSearchListener();
	});
}


function loadProfile()
{
	$('#profilePanel').empty();
	$('#profilePanel').load('fragments/mobile/profile.html #profileContent',function()
	{
		//Bind click handler for "Update" button
		$('#profilePanel').find('button[type="submit"]').click(updateProfile);
	});
}

function loadEmptyUserLines()
{
	$('#contactsPanel').load('fragments/mobile/contacts.html #contactsline',function()
	{
		// Bind click listener on contact sub tabs
		$('#contactsPanel a[data-toggle="tab"]').on('shown', function(e) {
			if (e.target.hash == '#friendsLine') 
	    	{
	    		if(directContatTabClick)
	    		{
	    			$('#friendsLine h2').html("My friends").removeClass('red');
	    			$('#friendsLine footer').attr('data-userFetch-key',login);
	    		}
	    		refreshCurrentUserLine();
	    	}
	    	else if (e.target.hash == '#followersLine')
	    	{
	    		if(directContatTabClick)
	    		{
	    			$('#followersLine h2').html("My followers").removeClass('red');
	    			$('#followersLine footer').attr('data-userFetch-key',login);
	    		}
	    		refreshCurrentUserLine();
	    	}	
		});	
		
		registerRefreshUserLineListeners();
		registerFetchUserHandlers();
		
	});
}	
/*
 *  Lines refresh
 */

function refreshUserSuggestions()
{
	$.ajax({
		type: HTTP_GET,
		url: USER_SUGGESTIONS_REST,
		dataType: JSON_DATA,
        success: function(data)
        {
    		var $tableBody = $('#userSuggestions');
    		$tableBody.empty();
    		if((data || []).length>0)
    		{
	        	$.each(data,function(index, user)
	        	{        		
	        		$tableBody.append(fillUserTemplate(user,"suggestions"));
	        	});
	        	
    		}
        	else
        	{
        		$newUserLine = $('#emptyUserTemplate').clone().attr('id','').appendTo($tableBody);
        	}	
        	
        }
    });	
}

function refreshHome()
{
	$.ajax({
		type: HTTP_GET,
		url: replaceIdInURL(USER_SHOW_REST,login),
		dataType: JSON_DATA,
		success: function(user) {
			
			var avartarSize = $(window).width() * 2/12;
			
			$('#homePanel').find('#picture').attr('src','http://www.gravatar.com/avatar/'+user.gravatar+'?s='+avartarSize).end()
			.find('#firstName').html(user.firstName).end()
			.find('#latName').html(user.lastName).end()
			.find('#tweetCount').html(user.tweetCount).end()
			.find('#friendsCount').html(user.friendsCount).end()
			.find('#followersCount').html(user.followersCount).end();
			
			
		}
	});
}

function refreshFriendsline(user)
{
	$('#contactsTab').tab('show');
	$('#friendsLine footer').attr('data-userFetch-key',user);
	if(user != login)
	{
		$('#friendsLine h2').html("Friends of "+user).addClass('red');
	}
	else
	{
		$('#friendsLine h2').html("My friends").removeClass('red');
	}
	
	if($('#friendsLine').hasClass('active'))
	{
		
		refreshCurrentUserLine();
	}	
	else
	{
		directContatTabClick = false;
		$('#friendsTab').tab('show');
		directContatTabClick = true;
	}	
}

function refreshFollowersline(user)
{
	$('#contactsTab').tab('show');
	$('#followersLine footer').attr('data-userFetch-key',user);
	if(user != login)
	{
		$('#followersLine h2').html("Followers of "+user).addClass('red');
	}
	else
	{
		$('#followersLine h2').html("My followers").removeClass('red');
	}
	
	if($('#followersLine').hasClass('active'))
	{
		refreshCurrentUserLine();
	}	
	else
	{
		directContatTabClick = false;
		$('#followersTab').tab('show');
		directContatTabClick = true;
	}
}

function refreshCurrentUserLine()
{
	var usersNb = $('#contactsline div.tab-pane.active tbody tr.data').size();
	var targetLine = $('#contactsline div.tab-pane.active.userLine').attr('id');
	if(targetLine != null)
	{
		refreshUserLine(targetLine,null,usersNb,true);
	}
		

	return false;
}	

function refreshUserLine(targetLine,startUser,count,clearAll)
{	
	var data_userFetch_url = $('#'+targetLine+' footer').attr('data-userFetch-url');
	var data_userFetch_type = $('#'+targetLine+' footer').attr('data-userFetch-type');
	var data_userFetch_key = $('#'+targetLine+' footer').attr('data-userFetch-key');
	var $tableBody = $('#'+targetLine+' .userLineContent');
	
	var userFetchRangeObject = buildUserFetchRange(startUser,count,data_userFetch_key);
	 
	$.ajax({
		type: HTTP_POST,
		url: data_userFetch_url,
		contentType: JSON_CONTENT,
        data:  JSON.stringify(userFetchRangeObject),
		dataType: JSON_DATA,
        success: function(data)
        {
        	if((data || []).length>0)
    		{
        		if(clearAll)
        		{
        			$tableBody.empty();
            		$('#userPaddingTemplate tr').clone().appendTo($tableBody);
            		$('#userPaddingTemplate tr').clone().appendTo($tableBody);
        		}
        		else
        		{
        			$tableBody.find('tr:last-child').remove();
        		}
        		
	        	$.each(data,function(index, user)
	        	{        		
	        		$tableBody.append(fillUserTemplate(user,data_userFetch_type));
	        	});
	        	
	        	$('#userPaddingTemplate tr').clone().appendTo($tableBody).show();
    		}
        	else if(clearAll)
    		{
        		$tableBody.empty();
    		}
        }
    });	
}


function buildUserFetchRange(startUser,count,functionalKey)
{
	return {
		startUser: startUser,
		count: count,
		functionalKey: functionalKey
	};
}


/*
 * Handlers registration
 */

function registerUserProfileModalListeners()
{
	bindListeners($('#userProfileStats'));
	bindListeners($('#userProfileAction'));
};

function registerHomePanelListeners()
{
	bindListeners($('#homePanel'));
}

function registerRefreshUserLineListeners()
{
	$('.refreshUserLineIcon').click(refreshCurrentUserLine);
}

function registerUserSearchListener()
{
	$('#userSearchForm button').click(function()
	{
		$('#searchErrorPanel').hide();
		$.ajax({
			type: HTTP_POST,
			url: USER_SEARCH_REST,
	        contentType: JSON_CONTENT,
	        data:  JSON.stringify({searchString: $.trim($("#followUserInput").val())}),			
			dataType: JSON_DATA,
			success: function(data) {
				
				var $tableBody = $('#userSearchList');
	    		$tableBody.empty();
	    		if((data || []).length>0)
	    		{
		        	$.each(data,function(index, user)
		        	{        		
		        		$tableBody.append(fillUserTemplate(user,"search"));
		        	});
		        	
	    		}
	        	else
	        	{
	        		$newUserLine = $('#emptyUserSearchTemplate').clone().attr('id','').appendTo($tableBody);
	        	}
				$('#userSearchPanel').show();
			},
			error: errorHandler($('#searchErrorPanel'))
		});
		
		return false;
	});
}

function registerFetchUserHandlers()
{
	$('.pageSelector')
	.find('option:eq(0)').html(FIRST_FETCH_SIZE).end()
	.find('option:eq(1)').html(SECOND_FETCH_SIZE).end()
	.find('option:eq(2)').html(THIRD_FETCH_SIZE);
	
	$('.userPagingButton').click(function(event)
	{
		var $target = $(event.target);
		var userNb = $target.closest('footer').find('.pageSelector option').filter(':selected').val(); 
		var targetLine =  $target.closest('div.tab-pane.active').attr('id');
		var startUser = $target.closest('footer').closest('div').find('.userLineContent tr.data').last().find('.tweetGravatar').attr('data-user');
		refreshUserLine(targetLine,startUser,parseInt(userNb),false);
				
		return false;
	});
}

/*
 *  User template handling
 */
function fillUserTemplate(user,data_userFetch_type)
{
	$newUserLine = $('#fullUserTemplate').clone().attr('id','');
	
	$newUserLine
	.find('.tweetGravatar').attr('data-user',user.login).attr('src','http://www.gravatar.com/avatar/'+user.gravatar+'?s=32').attr('data-modal-highlight','#userProfileModal').end()
	.find('.userLink').attr('data-user',user.login).attr('title','Show '+user.login+' tweets').end()
	.find('em').html('@'+user.login).end()
	.find('.userDetailsName').html(user.firstName+' '+user.lastName).end()
	.find('.badge').html(user.tweetCount).attr('data-user',user.login);
	
	if(data_userFetch_type == "suggestions")
	{
			$newUserLine.find('.userAction a').attr('data-follow',user.login)
			.attr('title','Follow '+user.login).attr('data-modal-hide','#userSearchModal')
			.find('i').addClass('icon-eye-open frame');	
	}
	else if(data_userFetch_type == "search" || data_userFetch_type == "followers")
	{
		// No action if user == currentUser
		if(login != user.login)
		{
			if(user.follow)
			{
				$newUserLine.find('.userAction a').attr('data-follow',user.login)
				.attr('title','Follow '+user.login).attr('data-modal-hide','.modal')
				.find('i').addClass('icon-eye-open frame');
			}
			else
			{
				$newUserLine.find('.userAction a').attr('data-unfollow',user.login)
				.attr('title','Stop following '+user.login).attr('data-modal-hide','.modal')
				.find('i').addClass('icon-eye-close frame');
			}	
		}
	}
	else if(data_userFetch_type == "friends")
	{
		// No action if user == currentUser
		if(login != user.login)
		{
			$newUserLine.find('.userAction a').attr('data-unfollow',user.login)
			.attr('title','Stop following '+user.login)
			.find('i').addClass('icon-eye-close frame');
		}	
	}
	
	bindListeners($newUserLine);
	return $newUserLine;
	
}


