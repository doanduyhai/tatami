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

public class UserlineServiceTest extends AbstractCassandraTatamiTest
{
	private User jdubois;
	private Tweet t1, t2, t3, t4, t5, tweet;

	@Test
	public void initUserlineServiceTest()
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

		this.userLineRepository.addTweetToUserline(jdubois, t1.getTweetId());
		this.userLineRepository.addTweetToUserline(jdubois, t2.getTweetId());
		this.userLineRepository.addTweetToUserline(jdubois, t3.getTweetId());
		this.userLineRepository.addTweetToUserline(jdubois, t4.getTweetId());
		this.userLineRepository.addTweetToUserline(jdubois, t5.getTweetId());
	}

	@Test(dependsOnMethods = "initUserlineServiceTest")
	public void testGetUserlineRangeAll() throws FunctionalException
	{
		Collection<Tweet> tweets = this.userlineService.getUserlineRange("jdubois", null, 10);

		assertEquals(tweets.size(), 5, "5 tweets in jdubois userline");
		assertTrue(tweets.contains(t1), "tweets contains 'tweet1'");
		assertTrue(tweets.contains(t2), "tweets contains 'tweet2'");
		assertTrue(tweets.contains(t3), "tweets contains 'tweet3'");
		assertTrue(tweets.contains(t4), "tweets contains 'tweet4'");
		assertTrue(tweets.contains(t5), "tweets contains 'tweet5'");
	}

	@Test(dependsOnMethods = "testGetUserlineRangeAll")
	public void testGetUserlineRangeOutOfBounds() throws FunctionalException
	{
		Collection<Tweet> tweets = this.userlineService.getUserlineRange("jdubois", t1.getTweetId(), 10);

		assertEquals(tweets.size(), 0, "0 tweet in jdubois userline after tweet1");
	}

	@Test(dependsOnMethods = "testGetUserlineRangeOutOfBounds")
	public void testOnPostTweetForUserline() throws FunctionalException
	{
		tweet = this.tweetService.createTransientTweet("tweet6");
		this.tweetRepository.saveTweet(tweet);
		this.userlineService.onTweetPost(tweet);

		Collection<Tweet> tweets = this.userlineService.getUserlineRange("jdubois", null, 10);
		assertEquals(tweets.size(), 6, "6 tweets in jdubois userline");
		assertTrue(tweets.contains(tweet), "tweets contains 'tweet6'");
	}
}
