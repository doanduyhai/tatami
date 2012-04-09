package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USER_INDEX_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import fr.ippon.tatami.repository.UserIndexRepository;

@Repository
public class CassandraUserIndexRepository extends CassandraAbstractRepository implements UserIndexRepository
{

	@Inject
	private Keyspace keyspaceOperator;

	@Override
	public void insertLogin(String login)
	{

		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.insert("login", USER_INDEX_CF, HFactory.createColumn(login, "", se, oe));
	}

	@Override
	public void insertName(String login, String name, String rowKey)
	{
		List<String> loginsFromDB = this.findExactName(name, rowKey);

		if (!loginsFromDB.contains(login))
		{
			loginsFromDB.add(login);
		}

		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.insert(rowKey, USER_INDEX_CF, HFactory.createColumn(name, loginsFromDB, se, oe));

	}

	@Override
	public void removeName(String login, String name, String rowKey)
	{
		List<String> loginsList = this.findExactName(name, rowKey);
		if (loginsList.size() > 0 && loginsList.contains(login))
		{
			loginsList.remove(login);
			Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
			mutator.insert(rowKey, USER_INDEX_CF, HFactory.createColumn(name, loginsList, se, oe));
		}
	}

	@Override
	public List<String> findLogin(String login, int limit)
	{
		List<String> results = new ArrayList<String>();

		List<HColumn<String, Object>> columns = createSliceQuery(keyspaceOperator, se, se, oe).setColumnFamily(USER_INDEX_CF).setKey("login")
				.setRange(login, "", false, limit).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			if (column.getName().startsWith(login))
			{
				results.add(column.getName());
			}
		}
		Collections.sort(results);
		return results;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findExactName(String name, String rowKey)
	{
		List<String> results = new ArrayList<String>();

		// Size = 1 to get the exact match. First result of text ordered columns is necessarily an exact match or no exact match
		List<HColumn<String, Object>> columns = createSliceQuery(keyspaceOperator, se, se, oe).setColumnFamily(USER_INDEX_CF).setKey(rowKey)
				.setRange(name, "", false, 1).execute().get().getColumns();
		if (columns.size() != 0)
		{
			if (StringUtils.equals(name, columns.get(0).getName()) && columns.get(0).getValue() != null && columns.get(0).getValue() instanceof List)
			{
				results = (List<String>) columns.get(0).getValue();
			}
			else
			{
				results = new ArrayList<String>();
			}
		}
		else
		{
			results = new ArrayList<String>();
		}
		return results;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findName(String name, int limit, String rowKey)
	{
		Set<String> set = new HashSet<String>();

		// Size = 1 to get the exact match. First result of text ordered columns is necessarily an exact match
		List<HColumn<String, Object>> columns = createSliceQuery(keyspaceOperator, se, se, oe).setColumnFamily(USER_INDEX_CF).setKey(rowKey)
				.setRange(name, "", false, limit).execute().get().getColumns();
		if (columns.size() > 0)
		{
			for (HColumn<String, Object> column : columns)
			{
				if (column.getName().startsWith(name) && column.getValue() != null && column.getValue() instanceof List)
					set.addAll((List<String>) column.getValue());
			}
		}
		List<String> results = new ArrayList<String>(set);
		Collections.sort(results);

		return results;
	}

	@Override
	public void insertFirstName(String firstName, String login)
	{
		this.insertName(login, firstName, "firstname");
	}

	@Override
	public void insertLastName(String lastName, String login)
	{
		this.insertName(login, lastName, "lastname");
	}

	@Override
	public void removeFirstName(String firstName, String login)
	{
		this.removeName(login, firstName, "firstname");
	}

	@Override
	public void removeLastName(String lastName, String login)
	{
		this.removeName(login, lastName, "lastname");
	}

	@Override
	public List<String> findFirstName(String firstName, int limit)
	{
		return this.findName(firstName, limit, "firstname");
	}

	@Override
	public List<String> findLastName(String lastName, int limit)
	{
		return this.findName(lastName, limit, "lastname");
	}

}
