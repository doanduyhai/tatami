package fr.ippon.tatami.service.pipeline.tweet;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;

public interface TweetHandler
{
	void onTweetPost(Tweet tweet) throws FunctionalException;

	void onTweetRemove(Tweet tweet) throws FunctionalException;
}
