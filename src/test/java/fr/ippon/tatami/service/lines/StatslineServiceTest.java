package fr.ippon.tatami.service.lines;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.service.security.AuthenticationService;

public class StatslineServiceTest extends AbstractCassandraTatamiTest
{

	private User jdubois, duyhai, tescolan;
	private Tweet t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, tweet;

	@Test
	public void initForStatslineServiceTest()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		duyhai = new User();
		duyhai.setLogin("duyhai");
		duyhai.setEmail("duyhai@ippon.fr");
		duyhai.setFirstName("DuyHai");
		duyhai.setLastName("DOAN");

		tescolan = new User();
		tescolan.setLogin("tescolan");
		tescolan.setEmail("tescolan@ippon.fr");
		tescolan.setFirstName("Thomas");
		tescolan.setLastName("ESCOLAN");

		this.userRepository.createUser(jdubois);
		this.userRepository.createUser(duyhai);
		this.userRepository.createUser(tescolan);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.userService.setAuthenticationService(mockAuthenticationService);

		// Julien tweets
		t1 = this.tweetService.createTransientTweet("tweet1");
		t2 = this.tweetService.createTransientTweet("tweet2");
		t3 = this.tweetService.createTransientTweet("tweet3");
		t4 = this.tweetService.createTransientTweet("tweet4");
		t5 = this.tweetService.createTransientTweet("tweet5");

		this.statsRepository.addTweetToDay(t1.getLogin(), "20120329");
		this.statsRepository.addTweetToDay(t2.getLogin(), "20120329");
		this.statsRepository.addTweetToDay(t3.getLogin(), "20120329");
		this.statsRepository.addTweetToDay(t4.getLogin(), "20120329");
		this.statsRepository.addTweetToDay(t5.getLogin(), "20120329");

		// DuyHai tweets
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		t6 = this.tweetService.createTransientTweet("tweet6");
		t7 = this.tweetService.createTransientTweet("tweet7");
		t8 = this.tweetService.createTransientTweet("tweet8");

		this.statsRepository.addTweetToDay(t6.getLogin(), "20120329");
		this.statsRepository.addTweetToDay(t7.getLogin(), "20120329");
		this.statsRepository.addTweetToDay(t8.getLogin(), "20120329");

		// Thomas tweets
		when(mockAuthenticationService.getCurrentUser()).thenReturn(tescolan);

		t9 = this.tweetService.createTransientTweet("tweet9");
		t10 = this.tweetService.createTransientTweet("tweet10");

		this.statsRepository.addTweetToDay(t9.getLogin(), "20120329");
		this.statsRepository.addTweetToDay(t10.getLogin(), "20120329");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
	}

	@Test(dependsOnMethods = "initForStatslineServiceTest")
	public void testGetDaylineFromDate()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2012, Calendar.MARCH, 29);

		List<UserTweetStat> tweetStats = new ArrayList<UserTweetStat>(this.statslineService.getDayline(cal.getTime()));

		assertEquals(tweetStats.size(), 3, "3 UserTweetStat for 20120329");
		assertEquals(tweetStats.get(0).getTweetsCount().longValue(), 5L, "jdubois posted 5 tweets for 20120329");
		assertEquals(tweetStats.get(1).getTweetsCount().longValue(), 3L, "duyhai posted 3 tweets for 20120329");
		assertEquals(tweetStats.get(2).getTweetsCount().longValue(), 2L, "tescolan posted 2 tweets for 20120329");
	}

	@Test(dependsOnMethods = "testGetDaylineFromDate")
	public void testGetDaylineFromString()
	{
		List<UserTweetStat> tweetStats = new ArrayList<UserTweetStat>(this.statslineService.getDayline("20120329"));

		assertEquals(tweetStats.size(), 3, "3 UserTweetStat for 20120329");
		assertEquals(tweetStats.get(0).getTweetsCount().longValue(), 5L, "jdubois posted 5 tweets for 20120329");
		assertEquals(tweetStats.get(1).getTweetsCount().longValue(), 3L, "duyhai posted 3 tweets for 20120329");
		assertEquals(tweetStats.get(2).getTweetsCount().longValue(), 2L, "tescolan posted 2 tweets for 20120329");
	}

	@Test(dependsOnMethods = "testGetDaylineFromString")
	public void testOnTweetPostForStatsLine()
	{

		tweet = this.tweetService.createTransientTweet("Test today tweet");
		this.statslineService.onTweetPost(tweet);

		List<UserTweetStat> tweetStats = new ArrayList<UserTweetStat>(this.statslineService.getDayline(new Date()));

		assertEquals(tweetStats.size(), 1, "1 UserTweetStat for today");
		assertEquals(tweetStats.get(0).getTweetsCount().longValue(), 1L, "1 tweet posted today");
		assertEquals(tweetStats.get(0).getLogin(), "jdubois", "jdubois is the only tweeter today");

	}

	@Test(dependsOnMethods = "testOnTweetPostForStatsLine")
	public void testOnTweetRemoveForStatsLine()
	{
		this.statslineService.onTweetRemove(tweet);
		List<UserTweetStat> tweetStats = new ArrayList<UserTweetStat>(this.statslineService.getDayline(new Date()));

		assertEquals(tweetStats.size(), 0, "0 UserTweetStat for today");
	}
}
