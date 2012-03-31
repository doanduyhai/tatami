package fr.ippon.tatami.repository;

import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.DayLine;
import fr.ippon.tatami.domain.MonthLine;
import fr.ippon.tatami.domain.WeekLine;
import fr.ippon.tatami.domain.YearLine;

public class StatsRepositoryTest extends AbstractCassandraTatamiTest
{

	@Test
	public void testAddTweetToDay()
	{
		this.statsRepository.addTweetToDay("tweet1", "20120329");
		this.statsRepository.addTweetToDay("tweet2", "20120329");
		this.statsRepository.addTweetToDay("tweet3", "20120329");
		this.statsRepository.addTweetToDay("tweet4", "20120329");
		this.statsRepository.addTweetToDay("tweet5", "20120329");

		Collection<String> tweetIds = this.entityManager.find(DayLine.class, "20120329").getTweetIds();

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains("tweet3"), "tweetIds has 'tweet3'");
		assertTrue(tweetIds.contains("tweet4"), "tweetIds has 'tweet4'");
		assertTrue(tweetIds.contains("tweet5"), "tweetIds has 'tweet5'");

	}

	@Test(dependsOnMethods = "testAddTweetToDay")
	public void testFindTweetsForDay()
	{
		Collection<String> tweetIds = this.statsRepository.findTweetsForDay("20120329");

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains("tweet3"), "tweetIds has 'tweet3'");
		assertTrue(tweetIds.contains("tweet4"), "tweetIds has 'tweet4'");
		assertTrue(tweetIds.contains("tweet5"), "tweetIds has 'tweet5'");
	}

	@Test(dependsOnMethods = "testFindTweetsForDay")
	public void testFindTweetsRangeForDay()
	{
		Collection<String> tweetIds = this.statsRepository.findTweetsRangeForDay("20120329", 2, 3);

		assertTrue(tweetIds.size() == 2, "tweetIds.size() == 2");
		assertTrue(tweetIds.contains("tweet3"), "tweetIds has 'tweet3'");
		assertTrue(tweetIds.contains("tweet4"), "tweetIds has 'tweet4'");
	}

	@Test(dependsOnMethods = "testFindTweetsRangeForDay")
	public void testFindTweetsRangeLimitsOutOfBoundForDay()
	{
		Collection<String> tweetIds = this.statsRepository.findTweetsRangeForDay("20120329", 5, 10);

		assertTrue(tweetIds.size() == 1, "tweetIds.size() == 1");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
	}

	@Test(dependsOnMethods = "testFindTweetsRangeLimitsOutOfBoundForDay")
	public void testFindTweetsRangeNegativeLimitsForDay()
	{
		Collection<String> tweetIds = this.statsRepository.findTweetsRangeForDay("20120329", -1, 1);

		assertTrue(tweetIds.size() == 1, "tweetIds.size() == 1");
		assertTrue(tweetIds.contains("tweet5"), "tweetIds has 'tweet5'");
	}

	@Test(dependsOnMethods = "testFindTweetsRangeNegativeLimitsForDay")
	public void testRemoveTweetFromDay()
	{
		this.statsRepository.removeTweetFromDay("tweet1", "20120329");
		this.statsRepository.removeTweetFromDay("tweet2", "20120329");
		this.statsRepository.removeTweetFromDay("tweet3", "20120329");
		this.statsRepository.removeTweetFromDay("tweet4", "20120329");
		this.statsRepository.removeTweetFromDay("tweet5", "20120329");

		Collection<String> tweetIds = this.statsRepository.findTweetsForDay("20120329");

		assertTrue(tweetIds.size() == 0, "tweetIds.size()==0");
	}

	@Test
	public void testAddTweetToWeek()
	{
		this.statsRepository.addTweetToWeek("tweet1", "13");
		this.statsRepository.addTweetToWeek("tweet2", "13");

		Collection<String> tweetIds = this.entityManager.find(WeekLine.class, "13").getTweetIds();

		assertTrue(tweetIds.size() == 2, "tweetIds.size() == 2");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
	}

	@Test(dependsOnMethods = "testAddTweetToWeek")
	public void testFindTweetsForWeek()
	{
		Collection<String> tweetIds = this.statsRepository.findTweetsForWeek("13");

		assertTrue(tweetIds.size() == 2, "tweetIds.size()");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
	}

	@Test(dependsOnMethods = "testFindTweetsForWeek")
	public void testRemoveTweetFromWeek()
	{
		this.statsRepository.removeTweetFromWeek("tweet1", "13");
		this.statsRepository.removeTweetFromWeek("tweet2", "13");

		Collection<String> tweetIds = this.statsRepository.findTweetsForWeek("13");

		assertTrue(tweetIds.size() == 0, "tweetIds.size()==0");
	}

	@Test
	public void testAddTweetToMonth()
	{
		this.statsRepository.addTweetToMonth("tweet1", "201203");
		this.statsRepository.addTweetToMonth("tweet2", "201203");

		Collection<String> tweetIds = this.entityManager.find(MonthLine.class, "201203").getTweetIds();

		assertTrue(tweetIds.size() == 2, "tweetIds.size() == 2");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
	}

	@Test(dependsOnMethods = "testAddTweetToMonth")
	public void testFindTweetsForMonth()
	{
		Collection<String> tweetIds = this.statsRepository.findTweetsForMonth("201203");

		assertTrue(tweetIds.size() == 2, "tweetIds.size()");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
	}

	@Test(dependsOnMethods = "testFindTweetsForMonth")
	public void testRemoveTweetFromMonth()
	{
		this.statsRepository.removeTweetFromMonth("tweet1", "201203");
		this.statsRepository.removeTweetFromMonth("tweet2", "201203");

		Collection<String> tweetIds = this.statsRepository.findTweetsForMonth("201203");

		assertTrue(tweetIds.size() == 0, "tweetIds.size()==0");
	}

	@Test
	public void testAddTweetToYear()
	{
		this.statsRepository.addTweetToYear("tweet1", "2012");
		this.statsRepository.addTweetToYear("tweet2", "2012");

		Collection<String> tweetIds = this.entityManager.find(YearLine.class, "2012").getTweetIds();

		assertTrue(tweetIds.size() == 2, "tweetIds.size() == 2");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
	}

	@Test(dependsOnMethods = "testAddTweetToYear")
	public void testFindTweetsForYear()
	{
		Collection<String> tweetIds = this.statsRepository.findTweetsForYear("2012");

		assertTrue(tweetIds.size() == 2, "tweetIds.size()");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
	}

	@Test(dependsOnMethods = "testFindTweetsForYear")
	public void testRemoveTweetFromYear()
	{
		this.statsRepository.removeTweetFromYear("tweet1", "2012");
		this.statsRepository.removeTweetFromYear("tweet2", "2012");

		Collection<String> tweetIds = this.statsRepository.findTweetsForYear("2012");

		assertTrue(tweetIds.size() == 0, "tweetIds.size()==0");
	}

}
