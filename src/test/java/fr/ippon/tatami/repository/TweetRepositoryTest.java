package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.DAYLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TAGLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TIMELINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.USERLINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Collection;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.ColumnSlice;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class TweetRepositoryTest extends AbstractCassandraTatamiTest
{

	private String newTweetId;

	private static final SimpleDateFormat DAYLINE_KEY_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@Test
	public void testCreateTweet()
	{
		Tweet newTweet = this.tweetRepository.createTweet("test", "My First tweet to #jdubois");

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

	@Test(dependsOnMethods = "testCreateTweet")
	public void testAddTweetToUserline()
	{
		Tweet tweet = this.tweetRepository.findTweetById(newTweetId);
		this.tweetRepository.addTweetToUserline(tweet);

		ColumnSlice<Long, String> result = createSliceQuery(keyspace, StringSerializer.get(), LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(USERLINE_CF).setKey("test").setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() > 0, " result size > 0");
		assertEquals(result.getColumns().get(0).getValue(), tweet.getTweetId(), "tweetId");
	}

	@Test(dependsOnMethods = "testCreateTweet")
	public void testAddTweetToTimeline()
	{
		Tweet tweet = this.tweetRepository.findTweetById(newTweetId);
		this.tweetRepository.addTweetToTimeline("test", tweet);

		ColumnSlice<Long, String> result = createSliceQuery(keyspace, StringSerializer.get(), LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(TIMELINE_CF).setKey("test").setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() > 0, " result size > 0");
		assertEquals(result.getColumns().get(0).getValue(), tweet.getTweetId(), "tweetId");
	}

	@Test(dependsOnMethods = "testCreateTweet")
	public void testAddTweetToFavoriteline()
	{
		Tweet tweet = this.tweetRepository.findTweetById(newTweetId);
		this.tweetRepository.addTweetToFavoritesline(tweet, "test");

		ColumnSlice<Long, String> result = createSliceQuery(keyspace, StringSerializer.get(), LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(FAVLINE_CF).setKey("test").setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() > 0, " result size > 0");
		assertEquals(result.getColumns().get(0).getValue(), tweet.getTweetId(), "tweetId");
	}

	@Test(dependsOnMethods = "testCreateTweet")
	public void testAddTweetToDayline()
	{
		Tweet tweet = this.tweetRepository.findTweetById(newTweetId);
		String key = DAYLINE_KEY_FORMAT.format(tweet.getTweetDate());
		this.tweetRepository.addTweetToDayline(tweet, key);

		ColumnSlice<Long, String> result = createSliceQuery(keyspace, StringSerializer.get(), LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(DAYLINE_CF).setKey(key).setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() > 0, " result size > 0");
		assertEquals(result.getColumns().get(0).getValue(), tweet.getTweetId(), "tweetId");
	}

	@Test(dependsOnMethods = "testCreateTweet")
	public void testAddTweetToTagline()
	{
		Tweet tweet = this.tweetRepository.findTweetById(newTweetId);
		this.tweetRepository.addTweetToTagline(tweet);

		ColumnSlice<Long, String> result = createSliceQuery(keyspace, StringSerializer.get(), LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(TAGLINE_CF).setKey("jdubois").setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() > 0, " result size > 0");
		assertEquals(result.getColumns().get(0).getValue(), tweet.getTweetId(), "tweetId");
	}

	@Test(dependsOnMethods = "testAddTweetToUserline")
	public void testGetUserline()
	{
		Collection<String> tweets = this.tweetRepository.getUserline("test", 1);

		assertNotNull(tweets, "tweets");
		assertTrue(tweets.size() > 0, "tweets.size() >0");

		Tweet tweet = this.entityManager.find(Tweet.class, tweets.iterator().next());
		assertEquals(tweet.getLogin(), "test", "tweet.getLogin()");
		assertEquals(tweet.getContent(), "My First tweet to #jdubois", "tweet.getContent()");
	}

	@Test(dependsOnMethods = "testAddTweetToTimeline")
	public void testGetTimeline()
	{
		Collection<String> tweets = this.tweetRepository.getTimeline("test", 1);

		assertNotNull(tweets, "tweets");
		assertTrue(tweets.size() > 0, "tweets.size() >0");

		Tweet tweet = this.entityManager.find(Tweet.class, tweets.iterator().next());
		assertEquals(tweet.getLogin(), "test", "tweet.getLogin()");
		assertEquals(tweet.getContent(), "My First tweet to #jdubois", "tweet.getContent()");
	}

	@Test(dependsOnMethods = "testAddTweetToFavoriteline")
	public void testGetFavoriteline()
	{
		Collection<String> tweets = this.tweetRepository.getFavoritesline("test");

		assertNotNull(tweets, "tweets");
		assertTrue(tweets.size() > 0, "tweets.size() >0");

		Tweet tweet = this.entityManager.find(Tweet.class, tweets.iterator().next());
		assertEquals(tweet.getLogin(), "test", "tweet.getLogin()");
		assertEquals(tweet.getContent(), "My First tweet to #jdubois", "tweet.getContent()");
	}

	@Test(dependsOnMethods = "testAddTweetToDayline")
	public void testGetDayline()
	{
		Tweet newTweet = this.tweetRepository.findTweetById(newTweetId);

		String key = DAYLINE_KEY_FORMAT.format(newTweet.getTweetDate());
		Collection<String> tweets = this.tweetRepository.getDayline(key);

		assertNotNull(tweets, "tweets");
		assertTrue(tweets.size() > 0, "tweets.size() >0");

		Tweet tweet = this.entityManager.find(Tweet.class, tweets.iterator().next());
		assertEquals(tweet.getLogin(), "test", "tweet.getLogin()");
		assertEquals(tweet.getContent(), "My First tweet to #jdubois", "tweet.getContent()");
	}

	@Test(dependsOnMethods = "testAddTweetToTagline")
	public void testGetTagline()
	{
		Collection<String> tweets = this.tweetRepository.getTagline("jdubois", 1);

		assertNotNull(tweets, "tweets");
		assertTrue(tweets.size() > 0, "tweets.size() >0");

		Tweet tweet = this.entityManager.find(Tweet.class, tweets.iterator().next());
		assertEquals(tweet.getLogin(), "test", "tweet.getLogin()");
		assertEquals(tweet.getContent(), "My First tweet to #jdubois", "tweet.getContent()");
	}
}
