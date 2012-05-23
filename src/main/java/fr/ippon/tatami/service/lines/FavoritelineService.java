package fr.ippon.tatami.service.lines;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.pipeline.tweet.FavoriteHandler;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;
import fr.ippon.tatami.service.util.TatamiConstants;

public class FavoritelineService extends AbstractlineService implements FavoriteHandler, TweetHandler
{

	private final Logger log = LoggerFactory.getLogger(FavoritelineService.class);

	@Override
	public void onAddToFavorite(Tweet tweet) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		log.debug("Adding tweet : {} to favorites for {} ", tweet.getTweetId(), currentUser.getLogin());

		Collection<String> favoriteTweets = this.favoriteLineRepository.findFavoritesForUser(currentUser.getLogin());

		if (!favoriteTweets.contains(tweet.getTweetId()))
		{
			this.favoriteLineRepository.addFavorite(currentUser.getLogin(), tweet.getTweetId());
			this.favoriteLineRepository.addTweetToFavoriteIndex(currentUser.getLogin(), tweet.getLogin(), tweet.getTweetId());
		}
		else
		{
			throw new FunctionalException("You already have this tweet in your favorites !");
		}

	}

	@Override
	public void onRemoveFromFavorite(Tweet tweet) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		log.debug("Removing tweet : {} from favorites for {} ", tweet.getTweetId(), currentUser.getLogin());

		Collection<String> favoriteTweets = this.favoriteLineRepository.findFavoritesForUser(currentUser.getLogin());

		if (favoriteTweets.contains(tweet.getTweetId()))
		{
			this.favoriteLineRepository.removeFavorite(currentUser.getLogin(), tweet.getTweetId());
			this.favoriteLineRepository.removeTweetFromFavoriteIndex(currentUser.getLogin(), tweet.getLogin(), tweet.getTweetId());
		}
		else
		{
			throw new FunctionalException("You do not have this tweet in your favorites so you can't remove it!");
		}

	}

	@Override
	public void onTweetPost(Tweet tweet) throws FunctionalException
	{
		// Do nothing

	}

	@Override
	public void onTweetRemove(Tweet tweet) throws FunctionalException
	{
		Collection<String> userLogins = this.favoriteLineRepository.getUsersForTweetFromIndex(tweet.getTweetId());

		User currentUser = userService.getCurrentUser();

		for (String login : userLogins)
		{
			this.favoriteLineRepository.removeFavorite(login, tweet.getTweetId());
		}

		this.favoriteLineRepository.removeIndexForTweet(currentUser.getLogin(), tweet.getLogin(), tweet.getTweetId());
	}

	public Collection<Tweet> getFavoriteslineRange(String startTweetId, int count) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		if (startTweetId == null && count < TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE;
		}

		Collection<String> tweetIds = favoriteLineRepository.findFavoritesRangeForUser(currentUser.getLogin(), startTweetId, count);
		return this.buildTweetsList(currentUser, tweetIds);
	}

}
