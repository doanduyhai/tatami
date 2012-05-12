package fr.ippon.tatami.service.tweet;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;

public class UrlShortenerService implements TweetHandler
{

	@Override
	public void onTweetPost(Tweet tweet)
	{
		// TODO implements URL reduction
	}

	@Override
	public void onTweetRemove(Tweet tweet) throws FunctionalException
	{
		// TODO Auto-generated method stub

	}

}
