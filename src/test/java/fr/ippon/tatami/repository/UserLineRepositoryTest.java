package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USERLINE_CF;
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

public class UserLineRepositoryTest extends AbstractCassandraTatamiTest
{

	private User user;

	@Test
	public void testAddTweetToUserLine()
	{
		user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		this.userLineRepository.addTweetToUserline(user, "tweet1");
		this.userLineRepository.addTweetToUserline(user, "tweet2");
		this.userLineRepository.addTweetToUserline(user, "tweet3");
		this.userLineRepository.addTweetToUserline(user, "tweet4");
		this.userLineRepository.addTweetToUserline(user, "tweet5");

		List<HColumn<Long, String>> columns = createSliceQuery(keyspace, se, le, se).setColumnFamily(USERLINE_CF).setKey(user.getLogin())
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

	@Test(dependsOnMethods = "testAddTweetToUserLine")
	public void testGetTweetsFromUserline()
	{
		Collection<String> tweetIds = this.userLineRepository.getTweetsFromUserline(user);

		assertTrue(tweetIds.size() == 5, "tweetIds.size() == 5");
		assertTrue(tweetIds.contains("tweet1"), "tweetIds has 'tweet1'");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains("tweet3"), "tweetIds has 'tweet3'");
		assertTrue(tweetIds.contains("tweet4"), "tweetIds has 'tweet4'");
		assertTrue(tweetIds.contains("tweet5"), "tweetIds has 'tweet5'");

	}

	@Test(dependsOnMethods = "testAddTweetToUserLine")
	public void testGetTweetsRangeFromUserline()
	{
		Collection<String> tweetIds = this.userLineRepository.getTweetsRangeFromUserline(user, 3, 4);

		assertTrue(tweetIds.size() == 2, "tweetIds.size() == 2");
		assertTrue(tweetIds.contains("tweet2"), "tweetIds has 'tweet2'");
		assertTrue(tweetIds.contains("tweet3"), "tweetIds has 'tweet3'");

		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get());
		cqlQuery.setQuery("truncate User");
		cqlQuery.execute();

		User deletedUser = this.userRepository.findUserByLogin("test");
		assertNull(deletedUser, "deletedUser");
	}
}
