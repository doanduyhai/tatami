
function loadWhoToFollow()
{
	$('#followPanel').empty();
	$('#followPanel').load('fragments/mobile/suggestions.html #followline',function()
	{
		//bindListeners($('#followPanel'));
	});
}