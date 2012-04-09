package fr.ippon.tatami.repository;

import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.FavoriteLine;
import fr.ippon.tatami.domain.User;

public class FavoriteRepositoryTest extends AbstractCassandraTatamiTest
{
	private User user;

	@Test
	public void testAddFavorite()
	{
		user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		this.favoriteRepository.addFavorite(user, "tweet1");
		this.favoriteRepository.addFavorite(user, "tweet2");
		this.favoriteRepository.addFavorite(user, "tweet3");
		this.favoriteRepository.addFavorite(user, "tweet4");
		this.favoriteRepository.addFavorite(user, "tweet5");

		User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFavorites = this.entityManager.find(FavoriteLine.class, "test").getFavorites();

		assertTrue(userFavorites.size() == 5, "userFavorites.size() == 5");
		assertTrue(refreshedUser.getFavoritesCount() == 5, "refreshedUser.getFavoritesCount() == 5");
		assertTrue(userFavorites.contains("tweet1"), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains("tweet2"), "refreshedUser has tweet2 as favorite");
		assertTrue(userFavorites.contains("tweet3"), "refreshedUser has tweet3 as favorite");
		assertTrue(userFavorites.contains("tweet4"), "refreshedUser has tweet4 as favorite");
		assertTrue(userFavorites.contains("tweet5"), "refreshedUser has tweet5 as favorite");
	}

	@Test(dependsOnMethods = "testAddFavorite")
	public void testFindFavoritesForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser(user, 1, DEFAULT_FAVORITE_LIST_SIZE);

		assertTrue(userFavorites.size() == 5, "userFavorites.size() == 5");
		assertTrue(userFavorites.contains("tweet1"), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains("tweet2"), "refreshedUser has tweet2 as favorite");
		assertTrue(userFavorites.contains("tweet3"), "refreshedUser has tweet3 as favorite");
		assertTrue(userFavorites.contains("tweet4"), "refreshedUser has tweet4 as favorite");
		assertTrue(userFavorites.contains("tweet5"), "refreshedUser has tweet5 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesForUser")
	public void testFindFavoritesRangeForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser(user, 1, 2);

		assertTrue(userFavorites.size() == 2, "userFavorites.size() == 2");
		assertTrue(userFavorites.contains("tweet4"), "refreshedUser has tweet4 as favorite");
		assertTrue(userFavorites.contains("tweet5"), "refreshedUser has tweet5 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesRangeForUser")
	public void testFindFavoritesRangeOutOfBoundsForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser(user, 7, 10);

		assertTrue(userFavorites.size() == 0, "userFavorites.size() == 0");
	}

	@Test(dependsOnMethods = "testFindFavoritesRangeOutOfBoundsForUser")
	public void testFindFavoritesRangeBoundsLimitForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser(user, 5, 10);

		assertTrue(userFavorites.size() == 1, "userFavorites.size() == 1");
		assertTrue(userFavorites.contains("tweet1"), "refreshedUser has tweet1 as favorite");
	}

	@Test(dependsOnMethods = "testFindFavoritesRangeOutOfBoundsForUser")
	public void testRemoveFavorite()
	{
		this.favoriteRepository.removeFavorite(user, "tweet1");
		this.favoriteRepository.removeFavorite(user, "tweet2");
		this.favoriteRepository.removeFavorite(user, "tweet3");
		this.favoriteRepository.removeFavorite(user, "tweet4");
		this.favoriteRepository.removeFavorite(user, "tweet5");

		User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesRangeForUser(user, 1, DEFAULT_FAVORITE_LIST_SIZE);

		assertTrue(userFavorites.size() == 0, "userFavorites.size()==0");
		assertTrue(refreshedUser.getFavoritesCount() == 0, "refreshedUser.getFavoritesCount()==0");

		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get());
		cqlQuery.setQuery("truncate User");
		cqlQuery.execute();

		cqlQuery.setQuery("truncate Tweet");
		cqlQuery.execute();

		cqlQuery.setQuery("truncate FavoriteLine");
		cqlQuery.execute();

		User deletedUser = this.userRepository.findUserByLogin("test");
		assertNull(deletedUser, "deletedUser");
	}
}
