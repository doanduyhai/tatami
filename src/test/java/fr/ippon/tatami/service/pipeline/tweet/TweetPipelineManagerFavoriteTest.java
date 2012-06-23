package fr.ippon.tatami.service.pipeline.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class TweetPipelineManagerFavoriteTest extends AbstractCassandraTatamiTest
{

	private User jdubois;
	private Tweet t1, t2, t3;
	private AuthenticationService mockAuthenticationService;

	@Test
	public void initTweetPipelineManagerFavoriteTest()
	{

		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.userService.setAuthenticationService(mockAuthenticationService);
		this.contactsService.setAuthenticationService(mockAuthenticationService);
		this.retweetService.setAuthenticationService(mockAuthenticationService);

	}

	@Test(dependsOnMethods = "initTweetPipelineManagerFavoriteTest")
	public void testOnAddToFavorite() throws FunctionalException
	{
		// jdubois posts tweets
		t1 = this.tweetPipelineManager.onPost("tweet1");
		t2 = this.tweetPipelineManager.onPost("tweet2");
		t3 = this.tweetPipelineManager.onPost("tweet3");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetPipelineManager.onAddToFavorite(t1.getTweetId());
		this.tweetPipelineManager.onAddToFavorite(t2.getTweetId());
		this.tweetPipelineManager.onAddToFavorite(t3.getTweetId());

		Collection<String> favoriteTweets = this.favoriteRepository.findFavoritesForUser("jdubois");

		assertEquals(favoriteTweets.size(), 3, "favoriteTweets.size() == 3");
		assertTrue(favoriteTweets.contains(t1.getTweetId()), "favoriteTweets contains tweet1");
		assertTrue(favoriteTweets.contains(t2.getTweetId()), "favoriteTweets contains tweet2");
		assertTrue(favoriteTweets.contains(t3.getTweetId()), "favoriteTweets contains tweet3");

	}

	@Test(dependsOnMethods = "testOnAddToFavorite", expectedExceptions = FunctionalException.class)
	public void testOnAddToFavoriteWithException() throws FunctionalException
	{
		// Exception because t1 is already in favorites
		this.tweetPipelineManager.onAddToFavorite(t1.getTweetId());
	}

	@Test(dependsOnMethods = "testOnAddToFavoriteWithException")
	public void testOnRemoveFromFavorite() throws FunctionalException
	{
		this.tweetPipelineManager.onRemoveFromFavorite(t2.getTweetId());
		this.tweetPipelineManager.onRemoveFromFavorite(t3.getTweetId());

		Collection<String> favoriteTweets = this.favoriteRepository.findFavoritesForUser("jdubois");

		assertEquals(favoriteTweets.size(), 1, "favoriteTweets.size() == 1");
		assertTrue(favoriteTweets.contains(t1.getTweetId()), "favoriteTweets contains tweet1");
	}

	@Test(dependsOnMethods = "testOnRemoveFromFavorite", expectedExceptions = FunctionalException.class)
	public void testOnRemoveFromFavoriteWithException() throws FunctionalException
	{
		// Exception because t3 is already removed from favorites
		this.tweetPipelineManager.onRemoveFromFavorite(t3.getTweetId());
	}
}
