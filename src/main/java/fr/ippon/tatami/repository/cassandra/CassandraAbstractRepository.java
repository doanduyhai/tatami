package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.thrift.ThriftCounterColumnQuery;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.TimeUUIDSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HCounterColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.CounterQuery;
import me.prettyprint.hom.EntityManagerImpl;

import com.eaio.uuid.UUID;

/**
 * @author DuyHai DOAN
 */
public abstract class CassandraAbstractRepository
{
	protected static final Serializer<String> se = StringSerializer.get();

	protected static final Serializer<Long> le = LongSerializer.get();

	protected static final Serializer<Object> oe = ObjectSerializer.get();

	protected static final Serializer<UUID> te = TimeUUIDSerializer.get();

	protected EntityManagerImpl em;

	protected Keyspace keyspaceOperator;

	public void setEm(EntityManagerImpl em)
	{
		this.em = em;
	}

	public void setKeyspaceOperator(Keyspace keyspaceOperator)
	{
		this.keyspaceOperator = keyspaceOperator;
	}

	protected void insertIntoCF(String CF, String key, String itemId)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.insert(key, CF, HFactory.createColumn(itemId, "", se, oe));
	}

	protected void insertIntoCFWithValue(String CF, String key, String itemId, Object value)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.insert(key, CF, HFactory.createColumn(itemId, value, se, oe));
	}

	protected Object getValueFromCF(String CF, String key, String itemId)
	{
		Object result = null;
		HColumn<String, Object> column = HFactory.createColumnQuery(keyspaceOperator, se, se, oe).setColumnFamily(CF).setKey(key).setName(itemId)
				.execute().get();
		if (column != null)
		{
			result = column.getValue();
		}
		return result;
	}

	protected void removeFromCF(String CF, String key, String itemId)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.delete(key, CF, itemId, se);
	}

	protected Collection<String> findRangeFromCF(String CF, String key, String startItemId, boolean reverse, int count)
	{
		List<String> items = new ArrayList<String>();

		List<HColumn<String, Object>> columns = createSliceQuery(keyspaceOperator, se, se, oe).setColumnFamily(CF).setKey(key)
				.setRange(startItemId, null, reverse, count + 1).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			items.add(column.getName());
		}

		if (items.size() > 0)
		{
			if (startItemId != null && items.contains(startItemId))
			{
				items.remove(startItemId);
			}

			if (items.size() > count)
			{
				items.remove(items.size() - 1);
			}
		}

		return items;
	}

	protected void removeRowFromCF(String CF, String key)
	{
		CqlQuery<String, String, Object> cqlQuery = new CqlQuery<String, String, Object>(keyspaceOperator, se, se, oe);
		cqlQuery.setQuery(" DELETE FROM " + CF + " WHERE KEY = '" + key + "';");
		cqlQuery.execute();

	}

	protected void removeCounter(String counterKey, String counterColumn)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.delete(counterKey, COUNTER_CF, counterColumn, se);

	}

	protected void incrementCounter(String counterKey, String counterColumn)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.incrementCounter(counterKey, COUNTER_CF, counterColumn, 1);
	}

	protected void decrementCounter(String counterKey, String counterColumn)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.decrementCounter(counterKey, COUNTER_CF, counterColumn, 1);

	}

	protected long getCounterValue(String counterKey, String counterColumn)
	{
		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspaceOperator, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(counterKey).setName(counterColumn);

		HCounterColumn<String> column = counter.execute().get();

		if (column == null)
		{
			return 0;
		}
		else
		{
			return column.getValue();
		}

	}
}
