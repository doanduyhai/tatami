package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USERLINE_CF;
import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;

public class UserLineRepositoryTest extends AbstractCassandraTatamiTest
{

	private User user;

	private Tweet tweet1;
	private Tweet tweet2;
	private Tweet tweet3;
	private Tweet tweet4;
	private Tweet tweet5;

	@Test
	public void testAddTweetToUserLine() throws InterruptedException
	{
		user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		tweet1 = this.tweetRepository.createTweet("test", "tweet1");
		Thread.sleep(5);
		tweet2 = this.tweetRepository.createTweet("test", "tweet2");
		Thread.sleep(5);
		tweet3 = this.tweetRepository.createTweet("test", "tweet3");
		Thread.sleep(5);
		tweet4 = this.tweetRepository.createTweet("test", "tweet4");
		Thread.sleep(5);
		tweet5 = this.tweetRepository.createTweet("test", "tweet5");

		this.userLineRepository.addTweetToUserline(user, tweet1.getTweetId());
		this.userLineRepository.addTweetToUserline(user, tweet2.getTweetId());
		this.userLineRepository.addTweetToUserline(user, tweet3.getTweetId());
		this.userLineRepository.addTweetToUserline(user, tweet4.getTweetId());
		this.userLineRepository.addTweetToUserline(user, tweet5.getTweetId());

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(USERLINE_CF).setKey(user.getLogin())
				.setRange(null, null, true, 100).execute().get().getColumns();

		List<String> tweetIds = new ArrayList<String>();
		for (HColumn<String, Object> column : columns)
		{
			tweetIds.add(column.getName());
		}

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains(tweet1.getTweetId()), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains(tweet2.getTweetId()), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains(tweet3.getTweetId()), "tweetIds has 'tweet3'");
		assertTrue(tweetIds.contains(tweet4.getTweetId()), "tweetIds has 'tweet4'");
		assertTrue(tweetIds.contains(tweet5.getTweetId()), "tweetIds has 'tweet5'");
	}

	@Test(dependsOnMethods = "testAddTweetToUserLine")
	public void testGetTweetsFromUserline()
	{
		Collection<String> tweetIds = this.userLineRepository.getTweetsRangeFromUserline(user, null, DEFAULT_TWEET_LIST_SIZE);

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains(tweet1.getTweetId()), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains(tweet2.getTweetId()), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains(tweet3.getTweetId()), "tweetIds has 'tweet3'");
		assertTrue(tweetIds.contains(tweet4.getTweetId()), "tweetIds has 'tweet4'");
		assertTrue(tweetIds.contains(tweet5.getTweetId()), "tweetIds has 'tweet5'");

	}

	@Test(dependsOnMethods = "testAddTweetToUserLine")
	public void testGetTweetsRangeFromUserline()
	{
		Collection<String> tweetIds = this.userLineRepository.getTweetsRangeFromUserline(user, tweet3.getTweetId(), 4);

		assertTrue(tweetIds.size() == 2, "tweetIds.size() == 2");
		assertTrue(tweetIds.contains(tweet1.getTweetId()), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains(tweet2.getTweetId()), "tweetIds has 'tweet2'");

	}

	@Test(dependsOnMethods = "testGetTweetsRangeFromUserline")
	public void testGetTweetsRangeOutOfBoundsFromUserline()
	{
		Collection<String> tweetIds = this.userLineRepository.getTweetsRangeFromUserline(user, tweet1.getTweetId(), 10);

		assertTrue(tweetIds.size() == 0, "tweetIds.size() == 0");

	}

	@Test(dependsOnMethods = "testGetTweetsRangeOutOfBoundsFromUserline")
	public void testRemoveFromUserline()
	{
		this.userLineRepository.removeTweetFromUserline(user, tweet2.getTweetId());

		this.userLineRepository.removeTweetFromUserline(user, tweet4.getTweetId());

		Collection<String> tweetIds = this.userLineRepository.getTweetsRangeFromUserline(user, tweet5.getTweetId(), 2);

		assertTrue(tweetIds.size() == 2, "userFavorites.size()==2");
		assertTrue(tweetIds.contains(tweet1.getTweetId()), "tweet1 has 'tag'");
		assertTrue(tweetIds.contains(tweet3.getTweetId()), "tweet3 has 'tag'");
	}
}
