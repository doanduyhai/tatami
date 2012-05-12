package fr.ippon.tatami.service.pipeline.tweet.rendering;

import fr.ippon.tatami.domain.Tweet;

public interface TweetRenderingHandler
{
	public void onRender(Tweet tweet);
}
