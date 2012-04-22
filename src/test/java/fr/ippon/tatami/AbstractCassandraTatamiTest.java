package fr.ippon.tatami;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

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
import fr.ippon.tatami.service.AuthenticationService;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.UserService;

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
	protected TimelineService timelineService;

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

	protected void mockAuthenticatedUser(User targetUser)
	{
		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(targetUser);
		userService.setAuthenticationService(mockAuthenticationService);
		timelineService.setAuthenticationService(mockAuthenticationService);
	}
}
