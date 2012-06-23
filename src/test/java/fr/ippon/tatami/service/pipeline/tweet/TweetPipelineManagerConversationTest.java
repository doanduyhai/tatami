package fr.ippon.tatami.service.pipeline.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.ConversationItem;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class TweetPipelineManagerConversationTest extends AbstractCassandraTatamiTest
{
	private User jdubois, duyhai;
	private Tweet sourceTweet, t1, t2, t3;
	private AuthenticationService mockAuthenticationService;

	@Test
	public void initTweetPipelineManagerConversationTest()
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

		mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.userService.setAuthenticationService(mockAuthenticationService);
		this.contactsService.setAuthenticationService(mockAuthenticationService);

		sourceTweet = this.tweetService.createTransientTweet("Original tweet");
		this.tweetService.onTweetPost(sourceTweet);
	}

	@Test(dependsOnMethods = "initTweetPipelineManagerConversationTest")
	public void testOnAddToConversation() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		this.tweetPipelineManager.onAddToConversation("agree @jdubois", sourceTweet.getTweetId());

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetPipelineManager.onAddToConversation("thanks", sourceTweet.getTweetId());

		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		this.tweetPipelineManager.onAddToConversation("welcomed", sourceTweet.getTweetId());

		Collection<ConversationItem> conversationItems = this.conversationlineService.getTweetsForConversation(sourceTweet.getTweetId());

		assertEquals(conversationItems.size(), 3, "3 tweets in conversation");

		Iterator<ConversationItem> iter = conversationItems.iterator();
		ConversationItem conversationItem = iter.next();

		assertEquals(conversationItem.getTweet().getContent(), "welcomed");
	}
}
