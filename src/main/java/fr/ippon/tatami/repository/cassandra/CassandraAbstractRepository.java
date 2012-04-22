package fr.ippon.tatami.repository.cassandra;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.TimeUUIDSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
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

}
