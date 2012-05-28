package fr.ippon.tatami;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javassist.Modifier;

import javax.inject.Inject;

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

import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.eaio.uuid.UUID;

import fr.ippon.tatami.config.ColumnFamilyKeys;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.BlockedUserRepository;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.repository.MentionLineRepository;
import fr.ippon.tatami.repository.ReTweetRepository;
import fr.ippon.tatami.repository.StatsRepository;
import fr.ippon.tatami.repository.TagLineRepository;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.repository.UserIndexRepository;
import fr.ippon.tatami.repository.UserLineRepository;
import fr.ippon.tatami.repository.UserRepository;
import fr.ippon.tatami.service.lines.FavoritelineService;
import fr.ippon.tatami.service.lines.MentionlineService;
import fr.ippon.tatami.service.lines.StatslineService;
import fr.ippon.tatami.service.lines.TaglineService;
import fr.ippon.tatami.service.lines.TimelineService;
import fr.ippon.tatami.service.lines.UserlineService;
import fr.ippon.tatami.service.pipeline.tweet.TweetPipelineManager;
import fr.ippon.tatami.service.pipeline.tweet.rendering.TweetRenderingPipelineManager;
import fr.ippon.tatami.service.pipeline.user.UserPipelineManager;
import fr.ippon.tatami.service.pipeline.user.rendering.UserRenderingPipelineManager;
import fr.ippon.tatami.service.renderer.tweet.ContentTweetRenderer;
import fr.ippon.tatami.service.renderer.tweet.DeleteTweetRenderer;
import fr.ippon.tatami.service.renderer.tweet.FavoriteTweetRenderer;
import fr.ippon.tatami.service.renderer.user.ContactsUserRenderer;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.tweet.RetweetService;
import fr.ippon.tatami.service.tweet.TweetService;
import fr.ippon.tatami.service.tweet.XssEncodingService;
import fr.ippon.tatami.service.user.ContactsService;
import fr.ippon.tatami.service.user.UserService;

@ContextConfiguration(locations =
{
		"classpath:tatami-test-properties.xml",
		"classpath:tatami-repository.xml",
		"classpath:tatami-user-service.xml",
		"classpath:tatami-tweet-service.xml",
		"classpath:tatami-lines-service.xml",
		"classpath:tatami-pipelines.xml"
})
public abstract class AbstractCassandraTatamiTest extends AbstractTestNGSpringContextTests
{

	private static boolean isInitialized = false;

	@Value("#{cassandraConfiguration.getKeyspace()}")
	protected Keyspace keyspace;

	@Inject
	protected EntityManagerImpl entityManager;

	@Inject
	protected UserRepository userRepository;

	@Inject
	protected TweetRepository tweetRepository;

	@Inject
	protected FriendRepository friendRepository;

	@Inject
	protected FollowerRepository followerRepository;

	@Inject
	protected FavoriteRepository favoriteRepository;

	@Inject
	protected TagLineRepository tagLineRepository;

	@Inject
	protected StatsRepository statsRepository;

	@Inject
	protected UserLineRepository userLineRepository;

	@Inject
	protected TimeLineRepository timeLineRepository;

	@Inject
	protected UserIndexRepository userIndexRepository;

	@Inject
	protected MentionLineRepository mentionLineRepository;

	@Inject
	protected BlockedUserRepository blockedUserRepository;

	@Inject
	protected ReTweetRepository retweetRepository;

	@Inject
	protected UserService userService;

	@Inject
	protected ContactsService contactsService;

	@Inject
	protected TimelineService timelineService;

	@Inject
	protected FavoritelineService favoritelineService;

	@Inject
	protected MentionlineService mentionlineService;

	@Inject
	protected StatslineService statslineService;

	@Inject
	protected TaglineService taglineService;

	@Inject
	protected UserlineService userlineService;

	@Inject
	protected UserPipelineManager userPipelineManager;

	@Inject
	protected TweetPipelineManager tweetPipelineManager;

	@Inject
	protected UserRenderingPipelineManager userRenderingPipelineManager;

	@Inject
	protected TweetRenderingPipelineManager tweetRenderingPipelineManager;

	@Inject
	protected TweetService tweetService;

	@Inject
	protected RetweetService retweetService;

	@Inject
	protected XssEncodingService xssEncodingService;

	@Inject
	protected ContactsUserRenderer contactsUserRenderer;

	@Inject
	protected FavoriteTweetRenderer favoriteTweetRenderer;

	@Inject
	protected DeleteTweetRenderer deleteTweetRenderer;

	@Inject
	protected ContentTweetRenderer contentTweetRenderer;

	protected static final Serializer<String> se = StringSerializer.get();

	protected static final Serializer<Long> le = LongSerializer.get();

	protected static final Serializer<Object> oe = ObjectSerializer.get();

	protected static final Serializer<UUID> te = TimeUUIDSerializer.get();

	@BeforeSuite
	public void prepareCassandraCluster() throws Exception
	{
		if (!isInitialized)
		{
			EmbeddedCassandraServerHelper.startEmbeddedCassandra();
			isInitialized = true;
			super.springTestContextPrepareTestInstance();
		}

	}

	// @AfterSuite
	@BeforeClass
	public void cleanCF() throws IllegalArgumentException, IllegalAccessException
	{
		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, se, se, se);

		for (Field field : ColumnFamilyKeys.class.getDeclaredFields())
		{
			if (Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()))
			{
				cqlQuery.setQuery(" truncate " + field.get(null));
				cqlQuery.execute();
			}
		}

	}

	protected AuthenticationService mockAuthenticatedUser(User targetUser)
	{
		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(targetUser);
		userService.setAuthenticationService(mockAuthenticationService);

		return mockAuthenticationService;

	}

	protected void insertIntoCF(String CF, String key, String itemId)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspace, se);
		mutator.insert(key, CF, HFactory.createColumn(itemId, "", se, oe));
	}

	protected void insertIntoCFWithValue(String CF, String key, String itemId, Object value)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspace, se);
		mutator.insert(key, CF, HFactory.createColumn(itemId, value, se, oe));
	}

	protected Object getValueFromCF(String CF, String key, String itemId)
	{
		Object result = null;
		HColumn<String, Object> column = HFactory.createColumnQuery(keyspace, se, se, oe).setColumnFamily(CF).setKey(key).setName(itemId).execute()
				.get();
		if (column != null)
		{
			result = column.getValue();
		}
		return result;
	}

	protected void removeFromCF(String CF, String key, String itemId)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspace, se);
		mutator.delete(key, CF, itemId, se);
	}

	protected Collection<String> findRangeFromCF(String CF, String key, String startItemId, boolean reverse, int count)
	{
		List<String> items = new ArrayList<String>();

		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(CF).setKey(key)
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

	protected Collection<HColumn<String, Object>> findInclusiveColumnsRangeFromCF(String CF, String key, String startItemId, boolean reverse,
			int count)
	{
		List<HColumn<String, Object>> columns = createSliceQuery(keyspace, se, se, oe).setColumnFamily(CF).setKey(key)
				.setRange(startItemId, null, reverse, count + 1).execute().get().getColumns();

		return columns;
	}

	protected void removeRowFromCF(String CF, String key)
	{
		CqlQuery<String, String, Object> cqlQuery = new CqlQuery<String, String, Object>(keyspace, se, se, oe);
		cqlQuery.setQuery(" DELETE FROM " + CF + " WHERE KEY = '" + key + "';");
		cqlQuery.execute();

	}

	protected void removeCounter(String counterKey, String counterColumn)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspace, se);
		mutator.delete(counterKey, COUNTER_CF, counterColumn, se);

	}

	protected void incrementCounter(String counterKey, String counterColumn)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspace, se);
		mutator.incrementCounter(counterKey, COUNTER_CF, counterColumn, 1);
	}

	protected void decrementCounter(String counterKey, String counterColumn)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspace, se);
		mutator.decrementCounter(counterKey, COUNTER_CF, counterColumn, 1);

	}

	protected long getCounterValue(String counterKey, String counterColumn)
	{
		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspace, se, se);

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
