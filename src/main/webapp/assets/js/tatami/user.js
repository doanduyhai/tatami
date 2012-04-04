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
		type: 'POST',
		url: "rest/users/" + login,
		contentType: "application/json; charset=UTF-8",
		data: JSON.stringify($("#updateUserForm").serializeObject()),
		dataType: "json",
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
		type: 'GET',
		url: "rest/usersStats/" + login + "/",
		dataType: "json",
		success: function(data) {
			setTimeout(function()
			{
				$("#tweetCount").text(data.tweetCount);
				$("#friendsCount").text(data.friendsCount);
				$("#followersCount").text(data.followersCount);
			},200);	
			
		}
	});
}

function tweet() {

	$('#tweetErrorPanel').hide();
	$.ajax({
        type: 'POST',
        url: "rest/tweets",
        async: false,
        contentType: "application/json;  charset=UTF-8",
        data:  JSON.stringify({content: $.trim($("#tweetContent").val())}),
        dataType: "json",
        success: function(data) {
			setTimeout(function()
			{
				$("#tweetContent").slideUp().val("").slideDown('fast');
				updateUserCounters();
				refreshTimeline();
				loadWhoToFollow();
			},300);	
        },
        error: errorHandler($('#tweetErrorPanel'))
    });
		
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
            o[this.name].push($.trim(this.value) || '');
        } else {
            o[this.name] = $.trim(this.value) || '';
        }
    });
    return o;
};


