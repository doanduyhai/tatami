var nbTweets;

function resetNbTweets() {
	nbTweets = 20;
}

function incrementNbTweets() {
	nbTweets += 10;
}

function loadHome()
{
	jQuery('#homeTabContent').empty();
	jQuery('#homeTabContent').load('fragments/user.html #homeContent',
	function()
	{
		jQuery('#tweetButton').click(tweet);
	});
}



function refreshProfile() {
	$.ajax({
		type: 'GET',
		url: "rest/users/" + login + "/",
		dataType: "json",
		success: function(data) {
			$("#picture").parent().css('width', '68px');	// optional
            $("#picture").attr('src', 'http://www.gravatar.com/avatar/' + data.gravatar + '?s=64');

            $("#firstName").text(data.firstName);
			$("#lastName").text(data.lastName);
			$("#tweetCount").text(data.tweetCount);
			$("#friendsCount").text(data.friendsCount);
			$("#followersCount").text(data.followersCount);
		}
	});
}


function tweet() {
	if (jQuery('#tweetContent').val() == "") {
		alert('Please type a message.');
		return false;
	}

	$.ajax({
        type: 'POST',
        url: "rest/tweets",
        contentType: "application/json",
        data: $("#tweetContent").val(),
        dataType: "json",
        success: function(data) {
            $("#tweetContent").slideUp().val("").slideDown('fast');
            setTimeout(function() {
            			$("#tweetCount").text($("#tweetCount").text()+1);
                        listTweets(true);
                    }, 1000);	//DEBUG wait for persistence consistency
        }
    });

	return false;
}

var userlineURL = '<a href="#" style="text-decoration:none" onclick="listUserTweets(\'LOGIN\')" title="Show LOGIN tweets">';
var userlineREG = new RegExp("LOGIN", "g");

function listTweets(reset) {

	if (reset)	resetNbTweets();
	else		incrementNbTweets();

	$.ajax({
		type: 'GET',
		url: "rest/tweets/" + login + "/" + nbTweets,
		dataType: "json",
		success: function(data) {
			makeTweetsList(data, $('#tweetsList'), true);
			$('#mainTab').tab('show');
		}
	});
}

function listUserTweets(login) {
	$.ajax({
		type: 'GET',
		url: "rest/ownTweets/" + login,
		dataType: "json",
		success: function(data) {
			makeTweetsList(data, $('#userTweetsList'), false);
			$('#userTab').tab('show');
		}
	});
}

var userrefREG = new RegExp("@(\\w+)", "g");
var userrefURL = '<a href="#" style="text-decoration:none" onclick="listUserTweets(\'$1\')" title="Show $1 tweets"><em>@$1</em></a>';

function makeTweetsList(data, dest, timelineMode) {
	dest.fadeTo(400, 0, function() {	//DEBUG do NOT use fadeIn/fadeOut which would scroll up the page
		dest.empty();

		$.each(data, function(entryIndex, entry) {
			var userline;
			if (timelineMode && login != entry['login']) {
				userline = userlineURL.replace(userlineREG, entry['login']);
			}

			var html = '<tr valign="top">';
			// identification de l'émetteur du message
			html += '<td style="width: 34px; ">';
			if (userline)	html += userline;
			html += '<img src="http://www.gravatar.com/avatar/' + entry['gravatar'] + '?s=32" />';
			if (userline)	html += '</a>';
			html += '</td>';
			html += '<td><article>';
			html += '<strong>' + entry['firstName'] + ' ' + entry['lastName'] + '</strong>&nbsp;';
			if (userline)	html += userline;
			html += '<em>@' + entry['login'] + '</em>';
			if (userline)	html += '</a>';
			// contenu du message
			html += '<br/>' + entry['content'].replace(userrefREG, userrefURL);
			html += '</article></td>';
			// colonne de suppression des abonnements
			html += '<td class="tweetFriend">';
			if (timelineMode && login != entry['login']) {
				html += '<a href="#" onclick="removeFriend(\'' + entry['login'] + '\')" title="Unfollow"><i class="icon-star-empty" /></a>';
			} else {
				html += '&nbsp;';
			}
			html += '</td>';
			// temps écoulé depuis la publication du message
			html += '<td class="tweetDate"><aside>' + entry['prettyPrintTweetDate'] + '</aside></td>';
			html += '</tr>';
	
			dest.append(html);
		});

		dest.fadeTo(400, 1);
	});
}

function whoToFollow() {
	$.ajax({
		type: 'GET',
		url: "rest/suggestions",
		dataType: "json",
		success: function(data) {
			makeUsersList(data, $('#suggestions'));
		}
	});
}

function makeUsersList(data, dest) {
	dest.fadeTo(400, 0, function() {	//DEBUG do NOT use fadeIn/fadeOut which would scroll up the page
		dest.empty();


		$.each(data, function(entryIndex, entry) {
			var userline;
			if (login != entry['login']) {
				userline = userlineURL.replace(userlineREG, entry['login']);
			}

			var html = '<tr valign="top">';
			// identification de l'émetteur du message
			html += '<td>';
			if (userline)	html += userline;
			html += '<img src="http://www.gravatar.com/avatar/' + entry['gravatar'] + '?s=32" /> ';
			html += '<em>@' + entry['login'] + '</em>';
			if (userline)	html += '</a>';
			html += '</td>';
			// colonne de suppression des abonnements
			html += '<td class="tweetFriend">';
			if (userline) {
				html += '<a href="#" onclick="followUser(\'' + entry['login'] + '\')" title="Follow"><i class="icon-star" /></a>';
			} else {
				html += '&nbsp;';
			}
			html += '</td>';
			html += '</tr>';
	
			dest.append(html);
		});

		dest.fadeTo(400, 1);
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
            setTimeout(function() {
                refreshProfile();
                listTweets(true);
            }, 500);	//DEBUG wait for persistence consistency
        },
    	error: function(xhr, ajaxOptions, thrownError) {
    		$('#followStatus').fadeIn("fast").text(thrownError);
            setTimeout($('#followStatus').fadeOut("slow"), 2000);
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
            setTimeout(refreshProfile(), 500);	//DEBUG wait for persistence consistency
        }
	});
}
