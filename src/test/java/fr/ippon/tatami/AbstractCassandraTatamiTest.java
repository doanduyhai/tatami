package fr.ippon.tatami;

import javax.inject.Inject;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hom.EntityManagerImpl;

import org.cassandraunit.DataLoader;
import org.cassandraunit.dataset.json.ClassPathJsonDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;

import fr.ippon.tatami.config.ApplicationTestConfiguration;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.CounterRepository;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.repository.UserRepository;
import fr.ippon.tatami.service.TimelineService;
import fr.ippon.tatami.service.UserService;

@ContextConfiguration(classes = ApplicationTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractCassandraTatamiTest extends AbstractTestNGSpringContextTests
{

	private static boolean isInitialized = false;

	@Inject
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
	protected CounterRepository counterRepository;

	@Inject
	protected UserService userService;

	@Inject
	protected TimelineService timelineService;

	@BeforeSuite
	public void prepareCassandraCluster() throws Exception
	{
		if (!isInitialized)
		{
			EmbeddedCassandraServerHelper.startEmbeddedCassandra();
			/* create structure and load data */
			String clusterName = "Tatami cluster";
			String host = "localhost:9171";
			DataLoader dataLoader = new DataLoader(clusterName, host);
			dataLoader.load(new ClassPathJsonDataSet("dataset/dataset.json"));
			isInitialized = true;
		}
		super.springTestContextPrepareTestInstance();
	}

	protected User constructAUser(String login, String email, String firstName, String lastName)
	{
		User user = new User();
		user.setLogin(login);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		return user;
	}

	protected User constructAUser(String login, String email)
	{
		return constructAUser(login, email, null, null);
	}
}
