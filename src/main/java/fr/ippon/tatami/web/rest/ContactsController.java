package fr.ippon.tatami.web.rest;

import static fr.ippon.tatami.web.view.RestAPIConstants.FOLLOWERSLINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.FRIENDSLINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.FRIEND_ADD_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.FRIEND_REMOVE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.USER_SUGGESTIONS_REST;

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
import fr.ippon.tatami.service.pipeline.UserPipelineManager;
import fr.ippon.tatami.service.user.ContactsService;
import fr.ippon.tatami.web.json.view.UserView;

@Controller
public class ContactsController extends AbstractRestController
{

	private ContactsService contactsService;

	private UserPipelineManager userPipelineManager;

	private final Logger log = LoggerFactory.getLogger(ContactsController.class);

	// /rest/users/{login}/followUser
	@RequestMapping(value = FRIEND_ADD_REST, method = RequestMethod.GET, consumes = "application/json")
	@ResponseBody
	public void followUser(@PathVariable("id") String loginToFollow) throws FunctionalException
	{
		log.debug("REST request to follow user login : {} ", loginToFollow);

		this.userPipelineManager.onFollow(loginToFollow);

	}

	// /rest/users/{login}/removeFriend
	@RequestMapping(value = FRIEND_REMOVE_REST, method = RequestMethod.GET, consumes = "application/json")
	@ResponseBody
	public void removeFriend(@PathVariable("id") String loginToRemove) throws FunctionalException
	{
		log.debug("REST request to remove friendLogin : {}", loginToRemove);
		this.userPipelineManager.onForget(loginToRemove);
	}

	// /rest/users/suggestions
	@RequestMapping(value = USER_SUGGESTIONS_REST, method = RequestMethod.GET, produces = "application/json")
	public void getSuggestions(HttpServletResponse response) throws FunctionalException
	{
		this.writeWithView((Object) this.contactsService.getUserSuggestions(), response, UserView.Minimum.class);
	}

	// /rest/userFetch/followers
	@RequestMapping(value = FOLLOWERSLINE_REST, method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void searchFollowers(@Valid @RequestBody UserFetchRange fetchRange, HttpServletResponse response) throws FunctionalException
	{
		log.debug("REST request to search followers of {} within range {}", fetchRange.getFunctionalKey(), fetchRange.getStartUser() + " - "
				+ fetchRange.getCount());
		this.writeWithView(contactsService.getFollowersForUser(fetchRange.getFunctionalKey(), fetchRange.getStartUser(), fetchRange.getCount()),
				response, UserView.Minimum.class);
	}

	// /rest/userFetch/friends
	@RequestMapping(value = FRIENDSLINE_REST, method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public void searchFriends(@Valid @RequestBody UserFetchRange fetchRange, HttpServletResponse response) throws FunctionalException
	{
		log.debug("REST request to search friends of {} within range {}", fetchRange.getFunctionalKey(), fetchRange.getStartUser() + " - "
				+ fetchRange.getCount());
		this.writeWithView(contactsService.getFriendsForUser(fetchRange.getFunctionalKey(), fetchRange.getStartUser(), fetchRange.getCount()),
				response, UserView.Minimum.class);
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
