package fr.ippon.tatami.service.renderer.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.user.UserService;

public class FavoriteTweetRendererTest extends AbstractCassandraTatamiTest
{

	private User jdubois;
	private UserService mockUserService;
	private FavoriteRepository mockFavoriteRepository;
	private FavoriteTweetRenderer favoriteTweetRenderer;

	@Test
	public void initFavoriteTweetRendererTest()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");

		mockUserService = mock(UserService.class);
		when(mockUserService.getCurrentUser()).thenReturn(jdubois);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetService.setAuthenticationService(mockAuthenticationService);

		mockFavoriteRepository = mock(FavoriteRepository.class);

		favoriteTweetRenderer = new FavoriteTweetRenderer();
		favoriteTweetRenderer.setFavoriteLineRepository(mockFavoriteRepository);
		favoriteTweetRenderer.setUserService(mockUserService);

	}

	@Test(dependsOnMethods = "initFavoriteTweetRendererTest")
	public void testOnRenderFavoriteTrue()
	{

		Tweet tweet = this.tweetService.createTransientTweet("tweet1");

		when(mockFavoriteRepository.findFavoritesForUser(jdubois)).thenReturn(Arrays.asList("123"));

		this.favoriteTweetRenderer.onRender(tweet);

		assertTrue(tweet.isAddToFavorite(), "Can add tweet1 to favorite");

	}

	@Test(dependsOnMethods = "testOnRenderFavoriteTrue")
	public void testOnRenderFavoriteFalse()
	{

		Tweet tweet = this.tweetService.createTransientTweet("tweet2");

		when(mockFavoriteRepository.findFavoritesForUser(jdubois)).thenReturn(Arrays.asList(tweet.getTweetId()));

		this.favoriteTweetRenderer.onRender(tweet);

		assertFalse(tweet.isAddToFavorite(), "Cannot add tweet2 to favorite");

	}
}
