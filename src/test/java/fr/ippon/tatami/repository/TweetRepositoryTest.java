package fr.ippon.tatami.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class TweetRepositoryTest extends AbstractCassandraTatamiTest
{

	private String newTweetId;

	@Test
	public void testCreateTweet()
	{
		Tweet newTweet = this.tweetRepository.createTweet("test", "My First tweet to #jdubois", false);

		assertNotNull(newTweet, "newTweet");

		this.newTweetId = newTweet.getTweetId();
		Tweet tweet = this.entityManager.find(Tweet.class, newTweet.getTweetId());

		assertNotNull(tweet, "tweet");
		assertEquals(tweet.getLogin(), "test", "Login");
		assertEquals(tweet.getContent(), "My First tweet to #jdubois", "Content");
		assertEquals(tweet.getTweetDate(), newTweet.getTweetDate(), "Tweet date");
	}

	@Test(dependsOnMethods = "testCreateTweet")
	public void testFindTweetById()
	{
		Tweet tweet = this.tweetRepository.findTweetById(newTweetId);
		assertNotNull(tweet, "tweet");
		assertEquals(tweet.getLogin(), "test", "Login");
		assertEquals(tweet.getContent(), "My First tweet to #jdubois", "Content");

	}

	@Test(dependsOnMethods = "testFindTweetById")
	public void testDeleteTweet()
	{
		Tweet tweet = this.tweetRepository.findTweetById(newTweetId);

		this.tweetRepository.removeTweet(tweet);

		Tweet removedTweet = this.tweetRepository.findTweetById(newTweetId);

		assertNull(removedTweet, "removedTweet");

	}
}
