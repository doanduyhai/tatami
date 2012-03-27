package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.DAYLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TAGLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TIMELINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.USERLINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.SliceQuery;
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
public class CassandraTweetRepository implements TweetRepository
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
	public void addTweetToFavoritesline(Tweet tweet, String login)
	{
		assert !tweet.getRemoved() : "tweet is not supposed to be removed";
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
		mutator.insert(login, FAVLINE_CF,
				HFactory.createColumn(Calendar.getInstance().getTimeInMillis(), tweet.getTweetId(), LongSerializer.get(), StringSerializer.get()));
	}

	@Override
	public void addTweetToDayline(Tweet tweet, String key)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
		mutator.insert(key, DAYLINE_CF,
				HFactory.createColumn(Calendar.getInstance().getTimeInMillis(), tweet.getTweetId(), LongSerializer.get(), StringSerializer.get()));
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

	private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

	@Override
	public void addTweetToTagline(Tweet tweet)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
		Matcher m = HASHTAG_PATTERN.matcher(tweet.getContent());
		while (m.find())
		{
			String tag = m.group(1);
			log.debug("tag list augmented : {} ", tag);
			mutator.insert(tag, TAGLINE_CF,
					HFactory.createColumn(Calendar.getInstance().getTimeInMillis(), tweet.getTweetId(), LongSerializer.get(), StringSerializer.get()));
		}
	}

	@Override
	public Collection<String> getDayline(String date)
	{
		SliceQuery<String, String, String> sq = createSliceQuery(keyspaceOperator, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get()).setColumnFamily(DAYLINE_CF).setKey(date).setRange(null, null, false, 100);

		Collection<String> tweetIds = new ArrayList<String>();
		ColumnSliceIterator<String, String, String> csi = new ColumnSliceIterator<String, String, String>(sq, null, "", false);
		while (csi.hasNext())
		{
			tweetIds.add(csi.next().getValue());
		}
		return tweetIds;
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
	public Collection<String> getTagline(String tag, int size)
	{
		ColumnSlice<Long, String> result = createSliceQuery(keyspaceOperator, StringSerializer.get(), LongSerializer.get(), StringSerializer.get())
				.setColumnFamily(TAGLINE_CF).setKey(tag).setRange(null, null, true, size).execute().get();

		Collection<String> tweetIds = new ArrayList<String>();
		for (HColumn<Long, String> column : result.getColumns())
		{
			tweetIds.add(column.getValue());
		}
		return tweetIds;
	}

	@Override
	public Collection<String> getFavoritesline(String login)
	{
		SliceQuery<String, String, String> sq = createSliceQuery(keyspaceOperator, StringSerializer.get(), StringSerializer.get(),
				StringSerializer.get()).setColumnFamily(FAVLINE_CF).setKey(login).setRange(null, null, false, 50);

		Collection<String> tweetIds = new ArrayList<String>();
		ColumnSliceIterator<String, String, String> csi = new ColumnSliceIterator<String, String, String>(sq, null, "", true);
		while (csi.hasNext())
		{
			tweetIds.add(csi.next().getValue());
		}
		return tweetIds;
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
