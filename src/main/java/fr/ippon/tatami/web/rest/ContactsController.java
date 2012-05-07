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

import fr.ippon.tatami.domain.json.UserFetchRange;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.lines.StatslineService;
import fr.ippon.tatami.service.pipeline.UserPipelineManager;
import fr.ippon.tatami.service.user.ContactsService;
import fr.ippon.tatami.web.json.view.UserView;

@Controller
public class ContactsController extends AbstractRESTController
{

	private ContactsService contactsService;

	private StatslineService statslineService;

	private UserPipelineManager userPipelineManager;

	private final Logger log = LoggerFactory.getLogger(ContactsController.class);

	@RequestMapping(value = "/rest/users/{login}/followUser", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void followUser(@PathVariable("login") String login, @RequestBody String loginToFollow) throws FunctionalException
	{
		log.debug("REST request to follow user login : {} ", loginToFollow);

		this.userPipelineManager.onFollow(loginToFollow);

	}

	@RequestMapping(value = "/rest/users/{login}/removeFriend", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void removeFriend(@PathVariable("login") String login, @RequestBody String friend) throws FunctionalException
	{
		log.debug("REST request to remove friendLogin : {}", friend);
		this.userPipelineManager.onForget(friend);
	}

	@RequestMapping(value = "/rest/users/suggestions", method = RequestMethod.GET, produces = "application/json")
	public void getSuggestions(HttpServletResponse response) throws FunctionalException
	{
		this.writeWithView((Object) this.contactsService.getUserSuggestions(), response, UserView.Minimum.class);
	}

	@RequestMapping(value = "/rest/userFetch/followers", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void searchFollowers(@Valid @RequestBody UserFetchRange fetchRange, HttpServletResponse response) throws FunctionalException
	{
		log.debug("REST request to search followers of {} within range {}", fetchRange.getFunctionalKey(), fetchRange.getStartUser() + " - "
				+ fetchRange.getCount());
		this.writeWithView(contactsService.getFollowersForUser(fetchRange.getFunctionalKey(), fetchRange.getStartUser(), fetchRange.getCount()),
				response, UserView.Minimum.class);
	}

	@RequestMapping(value = "/rest/userFetch/friends", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void searchFriends(@Valid @RequestBody UserFetchRange fetchRange, HttpServletResponse response) throws FunctionalException
	{
		log.debug("REST request to search friends of {} within range {}", fetchRange.getFunctionalKey(), fetchRange.getStartUser() + " - "
				+ fetchRange.getCount());
		this.writeWithView(contactsService.getFriendsForUser(fetchRange.getFunctionalKey(), fetchRange.getStartUser(), fetchRange.getCount()),
				response, UserView.Minimum.class);
	}

	public void setStatslineService(StatslineService statslineService)
	{
		this.statslineService = statslineService;
	}

	public void setContactsService(ContactsService contactsService)
	{
		this.contactsService = contactsService;
	}

	public void setUserPipelineManager(UserPipelineManager userPipelineManager)
	{
		this.userPipelineManager = userPipelineManager;
	}

}
