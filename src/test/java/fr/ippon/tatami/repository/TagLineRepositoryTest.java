package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.TAGLINE_CF;
import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_TAG_LIST_SIZE;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class TagLineRepositoryTest extends AbstractCassandraTatamiTest
{
	private Tweet tweet1;
	private Tweet tweet2;
	private Tweet tweet3;
	private Tweet tweet4;
	private Tweet tweet5;

	@Test
	public void testAddTweetToTagline()
	{
		tweet1 = this.tweetRepository.createTweet("test", "tweet1", false);
		tweet2 = this.tweetRepository.createTweet("test", "tweet2", false);
		tweet3 = this.tweetRepository.createTweet("test", "tweet3", false);
		tweet4 = this.tweetRepository.createTweet("test", "tweet4", false);
		tweet5 = this.tweetRepository.createTweet("test", "tweet5", false);

		this.tagLineRepository.addTweet("tag", tweet1.getTweetId());
		this.tagLineRepository.addTweet("tag", tweet2.getTweetId());
		this.tagLineRepository.addTweet("tag", tweet3.getTweetId());
		this.tagLineRepository.addTweet("tag", tweet4.getTweetId());
		this.tagLineRepository.addTweet("tag", tweet5.getTweetId());

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(TAGLINE_CF).setKey("tag")
				.setRange(null, null, true, 100).execute().get().getColumns();

		List<String> tweetIds = new ArrayList<String>();
		for (HColumn<String, Object> column : columns)
		{
			tweetIds.add(column.getName());
		}

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains(tweet1.getTweetId()), "tweet1 has 'tag'");
		assertTrue(tweetIds.contains(tweet2.getTweetId()), "tweet2 has 'tag'");
		assertTrue(tweetIds.contains(tweet3.getTweetId()), "tweet3 has 'tag'");
		assertTrue(tweetIds.contains(tweet4.getTweetId()), "tweet4 has 'tag'");
		assertTrue(tweetIds.contains(tweet5.getTweetId()), "tweet5 has 'tag'");
	}

	@Test(dependsOnMethods = "testAddTweetToTagline")
	public void testFindTweetsForTag()
	{
		Collection<String> tweetIds = this.tagLineRepository.findTweetsRangeForTag("tag", null, DEFAULT_TAG_LIST_SIZE);

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains(tweet1.getTweetId()), "tweet1 has 'tag'");
		assertTrue(tweetIds.contains(tweet2.getTweetId()), "tweet2 has 'tag'");
		assertTrue(tweetIds.contains(tweet3.getTweetId()), "tweet3 has 'tag'");
		assertTrue(tweetIds.contains(tweet4.getTweetId()), "tweet4 has 'tag'");
		assertTrue(tweetIds.contains(tweet5.getTweetId()), "tweet5 has 'tag'");
	}

	@Test(dependsOnMethods = "testFindTweetsForTag")
	public void testFindTweetsRangeForTag()
	{
		Collection<String> tweetIds = this.tagLineRepository.findTweetsRangeForTag("tag", tweet5.getTweetId(), 3);

		assertTrue(tweetIds.size() == 3, "tweetIds.size() == 3");
		assertTrue(tweetIds.contains(tweet2.getTweetId()), "tweet2 has 'tag'");
		assertTrue(tweetIds.contains(tweet3.getTweetId()), "tweet3 has 'tag'");
		assertTrue(tweetIds.contains(tweet4.getTweetId()), "tweet4 has 'tag'");
	}

	@Test(dependsOnMethods = "testFindTweetsRangeForTag")
	public void testFindTweetsRangeOutOfBoundsForTag()
	{
		Collection<String> tweetIds = this.tagLineRepository.findTweetsRangeForTag("tag", tweet1.getTweetId(), 3);

		assertTrue(tweetIds.size() == 0, "tweetIds.size() == 0");

	}

	@Test(dependsOnMethods = "testFindTweetsRangeOutOfBoundsForTag")
	public void testRemoveTweetFromTagLine()
	{
		this.tagLineRepository.removeTweet("tag", tweet2.getTweetId());

		this.tagLineRepository.removeTweet("tag", tweet4.getTweetId());

		Collection<String> tweetIds = this.tagLineRepository.findTweetsRangeForTag("tag", tweet5.getTweetId(), 2);

		assertTrue(tweetIds.size() == 2, "userFavorites.size()==2");
		assertTrue(tweetIds.contains(tweet1.getTweetId()), "tweet1 has 'tag'");
		assertTrue(tweetIds.contains(tweet3.getTweetId()), "tweet3 has 'tag'");
	}

}
