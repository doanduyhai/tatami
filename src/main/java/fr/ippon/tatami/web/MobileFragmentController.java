package fr.ippon.tatami.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.ippon.tatami.service.user.UserService;
import fr.ippon.tatami.service.util.TatamiConstants;
import fr.ippon.tatami.web.view.ViewConstants;

/**
 * @author DuyHai DOAN
 */
@Controller
public class MobileFragmentController
{

	UserService userService;

	@ModelAttribute
	public void defaultFetchSize(Model model)
	{

		// Set the default tweet first fetch to be defined as Javascript constant
		model.addAttribute("FIRST_FETCH_SIZE", TatamiConstants.FIRST_FETCH_SIZE);

		// Set the default tweet second fetch to be defined as Javascript constant
		model.addAttribute("SECOND_FETCH_SIZE", TatamiConstants.SECOND_FETCH_SIZE);

		// Set the default tweet third fetch to be defined as Javascript constant
		model.addAttribute("THIRD_FETCH_SIZE", TatamiConstants.THIRD_FETCH_SIZE);
	}

	@RequestMapping(ViewConstants.URL_MOBILE_FRAGMENT_PROFILE)
	public String profileFragment(Model model)
	{
		model.addAttribute("currentUser", userService.getCurrentUser());
		return ViewConstants.FRAGMENT_PROFILE;
	}

	@RequestMapping(ViewConstants.URL_MOBILE_FRAGMENT_SUGGESTIONS)
	public String whoToFollowFragment()
	{
		return ViewConstants.MOBILE_FRAGMENT_SUGGESTIONS;
	}

	@RequestMapping(ViewConstants.URL_MOBILE_FRAGMENT_TIMELINE)
	public String timelineFragment()
	{
		return ViewConstants.MOBILE_FRAGMENT_TIMELINE;
	}

	@RequestMapping(ViewConstants.URL_MOBILE_FRAGMENT_FAVLINE)
	public String favlineFragment()
	{
		return ViewConstants.MOBILE_FRAGMENT_FAVLINE;
	}

	@RequestMapping(ViewConstants.URL_MOBILE_FRAGMENT_USERLINE)
	public String userlineFragment()
	{
		return ViewConstants.MOBILE_FRAGMENT_USERLINE;
	}

	@RequestMapping(ViewConstants.URL_MOBILE_FRAGMENT_TAGLINE)
	public String taglineFragment()
	{
		return ViewConstants.MOBILE_FRAGMENT_TAGLINE;
	}

	@RequestMapping(ViewConstants.URL_MOBILE_FRAGMENT_CONTACTSLINE)
	public String contactslineFragment()
	{
		return ViewConstants.MOBILE_FRAGMENT_CONTACTSLINE;
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

}
