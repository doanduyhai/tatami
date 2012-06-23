package fr.ippon.tatami.service.lines;

import java.util.ArrayList;
import java.util.Collection;

import fr.ippon.tatami.domain.ConversationItem;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.service.pipeline.tweet.rendering.TweetRenderingPipelineManager;
import fr.ippon.tatami.service.user.UserService;

public abstract class AbstractlineService
{
	protected UserService userService;

	protected TweetRepository tweetRepository;

	protected FavoriteRepository favoriteLineRepository;

	protected TweetRenderingPipelineManager tweetRenderingPipelineManager;

	protected Collection<Tweet> buildTweetsList(User currentUser, Collection<String> tweetIds) throws FunctionalException
	{
		Collection<Tweet> tweets = new ArrayList<Tweet>(tweetIds.size());

		Tweet tweet;
		User tweetUser;
		for (String tweedId : tweetIds)
		{
			tweet = tweetRepository.findTweetById(tweedId);

			if (tweet != null)
			{
				tweetUser = userService.getUserByLogin(tweet.getLogin());

				// Duplicate to avoid modifying the cache
				tweet = tweet.duplicate();
				tweet.setFirstName(tweetUser.getFirstName());
				tweet.setLastName(tweetUser.getLastName());
				tweet.setGravatar(tweetUser.getGravatar());

				this.tweetRenderingPipelineManager.onTweetRender(tweet);

				tweets.add(tweet);
			}

		}
		return tweets;
	}

	protected Collection<ConversationItem> buildConversationTweetsList(User currentUser, Collection<ConversationItem> conversationItems)
			throws FunctionalException
	{

		Tweet tweet;
		User tweetUser;
		for (ConversationItem item : conversationItems)
		{
			tweet = tweetRepository.findTweetById(item.getTweetId());

			if (tweet != null)
			{
				tweetUser = userService.getUserByLogin(tweet.getLogin());

				// Duplicate to avoid modifying the cache
				tweet = tweet.duplicate();
				tweet.setFirstName(tweetUser.getFirstName());
				tweet.setLastName(tweetUser.getLastName());
				tweet.setGravatar(tweetUser.getGravatar());

				this.tweetRenderingPipelineManager.onTweetRender(tweet);

				item.setTweet(tweet);
			}
		}
		return conversationItems;
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

	public void setTweetRenderingPipelineManager(TweetRenderingPipelineManager tweetRenderingPipelineManager)
	{
		this.tweetRenderingPipelineManager = tweetRenderingPipelineManager;
	}

}
