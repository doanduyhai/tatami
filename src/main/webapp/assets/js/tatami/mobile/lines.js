
function loadEmptyLines()
{
	$('#timelinePanel').load('fragments/mobile/timeline.html #timeline',function()
	{
		//bindListeners($('#tweetsList'));
	});

	$('#favlinePanel').load('fragments/mobile/favline.html #favline',function()
	{
		//bindListeners($('#tweetsList'));
	});
	
	$('#userlinePanel').load('fragments/mobile/userline.html #userline',function()
	{
		//bindListeners($('#tweetsList'));
	});	

	$('#taglinePanel').load('fragments/mobile/tagline.html #tagline',function()
	{
		//bindListeners($('#tweetsList'));
	});
}