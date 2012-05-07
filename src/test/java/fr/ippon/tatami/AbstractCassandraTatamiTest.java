package fr.ippon.tatami;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.DAYLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITELINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWED_TWEET_INDEX_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWERS_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FRIENDS_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.MONTHLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TAGLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TIMELINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TWEET_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.USERLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.USER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.USER_INDEX_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.WEEKLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.YEARLINE_CF;
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
import fr.ippon.tatami.repository.FavoriteIndexRepository;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.repository.FollowedTweetIndexRepository;
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
import fr.ippon.tatami.service.pipeline.FavoritePipelineManager;
import fr.ippon.tatami.service.pipeline.TweetPipelineManager;
import fr.ippon.tatami.service.pipeline.UserPipelineManager;
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
	protected FollowedTweetIndexRepository followedTweetIndexRepository;

	@Inject
	protected FavoriteRepository favoriteRepository;

	@Inject
	protected FavoriteIndexRepository favoriteIndexRepository;

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
	protected UserPipelineManager userPipelineManager;

	@Inject
	protected TweetPipelineManager tweetPipelineManager;

	@Inject
	protected FavoritePipelineManager favoritePipelineManager;

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
			super.springTestContextPrepareTestInstance();
		}

	}

	// @AfterSuite
	@BeforeClass
	public void cleanCF()
	{
		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, se, se, se);

		cqlQuery.setQuery(" truncate " + USER_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + FRIENDS_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + FOLLOWERS_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + FOLLOWED_TWEET_INDEX_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + TWEET_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + DAYLINE_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + WEEKLINE_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + MONTHLINE_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + YEARLINE_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + FAVORITELINE_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + TAGLINE_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + TIMELINE_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + USERLINE_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + USER_INDEX_CF);
		cqlQuery.execute();
		cqlQuery.setQuery(" truncate " + COUNTER_CF);
		cqlQuery.execute();
	}

	protected void mockAuthenticatedUser(User targetUser)
	{
		AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
		when(mockAuthenticationService.getCurrentUser()).thenReturn(targetUser);
		userService.setAuthenticationService(mockAuthenticationService);

	}
}
