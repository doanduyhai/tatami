package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITE_INDEX_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITE_TWEET_USER_INDEX_CF;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_INDEX_COUNTER;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_USER_INDEX_COUNTER;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class FavoriteIndexRepositoryTest extends AbstractCassandraTatamiTest
{
	private Tweet t1, t2, t3;

	@SuppressWarnings("unchecked")
	@Test
	public void testAddTweetToIndexForFavorite()
	{
		t1 = this.tweetRepository.createTweet("jdubois", "tweet1", false);
		t2 = this.tweetRepository.createTweet("jdubois", "tweet2", false);
		t3 = this.tweetRepository.createTweet("jdubois", "tweet3", false);

		this.favoriteTweetIndexRepository.addTweetToFavoriteIndex("jdubois", t1.getLogin(), t1.getTweetId());
		this.favoriteTweetIndexRepository.addTweetToFavoriteIndex("duyhai", t1.getLogin(), t1.getTweetId());
		this.favoriteTweetIndexRepository.addTweetToFavoriteIndex("duyhai", t2.getLogin(), t2.getTweetId());
		this.favoriteTweetIndexRepository.addTweetToFavoriteIndex("duyhai", t3.getLogin(), t3.getTweetId());
		this.favoriteTweetIndexRepository.addTweetToFavoriteIndex("tescolan", t1.getLogin(), t1.getTweetId());
		this.favoriteTweetIndexRepository.addTweetToFavoriteIndex("test", t1.getLogin(), t1.getTweetId());

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
		Collection<String> userLogins = this.favoriteTweetIndexRepository.getUsersForTweetFromIndex(t1.getTweetId());

		assertEquals(userLogins.size(), 4, "4 users for tweet1 in favorite tweet index");
		assertTrue(userLogins.contains("jdubois"), "userLogins contains jdubois");
		assertTrue(userLogins.contains("duyhai"), "userLogins contains duyhai");
		assertTrue(userLogins.contains("tescolan"), "userLogins contains tescolan");
		assertTrue(userLogins.contains("test"), "userLogins contains test");
	}

	@Test(dependsOnMethods = "testGetUsersForTweetFromIndexForFavorite")
	public void testGetTweetsForUserFromIndexForFavorite()
	{
		Collection<String> tweetsForDuyHai = this.favoriteTweetIndexRepository.getTweetsForUserFromIndex("duyhai", "jdubois");
		assertEquals(tweetsForDuyHai.size(), 3, "duyhai:jdubois favorite set size == 3");
		assertTrue(tweetsForDuyHai.contains(t1.getTweetId()), "duyhai:jdubois favorite set contains tweet1");
		assertTrue(tweetsForDuyHai.contains(t2.getTweetId()), "duyhai:jdubois favorite set contains tweet2");
		assertTrue(tweetsForDuyHai.contains(t3.getTweetId()), "duyhai:jdubois favorite set contains tweet3");
	}

	@SuppressWarnings("unchecked")
	@Test(dependsOnMethods = "testGetTweetsForUserFromIndexForFavorite")
	public void testRemoveTweetFromFavoriteIndexForFavorite()
	{
		this.favoriteTweetIndexRepository.removeTweetFromFavoriteIndex("duyhai", t1.getLogin(), t1.getTweetId());
		this.favoriteTweetIndexRepository.removeTweetFromFavoriteIndex("tescolan", t1.getLogin(), t1.getTweetId());

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
		this.favoriteTweetIndexRepository.removeIndexForTweet("test", "jdubois", t1.getTweetId());

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
