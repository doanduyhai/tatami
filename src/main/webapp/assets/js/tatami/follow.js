function followMessage(targetLogin,follow)
{
	if(follow)
	{	
		$('#followSuccess').find('span').remove();
		$('#followSuccess').fadeIn("fast").append('<span>You are now following '+targetLogin+'</span>');
		$('#followSuccess').delay(2000).fadeOut(5000);
	}
	else
	{
		$('#followSuccess').find('span').remove();
		$('#followSuccess').fadeIn("fast").append('<span>You no longer follow '+targetLogin+'</span>');
	    $('#followSuccess').delay(2000).fadeOut(5000);		
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
		type: 'POST',
		url: "rest/users/" + login + "/followUser",
		contentType: "application/json; charset=UTF-8",
		data: loginToFollow,
		dataType: "json",
        success: function(data) {
            $("#followUserInput").val("");
            updateUserCounters();
            followMessage(loginToFollow,true);
            loadWhoToFollow();
        },
    	error: followError()
	});

	return false;
}

function removeFriend(friend) {
	
	$.ajax({
		type: 'POST',
		url: "rest/users/" + login + "/removeFriend",
		contentType: "application/json;  charset=UTF-8",
		data: friend,
		dataType: "json",
        success: function(data) {
        	updateUserCounters();
        	followMessage(friend,false);	
        	loadWhoToFollow();
        },
    	error: followError()
	});
}


function loadWhoToFollow()
{
	$('#userSuggestions').empty();
	$('#userSuggestions').load('fragments/followUser.html #userSuggestions tr',function()
	{
		bindListeners($('#userSuggestions'));
		
//		$('button[type="submit"]').click(function()
//		{
//			followUser($('#followline').val());
//			return false;
//		});
	});
}