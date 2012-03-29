package fr.ippon.tatami.repository;

import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.TagLine;

public class TagRepositoryTest extends AbstractCassandraTatamiTest
{
	@Test
	public void testAddTweet()
	{
		this.tagRepository.addTweet("tag", "tweet1");
		this.tagRepository.addTweet("tag", "tweet2");

		Collection<String> tweetIds = this.entityManager.find(TagLine.class, "tag").getTweetIds();

		assertTrue(tweetIds.size() == 2, "tweetIds.size() == 2");
		assertTrue(tweetIds.contains("tweet1"), "tweet1 has 'tag'");
		assertTrue(tweetIds.contains("tweet2"), "tweet2 has 'tag'");
	}

	@Test(dependsOnMethods = "testAddTweet")
	public void testFindTweetsForTag()
	{
		Collection<String> tweetIds = this.tagRepository.findTweetsForTag("tag");

		assertTrue(tweetIds.size() == 2, "tweetIds.size()");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
	}

	@Test(dependsOnMethods = "testFindTweetsForTag")
	public void testRemoveTweet()
	{
		this.tagRepository.removeTweet("tag", "tweet1");
		this.tagRepository.removeTweet("tag", "tweet2");

		Collection<String> tweetIds = this.tagRepository.findTweetsForTag("tag");

		assertTrue(tweetIds.size() == 0, "tweetIds.size()==0");

	}

}
