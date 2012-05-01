package fr.ippon.tatami.service.lines;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
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
	private User jdubois;
	private Tweet t1, t2, t3, t4, t5, tweet;

	@Test
	public void initTimelineServiceTest()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.userService.setAuthenticationService(mockAuthenticationService);

		t1 = this.tweetService.createTransientTweet("tweet1");
		this.tweetRepository.saveTweet(t1);
		t2 = this.tweetService.createTransientTweet("tweet2");
		this.tweetRepository.saveTweet(t2);
		t3 = this.tweetService.createTransientTweet("tweet3");
		this.tweetRepository.saveTweet(t3);
		t4 = this.tweetService.createTransientTweet("tweet4");
		this.tweetRepository.saveTweet(t4);
		t5 = this.tweetService.createTransientTweet("tweet5");
		this.tweetRepository.saveTweet(t5);

		this.timeLineRepository.addTweetToTimeline(jdubois, t1.getTweetId());
		this.timeLineRepository.addTweetToTimeline(jdubois, t2.getTweetId());
		this.timeLineRepository.addTweetToTimeline(jdubois, t3.getTweetId());
		this.timeLineRepository.addTweetToTimeline(jdubois, t4.getTweetId());
		this.timeLineRepository.addTweetToTimeline(jdubois, t5.getTweetId());
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
}