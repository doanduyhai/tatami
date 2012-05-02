package fr.ippon.tatami.repository.cassandra;

import java.util.Calendar;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.service.util.TimeUUIdReorder;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class CassandraTweetRepository extends CassandraAbstractRepository implements TweetRepository
{

	private final Logger log = LoggerFactory.getLogger(CassandraTweetRepository.class);

	@Override
	public Tweet createTweet(String login, String content)
	{
		Tweet tweet = new Tweet();
		tweet.setTweetId(TimeUUIdReorder.reorderTimeUUId(TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString()));
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
	public void saveTweet(Tweet tweet)
	{
		em.persist(tweet);
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

		return tweet;
	}

	@Override
	@CacheEvict(value = "tweet-cache", key = "#tweet.tweetId")
	public void removeTweet(Tweet tweet)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Updating Tweet : " + tweet);
		}
		em.persist(tweet);
	}
}
