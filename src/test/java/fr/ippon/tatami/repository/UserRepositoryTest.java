package fr.ippon.tatami.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;

public class UserRepositoryTest extends AbstractCassandraTatamiTest
{

	@Test
	public void testPersist()
	{
		User user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		User persistedUser = this.entityManager.find(User.class, "test");

		assertNotNull(persistedUser, "User not persisted");
		assertEquals(persistedUser.getEmail(), "test@ippon.fr", "Email");
		assertEquals(persistedUser.getFirstName(), "firstname", "Firstname");
		assertEquals(persistedUser.getLastName(), "lastname", "Lastname");
	}

	@Test(dependsOnMethods = "testPersist")
	public void testFindUserByLogin()
	{
		User test = this.userRepository.findUserByLogin("test");
		assertNotNull(test, "test");
	}

	@Test(dependsOnMethods = "testPersist")
	public void testUpdateUser()
	{
		User test = this.entityManager.find(User.class, "test");
		test.setFirstName("another-firstname");

		this.userRepository.updateUser(test);

		User refreshedTest = this.entityManager.find(User.class, "test");

		assertEquals(refreshedTest.getFirstName(), "another-firstname", "Firstname");

		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get());
		cqlQuery.setQuery("delete from User where KEY='test'");
		cqlQuery.execute();

		User deletedUser = this.userRepository.findUserByLogin("test");
		assertNull(deletedUser, "deletedUser");
	}

	// @Test(dependsOnMethods = "testRemoveFavoriteFromUser")
	// public void testCQL()
	// {
	// User test = this.userRepository.findUserByLogin("test");
	// String uuid = "";
	// for (int i = 0; i < 10; i++)
	// {
	// uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString();
	//
	// this.userRepository.addFavoriteToUser(test, uuid);
	// System.out.println(" " + i + " : " + uuid);
	// }
	//
	// CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, StringSerializer.get(), StringSerializer.get(),
	// StringSerializer.get());
	// cqlQuery.setQuery("select * from User where KEY='test'");
	// QueryResult<CqlRows<String, String, String>> result = cqlQuery.execute();
	//
	// for (HColumn<String, String> column : result.get().getList().get(0).getColumnSlice().getColumns())
	// {
	// System.out.println(column.getName() + "  = " + column.getValue().replaceAll(".+\\$(.[^$]+)", "$1"));
	// }
	//
	// }
}
