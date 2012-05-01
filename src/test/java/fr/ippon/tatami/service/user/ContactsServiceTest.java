package fr.ippon.tatami.service.user;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;

public class ContactsServiceTest extends AbstractCassandraTatamiTest
{

	private User jdubois, userToFollow;

	@Test
	public void init()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		String login = "userToFollow";
		String firstName = "New";
		String lastName = "User";
		String email = "nuser@ippon.fr";
		String gravatar = "newGravatar";

		userToFollow = new User();
		userToFollow.setLogin(login);
		userToFollow.setFirstName(firstName);
		userToFollow.setLastName(lastName);
		userToFollow.setEmail(email);
		userToFollow.setGravatar(gravatar);

		this.userRepository.createUser(userToFollow);

		assertNotNull(this.userRepository.findUserByLogin("jdubois"), "jdubois has been created");
		assertNotNull(this.userRepository.findUserByLogin("userToFollow"), "userToFollow has been created");
	}

	@Test(dependsOnMethods = "init")
	public void testOnTweetPostSpreadTweet() throws FunctionalException
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		this.userService.followUser("userToFollow");

		Tweet tweet = this.tweetRepository.createTweet("userToFollow", "Test tweet");

		User userToFollow = userService.getUserByLogin("userToFollow");
		mockAuthenticatedUser(userToFollow);

		this.contactsService.onTweetPost(tweet);

		Collection<String> tweets = this.timeLineRepository.getTweetsRangeFromTimeline(jdubois, null, 2);

		assertEquals(tweets.size(), 1, "jdubois timeline has 1 tweet");
		assertTrue(tweets.contains(tweet.getTweetId()), "tweets contains 'Test tweet'");
	}

	@Test(dependsOnMethods = "testOnTweetPostSpreadTweet")
	public void testOnTweetPostNoAction() throws FunctionalException
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		Tweet tweet = this.tweetRepository.createTweet("jdubois", "My name is BOIS, DU-BOIS");

		this.contactsService.onTweetPost(tweet);

		Collection<String> tweets = this.timeLineRepository.getTweetsRangeFromTimeline(userToFollow, null, 1);
		assertEquals(tweets.size(), 1, "userToFollow timeline has 1 tweet");

		Collection<String> tweetsJdubois = this.timeLineRepository.getTweetsRangeFromTimeline(jdubois, null, 1);
		assertEquals(tweetsJdubois.size(), 1, "jdubois timeline has 1 tweet");
	}
}
