package fr.ippon.tatami.service.lines;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class MentionlineServiceTest extends AbstractCassandraTatamiTest
{

	private User jdubois, duyhai;
	private Tweet tweet, tweet2, randomTweet;
	private AuthenticationService mockAuthenticationService;

	@Test
	public void initForMentionlineServiceTest()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		duyhai = new User();
		duyhai.setLogin("duyhai");
		duyhai.setEmail("duyhai@ippon.fr");
		duyhai.setFirstName("DuyHai");
		duyhai.setLastName("DOAN");

		this.userRepository.createUser(jdubois);
		this.userRepository.createUser(duyhai);

		mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.userService.setAuthenticationService(mockAuthenticationService);
	}

	@Test(dependsOnMethods = "initForMentionlineServiceTest")
	public void testOnTweetPostSpreadTweetForMentionLine() throws FunctionalException
	{
		tweet = this.tweetService.createTransientTweet("Hello &#x40;duyhai, how are you ?");
		this.mentionlineService.onTweetPost(tweet);

		Collection<String> duyhaiTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 2);

		assertEquals(duyhaiTimeline.size(), 1, "duyhai' timeline has 1 tweet");
		assertTrue(duyhaiTimeline.contains(tweet.getTweetId()), "duyhaiTimeline contains 'Hello &#x40;duyhai, how are you ?'");
	}

	@Test(dependsOnMethods = "testOnTweetPostSpreadTweetForMentionLine")
	public void testOnTweetPostNoActionForMentionLine() throws FunctionalException
	{
		randomTweet = this.tweetService.createTransientTweet("Random tweet with no mention");
		this.mentionlineService.onTweetPost(randomTweet);

		Collection<String> duyhaiTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 2);

		assertEquals(duyhaiTimeline.size(), 1, "duyhai' timeline has 1 tweet");
		assertFalse(duyhaiTimeline.contains(randomTweet.getTweetId()), "duyhaiTimeline does not contain 'Random tweet with no mention'");
	}

	@Test(dependsOnMethods = "testOnTweetPostNoActionForMentionLine")
	public void testOnTweetPostNoActionBecauseFollowerForMentionLine() throws FunctionalException
	{
		// DuyHai now follows Julien
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		this.contactsService.onUserFollow("jdubois");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		tweet2 = this.tweetService.createTransientTweet("Hello &#x40;duyhai, how are you second time?");
		this.mentionlineService.onTweetPost(tweet2);

		Collection<String> duyhaiTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 2);

		assertEquals(duyhaiTimeline.size(), 1, "duyhai' timeline has 1 tweet");
		assertFalse(duyhaiTimeline.contains(tweet2.getTweetId()), "duyhaiTimeline does not contain 'Hello &#x40;duyhai, how are you second time?'");

	}

	@Test(dependsOnMethods = "testOnTweetPostNoActionBecauseFollowerForMentionLine")
	public void testOnTweetRemoveNoActionForMentionLine() throws FunctionalException
	{
		this.mentionlineService.onTweetRemove(tweet2);
		Collection<String> duyhaiTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 2);

		assertEquals(duyhaiTimeline.size(), 1, "duyhai' timeline has 1 tweet");
	}

	@Test(dependsOnMethods = "testOnTweetRemoveNoActionForMentionLine")
	public void testOnTweetRemoveForMentionLine() throws FunctionalException
	{
		this.mentionlineService.onTweetRemove(tweet);
		Collection<String> duyhaiTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 2);

		assertEquals(duyhaiTimeline.size(), 0, "duyhai' timeline has no tweet");
	}
}
