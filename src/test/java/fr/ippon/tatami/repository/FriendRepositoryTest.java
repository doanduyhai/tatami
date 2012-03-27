package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FRIENDS_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.ColumnSlice;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;

public class FriendRepositoryTest extends AbstractCassandraTatamiTest
{
	@Test
	public void testAddFriend()
	{
		this.friendRepository.addFriend("test", "jdubois");

		ColumnSlice<String, Long> result = createSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), LongSerializer.get())
				.setColumnFamily(FRIENDS_CF).setKey("test").setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() > 0, " result size > 0");
		assertEquals(result.getColumns().get(0).getName(), "jdubois", "column name");
	}

	@Test(dependsOnMethods = "testAddFriend")
	public void testFindFriendsForUser()
	{
		Collection<String> friends = this.friendRepository.findFriendsForUser("test");

		assertTrue(friends.size() == 1, "friends.size()");
		assertEquals(friends.iterator().next(), "jdubois", "friends.get(0)");
	}

	@Test(dependsOnMethods = "testFindFriendsForUser")
	public void testRemoveFriend()
	{
		this.friendRepository.removeFriend("test", "jdubois");

		ColumnSlice<String, Long> result = createSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), LongSerializer.get())
				.setColumnFamily(FRIENDS_CF).setKey("test").setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() == 0, " result size == 0");
	}
}
