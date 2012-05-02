package fr.ippon.tatami.service.lines;

import static fr.ippon.tatami.service.util.TatamiConstants.HASHTAG_REGEXP;
import static fr.ippon.tatami.service.util.TatamiConstants.TAG_LINK_PATTERN;
import static fr.ippon.tatami.service.util.TatamiConstants.USER_LINK_PATTERN;
import static fr.ippon.tatami.service.util.TatamiConstants.USER_REGEXP;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.service.user.UserService;

public abstract class AbstractlineService
{
	protected UserService userService;

	protected TweetRepository tweetRepository;

	protected FavoriteRepository favoriteLineRepository;

	protected Collection<Tweet> buildTweetsList(User currentUser, Collection<String> tweetIds) throws FunctionalException
	{
		Collection<Tweet> tweets = new ArrayList<Tweet>(tweetIds.size());
		for (String tweedId : tweetIds)
		{
			Tweet tweet = tweetRepository.findTweetById(tweedId);
			User tweetUser = userService.getUserByLogin(tweet.getLogin());
			tweet.setFirstName(tweetUser.getFirstName());
			tweet.setLastName(tweetUser.getLastName());
			tweet.setGravatar(tweetUser.getGravatar());
			if (currentUser != null)
			{
				Collection<String> favorites = this.favoriteLineRepository.findFavoritesForUser(currentUser);

				if (!favorites.contains(tweet.getTweetId()))
				{
					tweet.setAddToFavorite(true);
				}
				else
				{
					tweet.setAddToFavorite(false);
				}

			}

			if (StringUtils.equals(currentUser.getLogin(), tweet.getLogin()))
			{
				tweet.setDeletable(true);
			}

			tweet.setContent(tweet.getContent().replaceAll(USER_REGEXP, USER_LINK_PATTERN).replaceAll(HASHTAG_REGEXP, TAG_LINK_PATTERN));

			tweets.add(tweet);
		}
		return tweets;
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	public void setTweetRepository(TweetRepository tweetRepository)
	{
		this.tweetRepository = tweetRepository;
	}

	public void setFavoriteLineRepository(FavoriteRepository favoriteLineRepository)
	{
		this.favoriteLineRepository = favoriteLineRepository;
	}

}
