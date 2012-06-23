package fr.ippon.tatami.service.pipeline.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class TweetPipelineManagerRetweetTest extends AbstractCassandraTatamiTest
{

	private User jdubois, duyhai, tescolan, uncleBob;
	private Tweet t5, t6;
	private AuthenticationService mockAuthenticationService;

	@Test
	public void initTweetPipelineManagerRetweetTest()
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

		tescolan = new User();
		tescolan.setLogin("tescolan");
		tescolan.setEmail("tescolan@ippon.fr");
		tescolan.setFirstName("Thomas");
		tescolan.setLastName("ESCOLAN");

		uncleBob = new User();
		uncleBob.setLogin("uncleBob");
		uncleBob.setEmail("uncleBob@ippon.fr");
		uncleBob.setFirstName("Uncle");
		uncleBob.setLastName("BOB");

		this.userRepository.createUser(jdubois);
		this.userRepository.createUser(duyhai);
		this.userRepository.createUser(tescolan);
		this.userRepository.createUser(uncleBob);

		mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetService.setAuthenticationService(mockAuthenticationService);
		this.userService.setAuthenticationService(mockAuthenticationService);
		this.contactsService.setAuthenticationService(mockAuthenticationService);
		this.retweetService.setAuthenticationService(mockAuthenticationService);

	}

	@Test(dependsOnMethods = "initTweetPipelineManagerRetweetTest")
	public void testOnRetweet() throws FunctionalException
	{

		// jdubois posts tweets
		this.tweetPipelineManager.onPost("tweet1");
		this.tweetPipelineManager.onPost("tweet2");
		this.tweetPipelineManager.onPost("tweet3");
		this.tweetPipelineManager.onPost("tweet4");

		// tescolan follows jdubois
		when(mockAuthenticationService.getCurrentUser()).thenReturn(tescolan);
		this.userPipelineManager.onFollow("jdubois");

		// DuyHai follows jdubois
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		this.userPipelineManager.onFollow("jdubois");

		// Refresh jdubois
		jdubois = this.userService.getUserByLogin("jdubois");

		// jdubois now follows uncleBob
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.userPipelineManager.onFollow("uncleBob");

		// uncleBob post a tweet
		when(mockAuthenticationService.getCurrentUser()).thenReturn(uncleBob);
		t5 = this.tweetPipelineManager.onPost("tweet5 advice");
		t6 = this.tweetPipelineManager.onPost("tweet6 wisdom");

		// jdubois now retweet uncle bob tweet
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetPipelineManager.onRetweet(t5.getTweetId());
		this.tweetPipelineManager.onRetweet(t6.getTweetId());

		Collection<String> t5Retweeters = this.retweetRepository.findRetweetersForTweet(t5.getTweetId());
		Collection<String> t6Retweeters = this.retweetRepository.findRetweetersForTweet(t6.getTweetId());

		assertTrue(t5Retweeters.contains("jdubois"), "jdubois is retweeter of tweet5");
		assertTrue(t6Retweeters.contains("jdubois"), "jdubois is retweeter of tweet6");

		String t5RetweetId = this.retweetRepository.findRetweetIdForRetweeter("jdubois", t5.getTweetId());
		String t6RetweetId = this.retweetRepository.findRetweetIdForRetweeter("jdubois", t6.getTweetId());

		Collection<String> retweetIdsForTweet5 = this.retweetRepository.findRetweetIdsForTweet(t5.getTweetId());
		Collection<String> retweetIdsForTweet6 = this.retweetRepository.findRetweetIdsForTweet(t6.getTweetId());

		assertTrue(retweetIdsForTweet5.contains(t5RetweetId));
		assertTrue(retweetIdsForTweet6.contains(t6RetweetId));

		boolean isT5inJduboisRetweetline = this.retweetRepository.isTweetInRetweetLine("jdubois", t5.getTweetId());
		boolean isT6inJduboisRetweetline = this.retweetRepository.isTweetInRetweetLine("jdubois", t6.getTweetId());

		assertTrue(isT5inJduboisRetweetline, "tweet5 is in jdubois retweet line");
		assertTrue(isT6inJduboisRetweetline, "tweet6 is in jdubois retweet line");

		Collection<String> duyhaiTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);
		Collection<String> tescolanTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("tescolan", null, 10);

		assertTrue(duyhaiTimeline.contains(t5RetweetId), "duyhai timeline contains a retweet of tweet5");
		assertTrue(duyhaiTimeline.contains(t6RetweetId), "duyhai timeline contains a retweet of tweet6");

		assertTrue(tescolanTimeline.contains(t5RetweetId), "tescolan timeline contains a retweet of tweet5");
		assertTrue(tescolanTimeline.contains(t6RetweetId), "tescolan timeline contains a retweet of tweet6");

	}

	@Test(dependsOnMethods = "testOnRetweet", expectedExceptions = FunctionalException.class)
	public void testOnRetweetWithException() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(uncleBob);

		// Exception, cannot retweet his own tweet
		this.tweetPipelineManager.onRetweet(t5.getTweetId());
	}

	@Test(dependsOnMethods = "testOnRetweetWithException")
	public void testOnCancelRetweet() throws FunctionalException
	{
		// jdubois now retweet uncle bob tweet
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetPipelineManager.onCancelRetweet(t5.getTweetId());

		Collection<String> t5Retweeters = this.retweetRepository.findRetweetersForTweet(t5.getTweetId());

		assertTrue(t5Retweeters.isEmpty(), "No retweeter for tweet5");

		String t5RetweetId = this.retweetRepository.findRetweetIdForRetweeter("jdubois", t5.getTweetId());

		assertNull(t5RetweetId, "t5RetweetId not found");

		Collection<String> retweetIdsForTweet5 = this.retweetRepository.findRetweetIdsForTweet(t5.getTweetId());
		assertTrue(retweetIdsForTweet5.isEmpty(), "No retweetId for tweet5");

		boolean isT5inJduboisRetweetline = this.retweetRepository.isTweetInRetweetLine("jdubois", t5.getTweetId());

		assertFalse(isT5inJduboisRetweetline, "tweet5 is not in jdubois retweet line");

		Collection<String> duyhaiTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);
		Collection<String> tescolanTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("tescolan", null, 10);

		assertEquals(duyhaiTimeline.size(), 1, "duyhai timeline has 1 tweets");
		assertEquals(tescolanTimeline.size(), 1, "tescolan timeline has 1 tweet");
	}

	@Test(dependsOnMethods = "testOnCancelRetweet", expectedExceptions = FunctionalException.class)
	public void testOnCancelRetweetWithException() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);

		// Exception because tweet5 retweet has been already canceled
		this.tweetPipelineManager.onCancelRetweet(t5.getTweetId());
	}

	@Test(dependsOnMethods = "testOnCancelRetweetWithException")
	public void testOnRemoveTweetWithRetweet() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(uncleBob);

		// Exception because tweet5 cannot cancel reweet on its own
		this.tweetPipelineManager.onRemove(t6.getTweetId());

		Collection<String> t6Retweeters = this.retweetRepository.findRetweetersForTweet(t6.getTweetId());

		assertTrue(t6Retweeters.isEmpty(), "No retweeter for tweet6");

		String t6RetweetId = this.retweetRepository.findRetweetIdForRetweeter("jdubois", t6.getTweetId());

		assertNull(t6RetweetId, "t6RetweetId not found");

		Collection<String> retweetIdsForTweet6 = this.retweetRepository.findRetweetIdsForTweet(t6.getTweetId());
		assertTrue(retweetIdsForTweet6.isEmpty(), "No retweetId for tweet6");

		boolean isT6inJduboisRetweetline = this.retweetRepository.isTweetInRetweetLine("jdubois", t6.getTweetId());

		assertFalse(isT6inJduboisRetweetline, "tweet6 is not in jdubois retweet line");

		Collection<String> duyhaiTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);
		Collection<String> tescolanTimeline = this.timeLineRepository.getTweetsRangeFromTimeline("tescolan", null, 10);

		assertEquals(duyhaiTimeline.size(), 0, "duyhai timeline has 0 tweets");
		assertEquals(tescolanTimeline.size(), 0, "tescolan timeline has 0 tweet");
	}
}
