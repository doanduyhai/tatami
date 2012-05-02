package fr.ippon.tatami.service.pipeline;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;

public interface TweetHandler
{
	void onTweetPost(Tweet tweet) throws FunctionalException;
}