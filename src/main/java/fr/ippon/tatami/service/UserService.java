package fr.ippon.tatami.service;

import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.CounterRepository;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.FriendRepository;
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
	private CounterRepository counterRepository;

	@Inject
	private AuthenticationService authenticationService;

	public User getUserByLogin(String login)
	{
		log.debug("Looking for user with login : {} ", login);

		return userRepository.findUserByLogin(login);
	}

	public User getUserProfileByLogin(String login)
	{
		User user = getUserByLogin(login);
		if (user != null)
		{
			user.setTweetCount(counterRepository.getTweetCounter(login));
			user.setFollowersCount(counterRepository.getFollowersCounter(login));
			user.setFriendsCount(counterRepository.getFriendsCounter(login));
		}
		return user;
	}

	public void updateUser(User user)
	{
		User currentUser = authenticationService.getCurrentUser();
		if (currentUser.getLogin().equals(user.getLogin()))
		{
			user.setGravatar(GravatarUtil.getHash(user.getEmail()));
			userRepository.updateUser(user);
		}
		else
		{
			log.info("Security alert : user {} tried to update user {} ", currentUser.getLogin(), user);
		}
	}

	public void createUser(User user)
	{
		user.setGravatar(GravatarUtil.getHash(user.getEmail()));
		counterRepository.createTweetCounter(user.getLogin());
		counterRepository.createFriendsCounter(user.getLogin());
		counterRepository.createFollowersCounter(user.getLogin());
		userRepository.createUser(user);
	}

	public void followUser(String loginToFollow)
	{
		log.debug("Adding friend : {}", loginToFollow);

		User currentUser = authenticationService.getCurrentUser();
		User followedUser = getUserByLogin(loginToFollow);
		if (followedUser != null && !followedUser.equals(currentUser))
		{
			boolean userAlreadyFollowed = false;
			if (counterRepository.getFriendsCounter(currentUser.getLogin()) > 0)
			{
				for (String alreadyFollowingTest : friendRepository.findFriendsForUser(currentUser.getLogin()))
				{
					if (alreadyFollowingTest.equals(loginToFollow))
					{
						userAlreadyFollowed = true;
					}
				}
			}
			if (!userAlreadyFollowed)
			{
				friendRepository.addFriend(currentUser.getLogin(), followedUser.getLogin());
				counterRepository.incrementFriendsCounter(currentUser.getLogin());
				followerRepository.addFollower(followedUser.getLogin(), currentUser.getLogin());
				counterRepository.incrementFollowersCounter(followedUser.getLogin());
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
		User followedUser = getUserByLogin(login);
		if (followedUser != null)
		{
			boolean userAlreadyFollowed = false;
			for (String alreadyFollowingTest : friendRepository.findFriendsForUser(currentUser.getLogin()))
			{
				if (alreadyFollowingTest.equals(login))
				{
					userAlreadyFollowed = true;
				}
			}
			if (userAlreadyFollowed)
			{
				friendRepository.removeFriend(currentUser.getLogin(), followedUser.getLogin());
				counterRepository.decrementFriendsCounter(currentUser.getLogin());
				followerRepository.removeFollower(followedUser.getLogin(), currentUser.getLogin());
				counterRepository.decrementFollowersCounter(followedUser.getLogin());
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

		return friendRepository.findFriendsForUser(login);
	}

	public User getCurrentUser()
	{
		SecurityContext securityContext = SecurityContextHolder.getContext();
		org.springframework.security.core.userdetails.User springSecurityUser = (org.springframework.security.core.userdetails.User) securityContext
				.getAuthentication().getPrincipal();

		return getUserByLogin(springSecurityUser.getUsername());

	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}
}