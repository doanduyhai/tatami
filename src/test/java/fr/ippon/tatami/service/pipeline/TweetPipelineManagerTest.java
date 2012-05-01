package fr.ippon.tatami.service.pipeline;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class TweetPipelineManagerTest extends AbstractCassandraTatamiTest
{
	private TweetPipelineManager manager;

	private User jdubois, duyhai, tescolan;
	private Tweet tweet, t1, t2, t3, t4;
	private AuthenticationService mockAuthenticationService;

	@Test
	public void init()
	{
		manager = new TweetPipelineManager();

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

		mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.userService.setAuthenticationService(mockAuthenticationService);

		manager.setTweetService(this.tweetService);

		TweetHandler handler1 = new TweetHandler()
		{

			@Override
			public void onTweetPost(Tweet tweet) throws FunctionalException
			{
				tweet.setContent("modified content by handler1");
				tweet.setLogin("foo");

			}
		};

		TweetHandler handler2 = new TweetHandler()
		{

			@Override
			public void onTweetPost(Tweet tweet) throws FunctionalException
			{
				tweet.setContent("modified content by handler2");
			}
		};

		manager.setTweetPostHandlers(Arrays.asList(handler1, handler2));

	}

	@Test(dependsOnMethods = "init")
	public void testOnPostWithMockedManager() throws FunctionalException
	{
		tweet = this.manager.onPost("Test tweet");

		assertEquals(tweet.getContent(), "modified content by handler2", "tweet content modified by handler2");
		assertEquals(tweet.getLogin(), "foo", "tweet login modified by handler1");
	}

	@Test(dependsOnMethods = "testOnPostWithMockedManager")
	public void testOnPostWithXssEncoding() throws FunctionalException
	{
		t1 = this.tweetPipelineManager.onPost("tweet1 <script>alert('toto');</script>");

		assertFalse(t1.getContent().contains("<script>alert('toto');</script>"), "t1 does not contain <script>alert('toto');</script>");

		Collection<Tweet> userlineTweets = this.userlineService.getUserlineRange("jdubois", null, 10);
		Collection<Tweet> timelineTweets = this.timelineService.getTimelineRange(null, 10);

		assertEquals(userlineTweets.size(), 1, "jdubois userline contains 1 tweet");
		assertTrue(userlineTweets.contains(t1), "jdubois userline contains 'tweet1'");

		assertEquals(timelineTweets.size(), 1, "jdubois timeline contains 1 tweet");
		assertTrue(timelineTweets.contains(t1), "jdubois timeline contains 'tweet1'");
	}

	@Test(dependsOnMethods = "testOnPostWithXssEncoding")
	public void testOnPostWithTagSpread() throws FunctionalException
	{
		t2 = this.tweetPipelineManager.onPost("tweet2 #Java");

		Collection<Tweet> userlineTweets = this.userlineService.getUserlineRange("jdubois", null, 10);
		assertEquals(userlineTweets.size(), 2, "jdubois userline contains 2 tweets");
		assertTrue(userlineTweets.contains(t1), "jdubois userline contains 'tweet1'");
		assertTrue(userlineTweets.contains(t2), "jdubois userline contains 'tweet2'");

		Collection<Tweet> taglineTweets = this.taglineService.getTaglineRange("Java", null, 10);
		assertEquals(taglineTweets.size(), 1, "jdubois tagline contains 1 tweet");
		assertTrue(taglineTweets.contains(t2), "jdubois tagline contains 'tweet2 #Java'");

	}

	@Test(dependsOnMethods = "testOnPostWithTagSpread")
	public void testOnPostWithMentionSpread() throws FunctionalException
	{
		t3 = this.tweetPipelineManager.onPost("tweet3 hello @duyhai");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		Collection<Tweet> timelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(timelineTweets.size(), 1, "duyhai timeline contains 1 tweet");
		assertTrue(timelineTweets.contains(t3), "duyhai timeline contains 'tweet3'");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
	}

	@Test(dependsOnMethods = "testOnPostWithMentionSpread")
	public void testOnPostCheckStatsLine() throws FunctionalException
	{
		Collection<UserTweetStat> dayStats = this.statslineService.getDayline(new Date());

		assertEquals(dayStats.size(), 1, "dayStats contains 1 UserTweetStat");

		UserTweetStat stats = dayStats.iterator().next();

		assertEquals(stats.getLogin(), "jdubois", "today tweeter is jdubois");
		assertEquals(stats.getTweetsCount().longValue(), 3, "today jdubois posted 3 tweets");
	}

	@Test(dependsOnMethods = "testOnPostCheckStatsLine")
	public void testOnPostWithContactsSpead() throws FunctionalException
	{
		// Thomas follows Julien
		when(mockAuthenticationService.getCurrentUser()).thenReturn(tescolan);
		this.userService.followUser("jdubois");

		// DuyHai follows Julien
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		this.userService.followUser("jdubois");

		// Refresh jdubois
		jdubois = this.userService.getUserByLogin("jdubois");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		t4 = this.tweetPipelineManager.onPost("tweet4 hello @duyhai second time");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		Collection<Tweet> duyhaiTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(duyhaiTimelineTweets.size(), 2, "duyhai timeline contains 2 tweet");
		assertTrue(duyhaiTimelineTweets.contains(t4), "duyhai timeline contains 'tweet4'");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(tescolan);

		Collection<Tweet> tescolanTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(tescolanTimelineTweets.size(), 1, "tescolan timeline contains 1 tweet");
		assertTrue(tescolanTimelineTweets.contains(t4), "tescolan timeline contains 'tweet4'");
	}

}
