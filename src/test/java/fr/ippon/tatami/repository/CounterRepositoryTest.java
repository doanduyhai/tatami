package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import me.prettyprint.cassandra.model.thrift.ThriftCounterColumnQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;

public class CounterRepositoryTest extends AbstractCassandraTatamiTest
{

	@Test
	public void testCreateTweetCounter()
	{
		this.counterRepository.createTweetCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("TWEET_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(0), "TWEET_COUNTER value");
	}

	@Test
	public void testCreateFriendsCounter()
	{
		this.counterRepository.createFriendsCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("FRIENDS_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(0), "FRIENDS_COUNTER value");
	}

	@Test
	public void testCreateFollowersCounter()
	{
		this.counterRepository.createFollowersCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("FOLLOWERS_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(0), "FOLLOWERS_COUNTER value");
	}

	@Test(dependsOnMethods = "testCreateTweetCounter")
	public void testIncrementTweetCounter()
	{
		this.counterRepository.incrementTweetCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("TWEET_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(1), "TWEET_COUNTER value");
	}

	@Test(dependsOnMethods = "testCreateFriendsCounter")
	public void testIncrementFriendsCounter()
	{
		this.counterRepository.incrementFriendsCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("FRIENDS_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(1), "FRIENDS_COUNTER value");
	}

	@Test(dependsOnMethods = "testCreateFollowersCounter")
	public void testIncrementFollowersCounter()
	{
		this.counterRepository.incrementFollowersCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("FOLLOWERS_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(1), "FOLLOWERS_COUNTER value");
	}

	@Test(dependsOnMethods = "testIncrementTweetCounter")
	public void testDecrementTweetCounter()
	{
		this.counterRepository.decrementTweetCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("TWEET_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(0), "TWEET_COUNTER value");
	}

	@Test(dependsOnMethods = "testIncrementFriendsCounter")
	public void testDecrementFriendsCounter()
	{
		this.counterRepository.decrementFriendsCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("FRIENDS_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(0), "FRIENDS_COUNTER value");
	}

	@Test(dependsOnMethods = "testIncrementFollowersCounter")
	public void testDecrementFollowersCounter()
	{
		this.counterRepository.decrementFollowersCounter("test");

		Long result = new ThriftCounterColumnQuery<String, String>(keyspace, StringSerializer.get(), StringSerializer.get())
				.setColumnFamily(COUNTER_CF).setKey("test").setName("FOLLOWERS_COUNTER").execute().get().getValue();

		assertNotNull(result, "result");
		assertEquals(result, new Long(0), "FOLLOWERS_COUNTER value");
	}
}
