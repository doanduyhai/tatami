package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USER_INDEX_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;

public class UserIndexRepositoryTest extends AbstractCassandraTatamiTest
{

	@Test
	public void testInsertLogin()
	{
		this.userIndexRepository.insertLogin("jdub");
		this.userIndexRepository.insertLogin("jdubois");
		this.userIndexRepository.insertLogin("jduboisier");
		this.userIndexRepository.insertLogin("jduboiseaux");
		this.userIndexRepository.insertLogin("jduboit");
		this.userIndexRepository.insertLogin("jduboiu");
		this.userIndexRepository.insertLogin("xxx");
		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(USER_INDEX_CF).setKey("login")
				.setRange("jdubois", "", false, 10).execute().get().getColumns();

		// 6 logins found, from jdubois to xxx
		assertEquals(columns.size(), 6, "6 logins found");
	}

	@Test(dependsOnMethods = "testInsertLogin")
	public void testFindLoginOk()
	{
		List<String> loginsList = this.userIndexRepository.findLogin("jdubois", 10);

		assertEquals(loginsList.size(), 3, "3 logins found");
		assertTrue(loginsList.contains("jdubois"), "'jdubois' found");
		assertTrue(loginsList.contains("jduboisier"), "'jduboisier' found");
		assertTrue(loginsList.contains("jduboiseaux"), "'jduboiseaux' found");
	}

	@Test(dependsOnMethods = "testInsertLogin")
	public void testFindLoginKo()
	{
		List<String> loginsList = this.userIndexRepository.findLogin("toto", 10);

		assertEquals(loginsList.size(), 0, "3 logins found");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertName()
	{
		this.userIndexRepository.insertName("jdubois", "Julien", "firstname");
		this.userIndexRepository.insertName("jduboisier", "Julien", "firstname");
		this.userIndexRepository.insertName("jduboiseaux", "Julien", "firstname");

		// Similar firstname with exact login
		this.userIndexRepository.insertName("jdubois", "Julienne", "firstname");

		// Similar firstname with different login
		this.userIndexRepository.insertName("jmartin", "Julienne", "firstname");

		// Similar firstname before Julien
		this.userIndexRepository.insertName("jdupont", "Jules", "firstname");

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(USER_INDEX_CF).setKey("firstname")
				.setRange("Julien", "", false, 10).execute().get().getColumns();

		// 6 logins found, from Julien to Julienne
		assertEquals(columns.size(), 2, "2 firstname found");
		assertEquals(((List<String>) columns.get(0).getValue()).size(), 3, " 3 logins found for 'Julien'");
		assertEquals(((List<String>) columns.get(1).getValue()).size(), 2, " 2 logins found for 'Julienne'");
	}

	@Test(dependsOnMethods = "testInsertName")
	public void testFindExactNameOk()
	{
		List<String> loginsList = this.userIndexRepository.findExactName("Julien", "firstname");
		assertEquals(loginsList.size(), 3, "3 logins found for 'Julien'");
		assertTrue(loginsList.contains("jdubois"), "'jdubois' found");
		assertTrue(loginsList.contains("jduboisier"), "'jduboisier' found");
		assertTrue(loginsList.contains("jduboiseaux"), "'jduboiseaux' found");
	}

	@Test(dependsOnMethods = "testInsertName")
	public void testFindExactNameKo()
	{
		List<String> loginsList = this.userIndexRepository.findExactName("Julia", "firstname");
		assertEquals(loginsList.size(), 0, "0 logins found for 'Julia'");
	}

	@Test(dependsOnMethods = "testInsertName")
	public void testFindNameOk()
	{
		List<String> loginsList = this.userIndexRepository.findName("Julien", 10, "firstname");
		assertEquals(loginsList.size(), 4, "4 logins found");
		assertTrue(loginsList.contains("jdubois"), "'jdubois' found");
		assertTrue(loginsList.contains("jduboisier"), "'jduboisier' found");
		assertTrue(loginsList.contains("jduboiseaux"), "'jduboiseaux' found");
		assertTrue(loginsList.contains("jmartin"), "'jmartin' found");
	}

	@Test(dependsOnMethods = "testInsertName")
	public void testFindNameKo()
	{
		List<String> loginsList = this.userIndexRepository.findName("Julia", 10, "firstname");
		assertEquals(loginsList.size(), 0, "0 logins found");
	}

	@Test(dependsOnMethods =
	{
			"testFindNameOk",
			"testFindExactNameOk"
	})
	public void testRemoveName()
	{
		this.userIndexRepository.removeName("jdubois", "Julien", "firstname");
		List<String> loginsList = this.userIndexRepository.findExactName("Julien", "firstname");
		assertEquals(loginsList.size(), 2, "2 logins found for 'Julien'");
		assertTrue(loginsList.contains("jduboisier"), "'jduboisier' found");
		assertTrue(loginsList.contains("jduboiseaux"), "'jduboiseaux' found");
	}

}
