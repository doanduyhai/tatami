package fr.ippon.tatami.service;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;

import me.prettyprint.cassandra.model.CqlQuery;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.DayLine;
import fr.ippon.tatami.domain.FavoriteLine;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;

public class TimelineServiceTest extends AbstractCassandraTatamiTest
{

	@Inject
	public TimelineService timelineService;

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

	@Test
	public void testPostWeet() throws Exception
	{
		User jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		mockAuthenticatedUser(jdubois);

		t1 = this.timelineService.postTweet("tweet1 with #Java");
		t2 = this.timelineService.postTweet("tweet2 with #Java");
		t3 = this.timelineService.postTweet("tweet3 with #Java");
		t4 = this.timelineService.postTweet("tweet4 with #Java");
		t5 = this.timelineService.postTweet("tweet5 with #Java");
		t6 = this.timelineService.postTweet("tweet6 with #Java");
		t7 = this.timelineService.postTweet("tweet7 with #Cassandra");
		t8 = this.timelineService.postTweet("tweet8 with #Cassandra");
		t9 = this.timelineService.postTweet("tweet9 with #Cassandra");
		t10 = this.timelineService.postTweet("tweet10 with #Spring");

		User refreshedJdubois = this.userService.getUserByLogin("jdubois");
		assertEquals(refreshedJdubois.getTweetCount(), 10L, "refreshedJdubois.getTweetCount() == 10");
		assertEquals(refreshedJdubois.getTimelineTweetCount(), 10L, "refreshedJdubois.getTimelineTweetCount() == 10");

		assertNotNull(entityManager.find(Tweet.class, t1.getTweetId()), "tweet1");
		assertNotNull(entityManager.find(Tweet.class, t2.getTweetId()), "tweet2");
		assertNotNull(entityManager.find(Tweet.class, t3.getTweetId()), "tweet3");
		assertNotNull(entityManager.find(Tweet.class, t4.getTweetId()), "tweet4");
		assertNotNull(entityManager.find(Tweet.class, t5.getTweetId()), "tweet5");
		assertNotNull(entityManager.find(Tweet.class, t6.getTweetId()), "tweet6");
		assertNotNull(entityManager.find(Tweet.class, t7.getTweetId()), "tweet7");
		assertNotNull(entityManager.find(Tweet.class, t8.getTweetId()), "tweet8");
		assertNotNull(entityManager.find(Tweet.class, t9.getTweetId()), "tweet9");
		assertNotNull(entityManager.find(Tweet.class, t10.getTweetId()), "tweet10");

	}

	/*********************************** USER LINE ***********************************/
	@Test(dependsOnMethods = "testPostWeet")
	public void shouldGetUserline() throws Exception
	{
		Collection<Tweet> tweets = this.timelineService.getUserline("jdubois", 10);

		assertEquals(tweets.size(), 10, "tweets.size == 10");
	}

	@Test(dependsOnMethods = "shouldGetUserline")
	public void shouldGetUserlineRange() throws Exception
	{
		// Tweet are reverse-ordered so tweet(2) = tweet9, tweet(3) = tweet8 & tweet(4) = tweet7
		Collection<Tweet> tweets = this.timelineService.getUserlineRange("jdubois", 2, 4);

		assertEquals(tweets.size(), 3, "tweets.size == 3");
		assertTrue(tweets.contains(t9), "tweets.contains(t9)");
		assertTrue(tweets.contains(t8), "tweets.contains(t8)");
		assertTrue(tweets.contains(t7), "tweets.contains(t7");
	}

	@Test(dependsOnMethods = "shouldGetUserlineRange")
	public void shouldGetUserlineRangeWithLimitsOutOfBound() throws Exception
	{
		// Tweet are reverse-ordered so tweet(9) = tweet2, tweet(10) = tweet1
		Collection<Tweet> tweets = this.timelineService.getUserlineRange("jdubois", 9, 14);

		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t2), "tweets.contains(t2)");
		assertTrue(tweets.contains(t1), "tweets.contains(t1)");
	}

	@Test(dependsOnMethods = "shouldGetUserlineRangeWithLimitsOutOfBound")
	public void shouldGetUserlineRangeWithNegativeLimits() throws Exception
	{
		// Tweet are reverse-ordered so tweet(1) = tweet10, tweet(1) = tweet9
		Collection<Tweet> tweets = this.timelineService.getUserlineRange("jdubois", -2, 2);

		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t10), "tweets.contains(t10)");
		assertTrue(tweets.contains(t9), "tweets.contains(t9)");
	}

	@Test(dependsOnMethods = "shouldGetUserlineRangeWithNegativeLimits")
	public void shouldReturnEmptyUserlineWithNullLoginSet() throws Exception
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		Collection<Tweet> tweets = timelineService.getUserline(null, 10);

		assertEquals(tweets.size(), 0, "tweets.size == 0");
	}

	@Test(dependsOnMethods = "shouldReturnEmptyUserlineWithNullLoginSet")
	public void shouldReturnEmptyUserlineWithEmptyLoginSet() throws Exception
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		Collection<Tweet> tweets = timelineService.getUserline("", 10);

		assertEquals(tweets.size(), 0, "tweets.size == 0");
	}

	/*********************************** TIME LINE ***********************************/
	@Test(dependsOnMethods = "shouldReturnEmptyUserlineWithEmptyLoginSet")
	public void shouldGetTimeline() throws Exception
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		Collection<Tweet> tweets = timelineService.getTimeline(10);

		assertEquals(tweets.size(), 10, "tweets.size == 10");
	}

	@Test(dependsOnMethods = "shouldGetTimeline")
	public void shouldGetTimelineRange() throws Exception
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		// Tweet are reverse-ordered so tweet(7) = tweet4 & tweet(8) = tweet3
		Collection<Tweet> tweets = timelineService.getTimelineRange(7, 8);

		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t4), "tweets.contains(t4)");
		assertTrue(tweets.contains(t3), "tweets.contains(t3)");
	}

	@Test(dependsOnMethods = "shouldGetTimelineRange")
	public void shouldGetTimelineRangeWithLimitsOutOfBound() throws Exception
	{
		// Tweet are reverse-ordered so tweet(9) = tweet2, tweet(10) = tweet1
		Collection<Tweet> tweets = this.timelineService.getTimelineRange(9, 14);

		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t2), "tweets.contains(t2)");
		assertTrue(tweets.contains(t1), "tweets.contains(t1)");
	}

	@Test(dependsOnMethods = "shouldGetTimelineRangeWithLimitsOutOfBound")
	public void shouldGetTimelineRangeWithNegativeLimits() throws Exception
	{
		// Tweet are reverse-ordered so tweet(1) = tweet10, tweet(1) = tweet9
		Collection<Tweet> tweets = this.timelineService.getTimelineRange(-2, 2);

		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t10), "tweets.contains(t10)");
		assertTrue(tweets.contains(t9), "tweets.contains(t9)");
	}

	/*********************************** DAY LINE ***********************************/
	@Test(dependsOnMethods = "shouldGetTimelineRangeWithNegativeLimits")
	public void shouldGetDaylineByDate() throws Exception
	{
		Collection<Tweet> tweets = timelineService.getDayline(new Date());
		assertEquals(tweets.size(), 10, "tweets.size == 10");
	}

	@Test(dependsOnMethods = "shouldGetDaylineByDate")
	public void shouldGetDaylineByString() throws Exception
	{
		String today = new DateTime(new Date()).toString(DayLine.dayFormat);
		Collection<Tweet> tweets = timelineService.getDayline(today);
		assertEquals(tweets.size(), 10, "tweets.size == 10");
	}

	@Test(dependsOnMethods = "shouldGetDaylineByString")
	public void shouldGetDaylineRangeByDate() throws Exception
	{
		Collection<Tweet> tweets = timelineService.getDayline(new Date(), 2, 3);
		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t9), "tweets.contains(t9)");
		assertTrue(tweets.contains(t8), "tweets.contains(t8)");
	}

	@Test(dependsOnMethods = "shouldGetDaylineRangeByDate")
	public void shouldGetDaylineRangeLimitOutOfBoundsByDate() throws Exception
	{
		Collection<Tweet> tweets = timelineService.getDayline(new Date(), 9, 20);
		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t1), "tweets.contains(t1)");
		assertTrue(tweets.contains(t2), "tweets.contains(t2)");
	}

	@Test(dependsOnMethods = "shouldGetDaylineRangeLimitOutOfBoundsByDate")
	public void shouldGetDaylineRangeNegativeLimitByDate() throws Exception
	{
		Collection<Tweet> tweets = timelineService.getDayline(new Date(), -5, 2);
		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t10), "tweets.contains(t10)");
		assertTrue(tweets.contains(t9), "tweets.contains(t9)");
	}

	/*********************************** TAG LINE ***********************************/
	@Test(dependsOnMethods = "shouldGetDaylineRangeNegativeLimitByDate")
	public void shouldGetTagline() throws Exception
	{
		Collection<Tweet> tweets = timelineService.getTagline("Cassandra", 10);

		assertEquals(tweets.size(), 3, "tweets.size == 3");
		assertTrue(tweets.contains(t9), "tweets.contains(t9)");
		assertTrue(tweets.contains(t8), "tweets.contains(t8)");
		assertTrue(tweets.contains(t7), "tweets.contains(t7)");

	}

	@Test(dependsOnMethods = "shouldGetTagline")
	public void shouldGetTaglineRangeLimitsOutOfBound() throws Exception
	{
		// Tweets are reverse-order so tweet(5) = tweet2 & tweet(6) = tweet1
		Collection<Tweet> tweets = timelineService.getTaglineRange("Java", 5, 10);

		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t1), "tweets.contains(t1)");
		assertTrue(tweets.contains(t2), "tweets.contains(t2)");
	}

	@Test(dependsOnMethods = "shouldGetTaglineRangeLimitsOutOfBound")
	public void shouldGetTaglineRangeNegativeLimit() throws Exception
	{
		// Tweets are reverse-order so tweet(5) = tweet2 & tweet(6) = tweet1
		Collection<Tweet> tweets = timelineService.getTaglineRange("Java", -2, 2);

		assertEquals(tweets.size(), 2, "tweets.size == 2");
		assertTrue(tweets.contains(t6), "tweets.contains(t6)");
		assertTrue(tweets.contains(t5), "tweets.contains(t5)");
	}

	/*********************************** FAVORITE LINE ***********************************/
	@Test(dependsOnMethods = "shouldGetTaglineRangeLimitsOutOfBound")
	public void shouldAddToFavorite()
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		this.favoriteRepository.addFavorite(jdubois, t1.getTweetId());
		this.favoriteRepository.addFavorite(jdubois, t3.getTweetId());
		this.favoriteRepository.addFavorite(jdubois, t5.getTweetId());
		this.favoriteRepository.addFavorite(jdubois, t7.getTweetId());

		User refreshedJdubois = this.userService.getUserByLogin("jdubois");
		assertTrue(refreshedJdubois.getFavoritesCount() == 4, "refreshedJdubois.getFavoritesCount() == 4");

		FavoriteLine favorites = entityManager.find(FavoriteLine.class, "jdubois");
		assertTrue(favorites.getFavorites().size() == 4, "favorites.getFavorites().size() == 4");
		assertTrue(favorites.getFavorites().contains(t1.getTweetId()), "favorites.getFavorites().contains('tweet1')");
		assertTrue(favorites.getFavorites().contains(t3.getTweetId()), "favorites.getFavorites().contains('tweet3')");
		assertTrue(favorites.getFavorites().contains(t5.getTweetId()), "favorites.getFavorites().contains('tweet5')");
		assertTrue(favorites.getFavorites().contains(t7.getTweetId()), "favorites.getFavorites().contains('tweet7')");
	}

	@Test(dependsOnMethods = "shouldAddToFavorite")
	public void shouldGetFavoriteline()
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		Collection<Tweet> tweets = this.timelineService.getFavoritesline();

		assertTrue(tweets.size() == 4, "favorites.getFavorites().size() == 4");
		assertTrue(tweets.contains(t1), "favorites.getFavorites().contains('t1')");
		assertTrue(tweets.contains(t3), "favorites.getFavorites().contains('t3')");
		assertTrue(tweets.contains(t5), "favorites.getFavorites().contains('t5')");
		assertTrue(tweets.contains(t7), "favorites.getFavorites().contains('t7')");
	}

	@Test(dependsOnMethods = "shouldGetFavoriteline")
	public void shouldGetFavoritelineRange()
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		Collection<Tweet> tweets = this.timelineService.getFavoriteslineByRange(2, 3);

		assertTrue(tweets.size() == 2, "favorites.getFavorites().size() == 2");
		assertTrue(tweets.contains(t5), "favorites.getFavorites().contains('t5')");
		assertTrue(tweets.contains(t3), "favorites.getFavorites().contains('t3')");
	}

	@Test(dependsOnMethods = "shouldGetFavoritelineRange")
	public void shouldGetFavoritelineRangeLimitOutOfBounds()
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		Collection<Tweet> tweets = this.timelineService.getFavoriteslineByRange(4, 10);

		assertTrue(tweets.size() == 1, "favorites.getFavorites().size() == 1");
		assertTrue(tweets.contains(t1), "favorites.getFavorites().contains('t1')");
	}

	@Test(dependsOnMethods = "shouldGetFavoritelineRangeLimitOutOfBounds")
	public void shouldGetFavoritelineRangeNegativeLimit()
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		Collection<Tweet> tweets = this.timelineService.getFavoriteslineByRange(-4, 1);

		assertTrue(tweets.size() == 1, "favorites.getFavorites().size() == 1");
		assertTrue(tweets.contains(t7), "favorites.getFavorites().contains('t7')");
	}

	@Test(dependsOnMethods = "shouldGetFavoritelineRangeNegativeLimit")
	public void shouldRemoveFromFavoriteline()
	{
		User jdubois = this.userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		this.timelineService.removeFavoriteTweet(t7.getTweetId());

		Collection<Tweet> tweets = this.timelineService.getFavoriteslineByRange(1, 10);

		assertTrue(tweets.size() == 3, "favorites.getFavorites().size() == 3");
		assertTrue(tweets.contains(t1), "favorites.getFavorites().contains('t1')");
		assertTrue(tweets.contains(t3), "favorites.getFavorites().contains('t3')");
		assertTrue(tweets.contains(t5), "favorites.getFavorites().contains('t5')");
		assertFalse(tweets.contains(t7), "favorites.getFavorites().contains('t7')");
	}

	@Test(dependsOnMethods = "shouldRemoveFromFavoriteline")
	public void shouldPostTweetWithFollower() throws Exception
	{
		User tescolan = new User();
		tescolan.setLogin("tescolan");
		tescolan.setEmail("tescolan@ippon.fr");
		tescolan.setFirstName("Thomas");
		tescolan.setLastName("ESCOLAN");

		this.userService.createUser(tescolan);

		User freshTescolan = this.userService.getUserByLogin("tescolan");
		User jdubois = this.userService.getUserByLogin("jdubois");

		// tescolan follows jdubois, jdubois gets one alert tweet
		mockAuthenticatedUser(freshTescolan);
		this.userService.followUser("jdubois");

		// jdubois quotes tescolan but since he already follows him, there will be no alert tweet
		mockAuthenticatedUser(jdubois);
		Tweet newTweet = this.timelineService.postTweet("tweet11 Hi @tescolan !!!");

		freshTescolan = this.userService.getUserByLogin("tescolan");
		mockAuthenticatedUser(freshTescolan);
		Collection<Tweet> userline = this.timelineService.getUserline("tescolan", 10);
		Collection<Tweet> timeline = this.timelineService.getTimeline(10);

		assertTrue(freshTescolan.getTimelineTweetCount() == 1, "freshTescolan.getTimelineTweetCount() == 1");
		assertTrue(freshTescolan.getTweetCount() == 0, "freshTescolan.getTimelineTweetCount() == 0");

		assertTrue(timeline.size() == 1, "timeline.size() == 1");
		assertTrue(userline.size() == 0, "userline.size() == 0");
		assertTrue(timeline.contains(newTweet), "timeline.contains(newTweet)");
	}

	@Test(dependsOnMethods = "shouldPostTweetWithFollower")
	public void shouldPostTweetWithQuotedUser() throws Exception
	{
		User tescolan = this.userService.getUserByLogin("tescolan");
		User jdubois = this.userService.getUserByLogin("jdubois");

		// tescolan no longer follows jdubois
		mockAuthenticatedUser(tescolan);
		this.userService.forgetUser("jdubois");

		// jdubois quotes tescolan, tescolan gests one alert tweet
		mockAuthenticatedUser(jdubois);
		this.timelineService.postTweet("tweet12 @tescolan is a very smart guy !!!");

		User freshTescolan = this.userService.getUserByLogin("tescolan");
		mockAuthenticatedUser(freshTescolan);
		Collection<Tweet> userline = this.timelineService.getUserline("tescolan", 10);
		Collection<Tweet> timeline = this.timelineService.getTimeline(10);

		assertTrue(freshTescolan.getTimelineTweetCount() == 2, "freshTescolan.getTimelineTweetCount() == 2");
		assertTrue(freshTescolan.getTweetCount() == 0, "freshTescolan.getTimelineTweetCount() == 0");

		assertTrue(timeline.size() == 2, "timeline.size() == 2");
		assertTrue(userline.size() == 0, "userline.size() == 0");

	}

	@Test(dependsOnMethods = "shouldPostTweetWithQuotedUser")
	public void cleanUp()
	{
		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, se, se, se);

		cqlQuery.setQuery("truncate User");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate UserFriends");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate UserFollowers");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate FavoriteLine");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate UserLine");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate TimeLine");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate TagLine");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate TagLineCount");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate DayLine");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate WeekLine");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate MonthLine");
		cqlQuery.execute();
		cqlQuery.setQuery("truncate YearLine");
		cqlQuery.execute();
	}

}