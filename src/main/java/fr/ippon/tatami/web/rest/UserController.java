package fr.ippon.tatami.web.rest;

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
import fr.ippon.tatami.domain.json.UserSearch;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.user.UserService;
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

	@RequestMapping(value = "/rest/users/{login}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User getUser(@PathVariable("login") String login) throws FunctionalException
	{
		log.debug("REST request to get Profile : {}", login);
		return userService.getUserByLogin(login);
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

	@RequestMapping(value = "/rest/usersSearch", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void searchUser(@Valid @RequestBody UserSearch userSearch, HttpServletResponse response)
	{
		log.debug("REST request to search for user with input {}", userSearch);
		this.writeWithView(userService.findUser(userSearch.getSearchString()), response, UserView.Minimum.class);
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}
