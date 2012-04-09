package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.TIMELINE_CF;
import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.util.TatamiConstants;

public class TimeLineRepositoryTest extends AbstractCassandraTatamiTest
{

	private User user;

	@Test
	public void testAddTweetToTimeLine()
	{
		user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		this.timeLineRepository.addTweetToTimeline(user, "tweet1");
		this.timeLineRepository.addTweetToTimeline(user, "tweet2");
		this.timeLineRepository.addTweetToTimeline(user, "tweet3");
		this.timeLineRepository.addTweetToTimeline(user, "tweet4");
		this.timeLineRepository.addTweetToTimeline(user, "tweet5");

		List<HColumn<Long, String>> columns = createSliceQuery(keyspace, se, le, se).setColumnFamily(TIMELINE_CF).setKey(user.getLogin())
				.setRange(5L, 0L, true, TatamiConstants.DEFAULT_TWEET_LIST_SIZE).execute().get().getColumns();

		List<String> tweetIds = new ArrayList<String>();
		for (HColumn<Long, String> column : columns)
		{
			tweetIds.add(column.getValue());
		}

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains("tweet3"), "tweetIds has 'tweet3'");
		assertTrue(tweetIds.contains("tweet4"), "tweetIds has 'tweet4'");
		assertTrue(tweetIds.contains("tweet5"), "tweetIds has 'tweet5'");
	}

	@Test(dependsOnMethods = "testAddTweetToTimeLine")
	public void testGetTweetsFromTimeline()
	{
		Collection<String> tweetIds = this.timeLineRepository.getTweetsRangeFromTimeline(user, 1, DEFAULT_TWEET_LIST_SIZE);

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains("tweet3"), "tweetIds has 'tweet3'");
		assertTrue(tweetIds.contains("tweet4"), "tweetIds has 'tweet4'");
		assertTrue(tweetIds.contains("tweet5"), "tweetIds has 'tweet5'");

	}

	@Test(dependsOnMethods = "testAddTweetToTimeLine")
	public void testGetTweetsRangeFromTimeline()
	{
		Collection<String> tweetIds = this.timeLineRepository.getTweetsRangeFromTimeline(user, 3, 4);

		assertTrue(tweetIds.size() == 2, "tweetIds.size() == 2");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains("tweet3"), "tweetIds has 'tweet3'");

	}

	@Test(dependsOnMethods = "testGetTweetsRangeFromTimeline")
	public void testGetTweetsRangeOutOfBoundsFromTimeline()
	{
		Collection<String> tweetIds = this.timeLineRepository.getTweetsRangeFromTimeline(user, 7, 10);

		assertTrue(tweetIds.size() == 0, "tweetIds.size() == 0");

		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get());
		cqlQuery.setQuery("truncate User");
		cqlQuery.execute();

		cqlQuery.setQuery("truncate Tweet");
		cqlQuery.execute();

		cqlQuery.setQuery("truncate TimeLine");
		cqlQuery.execute();

		User deletedUser = this.userRepository.findUserByLogin("test");
		assertNull(deletedUser, "deletedUser");
	}
}
