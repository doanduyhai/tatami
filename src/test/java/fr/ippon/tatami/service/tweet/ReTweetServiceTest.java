package fr.ippon.tatami.service.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class ReTweetServiceTest extends AbstractCassandraTatamiTest
{
	private Tweet t1, reTweet1;
	private User jdubois, duyhai;

	@Test
	public void testOnRetweetForService() throws FunctionalException
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

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.retweetService.setAuthenticationService(mockAuthenticationService);

		t1 = this.tweetService.createTransientTweet("Test tweet creation");
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		reTweet1 = this.tweetService.createTransientTweet("Test tweet creation");
		reTweet1.setOriginalTweetId(t1.getTweetId());

		this.retweetService.onRetweet(reTweet1);

		Collection<String> reTweeters = this.retweetRepository.findRetweetersForTweet(t1.getTweetId());

		assertEquals(reTweeters.size(), 1, "there is 1 reTweeter for t1");
		assertEquals(reTweeters.iterator().next(), "duyhai", "duyhai is retweeter for t1");

		String reTweetId = this.retweetRepository.findRetweetIdForRetweeter("duyhai", t1.getTweetId());

		assertNotNull(reTweetId, "t1 has a retweet");
		assertEquals(reTweetId, reTweet1.getTweetId(), "retweet1 is a retweet of t1");

		Collection<String> reTweetIds = this.retweetRepository.findRetweetIdsForTweet(t1.getTweetId());
		assertEquals(reTweetIds.size(), 1, "there is 1 reTweetId for t1");
		assertEquals(reTweetIds.iterator().next(), reTweet1.getTweetId(), "retweet1 is a retweet of t1");
	}

	@Test(dependsOnMethods = "testOnRetweetForService")
	public void testOnCancelRetweetForService() throws FunctionalException
	{
		this.retweetService.onCancelRetweet(t1.getTweetId());

		Collection<String> reTweeters = this.retweetRepository.findRetweetersForTweet(t1.getTweetId());

		assertEquals(reTweeters.size(), 0, "there is no reTweeter for t1");

		String reTweetId = this.retweetRepository.findRetweetIdForRetweeter("duyhai", t1.getTweetId());

		assertNull(reTweetId, "t1 has no retweet");

		Collection<String> reTweetIds = this.retweetRepository.findRetweetIdsForTweet(t1.getTweetId());
		assertEquals(reTweetIds.size(), 0, "there is no reTweetId for t1");
	}
}
