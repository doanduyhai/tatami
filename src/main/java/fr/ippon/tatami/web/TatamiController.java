package fr.ippon.tatami.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.ippon.tatami.service.UserService;
import fr.ippon.tatami.service.util.TatamiConstants;
import fr.ippon.tatami.web.view.ViewConstants;

/**
 * Main tatami page.
 * 
 * @author Duy Hai DOAN
 */
@Controller
public class TatamiController
{

	private final Logger log = LoggerFactory.getLogger(TatamiController.class);

	// @Inject
	UserService userService;

	@RequestMapping(ViewConstants.URL_LOGIN)
	public String welcome(HttpServletRequest servletRequest)
	{
		Device currentDevice = DeviceUtils.getCurrentDevice(servletRequest);

		log.info("is mobile : " + currentDevice.isMobile());

		if (currentDevice.isMobile())
		{
			return ViewConstants.MOBILE_LOGIN;
		}
		else
		{
			return ViewConstants.PAGE_LOGIN;
		}

	}

	@RequestMapping(value =
	{
			ViewConstants.URL_ROOT,
			ViewConstants.URL_HOME
	})
	public String tatami(Model model, HttpServletRequest servletRequest)
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

		Device currentDevice = DeviceUtils.getCurrentDevice(servletRequest);

		if (currentDevice.isMobile())
		{
			model.addAttribute("currentUser", userService.getCurrentUser());
			return ViewConstants.MOBILE_HOME;
		}
		else
		{
			return ViewConstants.PAGE_HOME;
		}

	}

	@RequestMapping(ViewConstants.URL_ABOUT)
	public String about(HttpServletRequest servletRequest)
	{
		Device currentDevice = DeviceUtils.getCurrentDevice(servletRequest);

		if (currentDevice.isMobile())
		{
			return ViewConstants.MOBILE_ABOUT;
		}
		else
		{
			return ViewConstants.PAGE_ABOUT;
		}
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

}
