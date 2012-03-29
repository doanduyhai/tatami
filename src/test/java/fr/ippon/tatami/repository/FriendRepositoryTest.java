package fr.ippon.tatami.repository;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserFriends;

public class FriendRepositoryTest extends AbstractCassandraTatamiTest
{

	@Test
	public void testAddFriend()
	{
		User user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		this.userRepository.createUser(user);

		this.friendRepository.addFriend("test", "jdubois");
		this.friendRepository.addFriend("test", "tescolan");

		User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFriends = this.entityManager.find(UserFriends.class, "test").getFriends();

		assertTrue(userFriends.size() == 2, "userFriends.size() == 2");
		assertTrue(refreshedUser.getFriendsCount() == 2, "refreshedUser.getFriendsCount() == 2");
		assertTrue(userFriends.contains("jdubois"), "refreshedUser has jdubois as friend");
		assertTrue(userFriends.contains("tescolan"), "refreshedUser has tescolan as friend");
	}

	@Test(dependsOnMethods = "testAddFriend")
	public void testFindFriendsForUser()
	{
		Collection<String> userFriends = this.friendRepository.findFriendsForUser("test");

		assertTrue(userFriends.size() == 2, "friends.size()");
		assertTrue(userFriends.contains("jdubois"), "userFriends has 'jdubois'");
		assertTrue(userFriends.contains("tescolan"), "userFriends has 'tescolan'");
	}

	@Test(dependsOnMethods = "testFindFriendsForUser")
	public void testRemoveFriend()
	{
		this.friendRepository.removeFriend("test", "jdubois");
		this.friendRepository.removeFriend("test", "tescolan");

		User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFriends = this.friendRepository.findFriendsForUser("test");

		assertTrue(userFriends.size() == 0, "userFriends.size()==0");
		assertTrue(refreshedUser.getFriendsCount() == 0, "refreshedUser.getFriendsCount()==0");

		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get());
		cqlQuery.setQuery("delete from User where KEY='test'");
		cqlQuery.execute();

		User deletedUser = this.userRepository.findUserByLogin("test");
		assertNull(deletedUser, "deletedUser");

	}
}
