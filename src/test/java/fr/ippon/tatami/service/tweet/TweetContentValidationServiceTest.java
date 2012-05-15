package fr.ippon.tatami.service.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.security.AuthenticationService;

public class TweetContentValidationServiceTest extends AbstractCassandraTatamiTest
{

	@Test(expectedExceptions = FunctionalException.class)
	public void testOnTweetPostExceptionForTweetContentValidationServiceTest() throws FunctionalException
	{
		User duyhai = new User();
		duyhai.setLogin("duyhai");

		this.userRepository.createUser(duyhai);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(duyhai);
		this.tweetService.setAuthenticationService(mockAuthenticationService);

		Tweet tweet = this.tweetService
				.createTransientTweet("123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789..");

		TweetContentValidationService tweetContentValidationService = new TweetContentValidationService();

		tweetContentValidationService.onTweetPost(tweet);
	}

	@Test(dependsOnMethods = "testOnTweetPostExceptionForTweetContentValidationServiceTest")
	public void testOnTweetPostForTweetContentValidationServiceTest() throws FunctionalException
	{
		Tweet tweet = this.tweetService
				.createTransientTweet("123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456 http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Class.html");
		TweetContentValidationService tweetContentValidationService = new TweetContentValidationService();

		tweetContentValidationService.onTweetPost(tweet);
	}
}
