package fr.ippon.tatami.web.rest;

import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * REST controller for managing tweets.
 * 
 * @author Julien Dubois
 */
@Controller
public class TweetController
{

	private final Logger log = LoggerFactory.getLogger(TweetController.class);

	@Inject
	private TimelineService timelineService;

	// @RequestMapping(value = "/rest/tweets/{login}/{nbTweets}", method = RequestMethod.GET, produces = "application/json")
	// @ResponseBody
	// public Collection<Tweet> listTweets(@PathVariable("login")
	// String login, @PathVariable("nbTweets")
	// String nbTweets)
	// {
	// log.debug("REST request to get the tweet list ({} sized).", nbTweets);
	// try
	// {
	// return timelineService.getTimeline(login, Integer.parseInt(nbTweets));
	// }
	// catch (NumberFormatException e)
	// {
	// log.warn("Page size undefined ; sizing to default", e);
	// return timelineService.getTimeline(login, TatamiConstants.DEFAULT_TWEET_NUMBER);
	// }
	// }

	@RequestMapping(value = "/rest/ownTweets/{login}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<Tweet> listTweets(@PathVariable("login")
	String login)
	{
		log.debug("REST request to get the own tweet list ( {} ).", login);
		return timelineService.getUserline(login, TatamiConstants.DEFAULT_TWEET_NUMBER);
	}

	@RequestMapping(value = "/rest/tweets", method = RequestMethod.POST)
	@ResponseBody
	public Tweet postTweet(@RequestBody
	String content)
	{
		log.debug("REST request to add tweet : {}", content);
		return timelineService.postTweet(content);
	}
}
