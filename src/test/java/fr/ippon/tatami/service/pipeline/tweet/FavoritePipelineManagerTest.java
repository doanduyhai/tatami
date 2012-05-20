package fr.ippon.tatami.service.pipeline.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class FavoritePipelineManagerTest extends AbstractCassandraTatamiTest
{
	private FavoritePipelineManager manager;
	private User jdubois;
	private Tweet t1, t2, t3, t4;

	private AuthenticationService mockAuthenticationService;
	private InOrder mockOrder;
	private FavoriteHandler handler1, handler2;

	@Test
	public void initFavoritePipelineManagerTest()
	{
		manager = new FavoritePipelineManager();

		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.userService.setAuthenticationService(mockAuthenticationService);
		this.tweetService.setAuthenticationService(mockAuthenticationService);

		t1 = this.tweetRepository.createTweet("jdubois", "tweet1", false);
		t2 = this.tweetRepository.createTweet("jdubois", "tweet2", false);
		t3 = this.tweetRepository.createTweet("jdubois", "tweet3", false);
		t4 = this.tweetRepository.createTweet("jdubois", "tweet4", false);

		handler1 = mock(FavoriteHandler.class);
		handler2 = mock(FavoriteHandler.class);

		mockOrder = Mockito.inOrder(handler1, handler2);

		manager.setTweetService(this.tweetService);
		manager.setFavoriteHandlers(Arrays.asList(handler1, handler2));
	}

	@Test(dependsOnMethods = "initFavoritePipelineManagerTest")
	public void testMockedOnAddToFavorite() throws FunctionalException
	{
		manager.onAddToFavorite(t1.getTweetId());

		mockOrder.verify(handler1).onAddToFavorite(t1);
		mockOrder.verify(handler2).onAddToFavorite(t1);
	}

	@Test(dependsOnMethods = "testMockedOnAddToFavorite")
	public void testMockedOnRemoveFromFavorite() throws FunctionalException
	{
		manager.onRemoveFromFavorite(t1.getTweetId());

		mockOrder.verify(handler1).onRemoveFromFavorite(t1);
		mockOrder.verify(handler2).onRemoveFromFavorite(t1);
	}

	@Test(dependsOnMethods = "testMockedOnRemoveFromFavorite")
	public void testOnAddToFavorite() throws FunctionalException
	{
		this.favoritePipelineManager.onAddToFavorite(t1.getTweetId());
		this.favoritePipelineManager.onAddToFavorite(t2.getTweetId());
		this.favoritePipelineManager.onAddToFavorite(t3.getTweetId());
		this.favoritePipelineManager.onAddToFavorite(t4.getTweetId());

		Collection<String> favoriteTweets = this.favoriteRepository.findFavoritesForUser("jdubois");

		assertEquals(favoriteTweets.size(), 4, "favoriteTweets.size() == 4");
		assertTrue(favoriteTweets.contains(t1.getTweetId()), "favoriteTweets contains tweet1");
		assertTrue(favoriteTweets.contains(t2.getTweetId()), "favoriteTweets contains tweet2");
		assertTrue(favoriteTweets.contains(t3.getTweetId()), "favoriteTweets contains tweet3");
		assertTrue(favoriteTweets.contains(t4.getTweetId()), "favoriteTweets contains tweet4");
	}

	@Test(dependsOnMethods = "testOnAddToFavorite", expectedExceptions = FunctionalException.class)
	public void testOnAddToFavoriteWithException() throws FunctionalException
	{
		this.favoritePipelineManager.onAddToFavorite(t1.getTweetId());
	}

	@Test(dependsOnMethods = "testOnAddToFavoriteWithException")
	public void testOnRemoveFromFavorite() throws FunctionalException
	{
		this.favoritePipelineManager.onRemoveFromFavorite(t2.getTweetId());
		this.favoritePipelineManager.onRemoveFromFavorite(t3.getTweetId());

		Collection<String> favoriteTweets = this.favoriteRepository.findFavoritesForUser("jdubois");

		assertEquals(favoriteTweets.size(), 2, "favoriteTweets.size() == 2");
		assertTrue(favoriteTweets.contains(t1.getTweetId()), "favoriteTweets contains tweet1");
		assertTrue(favoriteTweets.contains(t4.getTweetId()), "favoriteTweets contains tweet4");
	}

	@Test(dependsOnMethods = "testOnRemoveFromFavorite", expectedExceptions = FunctionalException.class)
	public void testOnRemoveFromFavoriteWithException() throws FunctionalException
	{
		this.favoritePipelineManager.onRemoveFromFavorite(t3.getTweetId());
	}
}
