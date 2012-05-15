package fr.ippon.tatami.service.tweet;

import java.util.Calendar;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.util.TimeUUIdReorder;

public class TweetService implements TweetHandler
{
	private TweetRepository tweetRepository;

	private AuthenticationService authenticationService;

	public Tweet createTransientTweet(String content)
	{
		User currentUser = authenticationService.getCurrentUser();

		Tweet tweet = new Tweet();
		tweet.setTweetId(TimeUUIdReorder.reorderTimeUUId(TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString()));
		tweet.setLogin(currentUser.getLogin());
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
		this.tweetRepository.removeTweet(tweet);
	}

	public void setTweetRepository(TweetRepository tweetRepository)
	{
		this.tweetRepository = tweetRepository;
	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}

}
