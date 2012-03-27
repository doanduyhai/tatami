package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWERS_CF;
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

public class FollowerRepositoryTest extends AbstractCassandraTatamiTest
{

	@Test
	public void testAddFollower()
	{
		this.followerRepository.addFollower("test", "jdubois");
		ColumnSlice<String, Long> result = createSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), LongSerializer.get())
				.setColumnFamily(FOLLOWERS_CF).setKey("test").setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() > 0, " result size > 0");
		assertEquals(result.getColumns().get(0).getName(), "jdubois", "column name");
	}

	@Test(dependsOnMethods = "testAddFollower")
	public void testFindFollowersForUser()
	{
		Collection<String> friends = this.followerRepository.findFollowersForUser("test");

		assertTrue(friends.size() == 1, "friends.size()");
		assertEquals(friends.iterator().next(), "jdubois", "friends.get(0)");
	}

	@Test(dependsOnMethods = "testFindFollowersForUser")
	public void testRemoveFollower()
	{
		this.followerRepository.removeFollower("test", "jdubois");

		ColumnSlice<String, Long> result = createSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), LongSerializer.get())
				.setColumnFamily(FOLLOWERS_CF).setKey("test").setRange(null, null, true, 10).execute().get();

		assertNotNull(result, "result");
		assertTrue(result.getColumns().size() == 0, " result size == 0");
	}
}
