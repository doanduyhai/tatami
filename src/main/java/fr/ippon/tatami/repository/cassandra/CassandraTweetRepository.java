package fr.ippon.tatami.repository.cassandra;

import java.util.Calendar;

import javax.inject.Inject;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hom.EntityManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.repository.TweetRepository;

/**
 * Cassandra implementation of the user repository.
 * 
 * @author Julien Dubois
 */
@Repository
public class CassandraTweetRepository extends CassandraAbstractRepository implements TweetRepository
{

	private final Logger log = LoggerFactory.getLogger(CassandraTweetRepository.class);

	@Inject
	private EntityManagerImpl em;

	@Inject
	private Keyspace keyspaceOperator;

	@Override
	public Tweet createTweet(String login, String content)
	{
		Tweet tweet = new Tweet();
		tweet.setTweetId(TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString());
		tweet.setLogin(login);
		tweet.setContent(content);
		tweet.setTweetDate(Calendar.getInstance().getTime());
		if (log.isDebugEnabled())
		{
			log.debug("Persisting Tweet : " + tweet);
		}
		em.persist(tweet);
		return tweet;
	}

	@Override
	@Cacheable("tweet-cache")
	public Tweet findTweetById(String tweetId)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Finding tweet : " + tweetId);
		}
		Tweet tweet = em.find(Tweet.class, tweetId);
		return Boolean.TRUE.equals(tweet.getRemoved()) ? null : tweet;
	}

	@Override
	@CacheEvict(value = "tweet-cache", key = "#tweet.tweetId")
	public void removeTweet(Tweet tweet)
	{
		tweet.setRemoved(true);
		if (log.isDebugEnabled())
		{
			log.debug("Updating Tweet : " + tweet);
		}
		em.persist(tweet);
	}
}
