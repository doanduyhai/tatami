package fr.ippon.tatami.service.tweet;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.service.pipeline.TweetHandler;

public class UrlShortenerService implements TweetHandler
{

	@Override
	public void onTweetPost(Tweet tweet)
	{
		// TODO implements URL reduction
	}

}
