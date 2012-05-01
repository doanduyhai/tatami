package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.DAYLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.MONTHLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.WEEKLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.YEARLINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import fr.ippon.tatami.repository.StatsRepository;

/**
 * @author DuyHai DOAN
 */
public class CassandraStatsRepository extends CassandraAbstractRepository implements StatsRepository
{
	@Override
	public void addTweetToDay(String login, String day)
	{
		HColumn<String, Long> result = HFactory.createColumnQuery(keyspaceOperator, se, se, le).setColumnFamily(DAYLINE_CF).setKey(day)
				.setName(login).execute().get();

		long count = 0;

		if (result != null && result.getValue() != null)
		{
			count = result.getValue();
		}
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.insert(day, DAYLINE_CF, HFactory.createColumn(login, count + 1, se, le));

	}

	@Override
	public void removeTweetFromDay(String login, String day)
	{
		HColumn<String, Long> result = HFactory.createColumnQuery(keyspaceOperator, se, se, le).setColumnFamily(DAYLINE_CF).setKey(day)
				.setName(login).execute().get();
		if (result != null && result.getValue() != null)
		{
			long count = result.getValue();
			Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
			if (count > 0)
			{

				mutator.insert(day, DAYLINE_CF, HFactory.createColumn(login, count - 1, se, le));
			}
			else
			{
				mutator.delete(day, DAYLINE_CF, login, se);
			}
		}

	}

	@Override
	public Map<String, Long> findTweetsForDay(String day)
	{
		int count = HFactory.createCountQuery(keyspaceOperator, se, se).setColumnFamily(DAYLINE_CF).setKey(day)
				.setRange(null, null, Integer.MAX_VALUE).execute().get();

		List<HColumn<String, Long>> columns = createSliceQuery(keyspaceOperator, se, se, le).setColumnFamily(DAYLINE_CF).setKey(day)
				.setRange(null, null, false, count).execute().get().getColumns();

		Map<String, Long> result = new HashMap<String, Long>();
		for (HColumn<String, Long> column : columns)
		{
			result.put(column.getName(), column.getValue());
		}

		return result;
	}

	@Override
	public void addTweetToWeek(String login, String week)
	{
		HColumn<String, Long> result = HFactory.createColumnQuery(keyspaceOperator, se, se, le).setColumnFamily(WEEKLINE_CF).setKey(week)
				.setName(login).execute().get();

		long count = 0;

		if (result != null && result.getValue() != null)
		{
			count = result.getValue();
		}
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.insert(week, WEEKLINE_CF, HFactory.createColumn(login, count + 1, se, le));
	}

	@Override
	public void removeTweetFromWeek(String login, String week)
	{
		HColumn<String, Long> result = HFactory.createColumnQuery(keyspaceOperator, se, se, le).setColumnFamily(WEEKLINE_CF).setKey(week)
				.setName(login).execute().get();
		if (result != null && result.getValue() != null)
		{
			long count = result.getValue();
			Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
			if (count > 0)
			{

				mutator.insert(week, WEEKLINE_CF, HFactory.createColumn(login, count - 1, se, le));
			}
			else
			{
				mutator.delete(week, WEEKLINE_CF, login, se);
			}
		}
	}

	@Override
	public Map<String, Long> findTweetsForWeek(String week)
	{
		int count = HFactory.createCountQuery(keyspaceOperator, se, se).setColumnFamily(WEEKLINE_CF).setKey(week)
				.setRange(null, null, Integer.MAX_VALUE).execute().get();

		List<HColumn<String, Long>> columns = createSliceQuery(keyspaceOperator, se, se, le).setColumnFamily(WEEKLINE_CF).setKey(week)
				.setRange(null, null, false, count).execute().get().getColumns();

		Map<String, Long> result = new HashMap<String, Long>();
		for (HColumn<String, Long> column : columns)
		{
			result.put(column.getName(), column.getValue());
		}

		return result;
	}

	@Override
	public void addTweetToMonth(String login, String month)
	{
		HColumn<String, Long> result = HFactory.createColumnQuery(keyspaceOperator, se, se, le).setColumnFamily(MONTHLINE_CF).setKey(month)
				.setName(login).execute().get();

		long count = 0;

		if (result != null && result.getValue() != null)
		{
			count = result.getValue();
		}
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.insert(month, MONTHLINE_CF, HFactory.createColumn(login, count + 1, se, le));

	}

	@Override
	public void removeTweetFromMonth(String login, String month)
	{
		HColumn<String, Long> result = HFactory.createColumnQuery(keyspaceOperator, se, se, le).setColumnFamily(MONTHLINE_CF).setKey(month)
				.setName(login).execute().get();
		if (result != null && result.getValue() != null)
		{
			long count = result.getValue();
			Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
			if (count > 0)
			{

				mutator.insert(month, MONTHLINE_CF, HFactory.createColumn(login, count - 1, se, le));
			}
			else
			{
				mutator.delete(month, MONTHLINE_CF, login, se);
			}
		}
	}

	@Override
	public Map<String, Long> findTweetsForMonth(String month)
	{
		int count = HFactory.createCountQuery(keyspaceOperator, se, se).setColumnFamily(MONTHLINE_CF).setKey(month)
				.setRange(null, null, Integer.MAX_VALUE).execute().get();

		List<HColumn<String, Long>> columns = createSliceQuery(keyspaceOperator, se, se, le).setColumnFamily(MONTHLINE_CF).setKey(month)
				.setRange(null, null, false, count).execute().get().getColumns();

		Map<String, Long> result = new HashMap<String, Long>();
		for (HColumn<String, Long> column : columns)
		{
			result.put(column.getName(), column.getValue());
		}

		return result;
	}

	@Override
	public void addTweetToYear(String login, String year)
	{
		HColumn<String, Long> result = HFactory.createColumnQuery(keyspaceOperator, se, se, le).setColumnFamily(YEARLINE_CF).setKey(year)
				.setName(login).execute().get();

		long count = 0;

		if (result != null && result.getValue() != null)
		{
			count = result.getValue();
		}
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.insert(year, YEARLINE_CF, HFactory.createColumn(login, count + 1, se, le));

	}

	@Override
	public void removeTweetFromYear(String login, String year)
	{
		HColumn<String, Long> result = HFactory.createColumnQuery(keyspaceOperator, se, se, le).setColumnFamily(YEARLINE_CF).setKey(year)
				.setName(login).execute().get();
		if (result != null && result.getValue() != null)
		{
			long count = result.getValue();
			Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
			if (count > 0)
			{

				mutator.insert(year, YEARLINE_CF, HFactory.createColumn(login, count - 1, se, le));
			}
			else
			{
				mutator.delete(year, YEARLINE_CF, login, se);
			}
		}

	}

	@Override
	public Map<String, Long> findTweetsForYear(String year)
	{
		int count = HFactory.createCountQuery(keyspaceOperator, se, se).setColumnFamily(YEARLINE_CF).setKey(year)
				.setRange(null, null, Integer.MAX_VALUE).execute().get();

		List<HColumn<String, Long>> columns = createSliceQuery(keyspaceOperator, se, se, le).setColumnFamily(YEARLINE_CF).setKey(year)
				.setRange(null, null, false, count).execute().get().getColumns();

		Map<String, Long> result = new HashMap<String, Long>();
		for (HColumn<String, Long> column : columns)
		{
			result.put(column.getName(), column.getValue());
		}

		return result;
	}

}
