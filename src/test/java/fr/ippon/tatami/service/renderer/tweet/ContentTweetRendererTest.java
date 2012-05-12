package fr.ippon.tatami.service.renderer.tweet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import org.owasp.esapi.reference.DefaultEncoder;
import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.security.AuthenticationService;

public class ContentTweetRendererTest extends AbstractCassandraTatamiTest
{

	private ContentTweetRenderer contentTweetRenderer;

	@Test
	public void initContentTweetRendererTest()
	{
		User jdubois = new User();
		jdubois.setLogin("jdubois");

		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(jdubois);
		this.tweetService.setAuthenticationService(mockAuthenticationService);

		contentTweetRenderer = new ContentTweetRenderer();
	}

	@Test(dependsOnMethods = "initContentTweetRendererTest")
	public void testOnRenderUserPattern()
	{
		Tweet tweet = this.tweetService.createTransientTweet(DefaultEncoder.getInstance().encodeForHTML("Hello @duyhai, how are you ?"));
		this.contentTweetRenderer.onRender(tweet);

		assertTrue(tweet.getContent().contains("<a href='#' data-user='duyhai' title='Show duyhai tweets'><em>@duyhai</em></a>"),
				"tweet contains <a href='#' data-user='duyhai' title='Show duyhai tweets'><em>@duyhai</em></a>");
	}

	@Test(dependsOnMethods = "initContentTweetRendererTest")
	public void testOnRenderHashtagPattern()
	{
		Tweet tweet = this.tweetService.createTransientTweet(DefaultEncoder.getInstance().encodeForHTML("Hello, #Cassandra is cool!"));
		this.contentTweetRenderer.onRender(tweet);

		assertTrue(tweet.getContent().contains("<a href='#' data-tag='Cassandra' title='Show Cassandra related tweets'><em>#Cassandra</em></a>"),
				"tweet contains <a href='#' data-tag='Cassandra' title='Show Cassandra related tweets'><em>#Cassandra</em></a>");
	}

	@Test(dependsOnMethods = "initContentTweetRendererTest")
	public void testOnRenderUrlPattern()
	{
		Tweet tweet = this.tweetService.createTransientTweet(DefaultEncoder.getInstance().encodeForHTML(
				"Hello, try http://www.lemonde.fr/election-presidentielle-2012/article/2012/05/11, it's cool"));
		this.contentTweetRenderer.onRender(tweet);

		assertTrue(tweet.getContent().contains(
				"<a href='http://www.lemonde.fr/election-presidentielle-2012/article/2012/05/11' "
						+ "title='http://www.lemonde.fr/election-presidentielle-2012/article/2012/05/11' target='_blank'>www.lemond...</a>"),
				"tweet contains <a href='http://www.lemonde.fr/election-presidentielle-2012/article/2012/05/11' "
						+ "title='http://www.lemonde.fr/election-presidentielle-2012/article/2012/05/11' target='_blank'>www.lemond...</a>");
	}

	@Test(dependsOnMethods = "initContentTweetRendererTest")
	public void testOnRenderWWWUrlPattern()
	{
		Tweet tweet = this.tweetService.createTransientTweet(DefaultEncoder.getInstance().encodeForHTML("Hello, try www.lemonde.fr: it's cool"));
		this.contentTweetRenderer.onRender(tweet);

		assertTrue(tweet.getContent().contains("<a href='www.lemonde.fr' " + "title='www.lemonde.fr' target='_blank'>www.lemond...</a>"),
				"tweet contains <a href='www.lemonde.fr' " + "title='www.lemonde.fr' target='_blank'>www.lemond...</a>");
	}

	@Test(dependsOnMethods = "initContentTweetRendererTest")
	public void testOnRenderHTTPSFullUrlPattern()
	{
		Tweet tweet = this.tweetService.createTransientTweet(DefaultEncoder.getInstance()
				.encodeForHTML("Hello, try https://www.gmail.com: it's cool"));
		this.contentTweetRenderer.onRender(tweet);

		assertTrue(tweet.getContent()
				.contains("<a href='https://www.gmail.com' " + "title='https://www.gmail.com' target='_blank'>www.gmail.com</a>"),
				"tweet contains <a href='https://www.gmail.com' " + "title='https://www.gmail.com' target='_blank'>www.gmail.com</a>");
	}
}
