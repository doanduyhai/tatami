package fr.ippon.tatami.service.lines;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class TimelineServiceTest extends AbstractCassandraTatamiTest
{
	private User jdubois, duyhai;
	private Tweet t1, t2, t3, t4, t5, tweet, duyhaiTweet1, duyhaiTweet2;

	@Test
	public void initTimelineServiceTest() throws FunctionalException
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

		this.userRepository.createUser(jdubois);
		this.userRepository.createUser(duyhai);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.userService.setAuthenticationService(mockAuthenticationService);

		t1 = this.tweetRepository.createTweet("jdubois", "tweet1", false);
		t2 = this.tweetRepository.createTweet("jdubois", "tweet2", false);
		t3 = this.tweetRepository.createTweet("jdubois", "tweet3", false);
		t4 = this.tweetRepository.createTweet("jdubois", "tweet4", false);
		t5 = this.tweetRepository.createTweet("jdubois", "tweet5", false);

		this.timeLineRepository.addTweetToTimeline("jdubois", t1.getTweetId());
		this.timeLineRepository.addTweetToTimeline("jdubois", t2.getTweetId());
		this.timeLineRepository.addTweetToTimeline("jdubois", t3.getTweetId());
		this.timeLineRepository.addTweetToTimeline("jdubois", t4.getTweetId());
		this.timeLineRepository.addTweetToTimeline("jdubois", t5.getTweetId());
	}

	@Test(dependsOnMethods = "initTimelineServiceTest")
	public void testGetTimelineRangeAll() throws FunctionalException
	{
		Collection<Tweet> tweets = this.timelineService.getTimelineRange(null, 10);

		assertEquals(tweets.size(), 5, "5 tweets in jdubois timeline");
		assertTrue(tweets.contains(t1), "tweets contains 'tweet1'");
		assertTrue(tweets.contains(t2), "tweets contains 'tweet2'");
		assertTrue(tweets.contains(t3), "tweets contains 'tweet3'");
		assertTrue(tweets.contains(t4), "tweets contains 'tweet4'");
		assertTrue(tweets.contains(t5), "tweets contains 'tweet5'");
	}

	@Test(dependsOnMethods = "testGetTimelineRangeAll")
	public void testGetTimelineRangeOutOfBounds() throws FunctionalException
	{
		Collection<Tweet> tweets = this.timelineService.getTimelineRange(t1.getTweetId(), 10);

		assertEquals(tweets.size(), 0, "0 tweet in jdubois timeline after tweet1");
	}

	@Test(dependsOnMethods = "testGetTimelineRangeOutOfBounds")
	public void testOnPostTweetForTimeline() throws FunctionalException
	{
		tweet = this.tweetService.createTransientTweet("tweet6");
		this.tweetRepository.saveTweet(tweet);
		this.timelineService.onTweetPost(tweet);

		Collection<Tweet> tweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(tweets.size(), 6, "6 tweets in jdubois timeline");
		assertTrue(tweets.contains(tweet), "tweets contains 'tweet6'");
	}

	@Test(dependsOnMethods = "testOnPostTweetForTimeline")
	public void testOnRemoveTweetForTimeline() throws FunctionalException
	{
		this.timelineService.onTweetRemove(tweet);

		Collection<Tweet> tweets = this.timelineService.getTimelineRange(null, 10);
		assertEquals(tweets.size(), 5, "5 tweets in jdubois timeline");
		assertFalse(tweets.contains(tweet), "tweets contains 'tweet6'");
	}

	@Test(dependsOnMethods = "testOnRemoveTweetForTimeline")
	public void testOnUserFollowForTimelineService() throws FunctionalException
	{
		this.timelineService.onUserFollow("duyhai");
		this.contactsService.onUserFollow("duyhai");

		Collection<String> duyhaiTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);

		assertEquals(duyhaiTimelineTweets.size(), 1, "duyhaiTimelineTweets.size() == 1");

		duyhai = userService.getUserByLogin("duyhai");
		mockAuthenticatedUser(duyhai);

		duyhaiTweet1 = this.tweetRepository.createTweet("duyhai", "duyhai tweet1", false);
		duyhaiTweet2 = this.tweetRepository.createTweet("duyhai", "duyhai tweet2", false);

		this.contactsService.onTweetPost(duyhaiTweet1);
		this.contactsService.onTweetPost(duyhaiTweet2);

		Collection<String> tweets = this.timeLineRepository.getTweetsRangeFromTimeline("jdubois", null, 10);
		assertEquals(tweets.size(), 7, "7 tweets in jdubois timeline");
		assertTrue(tweets.contains(duyhaiTweet1.getTweetId()), "tweets contains 'duyhai tweet1'");
		assertTrue(tweets.contains(duyhaiTweet2.getTweetId()), "tweets contains 'duyhai tweet2'");
	}

	@Test(dependsOnMethods = "testOnUserFollowForTimelineService", expectedExceptions = FunctionalException.class)
	public void testOnUserFollowSelfForTimelineService() throws FunctionalException
	{
		jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);
		this.timelineService.onUserFollow("jdubois");
	}

	@Test(dependsOnMethods = "testOnUserFollowSelfForTimelineService")
	public void testOnUserForgetForTimelineService() throws FunctionalException
	{
		jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		this.timelineService.onUserForget("duyhai");

		Collection<String> tweets = this.timeLineRepository.getTweetsRangeFromTimeline("jdubois", null, 10);
		assertEquals(tweets.size(), 5, "5 tweets in jdubois timeline");
		assertFalse(tweets.contains(duyhaiTweet1.getTweetId()), "tweets contains 'duyhai tweet1'");
		assertFalse(tweets.contains(duyhaiTweet2.getTweetId()), "tweets contains 'duyhai tweet2'");

	}

	@Test(dependsOnMethods = "testOnUserForgetForTimelineService", expectedExceptions = FunctionalException.class)
	public void testOnUserForgetSelfForTimelineService() throws FunctionalException
	{
		jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);
		this.timelineService.onUserForget("jdubois");
	}

	@Test(dependsOnMethods = "testOnUserForgetSelfForTimelineService")
	public void testOnAddToFavoriteForTimelineServiceTest() throws FunctionalException
	{
		jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		this.timelineService.onAddToFavorite(duyhaiTweet1);

		Collection<String> duyhaiTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);

		assertEquals(duyhaiTimelineTweets.size(), 2, "duyhaiTimelineTweets.size() == 2");

	}
}