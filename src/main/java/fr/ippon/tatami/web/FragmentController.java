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

	@RequestMapping(value = "/fragments/{nbTweets}/timeline")
	public String timelineFragment(@PathVariable("nbTweets") Integer nbTweets, Model model)
	{
		String minTweetNb = TatamiConstants.TWEET_NB_PATTERN;
		if (nbTweets < TatamiConstants.DEFAULT_TWEET_LIST_SIZE)
		{
			minTweetNb = TatamiConstants.DEFAULT_TWEET_LIST_SIZE + "";
			nbTweets = TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
		}

		model.addAttribute("dataURL", "fragments/" + minTweetNb + "/timeline");
		model.addAttribute("tweets", timelineService.getTimeline(nbTweets));
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

	@RequestMapping(value = "/fragments/{login}/userline")
	public String userLineFragment(@PathVariable("login") String targetUserLogin, Model model)
	{
		Collection<Tweet> tweets = timelineService.getUserline(targetUserLogin, TatamiConstants.DEFAULT_TWEET_LIST_SIZE);
		log.info("Listing {} tweets for user {}", tweets.size(), targetUserLogin);

		model.addAttribute("dataURL", "fragments/" + targetUserLogin + "/userline");
		model.addAttribute("userTweets", tweets);
		return "fragments/userline";
	}

	@RequestMapping(value = "/fragments/favline")
	public String favLineFragment(Model model)
	{
		model.addAttribute("dataURL", "fragments/favline");
		model.addAttribute("favoriteTweets", timelineService.getFavoritesline());

		return "fragments/favline";
	}

	@RequestMapping(value = "/fragments/{tag}/{nbTweets}/tagline")
	public String tagLineFragment(@PathVariable("tag") String tag, @PathVariable("nbTweets") int nbTweets, Model model)
	{
		String minTweetNb = TatamiConstants.TWEET_NB_PATTERN;
		if (nbTweets < TatamiConstants.DEFAULT_TAG_LIST_SIZE)
		{
			minTweetNb = TatamiConstants.DEFAULT_TAG_LIST_SIZE + "";
			nbTweets = TatamiConstants.DEFAULT_TAG_LIST_SIZE;
		}

		log.debug("REST request to get a  tweet list ( {} sized) with tag {}", nbTweets, tag);

		model.addAttribute("dataURL", "fragments/" + tag + "/" + minTweetNb + "/tagline");
		model.addAttribute("tagTweets", timelineService.getTagline(tag, nbTweets));

		return "fragments/tagline";
	}
}
