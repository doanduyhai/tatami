package fr.ippon.tatami.service.pipeline.tweet;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;

public interface RetweetHandler
{
	void onRetweet(Tweet reTweet) throws FunctionalException;

	void onCancelRetweet(String originalTweetId) throws FunctionalException;
}
