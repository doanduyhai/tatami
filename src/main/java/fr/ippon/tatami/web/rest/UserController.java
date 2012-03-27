package fr.ippon.tatami.web.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.UserService;
import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * REST controller for managing users.
 * 
 * @author Julien Dubois
 */
@Controller
public class UserController
{

	private final Logger log = LoggerFactory.getLogger(UserController.class);

	@Inject
	private TimelineService timelineService;

	@Inject
	private UserService userService;

	@RequestMapping(value = "/rest/users/{login}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User getUser(@PathVariable("login")
	String login)
	{
		log.debug("REST request to get Profile : {}", login);
		return userService.getUserProfileByLogin(login);
	}

	@RequestMapping(value = "/rest/users/{login}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void updateUser(@PathVariable("login")
	String login, @RequestBody
	User user)
	{
		log.debug("REST request to update user : {}", login);
		user.setLogin(login);
		userService.updateUser(user);
	}

	@RequestMapping(value = "/rest/users/{login}/followUser", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void followUser(@PathVariable("login")
	String login, @RequestBody
	String loginToFollow)
	{
		log.debug("REST request to follow user login : {} ", loginToFollow);
		User currentUser = userService.getCurrentUser();
		if (currentUser.getLogin().equals(login))
		{
			userService.followUser(loginToFollow);
			log.info("Completed");
		}
		else
		{
			log.info("Cannot follow a user for another user");
		}
	}

	@RequestMapping(value = "/rest/users/{login}/removeFriend", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void removeFriend(@PathVariable("login")
	String login, @RequestBody
	String friend)
	{
		log.debug("REST request to remove friendLogin : {}", friend);
		User currentUser = userService.getCurrentUser();
		if (currentUser.getLogin().equals(login))
		{
			userService.forgetUser(friend);
		}
		else
		{
			log.info("Cannot remove a friend from another user");
		}
	}

	@RequestMapping(value = "/rest/suggestions", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<User> suggestions()
	{
		User currentUser = userService.getCurrentUser();
		final String login = currentUser.getLogin();
		log.debug("REST request to get the last active tweeters list (except {} ).", login);

		Collection<String> exceptions = userService.getFriendsForUser(login);
		exceptions.add(login);

		Collection<Tweet> tweets = timelineService.getDayline(null);
		Map<String, User> users = new HashMap<String, User>();
		for (Tweet tweet : tweets)
		{
			if (exceptions.contains(tweet.getLogin()))
				continue;

			users.put(tweet.getLogin(), userService.getUserProfileByLogin(tweet.getLogin()));
			if (users.size() == TatamiConstants.USER_SUGGESTION_LIMIT)
				break; // suggestions list limit
		}
		return users.values();
	}

	@RequestMapping(value = "/rest/likeTweet/{tweet}", method = RequestMethod.GET)
	@ResponseBody
	public boolean likeTweet(@PathVariable("tweet")
	String tweet)
	{
		log.debug("REST request to like tweet : {} ");
		timelineService.addFavoriteTweet(tweet);
		log.info("Completed");

		return true;
	}
}
