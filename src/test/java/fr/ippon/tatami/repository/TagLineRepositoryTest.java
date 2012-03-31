package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.TAGLINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.service.util.TatamiConstants;

public class TagLineRepositoryTest extends AbstractCassandraTatamiTest
{
	@Test
	public void testAddTweet()
	{
		this.tagLineRepository.addTweet("tag", "tweet1");
		this.tagLineRepository.addTweet("tag", "tweet2");
		this.tagLineRepository.addTweet("tag", "tweet3");
		this.tagLineRepository.addTweet("tag", "tweet4");
		this.tagLineRepository.addTweet("tag", "tweet5");

		List<HColumn<Long, String>> columns = createSliceQuery(keyspace, se, le, se).setColumnFamily(TAGLINE_CF).setKey("tag")
				.setRange(4L, 0L, true, TatamiConstants.DEFAULT_TAG_LIST_SIZE).execute().get().getColumns();

		List<String> tweetIds = new ArrayList<String>();
		for (HColumn<Long, String> column : columns)
		{
			tweetIds.add(column.getValue());
		}

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains("tweet1"), "tweet1 has 'tag'");
		assertTrue(tweetIds.contains("tweet2"), "tweet2 has 'tag'");
		assertTrue(tweetIds.contains("tweet3"), "tweet3 has 'tag'");
		assertTrue(tweetIds.contains("tweet4"), "tweet4 has 'tag'");
		assertTrue(tweetIds.contains("tweet5"), "tweet5 has 'tag'");
	}

	@Test(dependsOnMethods = "testAddTweet")
	public void testFindTweetsForTag()
	{
		Collection<String> tweetIds = this.tagLineRepository.findTweetsForTag("tag");

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains("tweet1"), "tweet1 has 'tag'");
		assertTrue(tweetIds.contains("tweet2"), "tweet2 has 'tag'");
		assertTrue(tweetIds.contains("tweet3"), "tweet3 has 'tag'");
		assertTrue(tweetIds.contains("tweet4"), "tweet4 has 'tag'");
		assertTrue(tweetIds.contains("tweet5"), "tweet5 has 'tag'");
	}

	@Test(dependsOnMethods = "testAddTweet")
	public void testFindTweetsRangeForTag()
	{
		Collection<String> tweetIds = this.tagLineRepository.findTweetsRangeForTag("tag", 1, 3);

		assertTrue(tweetIds.size() == 3, "tweetIds.size() == 3");
		assertTrue(tweetIds.contains("tweet3"), "tweet3 has 'tag'");
		assertTrue(tweetIds.contains("tweet4"), "tweet4 has 'tag'");
		assertTrue(tweetIds.contains("tweet5"), "tweet5 has 'tag'");
	}

}
