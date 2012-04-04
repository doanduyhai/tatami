package fr.ippon.tatami.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * Main tatami page.
 * 
 * @author Duy Hai DOAN
 */
@Controller
public class TatamiController
{

	private final Logger log = LoggerFactory.getLogger(TatamiController.class);

	@RequestMapping("/login")
	public String welcome()
	{
		return "pages/login";
	}

	@RequestMapping(value =
	{
			"/",
			"/home"
	})
	public String tatami(Model model)
	{
		// Set the default nb tweets value to the model to be defined as Javascript constant
		model.addAttribute("DEFAULT_TWEET_LIST_SIZE", TatamiConstants.DEFAULT_TWEET_LIST_SIZE);

		// Set the default nb favorites value to the model to be defined as Javascript constant
		model.addAttribute("DEFAULT_FAVORITE_LIST_SIZE", TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE);

		// Set the default nb tags value to the model to be defined as Javascript constant
		model.addAttribute("DEFAULT_TAG_LIST_SIZE", TatamiConstants.DEFAULT_TAG_LIST_SIZE);

		// Set the default tweet number pattern to be defined as Javascript constant
		model.addAttribute("TWEET_NB_PATTERN", TatamiConstants.TWEET_NB_PATTERN);

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

		return "pages/home";
	}

	@RequestMapping("/about")
	public String about()
	{
		return "pages/about";
	}
}
