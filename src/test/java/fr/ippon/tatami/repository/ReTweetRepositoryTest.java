package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.RETWEETER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.RETWEETLINE_CF;
import static fr.ippon.tatami.config.CounterKeys.RETWEETER_COUNTER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class ReTweetRepositoryTest extends AbstractCassandraTatamiTest
{

	private Tweet t1, t2, t3, retweet1, retweet2, retweet3;

	@Test
	public void testAddRetweeterToRepository()
	{
		t1 = this.tweetRepository.createTweet("jdubois", "tweet1", false);

		retweet1 = this.tweetRepository.createTweet("duyhai", "tweet1", false);
		retweet2 = this.tweetRepository.createTweet("tescolan", "tweet1", false);
		retweet3 = this.tweetRepository.createTweet("test", "tweet1", false);

		this.retweetRepository.addRetweeter("duyhai", t1.getTweetId(), retweet1.getTweetId());
		this.retweetRepository.addRetweeter("tescolan", t1.getTweetId(), retweet2.getTweetId());
		this.retweetRepository.addRetweeter("test", t1.getTweetId(), retweet3.getTweetId());

		Collection<HColumn<String, Object>> retweeterColumns = this.findInclusiveColumnsRangeFromCF(RETWEETER_CF, t1.getTweetId(), null, false, 10);

		assertEquals(retweeterColumns.size(), 3, "3 retweeters for tweet1");

		List<String> retweeters = new ArrayList<String>();

		for (HColumn<String, Object> column : retweeterColumns)
		{
			retweeters.add(column.getName());
		}
		assertTrue(retweeters.contains("duyhai"), "duyhai is retweeter for tweet1");
		assertTrue(retweeters.contains("tescolan"), "tescolan is retweeter for tweet1");
		assertTrue(retweeters.contains("test"), "test is retweeter for tweet1");

		List<String> retweetIds = new ArrayList<String>();

		for (HColumn<String, Object> column : retweeterColumns)
		{
			retweetIds.add((String) column.getValue());
		}
		assertTrue(retweetIds.contains(retweet1.getTweetId()), "retweetIds contains retweet1");
		assertTrue(retweetIds.contains(retweet2.getTweetId()), "retweetIds contains retweet2");
		assertTrue(retweetIds.contains(retweet3.getTweetId()), "retweetIds contains retweet3");

		long retweetersCount = this.getCounterValue(RETWEETER_COUNTER, t1.getTweetId());
		assertEquals(retweetersCount, 3, "3 retweeters for tweet1 in counter");
	}

	@Test(dependsOnMethods = "testAddRetweeterToRepository")
	public void testFindRetweetIdForRetweeter()
	{
		String retweetIdByDuyhai = this.retweetRepository.findRetweetIdForRetweeter("duyhai", t1.getTweetId());

		assertEquals(retweetIdByDuyhai, retweet1.getTweetId(), "duyhai has retweeted with retweet1");
	}

	@Test(dependsOnMethods = "testFindRetweetIdForRetweeter")
	public void testCountRetweeterFromRepository()
	{
		long retweetersCount = this.retweetRepository.countRetweeters(t1.getTweetId());
		assertEquals(retweetersCount, 3, "3 retweeters for tweet1 in counter");
	}

	@Test(dependsOnMethods = "testCountRetweeterFromRepository")
	public void testFindAllRetweetIdsForTweetInRepository()
	{
		Collection<String> retweetIds = this.retweetRepository.findRetweetIdsForTweet(t1.getTweetId());

		assertEquals(retweetIds.size(), 3, "3 retweetIds for tweet1");

		assertTrue(retweetIds.contains(retweet1.getTweetId()), "retweetIds contains retweet1");
		assertTrue(retweetIds.contains(retweet2.getTweetId()), "retweetIds contains retweet2");
		assertTrue(retweetIds.contains(retweet3.getTweetId()), "retweetIds contains retweet3");
	}

	@Test(dependsOnMethods = "testFindAllRetweetIdsForTweetInRepository")
	public void testFindRetweetersForTweetInRepository()
	{
		Collection<String> retweeters = this.retweetRepository.findRetweetersForTweet(t1.getTweetId());
		assertEquals(retweeters.size(), 3, "3 retweeters for tweet1");
		assertTrue(retweeters.contains("duyhai"), "duyhai is retweeter for tweet1");
		assertTrue(retweeters.contains("tescolan"), "tescolan is retweeter for tweet1");
		assertTrue(retweeters.contains("test"), "test is retweeter for tweet1");
	}

	@Test(dependsOnMethods = "testFindRetweetersForTweetInRepository")
	public void testRemoveRetweeterFromRepository()
	{
		this.retweetRepository.removeRetweeter("duyhai", t1.getTweetId());

		Collection<HColumn<String, Object>> retweeterColumns = this.findInclusiveColumnsRangeFromCF(RETWEETER_CF, t1.getTweetId(), null, false, 10);

		assertEquals(retweeterColumns.size(), 2, "2 retweeters for tweet1");

		List<String> retweeters = new ArrayList<String>();

		for (HColumn<String, Object> column : retweeterColumns)
		{
			retweeters.add(column.getName());
		}
		assertTrue(retweeters.contains("tescolan"), "tescolan is retweeter for tweet1");
		assertTrue(retweeters.contains("test"), "test is retweeter for tweet1");

		List<String> retweetIds = new ArrayList<String>();

		for (HColumn<String, Object> column : retweeterColumns)
		{
			retweetIds.add((String) column.getValue());
		}
		assertTrue(retweetIds.contains(retweet2.getTweetId()), "retweetIds contains retweet2");
		assertTrue(retweetIds.contains(retweet3.getTweetId()), "retweetIds contains retweet3");

		long retweetersCount = this.getCounterValue(RETWEETER_COUNTER, t1.getTweetId());
		assertEquals(retweetersCount, 2, "2 retweeters for tweet1 in counter");
	}

	@Test(dependsOnMethods = "testRemoveRetweeterFromRepository")
	public void testRemoveRetweeterIndexFromRepository()
	{
		this.retweetRepository.removeRetweeterIndex(t1.getTweetId());

		Collection<HColumn<String, Object>> retweeterColumns = this.findInclusiveColumnsRangeFromCF(RETWEETER_CF, t1.getTweetId(), null, false, 10);

		assertEquals(retweeterColumns.size(), 0, "no retweeters for tweet1");

		long retweetersCount = this.getCounterValue(RETWEETER_COUNTER, t1.getTweetId());
		assertEquals(retweetersCount, 0, "0 retweeters for tweet1 in counter");
	}

	@Test(dependsOnMethods = "testRemoveRetweeterIndexFromRepository")
	public void testAddToRetweetLineInRepository()
	{
		t2 = this.tweetRepository.createTweet("jdubois", "tweet2", false);
		t3 = this.tweetRepository.createTweet("jdubois", "tweet3", false);

		this.retweetRepository.addToRetweetLine("duyhai", t1.getTweetId());
		this.retweetRepository.addToRetweetLine("duyhai", t2.getTweetId());
		this.retweetRepository.addToRetweetLine("duyhai", t3.getTweetId());

		Collection<String> retweetIds = this.findRangeFromCF(RETWEETLINE_CF, "duyhai", null, true, 10);

		assertEquals(retweetIds.size(), 3, "3 tweets in duyhai's retweet line");
		assertTrue(retweetIds.contains(t1.getTweetId()), "retweetIds contains tweet1");
		assertTrue(retweetIds.contains(t2.getTweetId()), "retweetIds contains tweet2");
		assertTrue(retweetIds.contains(t2.getTweetId()), "retweetIds contains tweet3");
	}

	@Test(dependsOnMethods = "testRemoveRetweeterIndexFromRepository")
	public void testIsTweetInRetweetLineInRepository()
	{
		boolean t1Retweeted = this.retweetRepository.isTweetInRetweetLine("duyhai", t1.getTweetId());
		boolean retweet1Retweeted = this.retweetRepository.isTweetInRetweetLine("duyhai", retweet1.getTweetId());

		assertTrue(t1Retweeted, "t1 is in duyhai's retweet line");
		assertFalse(retweet1Retweeted, "retweet1 is not in duyhai's retweet line");
	}

	@Test(dependsOnMethods = "testIsTweetInRetweetLineInRepository")
	public void testRemoveFromRetweetLineFromRepository()
	{
		this.retweetRepository.removeFromRetweetLine("duyhai", t1.getTweetId());

		Collection<String> retweetIds = this.findRangeFromCF(RETWEETLINE_CF, "duyhai", null, true, 10);

		assertEquals(retweetIds.size(), 2, "2 tweets in duyhai's retweet line");
		assertTrue(retweetIds.contains(t2.getTweetId()), "retweetIds contains tweet2");
		assertTrue(retweetIds.contains(t2.getTweetId()), "retweetIds contains tweet3");
	}
}
