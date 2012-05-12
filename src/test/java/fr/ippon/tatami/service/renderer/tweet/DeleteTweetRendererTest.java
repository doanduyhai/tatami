package fr.ippon.tatami.service.renderer.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.user.UserService;

public class DeleteTweetRendererTest extends AbstractCassandraTatamiTest
{
	private User jdubois;
	private UserService mockUserService;
	private DeleteTweetRenderer deleteTweetRenderer;

	@Test
	public void initDeleteTweetRendererTest()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");

		mockUserService = mock(UserService.class);
		when(mockUserService.getCurrentUser()).thenReturn(jdubois);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetService.setAuthenticationService(mockAuthenticationService);

		deleteTweetRenderer = new DeleteTweetRenderer();
		deleteTweetRenderer.setUserService(mockUserService);

	}

	@Test(dependsOnMethods = "initDeleteTweetRendererTest")
	public void testOnRenderDeleteTrue()
	{
		Tweet tweet = this.tweetService.createTransientTweet("tweet1");
		this.deleteTweetRenderer.onRender(tweet);

		assertTrue(tweet.getDeletable(), "Can delete tweet1");
	}

	@Test(dependsOnMethods = "testOnRenderDeleteTrue")
	public void testOnRenderDeleteFalse()
	{

		User duyhai = new User();
		duyhai.setLogin("duyhai");
		when(mockUserService.getCurrentUser()).thenReturn(duyhai);

		Tweet tweet = this.tweetService.createTransientTweet("tweet2");

		this.deleteTweetRenderer.onRender(tweet);

		assertFalse(tweet.getDeletable(), "Cannot delete tweet2");
	}

}
