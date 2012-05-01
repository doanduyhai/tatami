package fr.ippon.tatami.service.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.security.AuthenticationService;

public class XssEncodingServiceTest extends AbstractCassandraTatamiTest
{
	@Test
	public void testOnTweetPost()
	{
		User jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetService.setAuthenticationService(mockAuthenticationService);

		Tweet tweet = this.tweetService.createTransientTweet("Test tweet creation <script>alert('foo bar');</script>");

		this.xssEncodingService.onTweetPost(tweet);

		assertFalse(tweet.getContent().contains("<script>alert('foo bar');</script>"), "<script> tag has been encoded");
	}
}
