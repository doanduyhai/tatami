package fr.ippon.tatami.web.rest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.ippon.tatami.domain.DayTweetStat;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.domain.json.TweetFetchRange;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.lines.FavoritelineService;
import fr.ippon.tatami.service.lines.StatslineService;
import fr.ippon.tatami.service.lines.TaglineService;
import fr.ippon.tatami.service.lines.TimelineService;
import fr.ippon.tatami.service.lines.UserlineService;
import fr.ippon.tatami.service.pipeline.FavoritePipelineManager;
import fr.ippon.tatami.service.pipeline.TweetPipelineManager;
import fr.ippon.tatami.web.json.view.TweetView;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
@Controller
public class TweetController extends AbstractRESTController
{
	private final Logger log = LoggerFactory.getLogger(TweetController.class);

	private TimelineService timelineService;

	private StatslineService statslineService;

	private UserlineService userlineService;

	private FavoritelineService favoritelineService;

	private TaglineService taglineService;

	private TweetPipelineManager tweetPipelineManager;

	private FavoritePipelineManager favoritePipelineManager;

	@RequestMapping(value = "/rest/tweetStats/day", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Collection<UserTweetStat> listDayTweetStats()
	{
		log.debug("REST request to get the users stats.");

		return statslineService.getDayline(new Date());
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

			dayStat.setStats(statslineService.getDayline(date.getTime()));

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
				if (!stat.getStats().contains(new UserTweetStat(login, 0L)))
				{ // cf. UserTweetStat#compareTo
					stat.getStats().add(new UserTweetStat(login, 0L));
				}
			}
		}
	}

	@RequestMapping(value = "/rest/tweets", method = RequestMethod.POST)
	@ResponseBody
	public Tweet postTweet(@Valid @RequestBody Tweet tweet) throws FunctionalException
	{
		log.debug("REST request to add tweet : {}", tweet.getContent());
		return tweetPipelineManager.onPost(tweet.getContent());
	}

	@RequestMapping(value = "/rest/removeTweet/{tweet}", method = RequestMethod.GET)
	@ResponseBody
	public boolean removeTweet(@PathVariable("tweet") String tweetId) throws FunctionalException
	{
		log.debug("REST request to remove tweet : {}", tweetId);
		tweetPipelineManager.onRemove(tweetId);

		return true;
	}

	@RequestMapping(value = "/rest/likeTweet/{tweet}", method = RequestMethod.GET)
	@ResponseBody
	public boolean likeTweet(@PathVariable("tweet") String tweet) throws FunctionalException
	{
		log.debug("REST request to like tweet : {} ", tweet);

		this.favoritePipelineManager.onAddToFavorite(tweet);

		return true;
	}

	@RequestMapping(value = "/rest/unlikeTweet/{tweet}", method = RequestMethod.GET)
	@ResponseBody
	public boolean unlikeTweet(@PathVariable("tweet") String tweet) throws FunctionalException
	{
		log.debug("REST request to unlike tweet : {} ", tweet);

		this.favoritePipelineManager.onRemoveFromFavorite(tweet);

		return true;
	}

	@RequestMapping(value = "/rest/tweetFetch/timeline", method = RequestMethod.POST, consumes = "application/json")
	public void timelineTweetFetch(@Valid @RequestBody TweetFetchRange fetchRange, HttpServletResponse response) throws FunctionalException
	{

		log.debug("REST fetch tweet from {} to {} for current user ", new Object[]
		{
				fetchRange.getStartTweetId(),
				fetchRange.getCount()
		});

		this.writeWithView((Object) timelineService.getTimelineRange(fetchRange.getStartTweetId(), fetchRange.getCount()), response,
				TweetView.Full.class);
	}

	@RequestMapping(value = "/rest/tweetFetch/userline", method = RequestMethod.POST, consumes = "application/json")
	public void userlineTweetFetch(@Valid @RequestBody TweetFetchRange fetchRange, HttpServletResponse response) throws FunctionalException
	{

		log.debug("REST fetch tweet from {} to {} for user {}", new Object[]
		{
				fetchRange.getStartTweetId(),
				fetchRange.getCount(),
				fetchRange.getFunctionalKey()
		});

		this.writeWithView(
				(Object) userlineService.getUserlineRange(fetchRange.getFunctionalKey(), fetchRange.getStartTweetId(), fetchRange.getCount()),
				response, TweetView.Full.class);
	}

	@RequestMapping(value = "/rest/tweetFetch/favoriteline", method = RequestMethod.POST, consumes = "application/json")
	public void favoriteTweetFetch(@Valid @RequestBody TweetFetchRange fetchRange, HttpServletResponse response) throws FunctionalException
	{

		log.debug("REST fetch tweet from {} to {} for current user ", new Object[]
		{
				fetchRange.getFunctionalKey(),
				fetchRange.getCount()
		});

		this.writeWithView((Object) favoritelineService.getFavoriteslineRange(fetchRange.getStartTweetId(), fetchRange.getCount()), response,
				TweetView.Full.class);
	}

	@RequestMapping(value = "/rest/tweetFetch/tagline", method = RequestMethod.POST, consumes = "application/json")
	public void taglineTweetFetch(@Valid @RequestBody TweetFetchRange fetchRange, HttpServletResponse response) throws FunctionalException
	{

		log.debug("REST fetch tweet from {} to {} for tag {}", new Object[]
		{
				fetchRange.getStartTweetId(),
				fetchRange.getCount(),
				fetchRange.getFunctionalKey()
		});

		this.writeWithView(
				(Object) taglineService.getTaglineRange(fetchRange.getFunctionalKey(), fetchRange.getStartTweetId(), fetchRange.getCount()),
				response, TweetView.Full.class);
	}

	public void setTimelineService(TimelineService timelineService)
	{
		this.timelineService = timelineService;
	}

	public void setStatslineService(StatslineService statslineService)
	{
		this.statslineService = statslineService;
	}

	public void setUserlineService(UserlineService userlineService)
	{
		this.userlineService = userlineService;
	}

	public void setFavoritelineService(FavoritelineService favoritelineService)
	{
		this.favoritelineService = favoritelineService;
	}

	public void setTaglineService(TaglineService taglineService)
	{
		this.taglineService = taglineService;
	}

	public void setTweetPipelineManager(TweetPipelineManager tweetPipelineManager)
	{
		this.tweetPipelineManager = tweetPipelineManager;
	}

	public void setFavoritePipelineManager(FavoritePipelineManager favoritePipelineManager)
	{
		this.favoritePipelineManager = favoritePipelineManager;
	}

}
