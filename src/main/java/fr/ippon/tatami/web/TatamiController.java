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
		model.addAttribute("defaultNbTweets", TatamiConstants.DEFAULT_TWEET_LIST_SIZE);

		// Set the default nb tags value to the model to be defined as Javascript constant
		model.addAttribute("defaultNbTags", TatamiConstants.DEFAULT_TAG_LIST_SIZE);

		return "pages/home";
	}

	@RequestMapping("/about")
	public String about()
	{
		return "pages/about";
	}
}
