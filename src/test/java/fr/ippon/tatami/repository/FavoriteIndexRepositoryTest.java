package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITE_INDEX_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.cassandra.model.thrift.ThriftCounterColumnQuery;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.query.CounterQuery;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.repository.cassandra.CassandraFavoriteIndexRepository;

public class FavoriteIndexRepositoryTest extends AbstractCassandraTatamiTest
{
	private Tweet t1;

	@Test
	public void testAddTweetToFavoriteIndex()
	{
		t1 = this.tweetRepository.createTweet("jdubois", "tweet1", false);

		this.favoriteIndexRepository.addTweetToFavoriteIndex("jdubois", t1.getTweetId());
		this.favoriteIndexRepository.addTweetToFavoriteIndex("duyhai", t1.getTweetId());
		this.favoriteIndexRepository.addTweetToFavoriteIndex("tescolan", t1.getTweetId());
		this.favoriteIndexRepository.addTweetToFavoriteIndex("test", t1.getTweetId());

		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspace, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(CassandraFavoriteIndexRepository.FAVORITE_TWEET_INDEX_COUNTER).setName(t1.getTweetId());
		counter.execute().get().getValue();
		assertEquals(counter.execute().get().getValue().longValue(), 4, "4 users for tweet1 in favorite tweet counter");

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FAVORITE_INDEX_CF).setKey(t1.getTweetId())
				.setRange(null, null, false, 10).execute().get().getColumns();

		assertEquals(columns.size(), 4, "4 users for tweet1 in favorite tweet index");

		List<String> userLogins = new ArrayList<String>();

		for (HColumn<String, Object> column : columns)
		{
			userLogins.add(column.getName());
		}

		assertTrue(userLogins.contains("jdubois"), "userLogins contains jdubois");
		assertTrue(userLogins.contains("duyhai"), "userLogins contains duyhai");
		assertTrue(userLogins.contains("tescolan"), "userLogins contains tescolan");
		assertTrue(userLogins.contains("test"), "userLogins contains test");
	}

	@Test(dependsOnMethods = "testAddTweetToFavoriteIndex")
	public void testGetUsersForTweetFromIndex()
	{
		Collection<String> userLogins = this.favoriteIndexRepository.getUsersForTweetFromIndex(t1.getTweetId());

		assertEquals(userLogins.size(), 4, "4 users for tweet1 in favorite tweet index");
		assertTrue(userLogins.contains("jdubois"), "userLogins contains jdubois");
		assertTrue(userLogins.contains("duyhai"), "userLogins contains duyhai");
		assertTrue(userLogins.contains("tescolan"), "userLogins contains tescolan");
		assertTrue(userLogins.contains("test"), "userLogins contains test");
	}

	@Test(dependsOnMethods = "testGetUsersForTweetFromIndex")
	public void testRemoveTweetFromFavoriteIndex()
	{
		this.favoriteIndexRepository.removeTweetFromFavoriteIndex("duyhai", t1.getTweetId());
		this.favoriteIndexRepository.removeTweetFromFavoriteIndex("tescolan", t1.getTweetId());

		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspace, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(CassandraFavoriteIndexRepository.FAVORITE_TWEET_INDEX_COUNTER).setName(t1.getTweetId());
		counter.execute().get().getValue();
		assertEquals(counter.execute().get().getValue().longValue(), 2, "2 users for tweet1 in favorite tweet counter");

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FAVORITE_INDEX_CF).setKey(t1.getTweetId())
				.setRange(null, null, false, 10).execute().get().getColumns();

		assertEquals(columns.size(), 2, "2 users for tweet1 in favorite tweet index");

		List<String> userLogins = new ArrayList<String>();

		for (HColumn<String, Object> column : columns)
		{
			userLogins.add(column.getName());
		}

		assertTrue(userLogins.contains("jdubois"), "userLogins contains jdubois");
		assertTrue(userLogins.contains("test"), "userLogins contains test");
	}

	@Test(dependsOnMethods = "testRemoveTweetFromFavoriteIndex")
	public void testRemoveIndexForTweet()
	{
		this.favoriteIndexRepository.removeIndexForTweet(t1.getTweetId());

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FAVORITE_INDEX_CF).setKey(t1.getTweetId())
				.setRange(null, null, false, 10).execute().get().getColumns();

		assertEquals(columns.size(), 0, "0 users for tweet1 in favorite tweet index");

		columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(COUNTER_CF)
				.setKey(CassandraFavoriteIndexRepository.FAVORITE_TWEET_INDEX_COUNTER).setRange(null, null, false, 10).execute().get().getColumns();

		assertEquals(columns.size(), 0, "0 users for tweet1 in favorite tweet counter");
	}
}
