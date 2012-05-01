package fr.ippon.tatami.service.pipeline;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.tweet.TweetService;

public class TweetPipelineManager
{

	private final Logger log = LoggerFactory.getLogger(TweetPipelineManager.class);

	private TweetService tweetService;

	private List<TweetHandler> tweetPosthandlers;

	public Tweet onPost(String tweetContent) throws FunctionalException
	{
		log.debug("Creating new tweet : {}", tweetContent);

		Tweet newTweet = this.tweetService.createTransientTweet(tweetContent);

		for (TweetHandler handler : tweetPosthandlers)
		{
			handler.onTweetPost(newTweet);
		}
		return newTweet;
	}

	public void setTweetPostHandlers(List<TweetHandler> handlers)
	{
		this.tweetPosthandlers = handlers;
	}

	public void setTweetService(TweetService tweetService)
	{
		this.tweetService = tweetService;
	}

}
