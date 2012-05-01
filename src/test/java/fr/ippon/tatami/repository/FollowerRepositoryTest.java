package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWERS_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;

public class FollowerRepositoryTest extends AbstractCassandraTatamiTest
{

	private User user;
	private User jdubois;
	private User tescolan;
	private User duyhai;
	private User uncleBob;
	private User nford;

	@Test
	public void testAddFollower()
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

		this.followerRepository.addFollower(user, duyhai);
		this.followerRepository.addFollower(user, jdubois);
		this.followerRepository.addFollower(user, nford);
		this.followerRepository.addFollower(user, tescolan);
		this.followerRepository.addFollower(user, uncleBob);

		User refreshedUser = this.userRepository.findUserByLogin("test");

		List<String> followersLogin = new ArrayList<String>();

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(FOLLOWERS_CF).setKey(user.getLogin())
				.setRange(null, null, false, 100).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			followersLogin.add(column.getName());
		}

		assertTrue(followersLogin.size() == 5, "userFollowers.size() == 5");
		assertTrue(refreshedUser.getFollowersCount() == 5, "refreshedUser.getFollowersCount() == 5");
		assertTrue(followersLogin.contains("duyhai"), "refreshedUser has duyhai as follower");
		assertTrue(followersLogin.contains("jdubois"), "refreshedUser has jdubois as follower");
		assertTrue(followersLogin.contains("nford"), "refreshedUser has nford as follower");
		assertTrue(followersLogin.contains("tescolan"), "refreshedUser has tescolan as follower");
		assertTrue(followersLogin.contains("uncleBob"), "refreshedUser has uncleBob as follower");

	}

	@Test(dependsOnMethods = "testAddFollower")
	public void testFindFollowersForUser()
	{
		Collection<String> userFollowers = this.followerRepository.findFollowersForUser(user);

		assertTrue(userFollowers.size() == 5, "userFollowers.size() == 5");
		assertTrue(userFollowers.contains("duyhai"), "refreshedUser has duyhai as follower");
		assertTrue(userFollowers.contains("jdubois"), "refreshedUser has jdubois as follower");
		assertTrue(userFollowers.contains("nford"), "refreshedUser has nford as follower");
		assertTrue(userFollowers.contains("tescolan"), "refreshedUser has tescolan as follower");
		assertTrue(userFollowers.contains("uncleBob"), "refreshedUser has uncleBob as follower");
	}

	@Test(dependsOnMethods = "testFindFollowersForUser")
	public void testFindFollowersRangeForUser()
	{
		Collection<String> userFollowers = this.followerRepository.findFollowersForUser(user, "jdubois", 2);
		assertTrue(userFollowers.size() == 2, "userFollowers.size() == 2");
		assertTrue(userFollowers.contains("nford"), "refreshedUser has nford as follower");
		assertTrue(userFollowers.contains("tescolan"), "refreshedUser has tescolan as follower");
	}

	@Test(dependsOnMethods = "testFindFollowersRangeForUser")
	public void testFindFollowersRangeForUserOutOfBound()
	{
		Collection<String> userFollowers = this.followerRepository.findFollowersForUser(user, "uncleBob", 2);
		assertTrue(userFollowers.size() == 0, "userFollowers.size() == 0");
	}

	@Test(dependsOnMethods = "testFindFollowersRangeForUserOutOfBound")
	public void testRemoveFollower()
	{
		this.followerRepository.removeFollower(user, jdubois);
		this.followerRepository.removeFollower(user, tescolan);

		User refreshedUser = this.userRepository.findUserByLogin("test");
		Collection<String> userFollowers = this.followerRepository.findFollowersForUser(user);

		assertTrue(userFollowers.size() == 3, "userFollowers.size()==3");
		assertTrue(refreshedUser.getFollowersCount() == 3, "refreshedUser.getFollowersCount()==3");

		assertTrue(userFollowers.contains("duyhai"), "refreshedUser has duyhai as follower");
		assertTrue(userFollowers.contains("uncleBob"), "refreshedUser has uncleBob as follower");
		assertTrue(userFollowers.contains("nford"), "refreshedUser has nford as follower");
	}

}
