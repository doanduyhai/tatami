package fr.ippon.tatami.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.FollowerTweetIndexRepository;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.service.lines.StatslineService;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;
import fr.ippon.tatami.service.pipeline.user.UserHandler;
import fr.ippon.tatami.service.util.TatamiConstants;

public class ContactsService extends AbstractUserService implements TweetHandler, UserHandler
{

	private final Logger log = LoggerFactory.getLogger(ContactsService.class);

	private UserService userService;

	private StatslineService statslineService;

	private FollowerRepository followerRepository;

	private TimeLineRepository timeLineRepository;

	private FollowerTweetIndexRepository followerTweetIndexRepository;

	@Override
	public void onTweetPost(Tweet tweet) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		// Spread tweet to followers and followerTweetIndex
		for (String followerLogin : followerRepository.findFollowersForUser(currentUser))
		{
			timeLineRepository.addTweetToTimeline(followerLogin, tweet.getTweetId());
			followerTweetIndexRepository.addTweetToIndex(currentUser.getLogin(), followerLogin, tweet.getTweetId());
		}

	}

	@Override
	public void onTweetRemove(Tweet tweet) throws FunctionalException
	{
		if (!tweet.isNotification())
		{
			User currentUser = this.userService.getCurrentUser();

			// Remove tweet from followers and followerTweetIndex
			for (String followerLogin : this.getFollowersForUser(currentUser.getLogin()))
			{
				this.timeLineRepository.removeTweetFromTimeline(followerLogin, tweet.getTweetId());
				followerTweetIndexRepository.removeTweetFromIndex(currentUser.getLogin(), followerLogin, tweet.getTweetId());
			}
		}
	}

	@Override
	public void onUserFollow(String userLoginToFollow) throws FunctionalException
	{
		log.debug("Adding friend : {}", userLoginToFollow);

		User currentUser = userService.getCurrentUser();
		Collection<String> friends = this.friendRepository.findFriendsForUser(currentUser);
		User followedUser = userService.getUserByLogin(userLoginToFollow);

		if (followedUser != null && !followedUser.equals(currentUser))
		{
			if (!friends.contains(followedUser.getLogin()))
			{
				friendRepository.addFriend(currentUser, followedUser);
				followerRepository.addFollower(followedUser, currentUser);
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

		User currentUser = userService.getCurrentUser();
		Collection<String> friends = this.friendRepository.findFriendsForUser(currentUser);

		User followedUser = userService.getUserByLogin(userLoginToForget);

		if (followedUser != null && !followedUser.equals(currentUser))
		{
			if (friends.contains(followedUser.getLogin()))
			{
				friendRepository.removeFriend(currentUser, followedUser);
				followerRepository.removeFollower(followedUser, currentUser);
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

		User user = this.userService.getUserByLogin(login);

		return new ArrayList<String>(friendRepository.findFriendsForUser(user));
	}

	public Collection<User> getFriendsForUser(String login, String startUser, int count) throws FunctionalException
	{

		if (startUser == null && count < TatamiConstants.DEFAULT_USER_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_USER_LIST_SIZE;
		}

		log.debug("Retrieving followed users : {} within range {}", login, startUser + " - " + count);

		User currentUser = this.userService.getCurrentUser();
		User user = this.userService.getUserByLogin(login);

		return this.buildUserList(currentUser, friendRepository.findFriendsForUser(user, startUser, count));
	}

	public Collection<String> getFollowersForUser(String login) throws FunctionalException
	{
		log.debug("Retrieving following users : {}", login);

		User user = this.userService.getUserByLogin(login);

		return followerRepository.findFollowersForUser(user);
	}

	public Collection<User> getFollowersForUser(String login, String startUser, int count) throws FunctionalException
	{
		if (startUser == null && count < TatamiConstants.DEFAULT_USER_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_USER_LIST_SIZE;
		}

		log.debug("Retrieving following users : {} within range {}", login, startUser + " - " + count);

		User currentUser = this.userService.getCurrentUser();
		User user = this.userService.getUserByLogin(login);

		return this.buildUserList(currentUser, followerRepository.findFollowersForUser(user, startUser, count));
	}

	public List<User> getUserSuggestions() throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		String login = currentUser.getLogin();

		log.debug("REST request to get the last active tweeters list (except {} ).", login);

		Collection<String> exceptions = this.getFriendsForUser(login);
		exceptions.add(login);

		List<UserTweetStat> userStats = new ArrayList<UserTweetStat>(statslineService.getDayline(new Date()));

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

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	public void setFollowerRepository(FollowerRepository followerRepository)
	{
		this.followerRepository = followerRepository;
	}

	public void setTimeLineRepository(TimeLineRepository timeLineRepository)
	{
		this.timeLineRepository = timeLineRepository;
	}

	public void setFollowerTweetIndexRepository(FollowerTweetIndexRepository followerTweetIndexRepository)
	{
		this.followerTweetIndexRepository = followerTweetIndexRepository;
	}

	public void setStatslineService(StatslineService statslineService)
	{
		this.statslineService = statslineService;
	}

}
