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
import fr.ippon.tatami.service.security.AuthenticationService;

public class TweetServiceTest extends AbstractCassandraTatamiTest
{
	private Tweet t1;
	private User jdubois;

	@Test
	public void testCreateTransientTweet()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetService.setAuthenticationService(mockAuthenticationService);

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
	public void testOnTweetRemoveForTweetService()
	{
		this.tweetService.onTweetRemove(t1);

		Tweet savedTweet = this.tweetRepository.findTweetById(t1.getTweetId());
		assertNull(savedTweet, "savedTweet was removed");
	}

}
