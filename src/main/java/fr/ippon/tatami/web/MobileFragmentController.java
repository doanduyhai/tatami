package fr.ippon.tatami.web;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.ippon.tatami.service.UserService;
import fr.ippon.tatami.service.util.TatamiConstants;
import fr.ippon.tatami.web.view.ViewConstants;

@Controller
public class MobileFragmentController
{
	@Inject
	UserService userService;

	@ModelAttribute
	public void defaultFetchSize(Model model)
	{

		// Set the default start tweet nb pattern to be defined as Javascript constant
		model.addAttribute("START_TWEET_INDEX_PATTERN", TatamiConstants.START_TWEET_INDEX_PATTERN);

		// Set the default end tweet nb pattern to be defined as Javascript constant
		model.addAttribute("END_TWEET_INDEX_PATTERN", TatamiConstants.END_TWEET_INDEX_PATTERN);

		// Set the default user login pattern to be defined as Javascript constant
		model.addAttribute("USER_LOGIN_PATTERN", TatamiConstants.USER_LOGIN_PATTERN);

		// Set the default tag pattern to be defined as Javascript constant
		model.addAttribute("TAG_PATTERN", TatamiConstants.TAG_PATTERN);

		// Set the default tweet first fetch to be defined as Javascript constant
		model.addAttribute("TWEET_FIRST_FETCH_SIZE", TatamiConstants.TWEET_FIRST_FETCH_SIZE);

		// Set the default tweet second fetch to be defined as Javascript constant
		model.addAttribute("TWEET_SECOND_FETCH_SIZE", TatamiConstants.TWEET_SECOND_FETCH_SIZE);

		// Set the default tweet third fetch to be defined as Javascript constant
		model.addAttribute("TWEET_THIRD_FETCH_SIZE", TatamiConstants.TWEET_THIRD_FETCH_SIZE);
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
}
