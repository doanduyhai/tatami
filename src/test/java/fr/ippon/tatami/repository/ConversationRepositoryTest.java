package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.CONVERSATIONLINE_CF;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.ConversationItem;
import fr.ippon.tatami.domain.Tweet;

public class ConversationRepositoryTest extends AbstractCassandraTatamiTest
{

	Tweet t1, t2, t3, t4;

	@Test
	public void testAddTweetToConversationInRepository()
	{
		t1 = this.tweetRepository.createTweet("jdubois", "this is a good tweet", false);
		t2 = this.tweetRepository.createTweet("duyhai", "agree", false);
		t3 = this.tweetRepository.createTweet("jdubois", "thanks", false);
		t4 = this.tweetRepository.createTweet("duyhai", "welcomed", false);

		this.conversationRepository.addTweetToConversation(t2, t1.getTweetId());
		this.conversationRepository.addTweetToConversation(t3, t1.getTweetId());
		this.conversationRepository.addTweetToConversation(t4, t1.getTweetId());

		Collection<String> conversationTweets = this.findRangeFromCF(CONVERSATIONLINE_CF, t1.getTweetId(), null, true, 10);

		assertEquals(conversationTweets.size(), 3, "3 tweets for conversation");
		assertTrue(conversationTweets.contains(t2.getTweetId()), "conversation has t2");
		assertTrue(conversationTweets.contains(t2.getTweetId()), "conversation has t3");
		assertTrue(conversationTweets.contains(t2.getTweetId()), "conversation has t4");

		assertEquals(t2.getSourceTweetId(), t1.getTweetId(), "t1 is source of t2");
		assertEquals(t3.getSourceTweetId(), t1.getTweetId(), "t1 is source of t3");
		assertEquals(t4.getSourceTweetId(), t1.getTweetId(), "t1 is source of t4");
	}

	@Test(dependsOnMethods = "testAddTweetToConversationInRepository")
	public void testGetTweetsForConversationInRepository()
	{
		Collection<ConversationItem> conversationItems = this.conversationRepository.getTweetsForConversation(t1.getTweetId());

		assertEquals(conversationItems.size(), 3, "3 tweets in conversation");
		Iterator<ConversationItem> iter = conversationItems.iterator();

		ConversationItem item = iter.next();
		assertEquals(item.getTweetId(), t4.getTweetId());
		assertEquals(item.getAuthorLogin(), "duyhai");

		item = iter.next();
		assertEquals(item.getTweetId(), t3.getTweetId());
		assertEquals(item.getAuthorLogin(), "jdubois");

		item = iter.next();
		assertEquals(item.getTweetId(), t2.getTweetId());
		assertEquals(item.getAuthorLogin(), "duyhai");

	}

}
