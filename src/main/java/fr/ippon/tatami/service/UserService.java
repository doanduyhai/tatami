package fr.ippon.tatami.service;

import static fr.ippon.tatami.service.util.TatamiConstants.HASHTAG;

import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.repository.UserRepository;
import fr.ippon.tatami.service.util.GravatarUtil;

/**
 * Manages the application's users.
 * 
 * @author Julien Dubois
 */
@Service
@DependsOn(value = "authenticationService")
public class UserService
{

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	@Inject
	private UserRepository userRepository;

	@Inject
	private FollowerRepository followerRepository;

	@Inject
	private FriendRepository friendRepository;

	@Inject
	private AuthenticationService authenticationService;

	@Inject
	private TimeLineRepository timelineRepository;

	@Inject
	TweetRepository tweetRepository;

	public User getUserByLogin(String login)
	{
		log.debug("Looking for user with login : {} ", login);

		return userRepository.findUserByLogin(login);
	}

	public void updateUser(User updatedUser)

	{
		User currentUser = authenticationService.getCurrentUser();
		if (currentUser.getLogin().equals(updatedUser.getLogin()))
		{
			currentUser.setEmail(updatedUser.getEmail());
			currentUser.setGravatar(GravatarUtil.getHash(updatedUser.getEmail()));
			currentUser.setFirstName(updatedUser.getFirstName());
			currentUser.setLastName(updatedUser.getLastName());
			userRepository.updateUser(currentUser);
		}
		else
		{
			log.info("Security alert : user {} tried to update user {} ", currentUser.getLogin(), updatedUser);
		}
	}

	public void updateRandomUser(User user)
	{
		userRepository.updateUser(user);
	}

	public void createUser(User user)
	{
		user.setGravatar(GravatarUtil.getHash(user.getEmail()));
		userRepository.createUser(user);
	}

	public void followUser(String loginToFollow)
	{
		log.debug("Adding friend : {}", loginToFollow);

		User currentUser = authenticationService.getCurrentUser();
		Collection<String> friends = this.friendRepository.findFriendsForUser(currentUser);
		User followedUser = getUserByLogin(loginToFollow);

		if (followedUser != null && !followedUser.equals(currentUser))
		{
			if (!friends.contains(followedUser.getLogin()))
			{
				friendRepository.addFriend(currentUser, followedUser);
				followerRepository.addFollower(followedUser, currentUser);

				// Tweet alert
				String content = HASHTAG + currentUser.getLogin() + " <strong>is now followng you</strong>";
				Tweet alertTweet = tweetRepository.createTweet(currentUser.getLogin(), content); // removable
				timelineRepository.addTweetToTimeline(followedUser, alertTweet.getTweetId());

			}
		}
		else
		{
			log.debug("Followed user does not exist : {} ", loginToFollow);
		}
	}

	public void forgetUser(String login)
	{
		log.debug("Removing followed user : {} ", login);

		User currentUser = authenticationService.getCurrentUser();
		Collection<String> friends = this.friendRepository.findFriendsForUser(currentUser);

		User followedUser = getUserByLogin(login);

		if (followedUser != null)
		{
			if (friends.contains(followedUser.getLogin()))
			{
				friendRepository.removeFriend(currentUser, followedUser);
				followerRepository.removeFollower(followedUser, currentUser);

			}
		}
		else
		{
			log.debug("Followed user does not exist : {}", login);
		}
	}

	public Collection<String> getFriendsForUser(String login)
	{
		log.debug("Retrieving followed users : {}", login);

		User user = userRepository.findUserByLogin(login);

		return friendRepository.findFriendsForUser(user);
	}

	public Collection<String> getFollowersForUser(String login)
	{
		log.debug("Retrieving followed users : {}", login);

		User user = userRepository.findUserByLogin(login);

		return followerRepository.findFollowersForUser(user);
	}

	public User getCurrentUser()
	{
		return authenticationService.getCurrentUser();
	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}

	public void setFollowerRepository(FollowerRepository followerRepository)
	{
		this.followerRepository = followerRepository;
	}

	public void setFriendRepository(FriendRepository friendRepository)
	{
		this.friendRepository = friendRepository;
	}

	public void setUserRepository(UserRepository userRepository)
	{
		this.userRepository = userRepository;
	}

}