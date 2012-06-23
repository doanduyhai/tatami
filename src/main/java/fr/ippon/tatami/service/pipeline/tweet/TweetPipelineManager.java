package fr.ippon.tatami.service.pipeline.tweet;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.tweet.TweetService;
import fr.ippon.tatami.service.user.UserService;

public class TweetPipelineManager
{

	private final Logger log = LoggerFactory.getLogger(TweetPipelineManager.class);

	private TweetService tweetService;

	private UserService userService;

	private List<TweetHandler> tweetHandlers;

	private List<RetweetHandler> retweetHandlers;

	private List<FavoriteHandler> favoriteHandlers;

	private List<ConversationHandler> conversationHandlers;

	public Tweet onPost(String tweetContent) throws FunctionalException
	{
		log.debug("Creating new tweet : {}", tweetContent);

		Tweet newTweet = this.tweetService.createTransientTweet(tweetContent);

		for (TweetHandler handler : tweetHandlers)
		{
			handler.onTweetPost(newTweet);
		}
		return newTweet;
	}

	public void onRemove(String tweetId) throws FunctionalException
	{
		log.debug("Removing weet : {}", tweetId);

		Tweet tweet = this.tweetService.findTweetById(tweetId);

		User currentUser = this.userService.getCurrentUser();

		if (StringUtils.equals(currentUser.getLogin(), tweet.getLogin()))
		{
			for (TweetHandler handler : tweetHandlers)
			{
				handler.onTweetRemove(tweet);
			}
		}
		else
		{
			throw new FunctionalException("You cannot remove someone else tweet!!");
		}

	}

	public void onRetweet(String originalTweetId) throws FunctionalException
	{
		log.debug("Retweeting : {}", originalTweetId);
		Tweet originalTweet = this.tweetService.findTweetById(originalTweetId);
		Tweet newTweet = this.tweetService.createTransientTweet(originalTweet.getContent());

		newTweet.setOriginalAuthorLogin(originalTweet.getOriginalAuthorLogin());
		newTweet.setOriginalTweetId(originalTweetId);

		for (RetweetHandler handler : retweetHandlers)
		{
			handler.onRetweet(newTweet);
		}
	}

	public void onCancelRetweet(String originalTweetId) throws FunctionalException
	{
		log.debug("Cancel retweet of : {}", originalTweetId);

		for (RetweetHandler handler : retweetHandlers)
		{
			handler.onCancelRetweet(originalTweetId);
		}

	}

	public void onAddToFavorite(String tweetId) throws FunctionalException
	{
		log.debug("Adding {} to user favorite", tweetId);

		Tweet tweet = this.tweetService.findTweetById(tweetId);

		for (FavoriteHandler handler : favoriteHandlers)
		{
			handler.onAddToFavorite(tweet);
		}
	}

	public void onRemoveFromFavorite(String tweetId) throws FunctionalException
	{
		log.debug("Removing {} from user favorite", tweetId);

		Tweet tweet = this.tweetService.findTweetById(tweetId);

		for (FavoriteHandler handler : favoriteHandlers)
		{
			handler.onRemoveFromFavorite(tweet);
		}
	}

	public void onAddToConversation(String tweetContent, String sourceTweetId) throws FunctionalException
	{
		log.debug("Adding {} to conversation with {}", tweetContent, sourceTweetId);

		Tweet newTweet = this.tweetService.createTransientTweet(tweetContent);

		for (TweetHandler handler : tweetHandlers)
		{
			handler.onTweetPost(newTweet);
		}

		for (ConversationHandler handler : this.conversationHandlers)
		{
			handler.onAddToConversation(newTweet, sourceTweetId);
		}

	}

	public void setTweetHandlers(List<TweetHandler> handlers)
	{
		this.tweetHandlers = handlers;
	}

	public void setRetweetHandlers(List<RetweetHandler> retweetHandlers)
	{
		this.retweetHandlers = retweetHandlers;
	}

	public void setFavoriteHandlers(List<FavoriteHandler> favoriteHandlers)
	{
		this.favoriteHandlers = favoriteHandlers;
	}

	public void setConversationHandlers(List<ConversationHandler> conversationHandlers)
	{
		this.conversationHandlers = conversationHandlers;
	}

	public void setTweetService(TweetService tweetService)
	{
		this.tweetService = tweetService;
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

}
