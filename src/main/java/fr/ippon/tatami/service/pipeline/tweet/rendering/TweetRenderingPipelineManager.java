package fr.ippon.tatami.service.pipeline.tweet.rendering;

import java.util.List;

import fr.ippon.tatami.domain.Tweet;

public class TweetRenderingPipelineManager
{

	private List<TweetRenderingHandler> tweetRenderingHandlers;

	public void onTweetRender(Tweet tweet)
	{
		for (TweetRenderingHandler handler : tweetRenderingHandlers)
		{
			handler.onRender(tweet);
		}
	}

	public void setTweetRenderingHandlers(List<TweetRenderingHandler> tweetRenderingHandlers)
	{
		this.tweetRenderingHandlers = tweetRenderingHandlers;
	}

}
