package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.DAYLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.MONTHLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.WEEKLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.YEARLINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;

public class StatsRepositoryTest extends AbstractCassandraTatamiTest
{
	private User test;

	private User duyhai;

	@Test
	public void initStatsRepositoryTest()
	{
		test = new User();
		test.setLogin("test");
		test.setEmail("test@ippon.fr");
		test.setFirstName("firstname");
		test.setLastName("lastname");

		duyhai = new User();
		duyhai.setLogin("duyhai");
		duyhai.setEmail("duyhai@ippon.fr");
		duyhai.setFirstName("DuyHai");
		duyhai.setLastName("DOAN");

		this.userRepository.createUser(test);
		this.userRepository.createUser(duyhai);
	}

	@Test(dependsOnMethods = "initStatsRepositoryTest")
	public void testAddTweetToDay()
	{
		for (int i = 0; i < 5; i++)
		{
			this.statsRepository.addTweetToDay("duyhai", "20120329");
		}

		for (int i = 0; i < 2; i++)
		{
			this.statsRepository.addTweetToDay("test", "20120329");
		}

		List<HColumn<String, Long>> columns = createSliceQuery(keyspace, se, se, le).setColumnFamily(DAYLINE_CF).setKey("20120329")
				.setRange(null, null, false, 100).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 user for today stats");
		assertEquals(columns.get(0).getValue().longValue(), 5, "duyhai has 5 tweets today");

	}

	@Test(dependsOnMethods = "testAddTweetToDay")
	public void testFindTweetsForDay()
	{
		Map<String, Long> dayStats = this.statsRepository.findTweetsForDay("20120329");

		assertTrue(dayStats.size() == 2, "dayStats.size() == 2");
		assertTrue(dayStats.containsKey("duyhai"), "dayStats has 'duyhai'");
		assertTrue(dayStats.containsKey("test"), "dayStats has 'test'");
	}

	@Test(dependsOnMethods = "testFindTweetsForDay")
	public void testRemoveTweetFromDay()
	{
		for (int i = 2; i > 0; i--)
		{
			this.statsRepository.removeTweetFromDay("duyhai", "20120329");
		}

		List<HColumn<String, Long>> columns = createSliceQuery(keyspace, se, se, le).setColumnFamily(DAYLINE_CF).setKey("20120329")
				.setRange(null, null, false, 100).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 user for today stats");
		assertEquals(columns.get(0).getValue().longValue(), 3, "duyhai has 3 tweets today");
	}

	@Test(dependsOnMethods = "initStatsRepositoryTest")
	public void testAddTweetToWeek()
	{
		for (int i = 0; i < 5; i++)
		{
			this.statsRepository.addTweetToWeek("duyhai", "12");
		}

		for (int i = 0; i < 2; i++)
		{
			this.statsRepository.addTweetToWeek("test", "12");
		}

		List<HColumn<String, Long>> columns = createSliceQuery(keyspace, se, se, le).setColumnFamily(WEEKLINE_CF).setKey("12")
				.setRange(null, null, false, 100).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 user for this week stats");
		assertEquals(columns.get(0).getValue().longValue(), 5, "duyhai has 5 tweets this week");

	}

	@Test(dependsOnMethods = "testAddTweetToWeek")
	public void testFindTweetsForWeek()
	{
		Map<String, Long> weekStats = this.statsRepository.findTweetsForWeek("12");

		assertTrue(weekStats.size() == 2, "weekStats.size() == 2");
		assertTrue(weekStats.containsKey("duyhai"), "weekStats has 'duyhai'");
		assertTrue(weekStats.containsKey("test"), "weekStats has 'test'");
	}

	@Test(dependsOnMethods = "testFindTweetsForWeek")
	public void testRemoveTweetFromWeek()
	{
		for (int i = 2; i > 0; i--)
		{
			this.statsRepository.removeTweetFromWeek("duyhai", "12");
		}

		List<HColumn<String, Long>> columns = createSliceQuery(keyspace, se, se, le).setColumnFamily(WEEKLINE_CF).setKey("12")
				.setRange(null, null, false, 100).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 user for this week stats");
		assertEquals(columns.get(0).getValue().longValue(), 3, "duyhai has 3 tweets this week");
	}

	@Test(dependsOnMethods = "initStatsRepositoryTest")
	public void testAddTweetToMonth()
	{
		for (int i = 0; i < 5; i++)
		{
			this.statsRepository.addTweetToMonth("duyhai", "201203");
		}

		for (int i = 0; i < 2; i++)
		{
			this.statsRepository.addTweetToMonth("test", "201203");
		}

		List<HColumn<String, Long>> columns = createSliceQuery(keyspace, se, se, le).setColumnFamily(MONTHLINE_CF).setKey("201203")
				.setRange(null, null, false, 100).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 user for this month stats");
		assertEquals(columns.get(0).getValue().longValue(), 5, "duyhai has 5 tweets this month");
	}

	@Test(dependsOnMethods = "testAddTweetToMonth")
	public void testFindTweetsForMonth()
	{
		Map<String, Long> monthStats = this.statsRepository.findTweetsForMonth("201203");

		assertTrue(monthStats.size() == 2, "monthStats.size() == 2");
		assertTrue(monthStats.containsKey("duyhai"), "monthStats has 'duyhai'");
		assertTrue(monthStats.containsKey("test"), "monthStats has 'test'");
	}

	@Test(dependsOnMethods = "testFindTweetsForMonth")
	public void testRemoveTweetFromMonth()
	{
		for (int i = 2; i > 0; i--)
		{
			this.statsRepository.removeTweetFromMonth("duyhai", "201203");
		}

		List<HColumn<String, Long>> columns = createSliceQuery(keyspace, se, se, le).setColumnFamily(MONTHLINE_CF).setKey("201203")
				.setRange(null, null, false, 100).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 user for this month stats");
		assertEquals(columns.get(0).getValue().longValue(), 3, "duyhai has 3 tweets this month");
	}

	@Test(dependsOnMethods = "initStatsRepositoryTest")
	public void testAddTweetToYear()
	{
		for (int i = 0; i < 5; i++)
		{
			this.statsRepository.addTweetToYear("duyhai", "2012");
		}

		for (int i = 0; i < 2; i++)
		{
			this.statsRepository.addTweetToYear("test", "2012");
		}

		List<HColumn<String, Long>> columns = createSliceQuery(keyspace, se, se, le).setColumnFamily(YEARLINE_CF).setKey("2012")
				.setRange(null, null, false, 100).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 user for this year stats");
		assertEquals(columns.get(0).getValue().longValue(), 5, "duyhai has 5 tweets this year");
	}

	@Test(dependsOnMethods = "testAddTweetToYear")
	public void testFindTweetsForYear()
	{
		Map<String, Long> yearStats = this.statsRepository.findTweetsForYear("2012");

		assertTrue(yearStats.size() == 2, "yearStats.size() == 2");
		assertTrue(yearStats.containsKey("duyhai"), "yearStats has 'duyhai'");
		assertTrue(yearStats.containsKey("test"), "yearStats has 'test'");
	}

	@Test(dependsOnMethods = "testFindTweetsForYear")
	public void testRemoveTweetFromYear()
	{
		for (int i = 2; i > 0; i--)
		{
			this.statsRepository.removeTweetFromYear("duyhai", "2012");
		}

		List<HColumn<String, Long>> columns = createSliceQuery(keyspace, se, se, le).setColumnFamily(YEARLINE_CF).setKey("2012")
				.setRange(null, null, false, 100).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 user for this year stats");
		assertEquals(columns.get(0).getValue().longValue(), 3, "duyhai has 3 tweets this year");
	}

}
