package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITELINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITE_INDEX_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITE_TWEET_USER_INDEX_CF;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_COUNTER;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_INDEX_COUNTER;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_USER_INDEX_COUNTER;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import me.prettyprint.cassandra.model.thrift.ThriftCounterColumnQuery;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HCounterColumn;
import me.prettyprint.hector.api.query.CounterQuery;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;

public class FavoriteRepositoryTest extends AbstractCassandraTatamiTest
{
	private User user;

	private Tweet tweet1, tweet2, tweet3, tweet4, tweet5, t1, t2, t3;

	@Test
	public void testAddFavorite()
	{
		user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		tweet1 = this.tweetRepository.createTweet("test", "tweet1", false);
		tweet2 = this.tweetRepository.createTweet("test", "tweet2", false);
		tweet3 = this.tweetRepository.createTweet("test", "tweet3", false);
		tweet4 = this.tweetRepository.createTweet("test", "tweet4", false);
		tweet5 = this.tweetRepository.createTweet("test", "tweet5", false);

		this.favoriteRepository.addFavorite("test", tweet1.getTweetId());
		this.favoriteRepository.addFavorite("test", tweet2.getTweetId());
		this.favoriteRepository.addFavorite("test", tweet3.getTweetId());
		this.favoriteRepository.addFavorite("test", tweet4.getTweetId());
		this.favoriteRepository.addFavorite("test", tweet5.getTweetId());

		// User refreshedUser = this.userRepository.findUserByLogin("test");

		List<String> userFavorites = new ArrayList<String>();

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FAVORITELINE_CF).setKey(user.getLogin())
				.setRange(null, null, true, 100).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			userFavorites.add(column.getName());
		}

		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspace, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(FAVORITE_TWEET_COUNTER).setName(user.getLogin());

		HCounterColumn<String> counterColumn = counter.execute().get();

		long count;
		if (counterColumn == null)
		{
			count = 0;
		}
		else
		{
			count = counterColumn.getValue();
		}

		assertTrue(userFavorites.size() == 5, "userFavorites.size() == 5");
		assertTrue(count == 5, "refreshedUser.getFavoritesCount() == 5");
		assertTrue(userFavorites.contains(tweet1.getTweetId()), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains(tweet2.getTweetId()), "refreshedUser has tweet2 as favorite");
		assertTrue(userFavorites.contains(tweet3.getTweetId()), "refreshedUser has tweet3 as favorite");
		assertTrue(userFavorites.contains(tweet4.getTweetId()), "refreshedUser has tweet4 as favorite");
		assertTrue(userFavorites.contains(tweet5.getTweetId()), "refreshedUser has tweet5 as favorite");
	}

	@Test(dependsOnMethods = "testAddFavorite")
	public void testFindFavoritesForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesForUser("test");

		assertTrue(userFavorites.size() == 5, "userFavorites.size() == 5");

		assertTrue(userFavorites.contains(tweet1.getTweetId()), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains(tweet2.getTweetId()), "refreshedUser has tweet2 as favorite");
		assertTrue(userFavorites.contains(tweet3.getTweetId()), "refreshedUser has tweet3 as favorite");
		assertTrue(userFavorites.contains(tweet4.getTweetId()), "refreshedUser has tweet4 as favorite");
		assertTrue(userFavorites.contains(tweet5.getTweetId()), "refreshedUser has tweet5 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesForUser")
	public void testFindFavoritesRangeForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser("test", tweet3.getTweetId(), 2);

		assertTrue(userFavorites.size() == 2, "userFavorites.size() == 2");
		assertTrue(userFavorites.contains(tweet1.getTweetId()), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains(tweet2.getTweetId()), "refreshedUser has tweet2 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesRangeForUser")
	public void testFindFavoritesRangeOutOfBoundsForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser("test", tweet2.getTweetId(), 10);

		assertTrue(userFavorites.size() == 1, "userFavorites.size() == 1");
		assertTrue(userFavorites.contains(tweet1.getTweetId()), "refreshedUser has tweet1 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesRangeOutOfBoundsForUser")
	public void testFindFavoritesRangeBoundsLimitForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser("test", "000", 10);

		assertTrue(userFavorites.size() == 0, "userFavorites.size() == 0");

	}

	@Test(dependsOnMethods = "testFindFavoritesRangeOutOfBoundsForUser")
	public void testRemoveFavorite()
	{
		this.favoriteRepository.removeFavorite("test", tweet1.getTweetId());
		this.favoriteRepository.removeFavorite("test", tweet2.getTweetId());
		this.favoriteRepository.removeFavorite("test", tweet3.getTweetId());
		this.favoriteRepository.removeFavorite("test", tweet4.getTweetId());
		this.favoriteRepository.removeFavorite("test", tweet5.getTweetId());

		// User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesForUser("test");

		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspace, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(FAVORITE_TWEET_COUNTER).setName(user.getLogin());

		HCounterColumn<String> counterColumn = counter.execute().get();

		long count;
		if (counterColumn == null)
		{
			count = 0;
		}
		else
		{
			count = counterColumn.getValue();
		}

		assertTrue(userFavorites.size() == 0, "userFavorites.size()==0");
		assertTrue(count == 0, "refreshedUser.getFavoritesCount()==0");
	}

	@SuppressWarnings("unchecked")
	@Test(dependsOnMethods = "testRemoveFavorite")
	public void testAddTweetToIndexForFavorite()
	{
		t1 = this.tweetRepository.createTweet("jdubois", "tweet1", false);
		t2 = this.tweetRepository.createTweet("jdubois", "tweet2", false);
		t3 = this.tweetRepository.createTweet("jdubois", "tweet3", false);

		this.favoriteRepository.addTweetToFavoriteIndex("jdubois", t1.getLogin(), t1.getTweetId());
		this.favoriteRepository.addTweetToFavoriteIndex("duyhai", t1.getLogin(), t1.getTweetId());
		this.favoriteRepository.addTweetToFavoriteIndex("duyhai", t2.getLogin(), t2.getTweetId());
		this.favoriteRepository.addTweetToFavoriteIndex("duyhai", t3.getLogin(), t3.getTweetId());
		this.favoriteRepository.addTweetToFavoriteIndex("tescolan", t1.getLogin(), t1.getTweetId());
		this.favoriteRepository.addTweetToFavoriteIndex("test", t1.getLogin(), t1.getTweetId());

		long favoriteTweetCount = this.getCounterValue(FAVORITE_TWEET_INDEX_COUNTER, t1.getTweetId());
		assertEquals(favoriteTweetCount, 4, "4 users for tweet1 in favorite tweet counter");

		Collection<String> userLogins = this.findRangeFromCF(FAVORITE_INDEX_CF, t1.getTweetId(), null, false, 10);

		assertTrue(userLogins.contains("jdubois"), "userLogins contains jdubois");
		assertTrue(userLogins.contains("duyhai"), "userLogins contains duyhai");
		assertTrue(userLogins.contains("tescolan"), "userLogins contains tescolan");
		assertTrue(userLogins.contains("test"), "userLogins contains test");

		Set<String> duyHaiJduboisFavorites = (Set<String>) this.getValueFromCF(FAVORITE_TWEET_USER_INDEX_CF, "duyhai", "jdubois");
		assertTrue(duyHaiJduboisFavorites.contains(t1.getTweetId()), "duyhai:jdubois favorite set contains tweet1");
		assertTrue(duyHaiJduboisFavorites.contains(t2.getTweetId()), "duyhai:jdubois favorite set contains tweet2");
		assertTrue(duyHaiJduboisFavorites.contains(t3.getTweetId()), "duyhai:jdubois favorite set contains tweet3");

		Set<String> tescolanJduboisFavorites = (Set<String>) this.getValueFromCF(FAVORITE_TWEET_USER_INDEX_CF, "tescolan", "jdubois");
		assertTrue(tescolanJduboisFavorites.contains(t1.getTweetId()), "tescolan:jdubois favorite set contains tweet1");

		Set<String> testJduboisFavorites = (Set<String>) this.getValueFromCF(FAVORITE_TWEET_USER_INDEX_CF, "test", "jdubois");
		assertTrue(testJduboisFavorites.contains(t1.getTweetId()), "test:jdubois favorite set contains tweet1");

		long duyhaiJduboisFavoriteCount = this.getCounterValue(FAVORITE_TWEET_USER_INDEX_COUNTER, "duyhai" + LOGIN_SEPARATOR + "jdubois");
		assertEquals(duyhaiJduboisFavoriteCount, 3, "3 tweet for duyhai:jdubois favorite counter");

		long tescolanJduboisFavoriteCount = this.getCounterValue(FAVORITE_TWEET_USER_INDEX_COUNTER, "tescolan" + LOGIN_SEPARATOR + "jdubois");
		assertEquals(tescolanJduboisFavoriteCount, 1, "1 tweet for tescolan:jdubois favorite counter");

		long testJduboisFavoriteCount = this.getCounterValue(FAVORITE_TWEET_USER_INDEX_COUNTER, "test" + LOGIN_SEPARATOR + "jdubois");
		assertEquals(testJduboisFavoriteCount, 1, "1 tweet for test:jdubois favorite counter");
	}

	@Test(dependsOnMethods = "testAddTweetToIndexForFavorite")
	public void testGetUsersForTweetFromIndexForFavorite()
	{
		Collection<String> userLogins = this.favoriteRepository.getUsersForTweetFromIndex(t1.getTweetId());

		assertEquals(userLogins.size(), 4, "4 users for tweet1 in favorite tweet index");
		assertTrue(userLogins.contains("jdubois"), "userLogins contains jdubois");
		assertTrue(userLogins.contains("duyhai"), "userLogins contains duyhai");
		assertTrue(userLogins.contains("tescolan"), "userLogins contains tescolan");
		assertTrue(userLogins.contains("test"), "userLogins contains test");
	}

	@Test(dependsOnMethods = "testGetUsersForTweetFromIndexForFavorite")
	public void testGetTweetsForUserFromIndexForFavorite()
	{
		Collection<String> tweetsForDuyHai = this.favoriteRepository.getTweetsForUserFromIndex("duyhai", "jdubois");
		assertEquals(tweetsForDuyHai.size(), 3, "duyhai:jdubois favorite set size == 3");
		assertTrue(tweetsForDuyHai.contains(t1.getTweetId()), "duyhai:jdubois favorite set contains tweet1");
		assertTrue(tweetsForDuyHai.contains(t2.getTweetId()), "duyhai:jdubois favorite set contains tweet2");
		assertTrue(tweetsForDuyHai.contains(t3.getTweetId()), "duyhai:jdubois favorite set contains tweet3");
	}

	@SuppressWarnings("unchecked")
	@Test(dependsOnMethods = "testGetTweetsForUserFromIndexForFavorite")
	public void testRemoveTweetFromFavoriteIndexForFavorite()
	{
		this.favoriteRepository.removeTweetFromFavoriteIndex("duyhai", t1.getLogin(), t1.getTweetId());
		this.favoriteRepository.removeTweetFromFavoriteIndex("tescolan", t1.getLogin(), t1.getTweetId());

		long tweet1FavoriteCount = this.getCounterValue(FAVORITE_TWEET_INDEX_COUNTER, t1.getTweetId());
		assertEquals(tweet1FavoriteCount, 2, "2 users for tweet1 in favorite tweet counter");

		Collection<String> userLogins = this.findRangeFromCF(FAVORITE_INDEX_CF, t1.getTweetId(), null, false, 10);

		assertEquals(userLogins.size(), 2, "userLogins contains 2 users");
		assertTrue(userLogins.contains("jdubois"), "userLogins contains jdubois");
		assertTrue(userLogins.contains("test"), "userLogins contains test");

		Set<String> duyHaiJduboisFavorites = (Set<String>) this.getValueFromCF(FAVORITE_TWEET_USER_INDEX_CF, "duyhai", "jdubois");
		assertEquals(duyHaiJduboisFavorites.size(), 2, "duyhai:jdubois favorite set size is now == 2");
		assertTrue(duyHaiJduboisFavorites.contains(t2.getTweetId()), "duyhai:jdubois favorite set contains tweet2");
		assertTrue(duyHaiJduboisFavorites.contains(t3.getTweetId()), "duyhai:jdubois favorite set contains tweet3");

		long duyHaiJduboisFavoritesCount = this.getCounterValue(FAVORITE_TWEET_USER_INDEX_COUNTER, "duyhai" + LOGIN_SEPARATOR + "jdubois");
		assertEquals(duyHaiJduboisFavoritesCount, 2, "2 tweet for duyhai:jdubois favorite counter");

	}

	@SuppressWarnings("unchecked")
	@Test(dependsOnMethods = "testRemoveTweetFromFavoriteIndexForFavorite")
	public void testRemoveIndexForTweetForFavorite()
	{
		this.favoriteRepository.removeIndexForTweet("test", "jdubois", t1.getTweetId());

		Collection<String> userLogins = this.findRangeFromCF(FAVORITE_INDEX_CF, t1.getTweetId(), null, false, 10);
		assertEquals(userLogins.size(), 0, "0 users for tweet1 in favorite tweet index");

		long tweet1FavoriteCount = this.getCounterValue(FAVORITE_TWEET_INDEX_COUNTER, t1.getTweetId());
		assertEquals(tweet1FavoriteCount, 0, "no more tweet index counter for tweet1");

		Set<String> testJduboisFavorites = (Set<String>) this.getValueFromCF(FAVORITE_TWEET_USER_INDEX_CF, "test", "jdubois");
		assertNull(testJduboisFavorites, "no more tweet user index for test:jdubois");

		long testJduboisTweetCount = this.getCounterValue(FAVORITE_TWEET_USER_INDEX_COUNTER, "test" + LOGIN_SEPARATOR + "jdubois");
		assertEquals(testJduboisTweetCount, 0, "no more tweet user index counter for test:jdubois");
	}

}
