package fr.ippon.tatami.service.lines;

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

public class FavoritelineServiceTest extends AbstractCassandraTatamiTest
{
	private Tweet t1;
	private Tweet t2;
	private Tweet t3;
	private Tweet t4;
	private Tweet t5;
	private Tweet t6;
	private Tweet t7;
	private Tweet t8;
	private Tweet t9;
	private Tweet t10;
	private Tweet mentionTweet;

	private User jdubois;

	@Test
	public void testAddFavoriteTweet() throws FunctionalException, InterruptedException
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		User duyhai = new User();
		duyhai.setLogin("duyhai");
		duyhai.setEmail("duyhai@ippon.fr");
		duyhai.setFirstName("DuyHai");
		duyhai.setLastName("DOAN");

		this.userRepository.createUser(jdubois);
		this.userRepository.createUser(duyhai);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.userService.setAuthenticationService(mockAuthenticationService);

		t1 = this.tweetRepository.createTweet("jdubois", "tweet1");
		Thread.sleep(1);
		t2 = this.tweetRepository.createTweet("jdubois", "tweet2");
		Thread.sleep(1);
		t3 = this.tweetRepository.createTweet("jdubois", "tweet3");
		Thread.sleep(1);
		t4 = this.tweetRepository.createTweet("jdubois", "tweet4");
		Thread.sleep(1);
		t5 = this.tweetRepository.createTweet("jdubois", "tweet5");
		Thread.sleep(1);
		t6 = this.tweetRepository.createTweet("jdubois", "tweet6");
		Thread.sleep(1);
		t7 = this.tweetRepository.createTweet("jdubois", "tweet7");
		Thread.sleep(1);
		t8 = this.tweetRepository.createTweet("jdubois", "tweet8");
		Thread.sleep(1);
		t9 = this.tweetRepository.createTweet("jdubois", "tweet9");
		Thread.sleep(1);
		t10 = this.tweetRepository.createTweet("jdubois", "tweet10");

		this.favoritelineService.addFavoriteTweet(t1.getTweetId());
		this.favoritelineService.addFavoriteTweet(t2.getTweetId());
		this.favoritelineService.addFavoriteTweet(t3.getTweetId());
		this.favoritelineService.addFavoriteTweet(t4.getTweetId());
		this.favoritelineService.addFavoriteTweet(t5.getTweetId());
		this.favoritelineService.addFavoriteTweet(t6.getTweetId());
		this.favoritelineService.addFavoriteTweet(t7.getTweetId());
		this.favoritelineService.addFavoriteTweet(t8.getTweetId());
		this.favoritelineService.addFavoriteTweet(t9.getTweetId());
		this.favoritelineService.addFavoriteTweet(t10.getTweetId());

		User refreshedJdubois = this.userService.getUserByLogin("jdubois");
		assertTrue(refreshedJdubois.getFavoritesCount() == 10, "refreshedJdubois.getFavoritesCount() == 10");

		Collection<String> favorites = this.favoriteRepository.findFavoritesForUser(jdubois);

		assertTrue(favorites.contains(t1.getTweetId()), "favorites.contains('tweet1')");
		assertTrue(favorites.contains(t2.getTweetId()), "favorites.contains('tweet2')");
		assertTrue(favorites.contains(t3.getTweetId()), "favorites.contains('tweet3')");
		assertTrue(favorites.contains(t4.getTweetId()), "favorites.contains('tweet4')");
		assertTrue(favorites.contains(t5.getTweetId()), "favorites.contains('tweet5')");
		assertTrue(favorites.contains(t6.getTweetId()), "favorites.contains('tweet6')");
		assertTrue(favorites.contains(t7.getTweetId()), "favorites.contains('tweet7')");
		assertTrue(favorites.contains(t8.getTweetId()), "favorites.contains('tweet8')");
		assertTrue(favorites.contains(t9.getTweetId()), "favorites.contains('tweet9')");
		assertTrue(favorites.contains(t10.getTweetId()), "favorites.contains('tweet10')");
	}

	@Test(dependsOnMethods = "testAddFavoriteTweet", expectedExceptions = FunctionalException.class)
	public void testAddFavoriteTweetWithException() throws FunctionalException
	{
		this.favoritelineService.addFavoriteTweet("123");
	}

	@Test(dependsOnMethods = "testAddFavoriteTweetWithException")
	public void testGetFavoriteslineRange() throws FunctionalException
	{
		Collection<Tweet> tweets = this.favoritelineService.getFavoriteslineRange(t5.getTweetId(), 3);

		assertEquals(tweets.size(), 3, " tweets.size() == 3");
		assertTrue(tweets.contains(t4), "tweets contains 'tweet4'");
		assertTrue(tweets.contains(t3), "tweets contains 'tweet3'");
		assertTrue(tweets.contains(t2), "tweets contains 'tweet2'");
	}

	@Test(dependsOnMethods = "testGetFavoriteslineRange")
	public void testGetFavoriteslineRangeOutOfBounds() throws FunctionalException
	{
		Collection<Tweet> tweets = this.favoritelineService.getFavoriteslineRange(t2.getTweetId(), 3);

		assertEquals(tweets.size(), 1, " tweets.size() == 1");
		assertTrue(tweets.contains(t1), "tweets contains 'tweet1'");
	}

	@Test(dependsOnMethods = "testGetFavoriteslineRangeOutOfBounds")
	public void testGetFavoriteslineRangeFromStart() throws FunctionalException
	{
		Collection<Tweet> tweets = this.favoritelineService.getFavoriteslineRange(null, 3);

		assertEquals(tweets.size(), 10, " tweets.size() == 10");
		assertTrue(tweets.contains(t10), "tweets contains 'tweet10'");
		assertTrue(tweets.contains(t9), "tweets contains 'tweet9'");
		assertTrue(tweets.contains(t8), "tweets contains 'tweet8'");
		assertTrue(tweets.contains(t7), "tweets contains 'tweet7'");
		assertTrue(tweets.contains(t6), "tweets contains 'tweet6'");
		assertTrue(tweets.contains(t5), "tweets contains 'tweet5'");
		assertTrue(tweets.contains(t4), "tweets contains 'tweet4'");
		assertTrue(tweets.contains(t3), "tweets contains 'tweet3'");
		assertTrue(tweets.contains(t2), "tweets contains 'tweet2'");
		assertTrue(tweets.contains(t1), "tweets contains 'tweet1'");
	}

	@Test(dependsOnMethods = "testGetFavoriteslineRangeFromStart")
	public void testAddFavoriteTweetWithUserMentioned() throws FunctionalException
	{
		mentionTweet = this.tweetRepository.createTweet("duyhai", "This is my tweet");

		this.favoritelineService.addFavoriteTweet(mentionTweet.getTweetId());

		User refreshedDuyhai = this.userService.getUserByLogin("duyhai");
		assertEquals(refreshedDuyhai.getTimelineTweetCount(), 1, "refreshedDuyhai.getTimelineTweetCount() == 1");

		Collection<String> duyhaiTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline(refreshedDuyhai, null, 2);

		assertEquals(duyhaiTimelineTweets.size(), 1, "duyhaiTimelineTweets.size() == 1");
	}

	@Test(dependsOnMethods = "testAddFavoriteTweetWithUserMentioned")
	public void testRemoveFavoriteTweet() throws FunctionalException
	{
		this.favoritelineService.removeFavoriteTweet(mentionTweet.getTweetId());
		this.favoritelineService.removeFavoriteTweet(t9.getTweetId());
		this.favoritelineService.removeFavoriteTweet(t8.getTweetId());
		this.favoritelineService.removeFavoriteTweet(t7.getTweetId());

		Collection<String> favorites = this.favoriteRepository.findFavoritesRangeForUser(jdubois, t9.getTweetId(), 3);
		assertEquals(favorites.size(), 3, "favorites.size() == 3");

		assertTrue(favorites.contains(t6.getTweetId()), "favorites has 'tweet6'");
		assertTrue(favorites.contains(t5.getTweetId()), "favorites has 'tweet5'");
		assertTrue(favorites.contains(t4.getTweetId()), "favorites has 'tweet4'");
	}

}
