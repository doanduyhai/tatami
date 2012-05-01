package fr.ippon.tatami.service.tweet;

import java.util.Calendar;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.service.pipeline.TweetHandler;
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

	@Override
	public void onTweetPost(Tweet tweet)
	{
		this.tweetRepository.saveTweet(tweet);
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
