package fr.ippon.tatami.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

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

	@Test(dependsOnMethods = "testFindUserByLogin")
	public void testFindUserByNullLogin()
	{
		User test = this.userRepository.findUserByLogin(null);
		assertNull(test, "test");
	}

	@Test(dependsOnMethods = "testFindUserByNullLogin")
	public void testUpdateUser()
	{
		User test = this.entityManager.find(User.class, "test");
		test.setFirstName("another-firstname");

		this.userRepository.updateUser(test);

		User refreshedTest = this.entityManager.find(User.class, "test");

		assertEquals(refreshedTest.getFirstName(), "another-firstname", "Firstname");

	}
}
