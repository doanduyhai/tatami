package fr.ippon.tatami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.UserService;
import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * Fragment controller
 * 
 * @author Duy Hai DOAN
 */
@Controller
public class FragmentController
{
	private final Logger log = LoggerFactory.getLogger(FragmentController.class);

	@Inject
	UserService userService;

	@Inject
	private TimelineService timelineService;

	@ModelAttribute(value = "currentUser")
	public User getCurrentUser()
	{
		User user = userService.getCurrentUser();
		return userService.getUserProfileByLogin(user.getLogin());
	}

	@ModelAttribute(value = "userLinkPattern")
	public String getUserLinkPattern()
	{
		return "<a href='#' data-user='$1' title='Show $1 tweets'><em>@$1</em></a>";
	}

	@ModelAttribute(value = "tagLinkPattern")
	public String getTagLinkPattern()
	{
		return "<a href='#' data-tag='$1' title='Show $1 related tweets'><em>#$1</em></a>";
	}

	@RequestMapping(value = "/fragments/user")
	public String homeFragment()
	{
		return "fragments/user";
	}

	@RequestMapping(value = "/fragments/profile")
	public String profileFragment()
	{
		return "fragments/profile";
	}

	@RequestMapping(value = "/fragments/{nbTweets}/timeline")
	public String timelineFragment(@PathVariable("nbTweets")
	Integer nbTweets, Model model)
	{
		User user = userService.getCurrentUser();
		model.addAttribute("tweets", timelineService.getTimeline(user.getLogin(), nbTweets));
		return "fragments/timeline";
	}

	@RequestMapping(value = "/fragments/followUser")
	public String whoToFollowFragment(Model model)
	{
		User currentUser = userService.getCurrentUser();
		final String login = currentUser.getLogin();
		log.debug("REST request to get the last active tweeters list (except {} ).", login);

		Collection<String> exceptions = userService.getFriendsForUser(login);
		exceptions.add(login);

		Collection<Tweet> tweets = timelineService.getDayline(null);
		Map<String, User> users = new HashMap<String, User>();
		for (Tweet tweet : tweets)
		{
			if (exceptions.contains(tweet.getLogin()))
				continue;

			users.put(tweet.getLogin(), userService.getUserProfileByLogin(tweet.getLogin()));
			if (users.size() == TatamiConstants.USER_SUGGESTION_LIMIT)
				break; // suggestions list limit
		}

		model.addAttribute("tweets", users.values());
		return "fragments/followUser";
	}

	@RequestMapping(value = "/fragments/userline")
	public String userLineFragment(Model model)
	{
		model.addAttribute("userTweets", new ArrayList<Tweet>());
		return "fragments/userline";
	}

	@RequestMapping(value = "/fragments/{login}/userline")
	public String userLineFragment(@PathVariable("login")
	String targetUserLogin, Model model)
	{
		Collection<Tweet> tweets = timelineService.getUserline(targetUserLogin, TatamiConstants.DEFAULT_TWEET_NUMBER);
		log.info("Listing {} tweets for user {}", tweets.size(), targetUserLogin);
		model.addAttribute("userTweets", tweets);
		return "fragments/userline";
	}

	@RequestMapping(value = "/fragments/favline")
	public String favLineFragment(Model model)
	{
		User currentUser = userService.getCurrentUser();
		model.addAttribute("favoriteTweets", timelineService.getFavoritesline(currentUser.getLogin()));
		return "fragments/favline";
	}
}
