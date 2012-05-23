package fr.ippon.tatami.service.pipeline.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserTweetStat;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.tweet.TweetService;

public class TweetPipelineManagerTest extends AbstractCassandraTatamiTest
{
	private TweetPipelineManager manager;

	private User jdubois, duyhai, tescolan;
	private Tweet tweet, t1, t2, t3, t4;
	private AuthenticationService mockAuthenticationService;
	private InOrder mockOrder;
	private TweetHandler handler1, handler2;

	@Test
	public void initTweetPipelineManagerTest()
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
		manager.setUserService(this.userService);

		handler1 = mock(TweetHandler.class);
		handler2 = mock(TweetHandler.class);

		mockOrder = Mockito.inOrder(handler1, handler2);

		manager.setTweetHandlers(Arrays.asList(handler1, handler2));

	}

	@Test(dependsOnMethods = "initTweetPipelineManagerTest")
	public void testOnPostWithMockedManager() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		tweet = this.manager.onPost("Test tweet");

		mockOrder.verify(handler1).onTweetPost(tweet);
		mockOrder.verify(handler2).onTweetPost(tweet);
	}

	@Test(dependsOnMethods = "initTweetPipelineManagerTest")
	public void testOnRemoveWithMockedManager() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		TweetService mockedTweetService = mock(TweetService.class);
		when(mockedTweetService.findTweetById(tweet.getTweetId())).thenReturn(tweet);

		this.manager.setTweetService(mockedTweetService);

		this.manager.onRemove(tweet.getTweetId());

		mockOrder.verify(handler1).onTweetRemove(tweet);
		mockOrder.verify(handler2).onTweetRemove(tweet);

	}

	@Test(dependsOnMethods = "testOnRemoveWithMockedManager")
	public void testOnPostWithXssEncodingTweetPipelineTest() throws FunctionalException
	{
		// jdubois posts t1
		t1 = this.tweetPipelineManager.onPost("tweet1 <script>alert('toto');</script>");

		assertFalse(t1.getContent().contains("<script>alert('toto');</script>"), "t1 does not contain <script>alert('toto');</script>");

		Collection<Tweet> userlineTweets = this.userlineService.getUserlineRange("jdubois", null, 10);
		Collection<Tweet> timelineTweets = this.timelineService.getTimelineRange(null, 10);

		assertEquals(userlineTweets.size(), 1, "jdubois userline contains 1 tweet");
		assertTrue(userlineTweets.contains(t1), "jdubois userline contains 'tweet1'");

		assertEquals(timelineTweets.size(), 1, "jdubois timeline contains 1 tweet");
		assertTrue(timelineTweets.contains(t1), "jdubois timeline contains 'tweet1'");
	}

	@Test(dependsOnMethods = "testOnPostWithXssEncodingTweetPipelineTest")
	public void testOnPostWithTagSpreadTweetPipelineTest() throws FunctionalException
	{
		// jdubois posts t2
		t2 = this.tweetPipelineManager.onPost("tweet2 #Java");

		Collection<Tweet> userlineTweets = this.userlineService.getUserlineRange("jdubois", null, 10);
		assertEquals(userlineTweets.size(), 2, "jdubois userline contains 2 tweets");
		assertTrue(userlineTweets.contains(t1), "jdubois userline contains 'tweet1'");
		assertTrue(userlineTweets.contains(t2), "jdubois userline contains 'tweet2'");

		Collection<Tweet> taglineTweets = this.taglineService.getTaglineRange("Java", null, 10);
		assertEquals(taglineTweets.size(), 1, "jdubois tagline contains 1 tweet");
		assertTrue(taglineTweets.contains(t2), "jdubois tagline contains 'tweet2 #Java'");

	}

	@Test(dependsOnMethods = "testOnPostWithTagSpreadTweetPipelineTest")
	public void testOnPostWithMentionSpreadTweetPipelineTest() throws FunctionalException
	{
		// jdubois posts t3
		t3 = this.tweetPipelineManager.onPost("tweet3 hello @duyhai");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		Collection<Tweet> timelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(timelineTweets.size(), 1, "duyhai timeline contains 1 tweet");
		assertTrue(timelineTweets.contains(t3), "duyhai timeline contains 'tweet3'");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
	}

	@Test(dependsOnMethods = "testOnPostWithMentionSpreadTweetPipelineTest")
	public void testOnPostCheckStatsLineTweetPipelineTest() throws FunctionalException
	{
		Collection<UserTweetStat> dayStats = this.statslineService.getDayline(new Date());

		assertEquals(dayStats.size(), 1, "dayStats contains 1 UserTweetStat");

		UserTweetStat stats = dayStats.iterator().next();

		assertEquals(stats.getLogin(), "jdubois", "today tweeter is jdubois");
		assertEquals(stats.getTweetsCount().longValue(), 3, "today jdubois posted 3 tweets");
	}

	@Test(dependsOnMethods = "testOnPostCheckStatsLineTweetPipelineTest")
	public void testOnPostWithContactsSpreadTweetPipelineTest() throws FunctionalException
	{

		// Tescolan follows Julien
		when(mockAuthenticationService.getCurrentUser()).thenReturn(tescolan);
		this.userPipelineManager.onFollow("jdubois");

		// DuyHai follows Julien
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		this.userPipelineManager.onFollow("jdubois");

		// Refresh jdubois
		jdubois = this.userService.getUserByLogin("jdubois");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		// jdubois posts t4
		t4 = this.tweetPipelineManager.onPost("tweet4 hello @duyhai second time");

		// jdubois's timeline contains 6 tweets: 3 from previous tests, 2 from follow notification and 1 from direct tweet
		Collection<Tweet> jduboisTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(jduboisTimelineTweets.size(), 6, "jdubois timeline contains 6 tweet");
		assertTrue(jduboisTimelineTweets.contains(t4), "jdubois timeline contains 'tweet4'");

		// jdubois's userline contains 4 tweets: 3 from previous test and 1 from direct tweet
		Collection<Tweet> jduboisUserlineTweets = this.userlineService.getUserlineRange("jdubois", null, 10);
		assertEquals(jduboisUserlineTweets.size(), 4, "jdubois userline contains 4 tweet");
		assertTrue(jduboisUserlineTweets.contains(t4), "jdubois userline contains 'tweet4'");

		// check duyhai's timeline
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		// 2 tweets in duyhai's timeline, first one for quoted tweet 'tweet3 hello @duyhai' from jdubois
		// second one because duyhai is now following jdubois (tweet4)
		Collection<Tweet> duyhaiTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(duyhaiTimelineTweets.size(), 2, "duyhai timeline contains 2 tweet");
		assertTrue(duyhaiTimelineTweets.contains(t4), "duyhai timeline contains 'tweet4'");

		// check tescolan's timeline
		when(mockAuthenticationService.getCurrentUser()).thenReturn(tescolan);

		Collection<Tweet> tescolanTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(tescolanTimelineTweets.size(), 1, "tescolan timeline contains 1 tweet");
		assertTrue(tescolanTimelineTweets.contains(t4), "tescolan timeline contains 'tweet4'");
	}

	@Test(dependsOnMethods = "testOnPostCheckStatsLineTweetPipelineTest")
	public void testOnRemoveTweetSpreadTweetPipelineTest() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetPipelineManager.onRemove(t4.getTweetId());

		// jdubois's timeline contains 5 tweets: 3 from previous tests, 2 from follow notification
		Collection<Tweet> jduboisTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(jduboisTimelineTweets.size(), 5, "jdubois timeline contains 5 tweet");
		assertFalse(jduboisTimelineTweets.contains(t4), "jdubois timeline contains 'tweet4'");

		// jdubois's userline contains 4 tweets: 3 from previous test and 1 from direct tweet
		Collection<Tweet> jduboisUserlineTweets = this.userlineService.getUserlineRange("jdubois", null, 10);
		assertEquals(jduboisUserlineTweets.size(), 3, "jdubois userline contains 3 tweet");
		assertFalse(jduboisUserlineTweets.contains(t4), "jdubois userline contains 'tweet4'");

		// check duyhai's timeline
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		// 1 tweet in duyhai's timeline, first one for quoted tweet 'tweet3 hello @duyhai' from jdubois
		Collection<Tweet> duyhaiTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(duyhaiTimelineTweets.size(), 1, "duyhai timeline contains 1 tweet");
		assertFalse(duyhaiTimelineTweets.contains(t4), "duyhai timeline contains 'tweet4'");

		// check tescolan's timeline
		when(mockAuthenticationService.getCurrentUser()).thenReturn(tescolan);

		Collection<Tweet> tescolanTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(tescolanTimelineTweets.size(), 0, "tescolan timeline contains 0 tweet");
	}

	@Test(dependsOnMethods = "testOnRemoveTweetSpreadTweetPipelineTest")
	public void testOnRemoveTweetNoActionTweetPipelineTest() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		Collection<Tweet> jduboisTimelineTweets = this.timelineService.getTimelineRange(null, 10);

		Tweet alertTweet = jduboisTimelineTweets.iterator().next();

		assertTrue(alertTweet.getContent().contains("<strong>is now following you</strong>"), "first jdubois timeline tweet is an alert tweet");

		this.tweetPipelineManager.onRemove(alertTweet.getTweetId());

		// jdubois's timeline contains 5 tweets: 3 from previous tests, 2 from follow notification
		jduboisTimelineTweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(jduboisTimelineTweets.size(), 4, "jdubois timeline contains 4 tweet");
		assertFalse(jduboisTimelineTweets.contains(alertTweet), "jdubois timeline contains 'alertTweet'");
	}

	@Test(dependsOnMethods = "testOnRemoveTweetNoActionTweetPipelineTest", expectedExceptions = FunctionalException.class)
	public void testOnRemoveTweetExceptionTweetPipelineTest() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		// Exception because duyhai cannot remove jdubois tweet
		this.tweetPipelineManager.onRemove(t1.getTweetId());
	}

	@Test(dependsOnMethods = "testOnRemoveTweetExceptionTweetPipelineTest")
	public void testOnAddToFavorite() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetPipelineManager.onAddToFavorite(t1.getTweetId());
		this.tweetPipelineManager.onAddToFavorite(t2.getTweetId());
		this.tweetPipelineManager.onAddToFavorite(t3.getTweetId());

		Collection<String> favoriteTweets = this.favoriteRepository.findFavoritesForUser("jdubois");

		assertEquals(favoriteTweets.size(), 3, "favoriteTweets.size() == 3");
		assertTrue(favoriteTweets.contains(t1.getTweetId()), "favoriteTweets contains tweet1");
		assertTrue(favoriteTweets.contains(t2.getTweetId()), "favoriteTweets contains tweet2");
		assertTrue(favoriteTweets.contains(t3.getTweetId()), "favoriteTweets contains tweet3");

	}

	@Test(dependsOnMethods = "testOnAddToFavorite", expectedExceptions = FunctionalException.class)
	public void testOnAddToFavoriteWithException() throws FunctionalException
	{
		// Exception because t1 is already in favorites
		this.tweetPipelineManager.onAddToFavorite(t1.getTweetId());
	}

	@Test(dependsOnMethods = "testOnAddToFavoriteWithException")
	public void testOnRemoveFromFavorite() throws FunctionalException
	{
		this.tweetPipelineManager.onRemoveFromFavorite(t2.getTweetId());
		this.tweetPipelineManager.onRemoveFromFavorite(t3.getTweetId());

		Collection<String> favoriteTweets = this.favoriteRepository.findFavoritesForUser("jdubois");

		assertEquals(favoriteTweets.size(), 1, "favoriteTweets.size() == 1");
		assertTrue(favoriteTweets.contains(t1.getTweetId()), "favoriteTweets contains tweet1");
	}

	@Test(dependsOnMethods = "testOnRemoveFromFavorite", expectedExceptions = FunctionalException.class)
	public void testOnRemoveFromFavoriteWithException() throws FunctionalException
	{
		// Exception because t3 is already removed from favorites
		this.tweetPipelineManager.onRemoveFromFavorite(t3.getTweetId());
	}

}
