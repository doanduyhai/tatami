package fr.ippon.tatami.web;

import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE;
import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_TAG_LIST_SIZE;
import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
import static fr.ippon.tatami.service.util.TatamiConstants.FIRST_FETCH_SIZE;
import static fr.ippon.tatami.service.util.TatamiConstants.LINK_REGEXP;
import static fr.ippon.tatami.service.util.TatamiConstants.LINK_SHORT_LENGTH;
import static fr.ippon.tatami.service.util.TatamiConstants.MAX_CHARACTERS_PER_TWEET;
import static fr.ippon.tatami.service.util.TatamiConstants.SECOND_FETCH_SIZE;
import static fr.ippon.tatami.service.util.TatamiConstants.THIRD_FETCH_SIZE;
import static fr.ippon.tatami.web.view.RestAPIConstants.FAVORITELINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.FOLLOWERSLINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.FRIENDSLINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.TAGLINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.TIMELINE_REST;
import static fr.ippon.tatami.web.view.RestAPIConstants.USERLINE_REST;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.ippon.tatami.service.user.UserService;
import fr.ippon.tatami.service.util.JavascriptConstantsBuilder;
import fr.ippon.tatami.web.view.RestAPIConstants;
import fr.ippon.tatami.web.view.ViewConstants;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
@Controller
public class TatamiController
{

	private final Logger log = LoggerFactory.getLogger(TatamiController.class);

	UserService userService;

	@RequestMapping(ViewConstants.URL_LOGIN)
	public String loginPage(HttpServletRequest servletRequest)
	{
		Device currentDevice = DeviceUtils.getCurrentDevice(servletRequest);

		log.info("Current device is mobile : " + currentDevice.isMobile());

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
	// @Value("#{'${ajax.session.timeout.http.code}'}") String ajaxSessionTimeoutCode
	public String tatami(Model model, HttpServletRequest servletRequest)
	{
		Device currentDevice = DeviceUtils.getCurrentDevice(servletRequest);

		model.addAttribute("FRIENDSLINE_REST", FRIENDSLINE_REST);
		model.addAttribute("FOLLOWERSLINE_REST", FOLLOWERSLINE_REST);
		model.addAttribute("TIMELINE_REST", TIMELINE_REST);
		model.addAttribute("FAVORITELINE_REST", FAVORITELINE_REST);
		model.addAttribute("USERLINE_REST", USERLINE_REST);
		model.addAttribute("TAGLINE_REST", TAGLINE_REST);

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

	@RequestMapping(value = "/js/constants.js", method = RequestMethod.GET)
	public HttpEntity<String> scriptConstants() throws IllegalArgumentException, IllegalAccessException
	{
		Map<String, Object> constantsMap = new HashMap<String, Object>();
		constantsMap.put("DEFAULT_TWEET_LIST_SIZE", DEFAULT_TWEET_LIST_SIZE);
		constantsMap.put("DEFAULT_FAVORITE_LIST_SIZE", DEFAULT_FAVORITE_LIST_SIZE);
		constantsMap.put("DEFAULT_TAG_LIST_SIZE", DEFAULT_TAG_LIST_SIZE);
		constantsMap.put("FIRST_FETCH_SIZE", FIRST_FETCH_SIZE);
		constantsMap.put("SECOND_FETCH_SIZE", SECOND_FETCH_SIZE);
		constantsMap.put("THIRD_FETCH_SIZE", THIRD_FETCH_SIZE);
		constantsMap.put("LINK_SHORT_LENGTH", LINK_SHORT_LENGTH);
		constantsMap.put("LINK_REGEXP", LINK_REGEXP);
		constantsMap.put("MAX_CHARACTERS_PER_TWEET", MAX_CHARACTERS_PER_TWEET);
		constantsMap.put("HTTP_GET", "GET");
		constantsMap.put("HTTP_POST", "POST");
		constantsMap.put("JSON_DATA", "json");
		constantsMap.put("JSON_CONTENT", "application/json; charset=UTF-8");

		String script = new JavascriptConstantsBuilder().add(RestAPIConstants.class).add(constantsMap).build();

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/javascript");
		HttpEntity<String> httpEntity = new HttpEntity<String>(script, responseHeaders);

		return httpEntity;

	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

}
