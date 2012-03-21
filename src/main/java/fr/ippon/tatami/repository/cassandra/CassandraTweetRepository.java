package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.application.config.ColumnFamilyKeys.TIMELINE_CF;
import static fr.ippon.tatami.application.config.ColumnFamilyKeys.USERLINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CassandraTweetRepository implements TweetRepository
{

	private final Logger log = LoggerFactory.getLogger(CassandraTweetRepository.class);

	@Inject
	private EntityManager em;

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

		log.debug("Persisting Tweet {} ", tweet);

		em.persist(tweet);
		return tweet;
	}

	@Override
	public void addTweetToUserline(Tweet tweet)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
		mutator.insert(tweet.getLogin(), USERLINE_CF,
				HFactory.createColumn(Calendar.getInstance().getTimeInMillis(), tweet.getTweetId(), LongSerializer.get(), StringSerializer.get()));
	}

	@Override
	public void addTweetToTimeline(String login, Tweet tweet)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
		mutator.insert(login, TIMELINE_CF,
				HFactory.createColumn(Calendar.getInstance().getTimeInMillis(), tweet.getTweetId(), LongSerializer.get(), StringSerializer.get()));
	}

	@Override
	public Collection<String> getTimeline(String login, int size)
	{
		ColumnSlice<String, String> result = createSliceQuery(keyspaceOperator, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get()).setColumnFamily(TIMELINE_CF).setKey(login).setRange(null, null, true, size).execute().get();

		Collection<String> tweetIds = new ArrayList<String>();
		for (HColumn<String, String> column : result.getColumns())
		{
			tweetIds.add(column.getValue());
		}
		return tweetIds;
	}

	@Override
	public Collection<String> getUserline(String login, int size)
	{
		ColumnSlice<String, String> result = createSliceQuery(keyspaceOperator, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get()).setColumnFamily(USERLINE_CF).setKey(login).setRange(null, null, true, size).execute().get();

		Collection<String> tweetIds = new ArrayList<String>();
		for (HColumn<String, String> column : result.getColumns())
		{
			tweetIds.add(column.getValue());
		}
		return tweetIds;
	}

	@Override
	@Cacheable("tweet-cache")
	public Tweet findTweetById(String tweetId)
	{
		log.debug("Finding tweet {} ", tweetId);
		return em.find(Tweet.class, tweetId);
	}
}
