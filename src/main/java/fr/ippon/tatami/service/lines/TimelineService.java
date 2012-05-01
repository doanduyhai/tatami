package fr.ippon.tatami.service.lines;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.service.pipeline.TweetHandler;
import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class TimelineService extends AbstractlineService implements TweetHandler
{

	private final Logger log = LoggerFactory.getLogger(TimelineService.class);

	private TimeLineRepository timeLineRepository;

	@Override
	public void onTweetPost(Tweet tweet)
	{
		User currentUser = userService.getCurrentUser();

		timeLineRepository.addTweetToTimeline(currentUser, tweet.getTweetId());
	}

	public Collection<Tweet> getTimelineRange(String startTweetId, int count) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		if (startTweetId == null && count < TatamiConstants.DEFAULT_TWEET_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
		}
		Collection<String> tweetIds = timeLineRepository.getTweetsRangeFromTimeline(currentUser, startTweetId, count);

		return this.buildTweetsList(currentUser, tweetIds);
	}

	// public boolean removeTweet(String tweetId)
	// {
	// log.debug("Removing tweet : {} ", tweetId);
	//
	// Tweet tweet = tweetRepository.findTweetById(tweetId);
	//
	// User currentUser = userService.getCurrentUser();
	// if (tweet.getLogin().equals(currentUser.getLogin()) && !Boolean.TRUE.equals(tweet.getRemoved()))
	// {
	// tweetRepository.removeTweet(tweet);
	// currentUser.decrementTweetCount();
	// currentUser.decrementTimelineTweetCount();
	//
	// this.userService.updateUser(currentUser);
	//
	// return true;
	// }
	// return false;
	// }

	public void setTimeLineRepository(TimeLineRepository timeLineRepository)
	{
		this.timeLineRepository = timeLineRepository;
	}

}