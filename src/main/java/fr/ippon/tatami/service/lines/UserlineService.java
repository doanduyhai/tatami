package fr.ippon.tatami.service.lines;

import java.util.Arrays;
import java.util.Collection;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.UserLineRepository;
import fr.ippon.tatami.service.pipeline.TweetHandler;
import fr.ippon.tatami.service.util.TatamiConstants;

public class UserlineService extends AbstractlineService implements TweetHandler
{
	private UserLineRepository userLineRepository;

	public Collection<Tweet> getUserlineRange(String login, String startTweetId, int count) throws FunctionalException
	{
		User user = this.userService.getUserByLogin(login);
		User currentUser = this.userService.getCurrentUser();
		if (user == null)
		{
			return Arrays.asList();
		}

		if (startTweetId == null && count < TatamiConstants.DEFAULT_TWEET_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
		}

		Collection<String> tweetIds = userLineRepository.getTweetsRangeFromUserline(user, startTweetId, count);

		return this.buildTweetsList(currentUser, tweetIds);
	}

	@Override
	public void onTweetPost(Tweet tweet)
	{
		User currentUser = userService.getCurrentUser();
		userLineRepository.addTweetToUserline(currentUser, tweet.getTweetId());
	}

	public void setUserLineRepository(UserLineRepository userLineRepository)
	{
		this.userLineRepository = userLineRepository;
	}

}
