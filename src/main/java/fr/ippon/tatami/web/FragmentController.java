package fr.ippon.tatami.web;

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
		return userService.getUserByLogin(user.getLogin());
	}

	@RequestMapping(value = "/fragments/user")
	public String homeFragment(Model model)
	{
		return "fragments/user";
	}

	@RequestMapping(value = "/fragments/profile")
	public String profileFragment()
	{
		return "fragments/profile";
	}

	@RequestMapping(value = "/fragments/followUser")
	public String whoToFollowFragment(Model model)
	{
		User currentUser = userService.getCurrentUser();
		final String login = currentUser.getLogin();
		log.debug("REST request to get the last active tweeters list (except {} ).", login);

		Collection<String> exceptions = userService.getFriendsForUser(login);
		exceptions.add(login);

		Collection<Tweet> tweets = timelineService.getDayline("");
		Map<String, User> users = new HashMap<String, User>();
		for (Tweet tweet : tweets)
		{
			if (exceptions.contains(tweet.getLogin()))
				continue;

			users.put(tweet.getLogin(), userService.getUserByLogin(tweet.getLogin()));
			if (users.size() == TatamiConstants.USER_SUGGESTION_LIMIT)
				break; // suggestions list limit
		}

		model.addAttribute("tweets", users.values());
		return "fragments/followUser";
	}

	@RequestMapping(value = "/fragments/{nbTweets}/timeline")
	public String timelineFragment(@PathVariable("nbTweets") int nbTweets, Model model)
	{
		nbTweets = nbTweets < TatamiConstants.DEFAULT_TWEET_LIST_SIZE ? TatamiConstants.DEFAULT_TWEET_LIST_SIZE : nbTweets;

		model.addAttribute("dataURL", "fragments/" + TatamiConstants.TWEET_NB_PATTERN + "/timeline");
		Collection<Tweet> tweets = timelineService.getTimeline(nbTweets);
		model.addAttribute("tweets", tweets);
		return "fragments/timeline";
	}

	@RequestMapping(value = "/fragments/{login}/{nbTweets}/userline")
	public String userLineFragment(@PathVariable("login") String targetUserLogin, @PathVariable("nbTweets") int nbTweets, Model model)
	{
		nbTweets = nbTweets < TatamiConstants.DEFAULT_TWEET_LIST_SIZE ? TatamiConstants.DEFAULT_TWEET_LIST_SIZE : nbTweets;

		Collection<Tweet> tweets = timelineService.getUserline(targetUserLogin, nbTweets);
		log.info("Listing {} tweets for user {}", tweets.size(), targetUserLogin);

		model.addAttribute("dataURL", "fragments/" + targetUserLogin + "/" + TatamiConstants.TWEET_NB_PATTERN + "/userline");
		model.addAttribute("userTweets", tweets);
		return "fragments/userline";
	}

	@RequestMapping(value = "/fragments/{nbTweets}/favline")
	public String favLineFragment(@PathVariable("nbTweets") int nbTweets, Model model)
	{
		nbTweets = nbTweets < TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE ? TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE : nbTweets;

		model.addAttribute("dataURL", "fragments/" + TatamiConstants.TWEET_NB_PATTERN + "/favline");
		model.addAttribute("favoriteTweets", timelineService.getFavoriteslineByRange(1, nbTweets));

		return "fragments/favline";
	}

	@RequestMapping(value = "/fragments/{tag}/{nbTweets}/tagline")
	public String tagLineFragment(@PathVariable("tag") String tag, @PathVariable("nbTweets") int nbTweets, Model model)
	{
		nbTweets = nbTweets < TatamiConstants.DEFAULT_TAG_LIST_SIZE ? TatamiConstants.DEFAULT_TAG_LIST_SIZE : nbTweets;

		log.debug("REST request to get a  tweet list ( {} sized) with tag {}", nbTweets, tag);

		model.addAttribute("dataURL", "fragments/" + tag + "/" + TatamiConstants.TWEET_NB_PATTERN + "/tagline");
		model.addAttribute("tagTweets", timelineService.getTagline(tag, nbTweets));

		return "fragments/tagline";
	}
}
