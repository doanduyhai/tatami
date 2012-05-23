package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USERBLOCK_CF;
import static fr.ippon.tatami.config.CounterKeys.USERBLOCK_COUNTER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;

public class BlockedUserRepositoryTest extends AbstractCassandraTatamiTest
{
	@Test
	public void testAddUserToBlockRepository()
	{
		this.blockedUserRepository.addUserToBlockRepository("jdubois", "duyhai");
		this.blockedUserRepository.addUserToBlockRepository("jdubois", "tescolan");
		this.blockedUserRepository.addUserToBlockRepository("jdubois", "test");

		Collection<String> blockedUsers = this.findRangeFromCF(USERBLOCK_CF, "jdubois", null, false, 10);

		assertEquals(blockedUsers.size(), 3, "jdubois has 3 blocked users");
		assertTrue(blockedUsers.contains("duyhai"), "jdubois blocked duyhai");
		assertTrue(blockedUsers.contains("tescolan"), "jdubois blocked tescolan");
		assertTrue(blockedUsers.contains("test"), "jdubois blocked test");

		long blockedUsersCount = this.getCounterValue(USERBLOCK_COUNTER, "jdubois");
		assertEquals(blockedUsersCount, 3, "jdubois has 3 blocked users for counter");
	}

	@Test(dependsOnMethods = "testAddUserToBlockRepository")
	public void testGetUsersFromBlockRepository()
	{
		Collection<String> blockedUsers = this.blockedUserRepository.getUsersFromBlockRepository("jdubois");

		assertEquals(blockedUsers.size(), 3, "jdubois has 3 blocked users");
		assertTrue(blockedUsers.contains("duyhai"), "jdubois blocked duyhai");
		assertTrue(blockedUsers.contains("tescolan"), "jdubois blocked tescolan");
		assertTrue(blockedUsers.contains("test"), "jdubois blocked test");
	}

	@Test(dependsOnMethods = "testGetUsersFromBlockRepository")
	public void testRemoveUserFromBlockRepository()
	{
		this.blockedUserRepository.removeUserFromBlockRepository("jdubois", "duyhai");

		Collection<String> blockedUsers = this.findRangeFromCF(USERBLOCK_CF, "jdubois", null, false, 10);

		assertEquals(blockedUsers.size(), 2, "jdubois has 2 blocked users");
		assertTrue(blockedUsers.contains("tescolan"), "jdubois blocked tescolan");
		assertTrue(blockedUsers.contains("test"), "jdubois blocked test");

		long blockedUsersCount = this.getCounterValue(USERBLOCK_COUNTER, "jdubois");
		assertEquals(blockedUsersCount, 2, "jdubois has 2 blocked users for counter");
	}

}
