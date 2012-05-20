package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWED_TWEET_INDEX_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;

public class FollowerTweetIndexRepositoryTest extends AbstractCassandraTatamiTest
{

	private Tweet t1, t2, t3, t4;

	@Test
	public void testAddTweetToIndexForFollower()
	{
		t1 = this.tweetRepository.createTweet("duyhai", "tweet1", false);
		t2 = this.tweetRepository.createTweet("duyhai", "tweet2", false);
		t3 = this.tweetRepository.createTweet("duyhai", "tweet3", false);
		t4 = this.tweetRepository.createTweet("duyhai", "tweet4", false);

		this.followerTweetIndexRepository.addTweetToIndex("duyhai", "jdubois", t1.getTweetId());
		this.followerTweetIndexRepository.addTweetToIndex("duyhai", "jdubois", t2.getTweetId());
		this.followerTweetIndexRepository.addTweetToIndex("duyhai", "jdubois", t3.getTweetId());
		this.followerTweetIndexRepository.addTweetToIndex("duyhai", "jdubois", t4.getTweetId());

		List<String> followedTweets = new ArrayList<String>();
		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FOLLOWED_TWEET_INDEX_CF)
				.setKey("duyhai:jdubois").setRange(null, null, true, 100).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			followedTweets.add(column.getName());
		}

		assertTrue(followedTweets.size() == 4, "followedTweets.size() == 4");
	}

	@Test(dependsOnMethods = "testAddTweetToIndexForFollower")
	public void testFindTweetsForUserAndFollower()
	{
		Collection<String> followedTweets = this.followerTweetIndexRepository.findTweetsForUserAndFollower("duyhai", "jdubois");

		assertEquals(followedTweets.size(), 4, "followedTweets.size() == 4");
		assertTrue(followedTweets.contains(t1.getTweetId()), "followedTweets has tweet1");
		assertTrue(followedTweets.contains(t2.getTweetId()), "followedTweets has tweet2");
		assertTrue(followedTweets.contains(t3.getTweetId()), "followedTweets has tweet3");
		assertTrue(followedTweets.contains(t4.getTweetId()), "followedTweets has tweet4");
	}

	@Test(dependsOnMethods = "testFindTweetsForUserAndFollower")
	public void testRemoveTweetFromIndexForFollower()
	{
		this.followerTweetIndexRepository.removeTweetFromIndex("duyhai", "jdubois", t2.getTweetId());
		this.followerTweetIndexRepository.removeTweetFromIndex("duyhai", "jdubois", t3.getTweetId());

		List<String> followedTweets = new ArrayList<String>();
		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FOLLOWED_TWEET_INDEX_CF)
				.setKey("duyhai:jdubois").setRange(null, null, true, 100).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			followedTweets.add(column.getName());
		}

		assertTrue(followedTweets.size() == 2, "followedTweets.size() == 2");
		assertEquals(followedTweets.get(0), t4.getTweetId(), "followedTweets.get(0) == tweet4");
		assertEquals(followedTweets.get(1), t1.getTweetId(), "followedTweets.get(1) == tweet1");
	}

	@Test(dependsOnMethods = "testRemoveTweetFromIndexForFollower")
	public void testRemoveTweetIndexForFollower()
	{
		this.followerTweetIndexRepository.removeIndex("duyhai", "jdubois");
		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FOLLOWED_TWEET_INDEX_CF)
				.setKey("duyhai:jdubois").setRange(null, null, true, 100).execute().get().getColumns();

		assertTrue(columns.size() == 0, "columns.size() == 0");
	}
}
