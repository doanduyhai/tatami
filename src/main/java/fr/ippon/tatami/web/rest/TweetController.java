package fr.ippon.tatami.web.rest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.owasp.esapi.reference.DefaultEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.ippon.tatami.domain.DayTweetStat;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * REST controller for managing tweets.
 * 
 * @author Julien Dubois
 */
@Controller
public class TweetController extends AbstractRESTController
{
	private final Logger log = LoggerFactory.getLogger(TweetController.class);

	@Inject
	private TimelineService timelineService;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public String handleFunctionalException(MethodArgumentNotValidException ex, HttpServletRequest request)
	{
		log.error(" Validation exception raised : " + ex.getMessage());
		StringBuilder errorBuffer = new StringBuilder();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors())
		{
			errorBuffer.append(fieldError.getDefaultMessage()).append("<br/>");
		}
		return errorBuffer.toString();
	}

	@RequestMapping(value = "/rest/tweets/{login}/{nbTweets}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<Tweet> listTweets(@PathVariable("login") String login, @PathVariable("nbTweets") String nbTweets)
	{
		log.debug("REST request to get the tweet list ( {} sized).", nbTweets);
		try
		{
			return timelineService.getTimeline(login, Integer.parseInt(nbTweets));
		}
		catch (NumberFormatException e)
		{
			log.warn("Page size undefined ; sizing to default", e);
			return timelineService.getTimeline(login, 20);
		}
	}

	@RequestMapping(value = "/rest/tweetStats/day", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<UserTweetStat> listDayTweetStats()
	{
		log.debug("REST request to get the users stats.");

		String date = null; // TODO parameterized version
		Collection<Tweet> tweets = timelineService.getDayline(date);
		return this.extractUsersTweetStats(tweets, null);
	}

	private Collection<UserTweetStat> extractUsersTweetStats(Collection<Tweet> tweets, Set<String> usersCollector)
	{
		log.debug("Analysing {} items...", tweets.size());
		Map<String, Integer> users = new HashMap<String, Integer>();
		for (Tweet tweet : tweets)
		{
			Integer count = users.get(tweet.getLogin());
			if (count != null)
			{
				count = count.intValue() + 1;
			}
			else
			{
				if (usersCollector != null)
					usersCollector.add(tweet.getLogin());
				count = 1;
			}
			users.put(tweet.getLogin(), count);
		}

		log.debug("Fetched total of {} stats.", users.size());

		Collection<UserTweetStat> stats = new TreeSet<UserTweetStat>(); // cf. UserTweetStat#compareTo
		for (Entry<String, Integer> entry : users.entrySet())
		{
			stats.add(new UserTweetStat(entry.getKey(), entry.getValue()));
		}
		return stats;
	}

	@RequestMapping(value = "/rest/tweetStats/week", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<DayTweetStat> listWeekTweetStats()
	{
		log.debug("REST request to get the users stats.");

		Calendar date = Calendar.getInstance(); // TODO parameterized version
		DayTweetStat stats[] = new DayTweetStat[7];
		Set<String> users = new HashSet<String>();
		for (int i = stats.length; i > 0; i--)
		{
			date.add(Calendar.DATE, -1); // let's analyze the past week

			DayTweetStat dayStat = new DayTweetStat(date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH));
			log.debug("Scanning {} ...", dayStat.getDay());
			Collection<Tweet> tweets = timelineService.getDayline(date.getTime());
			dayStat.setStats(this.extractUsersTweetStats(tweets, users));

			stats[i - 1] = dayStat; // oldest first
		}
		this.enforceUsers(stats, users); // each day's users list has to be identical to the others

		return Arrays.asList(stats);
	}

	private void enforceUsers(DayTweetStat stats[], Set<String> allUsers)
	{
		for (DayTweetStat stat : stats)
		{
			for (String login : allUsers)
			{
				if (!stat.getStats().contains(new UserTweetStat(login, 0)))
				{ // cf. UserTweetStat#compareTo
					stat.getStats().add(new UserTweetStat(login, 0));
				}
			}
		}
	}

	@RequestMapping(value = "/rest/ownTweets/{login}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<Tweet> listTweets(@PathVariable("login") String login)
	{
		log.debug("REST request to get the own tweet list ( {} ).", login);
		return timelineService.getUserline(login, TatamiConstants.DEFAULT_TWEET_LIST_SIZE);
	}

	@RequestMapping(value = "/rest/tweets", method = RequestMethod.POST)
	@ResponseBody
	public Tweet postTweet(@Valid @RequestBody Tweet tweet)
	{

		// XSS protection by encoding input data with ESAPI api
		tweet.setContent(DefaultEncoder.getInstance().encodeForHTML(tweet.getContent()));

		log.debug("REST request to add tweet : {}", tweet.getContent());
		return timelineService.postTweet(tweet.getContent());
	}
}
