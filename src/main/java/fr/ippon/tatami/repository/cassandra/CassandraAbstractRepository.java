package fr.ippon.tatami.repository.cassandra;

import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.TimeUUIDSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
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
}
