package fr.ippon.tatami.service.lines;

import java.util.ArrayList;
import java.util.Collection;

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
		for (String tweedId : tweetIds)
		{
			Tweet tweet = tweetRepository.findTweetById(tweedId);
			User tweetUser = userService.getUserByLogin(tweet.getLogin());
			tweet.setFirstName(tweetUser.getFirstName());
			tweet.setLastName(tweetUser.getLastName());
			tweet.setGravatar(tweetUser.getGravatar());

			this.tweetRenderingPipelineManager.onTweetRender(tweet);

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

	public void setTweetRenderingPipelineManager(TweetRenderingPipelineManager tweetRenderingPipelineManager)
	{
		this.tweetRenderingPipelineManager = tweetRenderingPipelineManager;
	}

}
