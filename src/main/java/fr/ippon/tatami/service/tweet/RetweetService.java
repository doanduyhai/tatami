package fr.ippon.tatami.service.tweet;

import java.util.Collection;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.ReTweetRepository;
import fr.ippon.tatami.service.pipeline.tweet.RetweetHandler;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;
import fr.ippon.tatami.service.security.AuthenticationService;

public class RetweetService implements TweetHandler, RetweetHandler
{

	private ReTweetRepository retweetRepository;

	private AuthenticationService authenticationService;

	@Override
	public void onRetweet(Tweet reTweet) throws FunctionalException
	{
		User currentUser = authenticationService.getCurrentUser();

		this.retweetRepository.addToRetweetLine(currentUser.getLogin(), reTweet.getOriginalTweetId());

		this.retweetRepository.addRetweeter(currentUser.getLogin(), reTweet.getOriginalTweetId(), reTweet.getTweetId());

	}

	@Override
	public void onCancelRetweet(String originalTweetId) throws FunctionalException
	{
		User currentUser = authenticationService.getCurrentUser();

		this.retweetRepository.removeFromRetweetLine(currentUser.getLogin(), originalTweetId);

		this.retweetRepository.removeRetweeter(currentUser.getLogin(), originalTweetId);
	}

	public void setRetweetRepository(ReTweetRepository retweetRepository)
	{
		this.retweetRepository = retweetRepository;
	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}

	@Override
	public void onTweetPost(Tweet tweet) throws FunctionalException
	{
		// Do nothing

	}

	@Override
	public void onTweetRemove(Tweet tweet) throws FunctionalException
	{
		if (this.retweetRepository.countRetweeters(tweet.getTweetId()) > 0)
		{
			Collection<String> retweeters = this.retweetRepository.findRetweetersForTweet(tweet.getTweetId());
			for (String retweeter : retweeters)
			{
				this.retweetRepository.removeFromRetweetLine(retweeter, tweet.getTweetId());
			}

			this.retweetRepository.removeRetweeterIndex(tweet.getTweetId());
		}

	}
}
