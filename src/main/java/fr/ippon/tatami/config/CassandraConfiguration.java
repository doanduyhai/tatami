package fr.ippon.tatami.config;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.DAYLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWERS_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FRIENDS_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.MONTHLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TAGLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TAGLINE_COUNT_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TIMELINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TWEET_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.USERLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.USER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.WEEKLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.YEARLINE_CF;

import javax.inject.Inject;

import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.cassandra.service.ThriftCluster;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hom.EntityManagerImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Main configuration file.
 * 
 * @author Julien Dubois
 */
@Configuration
public class CassandraConfiguration
{

	private final Log log = LogFactory.getLog(CassandraConfiguration.class);

	@Inject
	Environment env;

	@Bean(name = "keyspaceOperator")
	public Keyspace keyspaceOperator()
	{

		String cassandraHost = env.getProperty("cassandra.host");
		String cassandraClusterName = env.getProperty("cassandra.clusterName");
		String cassandraKeyspace = env.getProperty("cassandra.keyspace");

		CassandraHostConfigurator cassandraHostConfigurator = new CassandraHostConfigurator(cassandraHost);
		ThriftCluster cluster = new ThriftCluster(cassandraClusterName, cassandraHostConfigurator);
		ConfigurableConsistencyLevel consistencyLevelPolicy = new ConfigurableConsistencyLevel();
		consistencyLevelPolicy.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);

		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(cassandraKeyspace);
		if (keyspaceDef == null)
		{
			log.warn("Keyspace \"" + cassandraKeyspace + "\" does not exist, creating it!");
			keyspaceDef = new ThriftKsDef(cassandraKeyspace);
			cluster.addKeyspace(keyspaceDef, true);

			addColumnFamilyWithStringColumn(cluster, USER_CF);
			addColumnFamilyWithStringColumn(cluster, FRIENDS_CF);
			addColumnFamilyWithStringColumn(cluster, FOLLOWERS_CF);
			addColumnFamilyWithStringColumn(cluster, TWEET_CF);
			addColumnFamilyWithStringColumn(cluster, DAYLINE_CF);
			addColumnFamilyWithStringColumn(cluster, WEEKLINE_CF);
			addColumnFamilyWithStringColumn(cluster, MONTHLINE_CF);
			addColumnFamilyWithStringColumn(cluster, YEARLINE_CF);
			addColumnFamilyWithStringColumn(cluster, FAVLINE_CF);
			addColumnFamilyWithLongColumn(cluster, TAGLINE_CF);
			addColumnFamilyWithStringColumn(cluster, TAGLINE_COUNT_CF);
			addColumnFamilyWithLongColumn(cluster, TIMELINE_CF);
			addColumnFamilyWithLongColumn(cluster, USERLINE_CF);

			ThriftCfDef cfDef = new ThriftCfDef(cassandraKeyspace, COUNTER_CF, ComparatorType.UTF8TYPE);

			cfDef.setDefaultValidationClass(ComparatorType.COUNTERTYPE.getClassName());
			cluster.addColumnFamily(cfDef);
		}
		return HFactory.createKeyspace(cassandraKeyspace, cluster, consistencyLevelPolicy);
	}

	private void addColumnFamilyWithStringColumn(ThriftCluster cluster, String cfName)
	{

		String cassandraKeyspace = env.getProperty("cassandra.keyspace");
		ColumnFamilyDefinition cfd = HFactory.createColumnFamilyDefinition(cassandraKeyspace, cfName, ComparatorType.UTF8TYPE);
		cfd.setKeyValidationClass("org.apache.cassandra.db.marshal.UTF8Type");
		cfd.setComparatorType(ComparatorType.UTF8TYPE);
		cluster.addColumnFamily(cfd);
	}

	private void addColumnFamilyWithLongColumn(ThriftCluster cluster, String cfName)
	{

		String cassandraKeyspace = env.getProperty("cassandra.keyspace");
		ColumnFamilyDefinition cfd = HFactory.createColumnFamilyDefinition(cassandraKeyspace, cfName, ComparatorType.UTF8TYPE);
		cfd.setKeyValidationClass("org.apache.cassandra.db.marshal.UTF8Type");
		cfd.setComparatorType(ComparatorType.LONGTYPE);
		cfd.setDefaultValidationClass("org.apache.cassandra.db.marshal.UTF8Type");
		cluster.addColumnFamily(cfd);
	}

	@Bean(name = "em")
	public EntityManagerImpl entityManager(Keyspace keyspace)
	{
		return new EntityManagerImpl(keyspace, "fr.ippon.tatami.domain");
	}

}
