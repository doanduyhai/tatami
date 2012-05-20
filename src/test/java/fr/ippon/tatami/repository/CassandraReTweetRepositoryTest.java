package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.RETWEETER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.RETWEET_TARGET_USER_CF;
import static fr.ippon.tatami.config.CounterKeys.RETWEETER_COUNTER;
import static fr.ippon.tatami.config.CounterKeys.RETWEET_TARGET_USER_COUNTER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class CassandraReTweetRepositoryTest extends AbstractCassandraTatamiTest
{

	private Tweet t1;

	@Test
	public void testAddRetweeterToRepository()
	{
		t1 = this.tweetRepository.createTweet("jdubois", "tweet1", false);

		this.retweetRepository.addRetweeter("duyhai", t1.getTweetId());
		this.retweetRepository.addRetweeter("tescolan", t1.getTweetId());
		this.retweetRepository.addRetweeter("test", t1.getTweetId());

		Collection<String> retweeters = this.findRangeFromCF(RETWEETER_CF, t1.getTweetId(), null, false, 10);

		assertEquals(retweeters.size(), 3, "3 retweeters for tweet1");
		assertTrue(retweeters.contains("duyhai"), "duyhai is retweeter for tweet1");
		assertTrue(retweeters.contains("tescolan"), "tescolan is retweeter for tweet1");
		assertTrue(retweeters.contains("test"), "test is retweeter for tweet1");

		long retweetersCount = this.getCounterValue(RETWEETER_COUNTER, t1.getTweetId());
		assertEquals(retweetersCount, 3, "3 retweeters for tweet1 in counter");
	}

	@Test(dependsOnMethods = "testAddRetweeterToRepository")
	public void testCountRetweeterFromRepository()
	{
		long retweetersCount = this.retweetRepository.countRetweeter(t1.getTweetId());
		assertEquals(retweetersCount, 3, "3 retweeters for tweet1 in counter");
	}

	@Test(dependsOnMethods = "testCountRetweeterFromRepository")
	public void testFindRetweetersForTweetInRepository()
	{
		Collection<String> retweeters = this.retweetRepository.findRetweeterForTweet(t1.getTweetId());
		assertEquals(retweeters.size(), 3, "3 retweeters for tweet1");
		assertTrue(retweeters.contains("duyhai"), "duyhai is retweeter for tweet1");
		assertTrue(retweeters.contains("tescolan"), "tescolan is retweeter for tweet1");
		assertTrue(retweeters.contains("test"), "test is retweeter for tweet1");
	}

	@Test
	public void testAddTargetUserForRetweetToRepository()
	{
		this.retweetRepository.addTargetUserForRetweet("duyhai", t1.getTweetId());
		this.retweetRepository.addTargetUserForRetweet("tescolan", t1.getTweetId());
		this.retweetRepository.addTargetUserForRetweet("test", t1.getTweetId());

		Collection<String> targetUsers = this.findRangeFromCF(RETWEET_TARGET_USER_CF, t1.getTweetId(), null, false, 10);

		assertEquals(targetUsers.size(), 3, "3 target users for tweet1");
		assertTrue(targetUsers.contains("duyhai"), "duyhai is target user for tweet1 retweet");
		assertTrue(targetUsers.contains("tescolan"), "tescolan is target user for tweet1 retweet");
		assertTrue(targetUsers.contains("test"), "test is target user for tweet1 retweet");

		long targetUserCount = this.getCounterValue(RETWEET_TARGET_USER_COUNTER, t1.getTweetId());
		assertEquals(targetUserCount, 3, "3 target users for tweet1 retweet");
	}

	@Test(dependsOnMethods = "testAddTargetUserForRetweetToRepository")
	public void testFindTargetUsersForRetweetInRepository()
	{
		Collection<String> targetUsers = this.retweetRepository.findTartgetUsersForRetweet(t1.getTweetId());
		assertEquals(targetUsers.size(), 3, "3 target users for tweet1");
		assertTrue(targetUsers.contains("duyhai"), "duyhai is target user for tweet1 retweet");
		assertTrue(targetUsers.contains("tescolan"), "tescolan is target user for tweet1 retweet");
		assertTrue(targetUsers.contains("test"), "test is target user for tweet1 retweet");

	}

	@Test(dependsOnMethods = "testFindTargetUsersForRetweetInRepository")
	public void testRemoveTargetUserForRetweet()
	{
		this.retweetRepository.removeTargetUserForRetweet("duyhai", t1.getTweetId());

		Collection<String> targetUsers = this.findRangeFromCF(RETWEET_TARGET_USER_CF, t1.getTweetId(), null, false, 10);

		assertEquals(targetUsers.size(), 2, "2 target users for tweet1");
		assertTrue(targetUsers.contains("tescolan"), "tescolan is target user for tweet1 retweet");
		assertTrue(targetUsers.contains("test"), "test is target user for tweet1 retweet");

		long targetUserCount = this.getCounterValue(RETWEET_TARGET_USER_COUNTER, t1.getTweetId());
		assertEquals(targetUserCount, 2, "2 target users for tweet1 retweet");

	}
}
