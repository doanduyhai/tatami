package fr.ippon.tatami.web.rest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.UserService;

/**
 * REST controller for managing users.
 * 
 * @author Julien Dubois
 */
@Controller
public class UserController extends AbstractRESTController
{

	private final Logger log = LoggerFactory.getLogger(UserController.class);

	@Inject
	private TimelineService timelineService;

	@Inject
	private UserService userService;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public String handleFunctionalException(MethodArgumentNotValidException ex, HttpServletRequest request)
	{
		log.error(" Validation exception raised : " + ex.getMessage());
		StringBuilder errorBuffer = new StringBuilder();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors())
		{
			errorBuffer.append(fieldError.getDefaultMessage()).append("<br/>");
		}
		return errorBuffer.toString();
	}

	@RequestMapping(value = "/rest/users/{login}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User getUser(@PathVariable("login") String login)
	{
		log.debug("REST request to get Profile : {}", login);
		return userService.getUserByLogin(login);
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
	public void followUser(@PathVariable("login") String login, @RequestBody String loginToFollow)
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
	public void removeFriend(@PathVariable("login") String login, @RequestBody String friend)
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

	@RequestMapping(value = "/rest/likeTweet/{tweet}", method = RequestMethod.GET)
	@ResponseBody
	public boolean likeTweet(@PathVariable("tweet") String tweet)
	{
		log.debug("REST request to like tweet : {} ");
		timelineService.addFavoriteTweet(tweet);
		log.info("Completed");

		return true;
	}
}
