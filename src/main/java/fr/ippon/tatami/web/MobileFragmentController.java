package fr.ippon.tatami.web;

import static fr.ippon.tatami.web.view.RestAPIConstants.FAVORITELINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.FOLLOWERSLINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.FRIENDSLINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.TAGLINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.TIMELINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.USERLINE_REST;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.ippon.tatami.service.user.UserService;
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
		model.addAttribute("FRIENDSLINE_REST", FRIENDSLINE_REST);
		model.addAttribute("FOLLOWERSLINE_REST", FOLLOWERSLINE_REST);
		model.addAttribute("TIMELINE_REST", TIMELINE_REST);
		model.addAttribute("FAVORITELINE_REST", FAVORITELINE_REST);
		model.addAttribute("USERLINE_REST", USERLINE_REST);
		model.addAttribute("TAGLINE_REST", TAGLINE_REST);
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
