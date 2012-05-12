package fr.ippon.tatami.service.lines;

import static fr.ippon.tatami.service.util.TatamiConstants.USERTAG;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.FollowedTweetIndexRepository;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.service.pipeline.tweet.FavoriteHandler;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;
import fr.ippon.tatami.service.pipeline.user.UserHandler;
import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class TimelineService extends AbstractlineService implements TweetHandler, UserHandler, FavoriteHandler
{

	private final Logger log = LoggerFactory.getLogger(TimelineService.class);

	private TimeLineRepository timeLineRepository;

	private FriendRepository friendRepository;

	private FollowedTweetIndexRepository followedTweetIndexRepository;

	@Override
	public void onTweetPost(Tweet tweet)
	{
		User currentUser = userService.getCurrentUser();

		timeLineRepository.addTweetToTimeline(currentUser.getLogin(), tweet.getTweetId());
	}

	@Override
	public void onTweetRemove(Tweet tweet) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();
		timeLineRepository.removeTweetFromTimeline(currentUser.getLogin(), tweet.getTweetId());

	}

	@Override
	public void onUserFollow(String userLoginToFollow) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();
		Collection<String> friends = this.friendRepository.findFriendsForUser(currentUser);

		if (!StringUtils.equals(userLoginToFollow, currentUser.getLogin()))
		{
			if (!friends.contains(userLoginToFollow))
			{
				// Tweet alert
				String content = USERTAG + currentUser.getLogin() + " <strong>is now following you</strong>";
				Tweet alertTweet = tweetRepository.createTweet(userLoginToFollow, content, true);
				timeLineRepository.addTweetToTimeline(userLoginToFollow, alertTweet.getTweetId());

			}
		}
		else
		{
			throw new FunctionalException("You cannot follow yourself!");
		}

	}

	@Override
	public void onUserForget(String userLoginToForget) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();
		Collection<String> friends = this.friendRepository.findFriendsForUser(currentUser);
		if (!StringUtils.equals(userLoginToForget, currentUser.getLogin()))
		{
			if (friends.contains(userLoginToForget))
			{
				Collection<String> indexedTweets = this.followedTweetIndexRepository.findTweetsForUserAndFollower(userLoginToForget,
						currentUser.getLogin());

				for (String tweetId : indexedTweets)
				{
					this.timeLineRepository.removeTweetFromTimeline(currentUser.getLogin(), tweetId);
				}
				this.followedTweetIndexRepository.removeIndex(userLoginToForget, currentUser.getLogin());
			}
		}
		else
		{
			throw new FunctionalException("You cannot forget yourself!");
		}

	}

	public Collection<Tweet> getTimelineRange(String startTweetId, int count) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		if (startTweetId == null && count < TatamiConstants.DEFAULT_TWEET_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
		}
		Collection<String> tweetIds = timeLineRepository.getTweetsRangeFromTimeline(currentUser.getLogin(), startTweetId, count);

		return this.buildTweetsList(currentUser, tweetIds);
	}

	@Override
	public void onAddToFavorite(Tweet tweet)
	{
		User currentUser = userService.getCurrentUser();

		// Tweet alert
		if (!currentUser.getLogin().equals(tweet.getLogin()))
		{

			String content = USERTAG + currentUser.getLogin() + " <strong>liked your tweet:</strong><br/><em>_PH_...</em>";

			int maxLength = TatamiConstants.MAX_TWEET_SIZE - content.length() + 4;
			if (tweet.getContent().length() > maxLength)
			{
				content = content.replace("_PH_", tweet.getContent().substring(0, maxLength));
			}
			else
			{
				content = content.replace("_PH_", tweet.getContent());
			}

			Tweet helloTweet = tweetRepository.createTweet(tweet.getLogin(), content, true);
			timeLineRepository.addTweetToTimeline(tweet.getLogin(), helloTweet.getTweetId());
		}

	}

	@Override
	public void onRemoveFromFavorite(Tweet tweet)
	{
		// Do nothing

	}

	public void setTimeLineRepository(TimeLineRepository timeLineRepository)
	{
		this.timeLineRepository = timeLineRepository;
	}

	public void setFriendRepository(FriendRepository friendRepository)
	{
		this.friendRepository = friendRepository;
	}

	public void setFollowedTweetIndexRepository(FollowedTweetIndexRepository followedTweetIndexRepository)
	{
		this.followedTweetIndexRepository = followedTweetIndexRepository;
	}

}