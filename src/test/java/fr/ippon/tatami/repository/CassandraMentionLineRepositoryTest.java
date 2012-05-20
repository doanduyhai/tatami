package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.MENTIONLINE_CF;
import static fr.ippon.tatami.config.CounterKeys.MENTION_LINE_COUNTER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class CassandraMentionLineRepositoryTest extends AbstractCassandraTatamiTest
{
	private Tweet t1, t2, t3, t4, t5;

	@Test
	public void testAddTweetToMentionline()
	{
		t1 = this.tweetRepository.createTweet("jdubois", "@duyhai tweet1", false);
		t2 = this.tweetRepository.createTweet("jdubois", "@duyhai tweet2", false);
		t3 = this.tweetRepository.createTweet("jdubois", "@duyhai tweet3", false);
		t4 = this.tweetRepository.createTweet("jdubois", "@duyhai tweet4", false);
		t5 = this.tweetRepository.createTweet("jdubois", "@duyhai tweet5", false);

		this.mentionLineRepository.addTweetToMentionline("duyhai", t1.getTweetId());
		this.mentionLineRepository.addTweetToMentionline("duyhai", t2.getTweetId());
		this.mentionLineRepository.addTweetToMentionline("duyhai", t3.getTweetId());
		this.mentionLineRepository.addTweetToMentionline("duyhai", t4.getTweetId());
		this.mentionLineRepository.addTweetToMentionline("duyhai", t5.getTweetId());

		Collection<String> mentionTweetForDuyhai = this.findRangeFromCF(MENTIONLINE_CF, "duyhai", null, false, 10);

		assertEquals(mentionTweetForDuyhai.size(), 5, "duyhai has 5 mention tweets");
		assertTrue(mentionTweetForDuyhai.contains(t1.getTweetId()), "duyhai is mentioned in tweet1");
		assertTrue(mentionTweetForDuyhai.contains(t2.getTweetId()), "duyhai is mentioned in tweet2");
		assertTrue(mentionTweetForDuyhai.contains(t3.getTweetId()), "duyhai is mentioned in tweet3");
		assertTrue(mentionTweetForDuyhai.contains(t4.getTweetId()), "duyhai is mentioned in tweet4");
		assertTrue(mentionTweetForDuyhai.contains(t5.getTweetId()), "duyhai is mentioned in tweet5");

		long duyhaiMentionTweetCount = this.getCounterValue(MENTION_LINE_COUNTER, "duyhai");

		assertEquals(duyhaiMentionTweetCount, 5, "duyhai mention tweet count == 5");
	}

	@Test(dependsOnMethods = "testAddTweetToMentionline")
	public void testFindMentionTweetsForUser()
	{
		Collection<String> duyhaiMentionTweets = this.mentionLineRepository.findMentionTweetsForUser("duyhai");

		assertEquals(duyhaiMentionTweets.size(), 5, "duyhai has 5 mention tweets");
		assertTrue(duyhaiMentionTweets.contains(t1.getTweetId()), "duyhai is mentioned in tweet1");
		assertTrue(duyhaiMentionTweets.contains(t2.getTweetId()), "duyhai is mentioned in tweet2");
		assertTrue(duyhaiMentionTweets.contains(t3.getTweetId()), "duyhai is mentioned in tweet3");
		assertTrue(duyhaiMentionTweets.contains(t4.getTweetId()), "duyhai is mentioned in tweet4");
		assertTrue(duyhaiMentionTweets.contains(t5.getTweetId()), "duyhai is mentioned in tweet5");
	}

	@Test(dependsOnMethods = "testFindMentionTweetsForUser")
	public void testFindMentionTweetsRangeForUser()
	{
		Collection<String> duyhaiMentionTweets = this.mentionLineRepository.findMentionTweetsRangeForUser("duyhai", t3.getTweetId(), 10);
		assertEquals(duyhaiMentionTweets.size(), 2, "duyhai has 2 mention tweets");
		assertTrue(duyhaiMentionTweets.contains(t1.getTweetId()), "duyhai is mentioned in tweet1");
		assertTrue(duyhaiMentionTweets.contains(t2.getTweetId()), "duyhai is mentioned in tweet2");

	}

	@Test(dependsOnMethods = "testFindMentionTweetsRangeForUser")
	public void removeTweetFromMentionline()
	{
		this.mentionLineRepository.removeTweetFromMentionline("duyhai", t1.getTweetId());

		Collection<String> mentionTweetForDuyhai = this.findRangeFromCF(MENTIONLINE_CF, "duyhai", null, false, 10);

		assertEquals(mentionTweetForDuyhai.size(), 4, "duyhai has 4 mention tweets");
		assertTrue(mentionTweetForDuyhai.contains(t2.getTweetId()), "duyhai is mentioned in tweet2");
		assertTrue(mentionTweetForDuyhai.contains(t3.getTweetId()), "duyhai is mentioned in tweet3");
		assertTrue(mentionTweetForDuyhai.contains(t4.getTweetId()), "duyhai is mentioned in tweet4");
		assertTrue(mentionTweetForDuyhai.contains(t5.getTweetId()), "duyhai is mentioned in tweet5");

		long duyhaiMentionTweetCount = this.getCounterValue(MENTION_LINE_COUNTER, "duyhai");

		assertEquals(duyhaiMentionTweetCount, 4, "duyhai mention tweet count == 4");
	}
}
