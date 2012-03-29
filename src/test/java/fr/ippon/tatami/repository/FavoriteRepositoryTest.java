package fr.ippon.tatami.repository;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.FavoriteLine;

public class FavoriteRepositoryTest extends AbstractCassandraTatamiTest
{
	@Test
	public void testAddFavorite()
	{
		User user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		this.favoriteRepository.addFavorite("test", "tweet1");
		this.favoriteRepository.addFavorite("test", "tweet2");

		User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFavorites = this.entityManager.find(FavoriteLine.class, "test").getFavorites();

		assertTrue(userFavorites.size() == 2, "userFavorites.size() == 2");
		assertTrue(refreshedUser.getFavoritesCount() == 2, "refreshedUser.getFavoritesCount() == 2");
		assertTrue(userFavorites.contains("tweet1"), "refreshedUser has tweet1 as favorite");
		assertTrue(userFavorites.contains("tweet2"), "refreshedUser has tweet2 as favorite");
	}

	@Test(dependsOnMethods = "testAddFavorite")
	public void testFindFavoritesForUser()
	{
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesForUser("test");

		assertTrue(userFavorites.size() == 2, "friends.size()");
		assertTrue(userFavorites.contains("tweet1"), "userFavorites has 'tweet1'");
		assertTrue(userFavorites.contains("tweet2"), "userFavorites has 'tweet2'");

	}

	@Test(dependsOnMethods = "testFindFavoritesForUser")
	public void testRemoveFavorite()
	{
		this.favoriteRepository.removeFavorite("test", "tweet1");
		this.favoriteRepository.removeFavorite("test", "tweet2");

		User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFavorites = this.favoriteRepository.findFavoritesForUser("test");

		assertTrue(userFavorites.size() == 0, "userFavorites.size()==0");
		assertTrue(refreshedUser.getFavoritesCount() == 0, "refreshedUser.getFavoritesCount()==0");

		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get());
		cqlQuery.setQuery("delete from User where KEY='test'");
		cqlQuery.execute();

		User deletedUser = this.userRepository.findUserByLogin("test");
		assertNull(deletedUser, "deletedUser");
	}
}
