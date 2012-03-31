package fr.ippon.tatami.repository.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import me.prettyprint.hom.EntityManagerImpl;

import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.DayLine;
import fr.ippon.tatami.domain.MonthLine;
import fr.ippon.tatami.domain.WeekLine;
import fr.ippon.tatami.domain.YearLine;
import fr.ippon.tatami.repository.StatsRepository;

@Repository
public class CassandraStatsRepository extends CassandraAbstractRepository implements StatsRepository
{

	@Inject
	private EntityManagerImpl em;

	@Override
	public void addTweetToDay(String tweetId, String day)
	{
		DayLine dayLine = em.find(DayLine.class, day);
		if (dayLine == null)
		{
			dayLine = new DayLine();
			dayLine.setDay(day);
		}

		dayLine.getTweetIds().add(tweetId);
		em.persist(dayLine);

	}

	@Override
	public void removeTweetFromDay(String tweetId, String day)
	{
		DayLine dayLine = em.find(DayLine.class, day);
		if (dayLine == null)
		{
			// TODO Functional exception
			return;
		}

		dayLine.getTweetIds().remove(tweetId);
		em.persist(dayLine);

	}

	@Override
	public Collection<String> findTweetsForDay(String day)
	{
		DayLine dayLine = em.find(DayLine.class, day);
		if (dayLine != null)
		{
			return dayLine.getTweetIds();
		}
		return Arrays.asList();
	}

	@Override
	public Collection<String> findTweetsRangeForDay(String day, int start, int end)
	{
		DayLine dayLine = em.find(DayLine.class, day);
		if (dayLine != null)
		{
			List<String> list = new ArrayList<String>(dayLine.getTweetIds());

			if (list.size() > 0)
			{
				if (start < 1)
				{
					start = 1;
				}

				if (end > list.size())
				{
					end = list.size();
				}
				Collections.reverse(list);
				return list.subList(start - 1, end);
			}

		}
		return Arrays.asList();
	}

	@Override
	public void addTweetToWeek(String tweetId, String week)
	{
		WeekLine weekLine = em.find(WeekLine.class, week);
		if (weekLine == null)
		{
			weekLine = new WeekLine();
			weekLine.setWeek(week);
		}

		weekLine.getTweetIds().add(tweetId);
		em.persist(weekLine);

	}

	@Override
	public void removeTweetFromWeek(String tweetId, String week)
	{
		WeekLine weekLine = em.find(WeekLine.class, week);
		if (weekLine == null)
		{
			// TODO Functional exception
			return;
		}

		weekLine.getTweetIds().remove(tweetId);
		em.persist(weekLine);

	}

	@Override
	public Collection<String> findTweetsForWeek(String week)
	{
		WeekLine weekLine = em.find(WeekLine.class, week);
		if (weekLine != null)
		{
			return weekLine.getTweetIds();
		}
		return Arrays.asList();
	}

	@Override
	public Collection<String> findTweetsRangeForWeek(String day, int start, int end)
	{
		WeekLine weekLine = em.find(WeekLine.class, day);
		if (weekLine != null)
		{
			List<String> list = new ArrayList<String>(weekLine.getTweetIds());
			if (list.size() > 0)
			{
				if (start < 1)
				{
					start = 1;
				}

				if (end > list.size())
				{
					end = list.size();
				}
				Collections.reverse(list);
				return list.subList(start - 1, end);
			}
		}
		return Arrays.asList();
	}

	@Override
	public void addTweetToMonth(String tweetId, String month)
	{
		MonthLine monthLine = em.find(MonthLine.class, month);
		if (monthLine == null)
		{
			monthLine = new MonthLine();
			monthLine.setMonth(month);
		}

		monthLine.getTweetIds().add(tweetId);
		em.persist(monthLine);

	}

	@Override
	public void removeTweetFromMonth(String tweetId, String month)
	{
		MonthLine monthLine = em.find(MonthLine.class, month);
		if (monthLine == null)
		{
			// TODO Functional exception
			return;
		}

		monthLine.getTweetIds().remove(tweetId);
		em.persist(monthLine);
	}

	@Override
	public Collection<String> findTweetsForMonth(String month)
	{
		MonthLine monthLine = em.find(MonthLine.class, month);
		if (monthLine != null)
		{
			return monthLine.getTweetIds();
		}
		return Arrays.asList();
	}

	@Override
	public Collection<String> findTweetsRangeForMonth(String day, int start, int end)
	{
		MonthLine monthLine = em.find(MonthLine.class, day);
		if (monthLine != null)
		{
			List<String> list = new ArrayList<String>(monthLine.getTweetIds());
			if (list.size() > 0)
			{
				if (start < 1)
				{
					start = 1;
				}

				if (end > list.size())
				{
					end = list.size();
				}
				Collections.reverse(list);
				return list.subList(start - 1, end);
			}
		}
		return Arrays.asList();
	}

	@Override
	public void addTweetToYear(String tweetId, String year)
	{
		YearLine yearLine = em.find(YearLine.class, year);
		if (yearLine == null)
		{
			yearLine = new YearLine();
			yearLine.setYear(year);
		}

		yearLine.getTweetIds().add(tweetId);
		em.persist(yearLine);

	}

	@Override
	public void removeTweetFromYear(String tweetId, String year)
	{
		YearLine yearLine = em.find(YearLine.class, year);
		if (yearLine == null)
		{
			// TODO Functional exception
			return;
		}

		yearLine.getTweetIds().remove(tweetId);
		em.persist(yearLine);

	}

	@Override
	public Collection<String> findTweetsRangeForYear(String day, int start, int end)
	{
		YearLine yearLine = em.find(YearLine.class, day);
		if (yearLine != null)
		{
			List<String> list = new ArrayList<String>(yearLine.getTweetIds());
			if (list.size() > 0)
			{
				if (start < 1)
				{
					start = 1;
				}

				if (end > list.size())
				{
					end = list.size();
				}
				Collections.reverse(list);
				return list.subList(start - 1, end);
			}
		}
		return Arrays.asList();
	}

	@Override
	public Collection<String> findTweetsForYear(String year)
	{
		YearLine yearLine = em.find(YearLine.class, year);
		if (yearLine != null)
		{
			return yearLine.getTweetIds();
		}
		return Arrays.asList();
	}

}
