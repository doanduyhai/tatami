function followMessage(targetLogin,follow)
{
	if(follow)
	{	
		$('#followSuccess').find('span').remove();
		$('#followSuccess').fadeIn("fast").append('<span>You are now following '+targetLogin+'</span>');
		$('#followSuccess').delay(500).fadeOut(5000);
	}
	else
	{
		$('#followSuccess').find('span').remove();
		$('#followSuccess').fadeIn("fast").append('<span>You no longer follow '+targetLogin+'</span>');
	    $('#followSuccess').delay(500).fadeOut(5000);		
	}	
}


function followError()
{
	return function(xhr, ajaxOptions, thrownError)
	{
		$('#followError').find('span').remove();
		$('#followError').fadeIn("fast").append('<span>'+xhr.responseText+'</span>');
        $('#followError').delay(2000).fadeOut(5000);		
	};
}

function followUser(loginToFollow) {
	$.ajax({
		type: HTTP_POST,
		url: "rest/users/" + login + "/followUser",
		contentType: JSON_CONTENT,
		data: loginToFollow,
		dataType: JSON_DATA,
        success: function(data) {

			setTimeout(function()
			{
	            $("#followUserInput").val("");
	            updateUserCounters();
	            followMessage(loginToFollow,true);
	            refreshUserSuggestions();
			},300);

        },
    	error: followError()
	});

	return false;
}

function removeFriend(friend) {
	
	$.ajax({
		type: HTTP_POST,
		url: "rest/users/" + login + "/removeFriend",
		contentType: "application/json;  charset=UTF-8",
		data: friend,
		dataType: JSON_DATA,
        success: function(data) {

			setTimeout(function()
			{
	        	updateUserCounters();
	        	followMessage(friend,false);	
	        	refreshUserSuggestions();
			},300);

        },
    	error: followError()
	});
}


function refreshUserSuggestions()
{
	$.ajax({
		type: HTTP_GET,
		url: 'rest/users/suggestions',
		dataType: JSON_DATA,
        success: function(data)
        {
    		var $tableBody = $('#userSuggestions');
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
        		$newUserLine = $('#emptyUserTemplate').clone().attr('id','').appendTo($tableBody);
        	}	
        	
        }
    });	
}