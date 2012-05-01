package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FRIENDS_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;

public class FriendRepositoryTest extends AbstractCassandraTatamiTest
{

	private User user;
	private User jdubois;
	private User tescolan;
	private User duyhai;
	private User uncleBob;
	private User nford;

	@Test
	public void testAddFriend()
	{
		user = new User();
		user.setLogin("test");
		user.setEmail("test@ippon.fr");
		user.setFirstName("firstname");
		user.setLastName("lastname");

		duyhai = new User();
		duyhai.setLogin("duyhai");
		duyhai.setEmail("duyhai@ippon.fr");
		duyhai.setFirstName("DuyHai");
		duyhai.setLastName("DOAN");

		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		nford = new User();
		nford.setLogin("nford");
		nford.setEmail("nford@ippon.fr");
		nford.setFirstName("Neal");
		nford.setLastName("FORD");

		tescolan = new User();
		tescolan.setLogin("tescolan");
		tescolan.setEmail("tescolan@ippon.fr");
		tescolan.setFirstName("Thomas");
		tescolan.setLastName("ESCOLAN");

		uncleBob = new User();
		uncleBob.setLogin("uncleBob");
		uncleBob.setEmail("uncleBob@ippon.fr");
		uncleBob.setFirstName("Uncle");
		uncleBob.setLastName("BOB");

		this.userRepository.createUser(user);
		this.userRepository.createUser(duyhai);
		this.userRepository.createUser(jdubois);
		this.userRepository.createUser(nford);
		this.userRepository.createUser(tescolan);
		this.userRepository.createUser(uncleBob);

		this.friendRepository.addFriend(user, duyhai);
		this.friendRepository.addFriend(user, jdubois);
		this.friendRepository.addFriend(user, nford);
		this.friendRepository.addFriend(user, tescolan);
		this.friendRepository.addFriend(user, uncleBob);

		User refreshedUser = this.userRepository.findUserByLogin("test");

		List<String> friendsLogin = new ArrayList<String>();

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FRIENDS_CF).setKey(user.getLogin())
				.setRange(null, null, false, 100).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			friendsLogin.add(column.getName());
		}

		assertTrue(friendsLogin.size() == 5, "userFriends.size() == 5");
		assertTrue(refreshedUser.getFriendsCount() == 5, "refreshedUser.getFriendsCount() == 5");

		assertTrue(friendsLogin.contains("duyhai"), "refreshedUser has duyhai as friend");
		assertTrue(friendsLogin.contains("jdubois"), "refreshedUser has jdubois as friend");
		assertTrue(friendsLogin.contains("nford"), "refreshedUser has nford as friend");
		assertTrue(friendsLogin.contains("tescolan"), "refreshedUser has tescolan as friend");
		assertTrue(friendsLogin.contains("uncleBob"), "refreshedUser has uncleBob as friend");
	}

	@Test(dependsOnMethods = "testAddFriend")
	public void testFindFriendsForUser()
	{
		Collection<String> userFriends = this.friendRepository.findFriendsForUser(user);

		assertTrue(userFriends.size() == 5, "userFriends.size() == 5");
		assertTrue(userFriends.contains("duyhai"), "refreshedUser has duyhai as friend");
		assertTrue(userFriends.contains("jdubois"), "refreshedUser has jdubois as friend");
		assertTrue(userFriends.contains("nford"), "refreshedUser has nford as friend");
		assertTrue(userFriends.contains("tescolan"), "refreshedUser has tescolan as friend");
		assertTrue(userFriends.contains("uncleBob"), "refreshedUser has uncleBob as friend");
	}

	@Test(dependsOnMethods = "testFindFriendsForUser")
	public void testFindFriendsRangeForUser()
	{
		Collection<String> userFriends = this.friendRepository.findFriendsForUser(user, "jdubois", 2);
		assertTrue(userFriends.size() == 2, "userFriends.size() == 2");
		assertTrue(userFriends.contains("nford"), "refreshedUser has nford as friend");
		assertTrue(userFriends.contains("tescolan"), "refreshedUser has tescolan as friend");
	}

	@Test(dependsOnMethods = "testFindFriendsRangeForUser")
	public void testFriendsRangeForUserOutOfBound()
	{
		Collection<String> userFriends = this.friendRepository.findFriendsForUser(user, "zzz", 2);
		assertTrue(userFriends.size() == 0, "userFriends.size() == 0");
	}

	@Test(dependsOnMethods = "testFriendsRangeForUserOutOfBound")
	public void testRemoveFriend()
	{
		this.friendRepository.removeFriend(user, jdubois);
		this.friendRepository.removeFriend(user, tescolan);

		User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFriends = this.friendRepository.findFriendsForUser(user);

		assertTrue(userFriends.size() == 3, "userFriends.size()==3");
		assertTrue(refreshedUser.getFriendsCount() == 3, "refreshedUser.getFriendsCount()==3");

		assertTrue(userFriends.contains("duyhai"), "refreshedUser has duyhai as friend");
		assertTrue(userFriends.contains("nford"), "refreshedUser has nford as friend");
		assertTrue(userFriends.contains("uncleBob"), "refreshedUser has uncleBob as friend");

	}

}
