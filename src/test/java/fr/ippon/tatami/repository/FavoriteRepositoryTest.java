package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITELINE_CF;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_COUNTER;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.cassandra.model.thrift.ThriftCounterColumnQuery;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HCounterColumn;
import me.prettyprint.hector.api.query.CounterQuery;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;

public class FavoriteRepositoryTest extends AbstractCassandraTatamiTest
{
	private User user;

	private Tweet tweet1;
	private Tweet tweet2;
	private Tweet tweet3;
	private Tweet tweet4;
	private Tweet tweet5;

	@Test
	public void testAddFavorite()
	{
		user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		tweet1 = this.tweetRepository.createTweet("test", "tweet1", false);
		tweet2 = this.tweetRepository.createTweet("test", "tweet2", false);
		tweet3 = this.tweetRepository.createTweet("test", "tweet3", false);
		tweet4 = this.tweetRepository.createTweet("test", "tweet4", false);
		tweet5 = this.tweetRepository.createTweet("test", "tweet5", false);

		this.favoriteRepository.addFavorite("test", tweet1.getTweetId());
		this.favoriteRepository.addFavorite("test", tweet2.getTweetId());
		this.favoriteRepository.addFavorite("test", tweet3.getTweetId());
		this.favoriteRepository.addFavorite("test", tweet4.getTweetId());
		this.favoriteRepository.addFavorite("test", tweet5.getTweetId());

		// User refreshedUser = this.userRepository.findUserByLogin("test");

		List<String> userFavorites = new ArrayList<String>();

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FAVORITELINE_CF).setKey(user.getLogin())
				.setRange(null, null, true, 100).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			userFavorites.add(column.getName());
		}

		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspace, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(FAVORITE_TWEET_COUNTER).setName(user.getLogin());

		HCounterColumn<String> counterColumn = counter.execute().get();

		long count;
		if (counterColumn == null)
		{
			count = 0;
		}
		else
		{
			count = counterColumn.getValue();
		}

		assertTrue(userFavorites.size() == 5, "userFavorites.size() == 5");
		assertTrue(count == 5, "refreshedUser.getFavoritesCount() == 5");
		assertTrue(userFavorites.contains(tweet1.getTweetId()), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains(tweet2.getTweetId()), "refreshedUser has tweet2 as favorite");
		assertTrue(userFavorites.contains(tweet3.getTweetId()), "refreshedUser has tweet3 as favorite");
		assertTrue(userFavorites.contains(tweet4.getTweetId()), "refreshedUser has tweet4 as favorite");
		assertTrue(userFavorites.contains(tweet5.getTweetId()), "refreshedUser has tweet5 as favorite");
	}

	@Test(dependsOnMethods = "testAddFavorite")
	public void testFindFavoritesForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesForUser("test");

		assertTrue(userFavorites.size() == 5, "userFavorites.size() == 5");

		assertTrue(userFavorites.contains(tweet1.getTweetId()), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains(tweet2.getTweetId()), "refreshedUser has tweet2 as favorite");
		assertTrue(userFavorites.contains(tweet3.getTweetId()), "refreshedUser has tweet3 as favorite");
		assertTrue(userFavorites.contains(tweet4.getTweetId()), "refreshedUser has tweet4 as favorite");
		assertTrue(userFavorites.contains(tweet5.getTweetId()), "refreshedUser has tweet5 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesForUser")
	public void testFindFavoritesRangeForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser("test", tweet3.getTweetId(), 2);

		assertTrue(userFavorites.size() == 2, "userFavorites.size() == 2");
		assertTrue(userFavorites.contains(tweet1.getTweetId()), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains(tweet2.getTweetId()), "refreshedUser has tweet2 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesRangeForUser")
	public void testFindFavoritesRangeOutOfBoundsForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser("test", tweet2.getTweetId(), 10);

		assertTrue(userFavorites.size() == 1, "userFavorites.size() == 1");
		assertTrue(userFavorites.contains(tweet1.getTweetId()), "refreshedUser has tweet1 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesRangeOutOfBoundsForUser")
	public void testFindFavoritesRangeBoundsLimitForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser("test", "000", 10);

		assertTrue(userFavorites.size() == 0, "userFavorites.size() == 0");

	}

	@Test(dependsOnMethods = "testFindFavoritesRangeOutOfBoundsForUser")
	public void testRemoveFavorite()
	{
		this.favoriteRepository.removeFavorite("test", tweet1.getTweetId());
		this.favoriteRepository.removeFavorite("test", tweet2.getTweetId());
		this.favoriteRepository.removeFavorite("test", tweet3.getTweetId());
		this.favoriteRepository.removeFavorite("test", tweet4.getTweetId());
		this.favoriteRepository.removeFavorite("test", tweet5.getTweetId());

		// User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesForUser("test");

		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspace, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(FAVORITE_TWEET_COUNTER).setName(user.getLogin());

		HCounterColumn<String> counterColumn = counter.execute().get();

		long count;
		if (counterColumn == null)
		{
			count = 0;
		}
		else
		{
			count = counterColumn.getValue();
		}

		assertTrue(userFavorites.size() == 0, "userFavorites.size()==0");
		assertTrue(count == 0, "refreshedUser.getFavoritesCount()==0");
	}

}
