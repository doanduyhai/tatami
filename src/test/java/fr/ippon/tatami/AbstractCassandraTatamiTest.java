package fr.ippon.tatami;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.TimeUUIDSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hom.EntityManagerImpl;

import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.eaio.uuid.UUID;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.FriendRepository;
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
import fr.ippon.tatami.service.pipeline.TweetPipelineManager;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.tweet.TweetService;
import fr.ippon.tatami.service.tweet.XssEncodingService;
import fr.ippon.tatami.service.user.ContactsService;
import fr.ippon.tatami.service.user.UserService;

@ContextConfiguration(locations =
{
		"classpath:tatami-test-properties.xml",
		"classpath:tatami-repository.xml",
		"classpath:tatami-service.xml"
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
	protected TweetPipelineManager tweetPipelineManager;

	@Inject
	protected TweetService tweetService;

	@Inject
	protected XssEncodingService xssEncodingService;

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
		}
		super.springTestContextPrepareTestInstance();
	}

	@BeforeClass
	public void cleanCF()
	{
		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, se, se, se);

		cqlQuery.setQuery(" truncate User");
		cqlQuery.execute();

		cqlQuery.setQuery(" truncate UserFriends");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate UserFollowers");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate Tweet");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate DayLine");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate WeekLine");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate MonthLine");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate YearLine");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate FavoriteLine");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate TagLine");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate TimeLine");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate UserLine");
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate UserIndex");

	}

	protected void mockAuthenticatedUser(User targetUser)
	{
		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(targetUser);
		userService.setAuthenticationService(mockAuthenticationService);

	}
}
