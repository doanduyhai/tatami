package fr.ippon.tatami.application.config;

import static fr.ippon.tatami.application.config.ColumnFamilyKeys.COUNTER_CF;
import static fr.ippon.tatami.application.config.ColumnFamilyKeys.FOLLOWERS_CF;
import static fr.ippon.tatami.application.config.ColumnFamilyKeys.FRIENDS_CF;
import static fr.ippon.tatami.application.config.ColumnFamilyKeys.TIMELINE_CF;
import static fr.ippon.tatami.application.config.ColumnFamilyKeys.TWEET_CF;
import static fr.ippon.tatami.application.config.ColumnFamilyKeys.USERLINE_CF;
import static fr.ippon.tatami.application.config.ColumnFamilyKeys.USER_CF;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

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
import org.apache.thrift.transport.TTransportException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Main configuration file.
 * 
 * @author Julien Dubois
 */
@Configuration
@ImportResource(
{
		"classpath:META-INF/spring/applicationContext-security.xml",
		"classpath:META-INF/spring/applicationContext-cache.xml"
})
@ComponentScan(basePackages =
{
		"fr.ippon.tatami.application",
		"fr.ippon.tatami.repository",
		"fr.ippon.tatami.service"
})
@PropertySource("META-INF/tatami/tatami.properties")
public class ApplicationConfiguration
{

	private final Log log = LogFactory.getLog(ApplicationConfiguration.class);

	private String cassandraHost;

	private String cassandraClusterName;

	private String cassandraKeyspace;

	@Inject
	Environment environment;

	@Bean
	public Keyspace keyspaceOperator()
	{
		this.cassandraHost = environment.getProperty("cassandra.host");
		this.cassandraClusterName = environment.getProperty("cassandra.clusterName");
		this.cassandraKeyspace = environment.getProperty("cassandra.keyspace");

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

			addColumnFamily(cluster, USER_CF);
			addColumnFamily(cluster, FRIENDS_CF);
			addColumnFamily(cluster, FOLLOWERS_CF);
			addColumnFamily(cluster, TWEET_CF);
			addColumnFamily(cluster, TIMELINE_CF);
			addColumnFamily(cluster, USERLINE_CF);

			ThriftCfDef cfDef = new ThriftCfDef(cassandraKeyspace, COUNTER_CF, ComparatorType.UTF8TYPE);

			cfDef.setDefaultValidationClass(ComparatorType.COUNTERTYPE.getClassName());
			cluster.addColumnFamily(cfDef);
		}
		return HFactory.createKeyspace(cassandraKeyspace, cluster, consistencyLevelPolicy);
	}

	private void addColumnFamily(ThriftCluster cluster, String cfName)
	{
		ColumnFamilyDefinition cfd = HFactory.createColumnFamilyDefinition(cassandraKeyspace, cfName);
		cluster.addColumnFamily(cfd);
	}

	@Bean
	public EntityManager entityManager(Keyspace keyspace)
	{
		return new EntityManagerImpl(keyspace, "fr.ippon.tatami.domain");
	}

	@PostConstruct
	public void initTatami() throws IOException, TTransportException
	{
		log.info("Tatami started!");
	}
}
