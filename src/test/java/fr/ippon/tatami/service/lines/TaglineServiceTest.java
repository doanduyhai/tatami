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

public class TaglineServiceTest extends AbstractCassandraTatamiTest
{
	private User jdubois;
	private Tweet t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, tweet, randomTweet;

	@Test
	public void initTagLineServiceTest()
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

		// #Java
		t1 = this.tweetService.createTransientTweet("tweet1 #Java");
		this.tweetRepository.saveTweet(t1);
		t2 = this.tweetService.createTransientTweet("tweet2 #Java");
		this.tweetRepository.saveTweet(t2);
		t3 = this.tweetService.createTransientTweet("tweet3 #Java");
		this.tweetRepository.saveTweet(t3);
		t4 = this.tweetService.createTransientTweet("tweet4 #Java");
		this.tweetRepository.saveTweet(t4);
		t5 = this.tweetService.createTransientTweet("tweet5 #Java");
		this.tweetRepository.saveTweet(t5);

		// #Cassandra
		t6 = this.tweetService.createTransientTweet("tweet6 #Cassandra");
		this.tweetRepository.saveTweet(t6);
		t7 = this.tweetService.createTransientTweet("tweet7 #Cassandra");
		this.tweetRepository.saveTweet(t7);
		t8 = this.tweetService.createTransientTweet("tweet8 #Cassandra");
		this.tweetRepository.saveTweet(t8);

		// #Spring
		t9 = this.tweetService.createTransientTweet("tweet9 #Spring");
		this.tweetRepository.saveTweet(t9);
		t10 = this.tweetService.createTransientTweet("tweet10 #Spring");
		this.tweetRepository.saveTweet(t10);

		this.tagLineRepository.addTweet("Java", t1.getTweetId());
		this.tagLineRepository.addTweet("Java", t2.getTweetId());
		this.tagLineRepository.addTweet("Java", t3.getTweetId());
		this.tagLineRepository.addTweet("Java", t4.getTweetId());
		this.tagLineRepository.addTweet("Java", t5.getTweetId());

		this.tagLineRepository.addTweet("Cassandra", t6.getTweetId());
		this.tagLineRepository.addTweet("Cassandra", t7.getTweetId());
		this.tagLineRepository.addTweet("Cassandra", t8.getTweetId());

		this.tagLineRepository.addTweet("Spring", t9.getTweetId());
		this.tagLineRepository.addTweet("Spring", t10.getTweetId());
	}

	@Test(dependsOnMethods = "initTagLineServiceTest")
	public void testGetTaglineRangeAll() throws FunctionalException
	{
		Collection<Tweet> tweets = this.taglineService.getTaglineRange("Java", null, 10);

		assertEquals(tweets.size(), 5, "5 tweets with #Java tag");
		assertTrue(tweets.contains(t1), "tweets contains 'tweet1'");
		assertTrue(tweets.contains(t2), "tweets contains 'tweet2'");
		assertTrue(tweets.contains(t3), "tweets contains 'tweet3'");
		assertTrue(tweets.contains(t4), "tweets contains 'tweet4'");
		assertTrue(tweets.contains(t5), "tweets contains 'tweet5'");
	}

	@Test(dependsOnMethods = "testGetTaglineRangeAll")
	public void testGetTaglineRangeOutOfBounds() throws FunctionalException
	{
		Collection<Tweet> tweets = this.taglineService.getTaglineRange("Java", t1.getTweetId(), 10);

		assertEquals(tweets.size(), 0, "0 tweets with #Java tag starting from t1 excluded");
	}

	@Test(dependsOnMethods = "testGetTaglineRangeOutOfBounds")
	public void testOnTweetPostSpreadTweetForTagLine() throws FunctionalException
	{
		tweet = this.tweetService.createTransientTweet("tweet11 &#x23;Tatami");
		this.tweetRepository.saveTweet(tweet);
		this.taglineService.onTweetPost(tweet);

		Collection<Tweet> tweets = this.taglineService.getTaglineRange("Tatami", null, 10);

		assertEquals(tweets.size(), 1, "1 tweets with #Tatami tag");
		assertTrue(tweets.contains(tweet), "tweets contains 'tweet11 &#x23;Tatami'");
	}

	@Test(dependsOnMethods = "testOnTweetPostSpreadTweetForTagLine")
	public void testOnTweetPostNoActionForTagLine() throws FunctionalException
	{
		randomTweet = this.tweetService.createTransientTweet("tweet12 Tatami");
		this.tweetRepository.saveTweet(randomTweet);
		this.taglineService.onTweetPost(randomTweet);

		Collection<Tweet> tweets = this.taglineService.getTaglineRange("Tatami", null, 10);

		assertEquals(tweets.size(), 1, "1 tweets with #Tatami tag");
	}
}
