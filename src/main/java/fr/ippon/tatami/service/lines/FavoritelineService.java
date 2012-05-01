package fr.ippon.tatami.service.lines;

import static fr.ippon.tatami.service.util.TatamiConstants.USERTAG;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.service.util.TatamiConstants;

public class FavoritelineService extends AbstractlineService
{

	private final Logger log = LoggerFactory.getLogger(FavoritelineService.class);

	private TimeLineRepository timeLineRepository;

	public void addFavoriteTweet(String tweetId) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		log.debug("Adding tweet : {} to favorites for {} ", tweetId, currentUser.getLogin());

		Tweet tweet = tweetRepository.findTweetById(tweetId);
		if (tweet == null)
		{
			throw new FunctionalException("Cannot add non-existing tweet to favorite");
		}

		// FavoriteLine
		favoriteLineRepository.addFavorite(currentUser, tweetId);

		// Tweet alert
		if (!currentUser.getLogin().equals(tweet.getLogin()))
		{

			String content = USERTAG + currentUser.getLogin() + " <strong>liked your tweet:</strong><br/><em>_PH_...</em>";

			int maxLength = TatamiConstants.MAX_TWEET_SIZE - content.length() + 4;
			if (tweet.getContent().length() > maxLength)
			{
				content = content.replace("_PH_", tweet.getContent().substring(0, maxLength));
			}
			else
			{
				content = content.replace("_PH_", tweet.getContent());
			}

			Tweet helloTweet = tweetRepository.createTweet(tweet.getLogin(), content); // removable

			User author = this.userService.getUserByLogin(tweet.getLogin());

			timeLineRepository.addTweetToTimeline(author, helloTweet.getTweetId());
		}
	}

	public void removeFavoriteTweet(String tweetId) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		log.debug("Removing tweet : {} from favorites for {} ", tweetId, currentUser.getLogin());

		Tweet tweet = tweetRepository.findTweetById(tweetId);
		if (tweet == null)
		{
			throw new FunctionalException("Cannot remove non-existing tweet from favorite");
		}

		// FavoriteLine
		favoriteLineRepository.removeFavorite(currentUser, tweetId);
	}

	public Collection<Tweet> getFavoriteslineRange(String startTweetId, int count) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		if (startTweetId == null && count < TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE;
		}

		Collection<String> tweetIds = favoriteLineRepository.findFavoritesRangeForUser(currentUser, startTweetId, count);
		return this.buildTweetsList(currentUser, tweetIds);
	}

	public void setTimeLineRepository(TimeLineRepository timeLineRepository)
	{
		this.timeLineRepository = timeLineRepository;
	}

}
