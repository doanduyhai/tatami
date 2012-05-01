package fr.ippon.tatami.web.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.domain.json.UserFetchRange;
import fr.ippon.tatami.domain.json.UserSearch;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.lines.StatslineService;
import fr.ippon.tatami.service.user.UserService;
import fr.ippon.tatami.service.util.TatamiConstants;
import fr.ippon.tatami.web.json.view.UserView;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
@Controller
public class UserController extends AbstractRESTController
{

	private final Logger log = LoggerFactory.getLogger(UserController.class);

	private UserService userService;

	private StatslineService statslineService;

	@RequestMapping(value = "/rest/users/{login}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User getUser(@PathVariable("login") String login) throws FunctionalException
	{
		log.debug("REST request to get Profile : {}", login);
		return userService.getUserByLogin(login);
	}

	@RequestMapping(value = "/rest/users/suggestions", method = RequestMethod.GET, produces = "application/json")
	public void getSuggestions(HttpServletResponse response)
	{
		User currentUser = userService.getCurrentUser();
		final String login = currentUser.getLogin();
		log.debug("REST request to get the last active tweeters list (except {} ).", login);

		Collection<String> exceptions = userService.getFriendsForUser(login);
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

		this.writeWithView((Object) this.userService.buildUserList(currentUser, logins), response, UserView.Minimum.class);
	}

	@RequestMapping(value = "/rest/usersStats/{login}", method = RequestMethod.GET, produces = "application/json")
	public void getUserStats(@PathVariable("login") String login, HttpServletResponse response) throws FunctionalException
	{
		log.debug("REST request to get Profile : {}", login);
		this.writeWithView(userService.getUserByLogin(login), response, UserView.Stats.class);
	}

	@RequestMapping(value = "/rest/usersDetails/{login}", method = RequestMethod.GET, produces = "application/json")
	public void getUserDetails(@PathVariable("login") String login, HttpServletResponse response) throws FunctionalException
	{
		log.debug("REST request to get Details : {}", login);
		this.writeWithView(userService.getUserByLogin(login), response, UserView.Details.class);
	}

	@RequestMapping(value = "/rest/usersProfile/{login}", method = RequestMethod.GET, produces = "application/json")
	public void getUserProfile(@PathVariable("login") String login, HttpServletResponse response) throws FunctionalException
	{
		log.debug("REST request to get Profile : {}", login);
		this.writeWithView(userService.getUserByLogin(login), response, UserView.Full.class);
	}

	@RequestMapping(value = "/rest/users/{login}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void updateUser(@PathVariable("login") String login, @Valid @RequestBody User user)
	{
		log.debug("REST request to update user : {}", login);
		user.setLogin(login);
		userService.updateUser(user);
	}

	@RequestMapping(value = "/rest/users/{login}/followUser", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void followUser(@PathVariable("login") String login, @RequestBody String loginToFollow) throws FunctionalException
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
	public void removeFriend(@PathVariable("login") String login, @RequestBody String friend) throws FunctionalException
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

	@RequestMapping(value = "/rest/usersSearch", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void searchUser(@Valid @RequestBody UserSearch userSearch, HttpServletResponse response)
	{
		log.debug("REST request to search for user with input {}", userSearch);
		this.writeWithView(userService.findUser(userSearch.getSearchString()), response, UserView.Minimum.class);
	}

	@RequestMapping(value = "/rest/userFetch/friends", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void searchFriends(@Valid @RequestBody UserFetchRange fetchRange, HttpServletResponse response)
	{
		log.debug("REST request to search friends of {} within range {}", fetchRange.getFunctionalKey(), fetchRange.getStartUser() + " - "
				+ fetchRange.getCount());
		this.writeWithView(userService.getFriendsForUser(fetchRange.getFunctionalKey(), fetchRange.getStartUser(), fetchRange.getCount()), response,
				UserView.Minimum.class);
	}

	@RequestMapping(value = "/rest/userFetch/followers", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void searchFollowers(@Valid @RequestBody UserFetchRange fetchRange, HttpServletResponse response)
	{
		log.debug("REST request to search followers of {} within range {}", fetchRange.getFunctionalKey(), fetchRange.getStartUser() + " - "
				+ fetchRange.getCount());
		this.writeWithView(userService.getFollowersForUser(fetchRange.getFunctionalKey(), fetchRange.getStartUser(), fetchRange.getCount()),
				response, UserView.Minimum.class);
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	public void setStatslineService(StatslineService statslineService)
	{
		this.statslineService = statslineService;
	}

}
