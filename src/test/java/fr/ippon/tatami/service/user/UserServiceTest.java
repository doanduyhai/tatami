package fr.ippon.tatami.service.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.util.GravatarUtil;

public class UserServiceTest extends AbstractCassandraTatamiTest
{
	private User user1, user2, user3, user4, user5, user6, user7, jdubois, userToFollow;

	@Test
	public void shouldGetAUserByLogin() throws FunctionalException
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		User user = userService.getUserByLogin("jdubois");
		assertThat(user, notNullValue());
		assertThat(user.getFirstName(), is("Julien"));
		assertThat(user.getLastName(), is("DUBOIS"));
		assertThat(user.getEmail(), is("jdubois@ippon.fr"));
		assertThat(user.getTweetCount(), is(0L));
		assertThat(user.getFollowersCount(), is(0L));
		assertThat(user.getFriendsCount(), is(0L));

	}

	@Test(expectedExceptions = FunctionalException.class)
	public void shouldNotGetAUserByLogin() throws FunctionalException
	{
		userService.getUserByLogin("unknownUserLogin");
	}

	@Test(dependsOnMethods = "shouldGetAUserByLogin")
	public void shouldUpdateUser() throws FunctionalException
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		jdubois.setEmail("uuser@ippon.fr");
		jdubois.setFirstName("UpdatedFirstName");
		jdubois.setLastName("UpdatedLastName");

		userService.updateUser(jdubois);

		User updatedUser = userService.getUserByLogin("jdubois");

		assertThat(updatedUser.getFirstName(), is("UpdatedFirstName"));
		assertThat(updatedUser.getLastName(), is("UpdatedLastName"));
		assertThat(updatedUser.getGravatar(), is(GravatarUtil.getHash("uuser@ippon.fr")));

	}

	@Test(dependsOnMethods = "shouldUpdateUser")
	public void shouldCreateAUser() throws FunctionalException
	{
		String login = "userToFollow";
		String firstName = "New";
		String lastName = "User";
		String email = "nuser@ippon.fr";
		String gravatar = "newGravatar";

		userToFollow = new User();
		userToFollow.setLogin(login);
		userToFollow.setFirstName(firstName);
		userToFollow.setLastName(lastName);
		userToFollow.setEmail(email);
		userToFollow.setGravatar(gravatar);

		userService.createUser(userToFollow);

		/* verify */
		User userToBeTheSame = userService.getUserByLogin(login);
		assertThat(userToBeTheSame.getLogin(), is(userToFollow.getLogin()));
		assertThat(userToBeTheSame.getFirstName(), is(userToFollow.getFirstName()));
		assertThat(userToBeTheSame.getLastName(), is(userToFollow.getLastName()));
		assertThat(userToBeTheSame.getGravatar(), is(userToFollow.getGravatar()));
		assertThat(userToBeTheSame.getTweetCount(), is(0L));
		assertThat(userToBeTheSame.getFollowersCount(), is(0L));
		assertThat(userToBeTheSame.getFriendsCount(), is(0L));
	}

	@Test(dependsOnMethods = "shouldCreateAUser")
	public void shouldCreateNewUserWithIndexes()
	{
		User tescolan = new User();
		tescolan.setBiography("");
		tescolan.setLogin("tescolan");
		tescolan.setFirstName("Thomas");
		tescolan.setLastName("ESCOLAN");
		tescolan.setEmail("tescolan@ippon.fr");

		this.userService.createUser(tescolan);

		List<String> logins = this.userIndexRepository.findLogin("tescolan", 10);
		List<String> firstNamelogins = this.userIndexRepository.findFirstName("thomas", 10);
		List<String> lastNameslogins = this.userIndexRepository.findLastName("escolan", 10);

		assertThat(logins.size(), is(1));
		assertThat(logins.get(0), is("tescolan"));
		assertThat(firstNamelogins.size(), is(1));
		assertThat(firstNamelogins.get(0), is("tescolan"));
		assertThat(lastNameslogins.size(), is(1));
		assertThat(lastNameslogins.get(0), is("tescolan"));
	}

	@Test(dependsOnMethods = "shouldCreateNewUserWithIndexes")
	public void shouldUpdateUserWithIndexes() throws FunctionalException
	{
		User tescolan = userService.getUserByLogin("tescolan");
		mockAuthenticatedUser(tescolan);

		User newTescolan = new User();
		newTescolan.setBiography("");
		newTescolan.setLogin("tescolan");
		newTescolan.setFirstName("Thierry");
		newTescolan.setLastName("MARTIN");
		newTescolan.setEmail("tescolan@ippon.fr");

		this.userService.updateUser(newTescolan);

		List<String> firstNamelogins = this.userIndexRepository.findFirstName("thierry", 10);
		List<String> lastNameslogins = this.userIndexRepository.findLastName("martin", 10);

		List<String> oldFirstNamelogins = this.userIndexRepository.findFirstName("thomas", 10);
		List<String> oldLastNameslogins = this.userIndexRepository.findLastName("escolan", 10);

		assertThat(firstNamelogins.size(), is(1));
		assertThat(oldFirstNamelogins.size(), is(0));
		assertThat(firstNamelogins.get(0), is("tescolan"));
		assertThat(lastNameslogins.size(), is(1));
		assertThat(oldLastNameslogins.size(), is(0));
		assertThat(lastNameslogins.get(0), is("tescolan"));
	}

	@Test(dependsOnMethods = "shouldUpdateUserWithIndexes")
	public void testFindUsersByLogin() throws FunctionalException
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		List<User> foundUsers = this.userService.findUser("@tescolan");
		assertThat(foundUsers.size(), is(1));
		assertThat(foundUsers.get(0).getLogin(), is("tescolan"));
		assertThat(foundUsers.get(0).getFirstName(), is("Thierry"));
		assertThat(foundUsers.get(0).getLastName(), is("MARTIN"));

	}

	@Test(dependsOnMethods = "testFindUsersByLogin")
	public void testFindUsersByFirstName() throws FunctionalException
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		List<User> foundUsers = this.userService.findUser("Thierry ");
		assertThat(foundUsers.size(), is(1));
		assertThat(foundUsers.get(0).getLogin(), is("tescolan"));
		assertThat(foundUsers.get(0).getFirstName(), is("Thierry"));
		assertThat(foundUsers.get(0).getLastName(), is("MARTIN"));
	}

	@Test(dependsOnMethods = "testFindUsersByFirstName")
	public void testFindUsersByLastName() throws FunctionalException
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		List<User> foundUsers = this.userService.findUser("MARTIN ");
		assertThat(foundUsers.size(), is(1));
		assertThat(foundUsers.get(0).getLogin(), is("tescolan"));
		assertThat(foundUsers.get(0).getFirstName(), is("Thierry"));
		assertThat(foundUsers.get(0).getLastName(), is("MARTIN"));
	}

	@Test(dependsOnMethods = "testFindUsersByLastName")
	public void testFindManyUsersByFirstName() throws FunctionalException
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		user1 = new User();
		user1.setLogin("user1");
		user1.setFirstName("Thomas");
		user1.setLastName("DUPONT");
		user1.setEmail("user1@ippon.fr");

		user2 = new User();
		user2.setLogin("user2");
		user2.setFirstName("Thomas");
		user2.setLastName("HUGUES");
		user2.setEmail("use21@ippon.fr");

		user3 = new User();
		user3.setLogin("user3");
		user3.setFirstName("Thomason");
		user3.setLastName("JOHANSON");
		user3.setEmail("user3@ippon.fr");

		user4 = new User();
		user4.setLogin("user4");
		user4.setFirstName("Thomat");
		user4.setLastName("DUPONTEL");
		user4.setEmail("user4@ippon.fr");

		user5 = new User();
		user5.setLogin("user5");
		user5.setFirstName("Jean");
		user5.setLastName("DUPONS");
		user5.setEmail("user5@ippon.fr");

		this.userService.createUser(user1);
		this.userService.createUser(user2);
		this.userService.createUser(user3);
		this.userService.createUser(user4);
		this.userService.createUser(user5);

		List<User> foundUsers = this.userService.findUser("Thomas");
		assertThat(foundUsers.size(), is(3));
		assertTrue(foundUsers.contains(user1), "foundUsers has 'Thomas DUPONT'");
		assertTrue(foundUsers.contains(user2), "foundUsers has 'Thomas HUGUES'");
		assertTrue(foundUsers.contains(user3), "foundUsers has 'Thomason JOHANSON'");

	}

	@Test(dependsOnMethods = "testFindManyUsersByFirstName")
	public void testFindManyUsersByLastName() throws FunctionalException
	{
		List<User> foundUsers = this.userService.findUser("DUPON");
		assertThat(foundUsers.size(), is(3));
		assertTrue(foundUsers.contains(user1), "foundUsers has 'Thomas DUPONT'");
		assertTrue(foundUsers.contains(user4), "foundUsers has 'Thomat DUPONTEL'");
		assertTrue(foundUsers.contains(user5), "foundUsers has 'Jean DUPONS'");
	}

	@Test(dependsOnMethods = "testFindManyUsersByLastName")
	public void testFindManyUsersByRange() throws FunctionalException
	{
		user6 = new User();
		user6.setLogin("user6");
		user6.setFirstName("Thomas");
		user6.setLastName("MARTIN");
		user6.setEmail("user6@ippon.fr");

		user7 = new User();
		user7.setLogin("user7");
		user7.setFirstName("Thomas");
		user7.setLastName("FRANC");
		user7.setEmail("user7@ippon.fr");

		this.userService.createUser(user6);
		this.userService.createUser(user7);

		// user2, user3 & user6
		List<User> foundUsers = this.userService.findUser("Thomas", 2, 4);
		assertThat(foundUsers.size(), is(3));

		assertTrue(foundUsers.contains(user2), "foundUsers has 'Thomas HUGUES'");
		assertTrue(foundUsers.contains(user3), "foundUsers has 'Thomason JOHANSON'");
		assertTrue(foundUsers.contains(user6), "foundUsers has 'Thomas MARTIN'");
	}

	@Test(dependsOnMethods = "testFindManyUsersByRange")
	public void testFindManyUsersByOutOfLimitRange() throws FunctionalException
	{
		// user3, user6 & user7
		List<User> foundUsers = this.userService.findUser("Thomas", 3, 10);
		assertThat(foundUsers.size(), is(3));
		assertTrue(foundUsers.contains(user3), "foundUsers has 'Thomason JOHANSON'");
		assertTrue(foundUsers.contains(user6), "foundUsers has 'Thomas MARTIN'");
		assertTrue(foundUsers.contains(user7), "foundUsers has 'Thomas FRANC'");
	}

	@Test(dependsOnMethods = "testFindManyUsersByOutOfLimitRange")
	public void testFindManyUsersByOutOfLimitRangeNoResult() throws FunctionalException
	{
		List<User> foundUsers = this.userService.findUser("Thomas", 7, 10);
		assertThat(foundUsers.size(), is(0));
	}
}