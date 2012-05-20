package fr.ippon.tatami.service.pipeline.tweet.rendering;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.user.UserService;

public class TweetRenderingPipelineManagerTest extends AbstractCassandraTatamiTest
{
	private FavoriteRepository mockFavoriteRepository;
	private User jdubois;

	@Test
	public void initTweetRenderingPipelineManagerTest()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");

		UserService mockUserService = mock(UserService.class);
		when(mockUserService.getCurrentUser()).thenReturn(jdubois);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetService.setAuthenticationService(mockAuthenticationService);

		mockFavoriteRepository = mock(FavoriteRepository.class);

		this.favoriteTweetRenderer.setUserService(mockUserService);
		this.favoriteTweetRenderer.setFavoriteLineRepository(mockFavoriteRepository);

		this.deleteTweetRenderer.setUserService(mockUserService);
	}

	@Test(dependsOnMethods = "initTweetRenderingPipelineManagerTest")
	public void testOnTweetRenderFavorite()
	{
		Tweet tweet = this.tweetService.createTransientTweet("tweet1");
		when(mockFavoriteRepository.findFavoritesForUser("jdubois")).thenReturn(Arrays.asList("123"));

		this.tweetRenderingPipelineManager.onTweetRender(tweet);

		assertTrue(tweet.isAddToFavorite(), "Can add tweet1 to favorite");
	}

	@Test(dependsOnMethods = "testOnTweetRenderFavorite")
	public void testOnTweetRenderDeletable()
	{

		Tweet tweet = this.tweetService.createTransientTweet("tweet2");
		this.tweetRenderingPipelineManager.onTweetRender(tweet);

		assertTrue(tweet.getDeletable(), "Can delete tweet2");
	}

	@Test(dependsOnMethods = "testOnTweetRenderDeletable")
	public void testOnTweetRenderUserHashAndUrls()
	{
		Tweet tweet = this.tweetService.createTransientTweet("Hello @duyhai, if you like #Java, check http://docs.oracle.com/javase/tutorial/");

		this.xssEncodingService.onTweetPost(tweet);
		this.tweetRenderingPipelineManager.onTweetRender(tweet);

		assertTrue(tweet.getContent().contains("<a href='#' data-user='duyhai' title='Show duyhai tweets'><em>@duyhai</em></a>"),
				"tweet contains <a href='#' data-user='duyhai' title='Show duyhai tweets'><em>@duyhai</em></a>");

		assertTrue(tweet.getContent().contains("<a href='#' data-tag='Java' title='Show Java related tweets'><em>#Java</em></a>"),
				"tweet contains <a href='#' data-tag='Java' title='Show Java related tweets'><em>#Java</em></a>");

		assertTrue(tweet.getContent().contains(
				"<a href='http://docs.oracle.com/javase/tutorial/' "
						+ "title='http://docs.oracle.com/javase/tutorial/' target='_blank'>docs.oracl...</a>"),
				"tweet contains <a href='http://docs.oracle.com/javase/tutorial/' title='http://docs.oracle.com/javase/tutorial/' target='_blank'>docs.oracl...</a>");

	}
}
