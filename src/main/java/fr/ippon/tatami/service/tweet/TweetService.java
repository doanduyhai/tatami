package fr.ippon.tatami.service.tweet;

import java.util.Calendar;
import java.util.Collection;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.ReTweetRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.service.pipeline.tweet.RetweetHandler;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.util.TimeUUIdReorder;

public class TweetService implements TweetHandler, RetweetHandler
{
	private TweetRepository tweetRepository;

	private ReTweetRepository retweetRepository;

	private AuthenticationService authenticationService;

	public Tweet createTransientTweet(String content)
	{
		User currentUser = authenticationService.getCurrentUser();

		Tweet tweet = new Tweet();
		tweet.setTweetId(TimeUUIdReorder.reorderTimeUUId(TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString()));
		tweet.setOriginalTweetId(tweet.getTweetId());
		tweet.setSourceTweetId(tweet.getTweetId());
		tweet.setLogin(currentUser.getLogin());
		tweet.setOriginalAuthorLogin(currentUser.getLogin());
		tweet.setContent(content);
		tweet.setTweetDate(Calendar.getInstance().getTime());
		tweet.setFirstName(currentUser.getFirstName());
		tweet.setLastName(currentUser.getLastName());
		tweet.setGravatar(currentUser.getGravatar());

		return tweet;
	}

	public Tweet findTweetById(String tweetId) throws FunctionalException
	{
		Tweet tweet = this.tweetRepository.findTweetById(tweetId);
		if (tweet == null)
		{
			throw new FunctionalException("Cannot find tweet with id '" + tweetId + "'");
		}

		return tweet.duplicate();
	}

	@Override
	public void onTweetPost(Tweet tweet)
	{
		this.tweetRepository.saveTweet(tweet);
	}

	@Override
	public void onTweetRemove(Tweet tweet)
	{
		if (this.retweetRepository.countRetweeters(tweet.getTweetId()) > 0)
		{
			// Remove all retweets
			Collection<String> retweetIds = this.retweetRepository.findRetweetIdsForTweet(tweet.getTweetId());

			Tweet retweet;
			for (String retweetId : retweetIds)
			{
				retweet = this.tweetRepository.findTweetById(retweetId);
				this.tweetRepository.removeTweet(retweet);
			}
		}
		this.tweetRepository.removeTweet(tweet);
	}

	@Override
	public void onRetweet(Tweet reTweet) throws FunctionalException
	{
		this.tweetRepository.saveTweet(reTweet);
	}

	@Override
	public void onCancelRetweet(String originalTweetId) throws FunctionalException
	{
		User currentUser = authenticationService.getCurrentUser();

		String retweetId = this.retweetRepository.findRetweetIdForRetweeter(currentUser.getLogin(), originalTweetId);

		Tweet retweet = this.tweetRepository.findTweetById(retweetId);

		this.tweetRepository.removeTweet(retweet);
	}

	public void setTweetRepository(TweetRepository tweetRepository)
	{
		this.tweetRepository = tweetRepository;
	}

	public void setRetweetRepository(ReTweetRepository retweetRepository)
	{
		this.retweetRepository = retweetRepository;
	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}

}
