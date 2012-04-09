function loadHome()
{
	$('#homeTabContent').empty();
	$('#homeTabContent').load('fragments/user.html #homeContent',
	function()
	{
		bindListeners($('#homeTabContent'));	
	});
}

function updateProfile() {
	
	$('#userProfileErrorPanel').hide();
	
	$.ajax({
		type: HTTP_POST,
		url: "rest/users/" + login,
		contentType: JSON_CONTENT,
		data: JSON.stringify($("#updateUserForm").serializeObject()),
		dataType: JSON_DATA,
		success: function(data) {
			$('#defaultTab').tab('show');
			setTimeout(function()
			{
				loadHome();
				updateUserCounters();
			},300);		
		},
		error: errorHandler($('#userProfileErrorPanel'))
	});
	return false;
}

function loadProfile()
{
	$('#profileTabContent').empty();
	$('#profileTabContent').load('fragments/profile.html #profileContent',function()
	{
		bindListeners($('#profileTabContent'));
	});
	
}


function updateUserCounters()
{
	$.ajax({
		type: HTTP_GET,
		url: "rest/usersStats/" + login,
		dataType: JSON_DATA,
		success: function(data) {
			$("#tweetCount").text(data.tweetCount);
			$("#friendsCount").text(data.friendsCount);
			$("#followersCount").text(data.followersCount);
			
		}
	});
}

function tweet() {

	$('#tweetErrorPanel').hide();
	$.ajax({
        type: HTTP_POST,
        url: "rest/tweets",
        contentType: "application/json;  charset=UTF-8",
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

function showUserProfile(login)
{
	$.ajax({
		type: HTTP_GET,
		url: "rest/usersProfile/" + login,
		dataType: JSON_DATA,
		success: function(data) {
			
			updateUserProfileModal(data);
			$('#userProfileModal').modal('show');
		}
	});
	
}

function updateUserProfileModal(data)
{
	$('#userProfileModal')
	.find('#userProfileLogin').html('@'+data.login).end()
	.find('#userProfileGravatar .tweetGravatar').attr('src','http://www.gravatar.com/avatar/'+data.gravatar+'?s=128').end()
	.find('#userProfileName').html(data.firstName+'&nbsp;'+data.lastName).end()
	.find('#userProfileLocation span:nth-child(2)').html(data.location).end()
	.find('#userProfileWebsite a').html(data.website).attr('href',data.website).end()
	.find('#userProfileBio').html(data.biography).end()
	.find('#userProfileTweetsCount').html(data.tweetCount).end()
	.find('#userProfileFriendsCount').html(data.friendsCount).end()
	.find('#userProfileFollowersCount').html(data.followersCount);
}

function registerUserSearchListener()
{
	$('#userSearchForm button').click(function()
	{
		$('#searchErrorPanel').hide();
		$.ajax({
			type: HTTP_POST,
			url: "rest/usersSearch",
	        contentType: "application/json;  charset=UTF-8",
	        data:  JSON.stringify({searchString: $.trim($("#followUserInput").val())}),			
			dataType: JSON_DATA,
			success: function(data) {
				
				var $tableBody = $('#userSearchList');
	    		$tableBody.empty();
	        	if(data.length>0)
	    		{
		        	$.each(data,function(index, user)
		        	{        		
		        		$tableBody.append(fillUserTemplate(user));
		        	});
		        	
	    		}
	        	else
	        	{
	        		$newUserLine = $('#emptyUserSearchTemplate').clone().attr('id','').appendTo($tableBody);
	        	}
				$('#userSearchModal').modal('show');
			},
			error: errorHandler($('#searchErrorPanel'))
		});
		
		return false;
	});
}

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


