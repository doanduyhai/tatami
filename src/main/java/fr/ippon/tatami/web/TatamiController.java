package fr.ippon.tatami.web;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.ippon.tatami.service.UserService;

/**
 * Main tatami page.
 * 
 * @author Julien Dubois
 */
@Controller
public class TatamiController
{

	private final Logger log = LoggerFactory.getLogger(TatamiController.class);

	@Inject
	UserService userService;

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
	public String tatami()
	{
		return "pages/home";
	}

	@RequestMapping("/about")
	public String about()
	{
		return "pages/about";
	}

	@RequestMapping(value = "/fragments/user")
	public String homeFragment(Model model)
	{
		model.addAttribute("currentUser", userService.getCurrentUser());
		return "fragments/user";
	}
}
