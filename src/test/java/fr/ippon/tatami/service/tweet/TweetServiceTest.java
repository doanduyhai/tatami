package fr.ippon.tatami.service.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class TweetServiceTest extends AbstractCassandraTatamiTest
{
	private Tweet t1, reTweet1;
	private User jdubois, duyhai;
	private AuthenticationService mockAuthenticationService;

	@Test
	public void testCreateTransientTweet()
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
		this.retweetService.setAuthenticationService(mockAuthenticationService);

		t1 = this.tweetService.createTransientTweet("Test tweet creation");

		assertNotNull(t1, "tweet is null");

		assertEquals(t1.getLogin(), "jdubois", "tweet.getLogin() == 'jdubois'");
		assertEquals(t1.getContent(), "Test tweet creation", "tweet.getContent() == 'Test tweet creation'");

		assertNull(this.tweetRepository.findTweetById(t1.getTweetId()), "t1 is not persisted");
	}

	@Test(dependsOnMethods = "testCreateTransientTweet")
	public void testOnTweetPostForTweetService()
	{
		this.tweetService.onTweetPost(t1);

		Tweet savedTweet = this.tweetRepository.findTweetById(t1.getTweetId());

		assertNotNull(savedTweet, "savedTweet was persisted");
		assertEquals(savedTweet.getTweetId(), t1.getTweetId(), "savedTweet == t1");
	}

	@Test(dependsOnMethods = "testOnTweetPostForTweetService")
	public void testOnReTweetForTweetService() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		reTweet1 = this.tweetService.createTransientTweet("Test tweet creation");
		reTweet1.setOriginalTweetId(t1.getTweetId());
		this.retweetService.onRetweet(reTweet1);
		this.tweetService.onRetweet(reTweet1);

		Tweet retweet = this.tweetRepository.findTweetById(reTweet1.getTweetId());

		assertNotNull(retweet, "reTweet1 has been saved");
		assertEquals(reTweet1.getTweetId(), retweet.getTweetId(), "reTweet1 = reTweet");

	}

	@Test(dependsOnMethods = "testOnReTweetForTweetService")
	public void testOnCancelRetweetForTweetService() throws FunctionalException
	{
		this.tweetService.onCancelRetweet(t1.getTweetId());
		this.retweetService.onCancelRetweet(t1.getTweetId());

		Tweet retweet = this.tweetRepository.findTweetById(reTweet1.getTweetId());
		assertNull(retweet, "reTweet1 has been removed");

	}

	@Test(dependsOnMethods = "testOnCancelRetweetForTweetService")
	public void testOnTweetRemoveForTweetService()
	{
		this.tweetService.onTweetRemove(t1);

		Tweet savedTweet = this.tweetRepository.findTweetById(t1.getTweetId());
		assertNull(savedTweet, "savedTweet was removed");
	}

}
