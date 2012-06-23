package fr.ippon.tatami.service.lines;

import static fr.ippon.tatami.config.ColumnFamilyKeys.CONVERSATIONLINE_CF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.ConversationItem;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class ConversationlineServiceTest extends AbstractCassandraTatamiTest
{
	Tweet sourceTweet, t1, t2, t3, t4;
	User jdubois, duyhai;
	private AuthenticationService mockedAuthenticationService;

	@Test
	public void testOnAddToConversationInService()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		duyhai = new User();
		duyhai.setLogin("duyhai");
		duyhai.setEmail("duyhai@ippon.fr");
		duyhai.setFirstName("DuyHai");
		duyhai.setLastName("DOAN");

		this.userRepository.createUser(jdubois);
		this.userRepository.createUser(duyhai);

		mockedAuthenticationService = mock(AuthenticationService.class);
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetService.setAuthenticationService(mockedAuthenticationService);
		this.userService.setAuthenticationService(mockedAuthenticationService);

		sourceTweet = this.tweetService.createTransientTweet("Original tweet");
		this.tweetService.onTweetPost(sourceTweet);

		when(mockedAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		t1 = this.tweetService.createTransientTweet("agree @jdubois");
		this.conversationlineService.onAddToConversation(t1, sourceTweet.getTweetId());

		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		t2 = this.tweetService.createTransientTweet("thanks");
		this.conversationlineService.onAddToConversation(t2, sourceTweet.getTweetId());

		when(mockedAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		t3 = this.tweetService.createTransientTweet("welcomed");
		this.conversationlineService.onAddToConversation(t3, sourceTweet.getTweetId());

		Collection<String> conversationTweets = this.findRangeFromCF(CONVERSATIONLINE_CF, sourceTweet.getTweetId(), null, true, 10);

		assertEquals(conversationTweets.size(), 3, "3 tweets in conversation");
		assertTrue(conversationTweets.contains(t1.getTweetId()), "conversation has t1");
		assertTrue(conversationTweets.contains(t2.getTweetId()), "conversation has t2");
		assertTrue(conversationTweets.contains(t3.getTweetId()), "conversation has t3");
	}

	@Test(dependsOnMethods = "testOnAddToConversationInService")
	public void testGetTweetsForConversationInService() throws FunctionalException
	{
		Collection<ConversationItem> conversationItems = this.conversationlineService.getTweetsForConversation(sourceTweet.getTweetId());

		assertEquals(conversationItems.size(), 3, "3 tweets in conversation");

		Iterator<ConversationItem> iter = conversationItems.iterator();

		ConversationItem conversationItem = iter.next();

		assertEquals(conversationItem.getTweetId(), t3.getTweetId());
		assertEquals(conversationItem.getTweet(), t3);

		conversationItem = iter.next();
		assertEquals(conversationItem.getTweetId(), t2.getTweetId());
		assertEquals(conversationItem.getTweet(), t2);

		conversationItem = iter.next();
		assertEquals(conversationItem.getTweetId(), t1.getTweetId());
		assertEquals(conversationItem.getTweet(), t1);
	}
}
