package fr.ippon.tatami.service.pipeline.user;

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
import fr.ippon.tatami.service.pipeline.user.UserHandler;
import fr.ippon.tatami.service.pipeline.user.UserPipelineManager;
import fr.ippon.tatami.service.security.AuthenticationService;

public class UserPipelineManagerTest extends AbstractCassandraTatamiTest
{
	private UserPipelineManager manager;
	private User jdubois, duyhai;

	private AuthenticationService mockAuthenticationService;
	private InOrder mockOrder;
	private UserHandler handler1, handler2;

	@Test
	public void initUserPipelineManagerTest()
	{
		manager = new UserPipelineManager();

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
		this.userService.setAuthenticationService(mockAuthenticationService);
		this.tweetService.setAuthenticationService(mockAuthenticationService);

		handler1 = mock(UserHandler.class);
		handler2 = mock(UserHandler.class);

		mockOrder = Mockito.inOrder(handler1, handler2);

		manager.setUserHandlers(Arrays.asList(handler1, handler2));
	}

	@Test(dependsOnMethods = "initUserPipelineManagerTest")
	public void testMockedOnFollow() throws FunctionalException
	{
		manager.onFollow("duyhai");

		mockOrder.verify(handler1).onUserFollow("duyhai");
		mockOrder.verify(handler2).onUserFollow("duyhai");
	}

	@Test(dependsOnMethods = "testMockedOnFollow")
	public void testMockedOnForget() throws FunctionalException
	{
		manager.onForget("duyhai");

		mockOrder.verify(handler1).onUserForget("duyhai");
		mockOrder.verify(handler2).onUserForget("duyhai");
	}

	@Test(dependsOnMethods = "testMockedOnForget")
	public void testOnFollow() throws FunctionalException
	{
		this.userPipelineManager.onFollow("duyhai");

		jdubois = this.userService.getUserByLogin("jdubois");
		duyhai = this.userService.getUserByLogin("duyhai");

		assertEquals(jdubois.getFriendsCount(), 1, "jdubois has 1 friend");
		assertEquals(duyhai.getFollowersCount(), 1, "duyhai has 1 follower");

		Collection<String> duyhaiTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("duyhai", null, 10);

		assertEquals(duyhaiTimelineTweets.size(), 1, "duyhaiTimelineTweets has 1 alert tweet");

		Tweet tweet = this.tweetRepository.findTweetById(duyhaiTimelineTweets.iterator().next());

		assertTrue(tweet.getContent().contains("<strong>is now following you</strong>"),
				"alert tweet contains '<strong>is now following you</strong>'");

		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);

		Tweet duyhaiTweet = this.tweetRepository.createTweet("duyhai", "Hello @jdubois", false);

		this.contactsService.onTweetPost(duyhaiTweet);
		this.timelineService.onTweetPost(duyhaiTweet);

		Collection<String> jduboisTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("jdubois", null, 10);

		assertEquals(jduboisTimelineTweets.size(), 1, "jduboisTimelineTweets has 1 tweet");

		tweet = this.tweetRepository.findTweetById(jduboisTimelineTweets.iterator().next());

		assertTrue(tweet.getContent().contains("Hello @jdubois"), "jdubois timeline tweet contains 'Hello @jdubois'");

		duyhaiTweet = this.tweetRepository.createTweet("duyhai", "Hello @jdubois second time", false);

		this.contactsService.onTweetPost(duyhaiTweet);
		this.timelineService.onTweetPost(duyhaiTweet);

		jduboisTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("jdubois", null, 10);

		assertEquals(jduboisTimelineTweets.size(), 2, "jduboisTimelineTweets has 2 tweets");
	}

	@Test(dependsOnMethods = "testOnFollow", expectedExceptions = FunctionalException.class)
	public void testOnFollowWithException() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.userPipelineManager.onFollow("jdubois");
	}

	@Test(dependsOnMethods = "testOnFollow")
	public void testOnForget() throws FunctionalException
	{

		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.userPipelineManager.onForget("duyhai");

		jdubois = this.userService.getUserByLogin("jdubois");
		duyhai = this.userService.getUserByLogin("duyhai");

		assertEquals(jdubois.getFriendsCount(), 0, "jdubois has 0 friend");
		assertEquals(duyhai.getFollowersCount(), 0, "duyhai has 0 follower");

		Collection<String> jduboisTimelineTweets = this.timeLineRepository.getTweetsRangeFromTimeline("jdubois", null, 10);

		assertEquals(jduboisTimelineTweets.size(), 0, "jduboisTimelineTweets has been cleaned");

	}

	@Test(dependsOnMethods = "testOnForget", expectedExceptions = FunctionalException.class)
	public void testOnForgetWithException() throws FunctionalException
	{
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.userPipelineManager.onForget("jdubois");
	}
}
