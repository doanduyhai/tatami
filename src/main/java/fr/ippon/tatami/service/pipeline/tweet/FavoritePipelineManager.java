package fr.ippon.tatami.service.pipeline.tweet;

import java.util.List;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.tweet.TweetService;

public class FavoritePipelineManager
{

	private List<FavoriteHandler> favoriteHandlers;

	private TweetService tweetService;

	public void onAddToFavorite(String tweetId) throws FunctionalException
	{
		Tweet tweet = this.tweetService.findTweetById(tweetId);

		for (FavoriteHandler handler : favoriteHandlers)
		{
			handler.onAddToFavorite(tweet);
		}
	}

	public void onRemoveFromFavorite(String tweetId) throws FunctionalException
	{
		Tweet tweet = this.tweetService.findTweetById(tweetId);

		for (FavoriteHandler handler : favoriteHandlers)
		{
			handler.onRemoveFromFavorite(tweet);
		}
	}

	public void setFavoriteHandlers(List<FavoriteHandler> favoriteHandlers)
	{
		this.favoriteHandlers = favoriteHandlers;
	}

	public void setTweetService(TweetService tweetService)
	{
		this.tweetService = tweetService;
	}

}
