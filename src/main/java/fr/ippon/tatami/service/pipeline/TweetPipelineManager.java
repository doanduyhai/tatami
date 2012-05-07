package fr.ippon.tatami.service.pipeline;

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

	public void setTweetHandlers(List<TweetHandler> handlers)
	{
		this.tweetHandlers = handlers;
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
