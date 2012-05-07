package fr.ippon.tatami.service.lines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.repository.StatsRepository;
import fr.ippon.tatami.service.pipeline.TweetHandler;

public class StatslineService extends AbstractlineService implements TweetHandler
{

	private StatsRepository statsRepository;

	private static final String DAYLINE_KEY_FORMAT = "yyyyMMdd";
	private static final String WEEKLINE_KEY_FORMAT = "w";
	private static final String MONTHLINE_KEY_FORMAT = "yyyyMM";
	private static final String YEARLINE_KEY_FORMAT = "yyyy";

	public Collection<UserTweetStat> getDayline(Date date)
	{
		return this.getDayline(new DateTime(date).toString(DAYLINE_KEY_FORMAT));
	}

	public Collection<UserTweetStat> getDayline(String date)
	{
		Map<String, Long> dayStats = statsRepository.findTweetsForDay(date);

		return this.buildUserTweetStats(dayStats);
	}

	private Collection<UserTweetStat> buildUserTweetStats(Map<String, Long> dayStats)
	{
		List<UserTweetStat> userTweetStats = new ArrayList<UserTweetStat>();

		for (Entry<String, Long> entry : dayStats.entrySet())
		{
			userTweetStats.add(new UserTweetStat(entry.getKey(), entry.getValue()));
		}

		Collections.sort(userTweetStats);
		Collections.reverse(userTweetStats);

		return userTweetStats;
	}

	@Override
	public void onTweetPost(Tweet tweet)
	{
		DateTime today = new DateTime(new Date());

		statsRepository.addTweetToDay(tweet.getLogin(), today.toString(DAYLINE_KEY_FORMAT));
		statsRepository.addTweetToWeek(tweet.getLogin(), today.toString(WEEKLINE_KEY_FORMAT));
		statsRepository.addTweetToMonth(tweet.getLogin(), today.toString(MONTHLINE_KEY_FORMAT));
		statsRepository.addTweetToYear(tweet.getLogin(), today.toString(YEARLINE_KEY_FORMAT));

	}

	@Override
	public void onTweetRemove(Tweet tweet)
	{
		if (!tweet.isNotification())
		{
			DateTime tweetDate = new DateTime(tweet.getTweetDate());

			statsRepository.removeTweetFromDay(tweet.getLogin(), tweetDate.toString(DAYLINE_KEY_FORMAT));
			statsRepository.removeTweetFromWeek(tweet.getLogin(), tweetDate.toString(WEEKLINE_KEY_FORMAT));
			statsRepository.removeTweetFromMonth(tweet.getLogin(), tweetDate.toString(MONTHLINE_KEY_FORMAT));
			statsRepository.removeTweetFromYear(tweet.getLogin(), tweetDate.toString(YEARLINE_KEY_FORMAT));
		}
	}

	public void setStatsRepository(StatsRepository statsRepository)
	{
		this.statsRepository = statsRepository;
	}

}
