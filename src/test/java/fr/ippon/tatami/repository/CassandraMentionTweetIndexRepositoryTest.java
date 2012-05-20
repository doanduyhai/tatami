package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.MENTION_TWEET_INDEX_CF;
import static fr.ippon.tatami.config.CounterKeys.MENTION_TWEET_INDEX_COUNTER;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class CassandraMentionTweetIndexRepositoryTest extends AbstractCassandraTatamiTest
{
	private Tweet t1, t2, t3, t4, t5;

	@Test
	public void testAddTweetToIndexForMentionline()
	{

		t1 = this.tweetRepository.createTweet("jdubois", "@duyhai tweet1", false);
		t2 = this.tweetRepository.createTweet("jdubois", "@duyhai tweet2", false);
		t3 = this.tweetRepository.createTweet("jdubois", "@duyhai tweet3", false);
		t4 = this.tweetRepository.createTweet("tescolan", "@duyhai tweet4", false);
		t5 = this.tweetRepository.createTweet("tescolan", "@duyhai tweet5", false);

		this.mentionTweetIndexRepository.addTweetToIndex("jdubois", "duyhai", t1.getTweetId());
		this.mentionTweetIndexRepository.addTweetToIndex("jdubois", "duyhai", t2.getTweetId());
		this.mentionTweetIndexRepository.addTweetToIndex("jdubois", "duyhai", t3.getTweetId());
		this.mentionTweetIndexRepository.addTweetToIndex("tescolan", "duyhai", t4.getTweetId());
		this.mentionTweetIndexRepository.addTweetToIndex("tescolan", "duyhai", t5.getTweetId());

		Collection<String> duyhaiJduboisMentionTweets = this.findRangeFromCF(MENTION_TWEET_INDEX_CF, "jdubois" + LOGIN_SEPARATOR + "duyhai", null,
				true, 10);
		assertEquals(duyhaiJduboisMentionTweets.size(), 3, "duyhai has been mentioned in 3 tweets by jdubois");
		assertTrue(duyhaiJduboisMentionTweets.contains(t1.getTweetId()), "duyhai mentioned in tweet1");
		assertTrue(duyhaiJduboisMentionTweets.contains(t2.getTweetId()), "duyhai mentioned in tweet2");
		assertTrue(duyhaiJduboisMentionTweets.contains(t3.getTweetId()), "duyhai mentioned in tweet3");

		long duyhaiJduboisMentionCount = this.getCounterValue(MENTION_TWEET_INDEX_COUNTER, "jdubois" + LOGIN_SEPARATOR + "duyhai");
		assertEquals(duyhaiJduboisMentionCount, 3, "duyhai has been mentioned 3 times by jdubois");

		Collection<String> duyhaiTescolanMentionTweets = this.findRangeFromCF(MENTION_TWEET_INDEX_CF, "tescolan" + LOGIN_SEPARATOR + "duyhai", null,
				true, 10);
		assertEquals(duyhaiTescolanMentionTweets.size(), 2, "duyhai has been mentioned in 2 tweets by tescolan");
		assertTrue(duyhaiTescolanMentionTweets.contains(t4.getTweetId()), "duyhai mentioned in tweet4");
		assertTrue(duyhaiTescolanMentionTweets.contains(t5.getTweetId()), "duyhai mentioned in tweet5");

		long duyhaiTescolanMentionCount = this.getCounterValue(MENTION_TWEET_INDEX_COUNTER, "tescolan" + LOGIN_SEPARATOR + "duyhai");
		assertEquals(duyhaiTescolanMentionCount, 2, "duyhai has been mentioned 2 times by tescolan");
	}

	@Test(dependsOnMethods = "testAddTweetToIndexForMentionline")
	public void testFindTweetsForUserAndMentioner()
	{
		Collection<String> duyhaiJduboisMentionTweets = this.mentionTweetIndexRepository.findTweetsForUserAndMentioner("jdubois", "duyhai");
		assertEquals(duyhaiJduboisMentionTweets.size(), 3, "duyhai has been mentioned in 3 tweets by jdubois");
		assertTrue(duyhaiJduboisMentionTweets.contains(t1.getTweetId()), "duyhai mentioned in tweet1");
		assertTrue(duyhaiJduboisMentionTweets.contains(t2.getTweetId()), "duyhai mentioned in tweet2");
		assertTrue(duyhaiJduboisMentionTweets.contains(t3.getTweetId()), "duyhai mentioned in tweet3");

		Collection<String> duyhaiTescolanMentionTweets = this.mentionTweetIndexRepository.findTweetsForUserAndMentioner("tescolan", "duyhai");
		assertEquals(duyhaiTescolanMentionTweets.size(), 2, "duyhai has been mentioned in 2 tweets by tescolan");
		assertTrue(duyhaiTescolanMentionTweets.contains(t4.getTweetId()), "duyhai mentioned in tweet4");
		assertTrue(duyhaiTescolanMentionTweets.contains(t5.getTweetId()), "duyhai mentioned in tweet5");
	}

	@Test(dependsOnMethods = "testFindTweetsForUserAndMentioner")
	public void testRemoveTweetFromIndexForMentionline()
	{
		this.mentionTweetIndexRepository.removeTweetFromIndex("jdubois", "duyhai", t1.getTweetId());
		this.mentionTweetIndexRepository.removeTweetFromIndex("jdubois", "duyhai", t2.getTweetId());

		Collection<String> duyhaiJduboisMentionTweets = this.findRangeFromCF(MENTION_TWEET_INDEX_CF, "jdubois" + LOGIN_SEPARATOR + "duyhai", null,
				true, 10);
		assertEquals(duyhaiJduboisMentionTweets.size(), 1, "duyhai has been mentioned in 1 tweet by jdubois");
		assertTrue(duyhaiJduboisMentionTweets.contains(t3.getTweetId()), "duyhai mentioned in tweet3");

		long duyhaiJduboisMentionCount = this.getCounterValue(MENTION_TWEET_INDEX_COUNTER, "jdubois" + LOGIN_SEPARATOR + "duyhai");
		assertEquals(duyhaiJduboisMentionCount, 1, "duyhai has been mentioned 1 time by jdubois");
	}

	@Test(dependsOnMethods = "testRemoveTweetFromIndexForMentionline")
	public void testRemoveIndexForMentionline()
	{
		this.mentionTweetIndexRepository.removeIndex("tescolan", "duyhai");

		Collection<String> duyhaiTescolanMentionTweets = this.findRangeFromCF(MENTION_TWEET_INDEX_CF, "tescolan" + LOGIN_SEPARATOR + "duyhai", null,
				true, 10);
		assertEquals(duyhaiTescolanMentionTweets.size(), 0, "tescolan:duyhai index empty");

		long duyhaiTescoalnMentionCount = this.getCounterValue(MENTION_TWEET_INDEX_COUNTER, "tescolan" + LOGIN_SEPARATOR + "duyhai");
		assertEquals(duyhaiTescoalnMentionCount, 0, "tescolan:duyhai counter empty");

	}
}
