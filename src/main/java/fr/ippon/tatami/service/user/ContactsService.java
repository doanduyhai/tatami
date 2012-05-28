package fr.ippon.tatami.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.ReTweetRepository;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.service.lines.StatslineService;
import fr.ippon.tatami.service.pipeline.tweet.RetweetHandler;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;
import fr.ippon.tatami.service.pipeline.user.UserHandler;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.util.TatamiConstants;

public class ContactsService extends AbstractUserService implements TweetHandler, RetweetHandler, UserHandler
{

	private final Logger log = LoggerFactory.getLogger(ContactsService.class);

	private AuthenticationService authenticationService;

	private StatslineService statslineService;

	private FollowerRepository followerRepository;

	private TimeLineRepository timeLineRepository;

	private ReTweetRepository retweetRepository;

	private TweetRepository tweetRepository;

	@Override
	public void onTweetPost(Tweet tweet) throws FunctionalException
	{
		User currentUser = this.authenticationService.getCurrentUser();

		// Spread tweet to followers and followerTweetIndex
		Collection<String> followerLogins = this.followerRepository.findFollowersForUser(currentUser);

		for (String followerLogin : followerLogins)
		{
			this.timeLineRepository.addTweetToTimeline(followerLogin, tweet.getTweetId());
			this.followerRepository.addTweetToIndex(currentUser.getLogin(), followerLogin, tweet.getTweetId());
		}

	}

	@Override
	public void onTweetRemove(Tweet tweet) throws FunctionalException
	{
		if (!tweet.isNotification())
		{
			User currentUser = this.authenticationService.getCurrentUser();

			// Remove tweet from followers and followerTweetIndex
			Collection<String> followerLogins = this.getFollowersForUser(currentUser.getLogin());
			for (String followerLogin : followerLogins)
			{
				this.timeLineRepository.removeTweetFromTimeline(followerLogin, tweet.getTweetId());
				this.followerRepository.removeTweetFromIndex(currentUser.getLogin(), followerLogin, tweet.getTweetId());
			}

			// Tweet has been retweeted, remove also retweets
			if (this.retweetRepository.countRetweeters(tweet.getTweetId()) > 0)
			{

				// Remove all retweets
				Collection<String> retweetIds = this.retweetRepository.findRetweetIdsForTweet(tweet.getTweetId());

				Tweet retweet;
				User retweeter;
				Collection<String> retweeterFollowers;
				for (String retweetId : retweetIds)
				{
					retweet = this.tweetRepository.findTweetById(retweetId);
					retweeter = this.userRepository.findUserByLogin(retweet.getLogin());
					retweeterFollowers = this.followerRepository.findFollowersForUser(retweeter);

					for (String retweeterFollowerLogin : retweeterFollowers)
					{
						this.timeLineRepository.removeTweetFromTimeline(retweeterFollowerLogin, retweet.getTweetId());
						this.followerRepository.removeTweetFromIndex(retweeter.getLogin(), retweeterFollowerLogin, retweet.getTweetId());
					}

				}
			}
		}
	}

	@Override
	public void onRetweet(Tweet reTweet) throws FunctionalException
	{
		User currentUser = authenticationService.getCurrentUser();

		if (StringUtils.equals(reTweet.getOriginalAuthorLogin(), currentUser.getLogin()))
		{
			throw new FunctionalException("You cannot retweet your own tweet");
		}
		else
		{
			// Spread to followers like a normal tweet post
			this.onTweetPost(reTweet);

		}

	}

	@Override
	public void onCancelRetweet(String originalTweetId) throws FunctionalException
	{
		User currentUser = this.authenticationService.getCurrentUser();

		String retweetId = this.retweetRepository.findRetweetIdForRetweeter(currentUser.getLogin(), originalTweetId);
		if (retweetId == null)
		{
			throw new FunctionalException("You cannot cancel a retweet you did not do!");
		}
		else
		{
			Tweet retweet = this.tweetRepository.findTweetById(retweetId);

			// Remove from all followers timeline
			this.onTweetRemove(retweet);
		}

	}

	@Override
	public void onUserFollow(String userLoginToFollow) throws FunctionalException
	{
		log.debug("Adding friend : {}", userLoginToFollow);

		User currentUser = this.authenticationService.getCurrentUser();
		Collection<String> friends = this.friendRepository.findFriendsForUser(currentUser);
		User followedUser = this.userRepository.findUserByLogin(userLoginToFollow);

		if (followedUser != null && !followedUser.equals(currentUser))
		{
			if (!friends.contains(followedUser.getLogin()))
			{
				this.friendRepository.addFriend(currentUser, followedUser);
				this.followerRepository.addFollower(followedUser, currentUser);
			}
			else
			{
				throw new FunctionalException("You are already following '" + userLoginToFollow + "'");
			}
		}
		else if (followedUser.equals(currentUser))
		{
			throw new FunctionalException("You cannot follow yourself!");
		}
	}

	@Override
	public void onUserForget(String userLoginToForget) throws FunctionalException
	{
		log.debug("Forgetting user : {} ", userLoginToForget);

		User currentUser = this.authenticationService.getCurrentUser();
		Collection<String> friends = this.friendRepository.findFriendsForUser(currentUser);

		User followedUser = this.userRepository.findUserByLogin(userLoginToForget);

		if (followedUser != null && !followedUser.equals(currentUser))
		{
			if (friends.contains(followedUser.getLogin()))
			{
				this.friendRepository.removeFriend(currentUser, followedUser);
				this.followerRepository.removeFollower(followedUser, currentUser);
			}
			else
			{
				throw new FunctionalException("You are not following '" + userLoginToForget + "' so you can't forget him/her");
			}
		}
		else if (followedUser.equals(currentUser))
		{
			throw new FunctionalException("You cannot forget yourself!");
		}
	}

	public Collection<String> getFriendsForUser(String login) throws FunctionalException
	{
		log.debug("Retrieving followed users : {}", login);

		User user = this.userRepository.findUserByLogin(login);

		return new ArrayList<String>(this.friendRepository.findFriendsForUser(user));
	}

	public Collection<User> getFriendsForUser(String login, String startUser, int count) throws FunctionalException
	{

		if (startUser == null && count < TatamiConstants.DEFAULT_USER_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_USER_LIST_SIZE;
		}

		log.debug("Retrieving followed users : {} within range {}", login, startUser + " - " + count);

		User currentUser = this.authenticationService.getCurrentUser();
		User user = this.userRepository.findUserByLogin(login);

		return this.buildUserList(currentUser, this.friendRepository.findFriendsForUser(user, startUser, count));
	}

	public Collection<String> getFollowersForUser(String login) throws FunctionalException
	{
		log.debug("Retrieving following users : {}", login);

		User user = this.userRepository.findUserByLogin(login);

		return this.followerRepository.findFollowersForUser(user);
	}

	public Collection<User> getFollowersForUser(String login, String startUser, int count) throws FunctionalException
	{
		if (startUser == null && count < TatamiConstants.DEFAULT_USER_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_USER_LIST_SIZE;
		}

		log.debug("Retrieving following users : {} within range {}", login, startUser + " - " + count);

		User currentUser = this.authenticationService.getCurrentUser();
		User user = this.userRepository.findUserByLogin(login);

		return this.buildUserList(currentUser, this.followerRepository.findFollowersForUser(user, startUser, count));
	}

	public List<User> getUserSuggestions() throws FunctionalException
	{
		User currentUser = this.authenticationService.getCurrentUser();

		String login = currentUser.getLogin();

		log.debug("REST request to get the last active tweeters list (except {} ).", login);

		Collection<String> exceptions = this.getFriendsForUser(login);
		exceptions.add(login);

		List<UserTweetStat> userStats = new ArrayList<UserTweetStat>(this.statslineService.getDayline(new Date()));

		Collections.reverse(userStats);

		Set<String> logins = new HashSet<String>();
		for (UserTweetStat userStat : userStats)
		{
			if (exceptions.contains(userStat.getLogin()))
				continue;

			logins.add(userStat.getLogin());

			if (logins.size() == TatamiConstants.USER_SUGGESTION_LIMIT)
				break; // suggestions list limit
		}

		return this.buildUserList(currentUser, logins);
	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}

	public void setFollowerRepository(FollowerRepository followerRepository)
	{
		this.followerRepository = followerRepository;
	}

	public void setTimeLineRepository(TimeLineRepository timeLineRepository)
	{
		this.timeLineRepository = timeLineRepository;
	}

	public void setStatslineService(StatslineService statslineService)
	{
		this.statslineService = statslineService;
	}

	public void setRetweetRepository(ReTweetRepository retweetRepository)
	{
		this.retweetRepository = retweetRepository;
	}

	public void setTweetRepository(TweetRepository tweetRepository)
	{
		this.tweetRepository = tweetRepository;
	}

}
