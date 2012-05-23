package fr.ippon.tatami.service.user;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.ReTweetRepository;
import fr.ippon.tatami.service.security.AuthenticationService;

public class ContactsServiceTest extends AbstractCassandraTatamiTest
{

	private User jdubois, duyhai, tescolan, userToFollow;
	private Tweet tweet, tweet2, retweet, retweet2;
	private AuthenticationService mockedAuthenticationService;

	@Test
	public void initForContactServiceTest()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		userToFollow = new User();
		userToFollow.setLogin("userToFollow");
		userToFollow.setFirstName("New");
		userToFollow.setLastName("User");
		userToFollow.setEmail("nuser@ippon.fr");
		userToFollow.setGravatar("newGravatar");

		duyhai = new User();
		duyhai.setLogin("duyhai");
		duyhai.setEmail("duyhai@ippon.fr");
		duyhai.setFirstName("DuyHai");
		duyhai.setLastName("DOAN");

		tescolan = new User();
		tescolan.setLogin("tescolan");
		tescolan.setEmail("tescolan@ippon.fr");
		tescolan.setFirstName("Thomas");
		tescolan.setLastName("ESCOLAN");

		this.userRepository.createUser(jdubois);
		this.userRepository.createUser(userToFollow);
		this.userRepository.createUser(duyhai);
		this.userRepository.createUser(tescolan);

		assertNotNull(this.userRepository.findUserByLogin("jdubois"), "jdubois has been created");
		assertNotNull(this.userRepository.findUserByLogin("userToFollow"), "userToFollow has been created");
		assertNotNull(this.userRepository.findUserByLogin("duyhai"), "duyhai has been created");
		assertNotNull(this.userRepository.findUserByLogin("tescolan"), "tescolan has been created");
	}

	@Test(dependsOnMethods = "initForContactServiceTest")
	public void testOnUserFollowForContactsService() throws FunctionalException
	{
		jdubois = userService.getUserByLogin("jdubois");
		this.mockedAuthenticationService = mockAuthenticatedUser(jdubois);
		this.contactsService.setAuthenticationService(mockedAuthenticationService);

		// jdubois follows userToFollow, duyhai & tescolan
		this.contactsService.onUserFollow("userToFollow");
		this.contactsService.onUserFollow("duyhai");
		this.contactsService.onUserFollow("tescolan");

		// duyhai follows userToFollow
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		this.contactsService.onUserFollow("userToFollow");

		// tescolan follows userToFollow
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(tescolan);
		this.contactsService.onUserFollow("userToFollow");

		jdubois = userService.getUserByLogin("jdubois");
		User userToFollow = userService.getUserByLogin("userToFollow");

		assertEquals(jdubois.getFriendsCount(), 3, "jdubois has 3 friends");
		assertEquals(userToFollow.getFollowersCount(), 3, "userToFollow has 3 followers");
	}

	@Test(dependsOnMethods = "testOnUserFollowForContactsService", expectedExceptions = FunctionalException.class)
	public void testOnUserFollowSelfForContactsService() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.contactsService.onUserFollow("jdubois");
	}

	@Test(dependsOnMethods = "testOnUserFollowSelfForContactsService", expectedExceptions = FunctionalException.class)
	public void testOnUserFollowAgainForContactsService() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.contactsService.onUserFollow("userToFollow");
	}

	@Test(dependsOnMethods = "testOnUserFollowAgainForContactsService")
	public void testGetFriendsForUser() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		Collection<String> jduboisFriends = this.contactsService.getFriendsForUser("jdubois");

		assertEquals(jduboisFriends.size(), 3, "jdubois has 3 friends");
		assertTrue(jduboisFriends.contains("userToFollow"), "jduboisFriends contains userToFollow");
		assertTrue(jduboisFriends.contains("duyhai"), "jduboisFriends contains duyhai");
		assertTrue(jduboisFriends.contains("tescolan"), "jduboisFriends contains tescolan");
	}

	@Test(dependsOnMethods = "testGetFriendsForUser")
	public void testGetFriendsRangeForUser() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		Collection<User> jduboisFriends = this.contactsService.getFriendsForUser("jdubois", "duyhai", 2);

		assertEquals(jduboisFriends.size(), 2, "jduboisFriends.size() == 2");
		assertTrue(jduboisFriends.contains(tescolan), "jduboisFriends contains tescolan");
		assertTrue(jduboisFriends.contains(userToFollow), "jduboisFriends contains userToFollow");
	}

	@Test(dependsOnMethods = "testGetFriendsRangeForUser")
	public void testGetFriendsRangeOutOfBoundsForUser() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		Collection<User> jduboisFriends = this.contactsService.getFriendsForUser("jdubois", "userToFollow", 100);

		assertEquals(jduboisFriends.size(), 0, "jduboisFriends.size() == 0");
	}

	@Test(dependsOnMethods = "testGetFriendsRangeOutOfBoundsForUser")
	public void testGetFollowersForUser() throws FunctionalException
	{
		Collection<String> userToFollowFollowers = this.contactsService.getFollowersForUser("userToFollow");

		assertEquals(userToFollowFollowers.size(), 3, "userToFollow has 3 followers");
		assertTrue(userToFollowFollowers.contains("jdubois"), "userToFollowFollowers contains jdubois");
		assertTrue(userToFollowFollowers.contains("duyhai"), "userToFollowFollowers contains duyhai");
		assertTrue(userToFollowFollowers.contains("tescolan"), "userToFollowFollowers contains tescolan");
	}

	@Test(dependsOnMethods = "testGetFollowersForUser")
	public void testGetFollowersRangeForUser() throws FunctionalException
	{
		Collection<User> userToFollowFollowers = this.contactsService.getFollowersForUser("userToFollow", "jdubois", 10);

		assertEquals(userToFollowFollowers.size(), 1, "userToFollow has 1 follower");
		assertTrue(userToFollowFollowers.contains(tescolan), "userToFollowFollowers contains tescolan");
	}

	@Test(dependsOnMethods = "testGetFollowersRangeForUser")
	public void testOnTweetPostSpreadTweetForContactsService() throws FunctionalException
	{

		tweet = this.tweetRepository.createTweet("userToFollow", "Test tweet", false);
		tweet2 = this.tweetRepository.createTweet("userToFollow", "Test tweet2", false);

		userToFollow = userService.getUserByLogin("userToFollow");

		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);

		this.contactsService.onTweetPost(tweet);
		this.contactsService.onTweetPost(tweet2);

		Collection<String> jduboisTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("jdubois", null, 10);

		assertEquals(jduboisTimelineTweets.size(), 2, "jdubois timeline has 2 tweets");
		assertTrue(jduboisTimelineTweets.contains(tweet.getTweetId()), "jduboisTimelineTweets contains 'Test tweet'");
		assertTrue(jduboisTimelineTweets.contains(tweet2.getTweetId()), "jduboisTimelineTweets contains 'Test tweet2'");

		Collection<String> duyhaiTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);

		assertEquals(duyhaiTimelineTweets.size(), 2, "duyhai timeline has 2 tweets");
		assertTrue(duyhaiTimelineTweets.contains(tweet.getTweetId()), "duyhaiTimelineTweets contains 'Test tweet'");
		assertTrue(duyhaiTimelineTweets.contains(tweet2.getTweetId()), "duyhaiTimelineTweets contains 'Test tweet2'");

		Collection<String> tescolanTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("tescolan", null, 10);

		assertEquals(tescolanTimelineTweets.size(), 2, "tescolan timeline has 2 tweets");
		assertTrue(tescolanTimelineTweets.contains(tweet.getTweetId()), "tescolanTimelineTweets contains 'Test tweet'");
		assertTrue(tescolanTimelineTweets.contains(tweet2.getTweetId()), "tescolanTimelineTweets contains 'Test tweet2'");
	}

	@Test(dependsOnMethods = "testOnTweetPostSpreadTweetForContactsService")
	public void testOnTweetPostNoActionForContactsService() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		Tweet tweet = this.tweetRepository.createTweet("jdubois", "My name is BOIS, DU-BOIS", false);

		this.contactsService.onTweetPost(tweet);

		// Only 2 tweets in jdubois timeline because of spreading of userToFollow tweets
		// The new 'My name is BOIS, DU-BOIS' tweet is not in jdubois timeline because timelineService.onTweetPost() not called

		Collection<String> tweetsJdubois = this.timeLineRepository.getTweetsRangeFromTimeline("jdubois", null, 10);
		assertEquals(tweetsJdubois.size(), 2, "jdubois timeline has 2 tweets");
	}

	@Test(dependsOnMethods = "testOnTweetPostNoActionForContactsService")
	public void testOnTweetRemoveForContactsService() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);
		this.contactsService.onTweetRemove(tweet2);
		Collection<String> jduboisTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("jdubois", null, 10);

		assertEquals(jduboisTimelineTweets.size(), 1, "jdubois timeline has 1 tweet");
		assertTrue(jduboisTimelineTweets.contains(tweet.getTweetId()), "jduboisTimelineTweets contains 'Test tweet'");
	}

	@Test(dependsOnMethods = "testOnTweetRemoveForContactsService")
	public void testOnUserForgetForContactsService() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.contactsService.onUserForget("userToFollow");

		jdubois = userService.getUserByLogin("jdubois");
		userToFollow = userService.getUserByLogin("userToFollow");

		assertEquals(jdubois.getFriendsCount(), 2, "jdubois has 2 friends");
		assertEquals(userToFollow.getFollowersCount(), 2, "userToFollow has 2 followers");
	}

	@Test(dependsOnMethods = "testOnUserForgetForContactsService", expectedExceptions = FunctionalException.class)
	public void testOnUserForgetSelfForContactsService() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);
		this.contactsService.onUserForget("jdubois");
	}

	@Test(dependsOnMethods = "testOnUserForgetSelfForContactsService", expectedExceptions = FunctionalException.class)
	public void testOnUserForgetAgainForContactsService() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);
		this.contactsService.onUserForget("userToFollow");
	}

	@Test(dependsOnMethods = "testOnUserForgetAgainForContactsService")
	public void testGetUserSuggestions() throws FunctionalException
	{
		String DAYLINE_KEY_FORMAT = "yyyyMMdd";

		DateTime today = new DateTime(new Date());
		statsRepository.addTweetToDay(tweet.getLogin(), today.toString(DAYLINE_KEY_FORMAT));
		statsRepository.addTweetToDay(tweet2.getLogin(), today.toString(DAYLINE_KEY_FORMAT));

		when(mockedAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		List<User> userSuggestions = this.contactsService.getUserSuggestions();

		assertEquals(userSuggestions.size(), 1, "userSuggestions size == 1 ");
		assertTrue(userSuggestions.contains(userToFollow), "userSuggestions contains userToFollow");
	}

	@Test(dependsOnMethods = "testGetUserSuggestions")
	public void testGetEmptyUserSuggestions() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		List<User> userSuggestions = this.contactsService.getUserSuggestions();

		assertEquals(userSuggestions.size(), 0, "userSuggestions size == 0 ");
	}

	@Test(dependsOnMethods = "testGetEmptyUserSuggestions")
	public void testOnRetweetForContactServiceTest() throws FunctionalException
	{

		Tweet newTweet = this.tweetRepository.createTweet("jdubois", "tweet3", false);
		Tweet newTweet2 = this.tweetRepository.createTweet("jdubois", "tweet4", false);

		retweet = this.tweetRepository.createTweet("userToFollow", "tweet3", false);
		retweet.setOriginalAuthorLogin("jdubois");
		retweet.setOriginalTweetId(newTweet.getTweetId());

		retweet2 = this.tweetRepository.createTweet("userToFollow", "tweet4", false);
		retweet2.setOriginalAuthorLogin("jdubois");
		retweet2.setOriginalTweetId(newTweet2.getTweetId());

		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);

		this.contactsService.onRetweet(retweet);
		this.contactsService.onRetweet(retweet2);

		Collection<String> duyhaiTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);
		assertTrue(duyhaiTimelineTweets.contains(retweet.getTweetId()), "duyhaiTimelineTweets contains 'tweet3'");
		assertTrue(duyhaiTimelineTweets.contains(retweet2.getTweetId()), "duyhaiTimelineTweets contains 'tweet4'");

		Collection<String> tweetsFollowedByDuyhaiFromUserToFollow = this.followerRepository.findTweetsForUserAndFollower("userToFollow", "duyhai");
		assertTrue(tweetsFollowedByDuyhaiFromUserToFollow.contains(retweet.getTweetId()), "tweetsFollowedByDuyhaiFromUserToFollow contains 'tweet3'");
		assertTrue(tweetsFollowedByDuyhaiFromUserToFollow.contains(retweet2.getTweetId()), "tweetsFollowedByDuyhaiFromUserToFollow contains 'tweet4'");

		Collection<String> tescolanTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("tescolan", null, 10);
		assertTrue(tescolanTimelineTweets.contains(retweet.getTweetId()), "tescolanTimelineTweets contains 'tweet3'");
		assertTrue(tescolanTimelineTweets.contains(retweet2.getTweetId()), "tescolanTimelineTweets contains 'tweet4'");

		Collection<String> tweetsFollowedByTescolanFromUserToFollow = this.followerRepository
				.findTweetsForUserAndFollower("userToFollow", "tescolan");
		assertTrue(tweetsFollowedByTescolanFromUserToFollow.contains(retweet.getTweetId()),
				"tweetsFollowedByTescolanFromUserToFollow contains 'tweet3'");
		assertTrue(tweetsFollowedByTescolanFromUserToFollow.contains(retweet2.getTweetId()),
				"tweetsFollowedByTescolanFromUserToFollow contains 'tweet4'");

	}

	@Test(dependsOnMethods = "testOnRetweetForContactServiceTest", expectedExceptions = FunctionalException.class)
	public void testOnRetweetExceptionForContactServiceTest() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);

		// Exception because userToFollow cannot retweet his own tweet
		this.contactsService.onRetweet(tweet);
	}

	@Test(dependsOnMethods = "testOnRetweetExceptionForContactServiceTest")
	public void testOnCancelTweetForContactServiceTest() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);

		ReTweetRepository mockedRetweetRepository = mock(ReTweetRepository.class);
		when(mockedRetweetRepository.findRetweetIdForRetweeter("userToFollow", retweet.getOriginalTweetId())).thenReturn(retweet.getTweetId());
		this.contactsService.setRetweetRepository(mockedRetweetRepository);

		this.contactsService.onCancelRetweet(retweet.getOriginalTweetId());

		Collection<String> duyhaiTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);
		assertFalse(duyhaiTimelineTweets.contains(retweet.getTweetId()), "duyhaiTimelineTweets doest not contain 'tweet3'");
		assertTrue(duyhaiTimelineTweets.contains(retweet2.getTweetId()), "duyhaiTimelineTweets contains 'tweet4'");

		Collection<String> tweetsFollowedByDuyhaiFromUserToFollow = this.followerRepository.findTweetsForUserAndFollower("userToFollow", "duyhai");
		assertFalse(tweetsFollowedByDuyhaiFromUserToFollow.contains(retweet.getTweetId()),
				"tweetsFollowedByDuyhaiFromUserToFollow doest not contain 'tweet3'");
		assertTrue(tweetsFollowedByDuyhaiFromUserToFollow.contains(retweet2.getTweetId()), "tweetsFollowedByDuyhaiFromUserToFollow contains 'tweet4'");

		Collection<String> tescolanTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("tescolan", null, 10);
		assertFalse(tescolanTimelineTweets.contains(retweet.getTweetId()), "tescolanTimelineTweets doest not contain 'tweet3'");
		assertTrue(tescolanTimelineTweets.contains(retweet2.getTweetId()), "tescolanTimelineTweets contains 'tweet4'");

		Collection<String> tweetsFollowedByTescolanFromUserToFollow = this.followerRepository
				.findTweetsForUserAndFollower("userToFollow", "tescolan");
		assertFalse(tweetsFollowedByTescolanFromUserToFollow.contains(retweet.getTweetId()),
				"tweetsFollowedByTescolanFromUserToFollow doest not contain 'tweet3'");
		assertTrue(tweetsFollowedByTescolanFromUserToFollow.contains(retweet2.getTweetId()),
				"tweetsFollowedByTescolanFromUserToFollow contains 'tweet4'");

	}

	@Test(dependsOnMethods = "testOnCancelTweetForContactServiceTest", expectedExceptions = FunctionalException.class)
	public void testOnCancelTweetExceptionForContactServiceTest() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);
		this.contactsService.setRetweetRepository(this.retweetRepository);

		// Exception because userToFollow never retweeted his own retweet...
		this.contactsService.onCancelRetweet(retweet.getTweetId());
	}

	@Test(dependsOnMethods = "testOnCancelTweetExceptionForContactServiceTest")
	public void testOnTweetRemoveWithRetweetForContactServiceTest() throws FunctionalException
	{
		when(mockedAuthenticationService.getCurrentUser()).thenReturn(userToFollow);

		this.contactsService.onTweetRemove(tweet);
	}
}
