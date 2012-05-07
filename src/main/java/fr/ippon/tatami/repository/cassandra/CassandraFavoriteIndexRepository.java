package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITE_INDEX_CF;

import java.util.Collection;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.thrift.ThriftCounterColumnQuery;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.CounterQuery;
import fr.ippon.tatami.repository.FavoriteIndexRepository;

public class CassandraFavoriteIndexRepository extends CassandraAbstractRepository implements FavoriteIndexRepository
{
	public static final String FAVORITE_TWEET_INDEX_COUNTER = "FavoriteTweetIndexCounter";

	@Override
	public void addTweetToFavoriteIndex(String userLogin, String tweetId)
	{
		this.insertIntoCF(FAVORITE_INDEX_CF, tweetId, userLogin);
		this.incrementCounterForTweet(tweetId);

	}

	@Override
	public void removeTweetFromFavoriteIndex(String userLogin, String tweetId)
	{
		this.removeFromCF(FAVORITE_INDEX_CF, tweetId, userLogin);
		this.decrementCounterForTweet(tweetId);
	}

	@Override
	public Collection<String> getUsersForTweetFromIndex(String tweetId)
	{
		long userCount = this.countUsersForTweet(tweetId);
		return this.findRangeFromCF(FAVORITE_INDEX_CF, tweetId, null, false, (int) userCount);
	}

	@Override
	public void removeIndexForTweet(String tweetId)
	{
		CqlQuery<String, String, Object> cqlQuery = new CqlQuery<String, String, Object>(keyspaceOperator, se, se, oe);
		cqlQuery.setQuery(" DELETE FROM " + FAVORITE_INDEX_CF + " WHERE KEY = '" + tweetId + "';");
		cqlQuery.execute();
		this.removeCounterForTweet(tweetId);

	}

	private void removeCounterForTweet(String tweetId)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.delete(FAVORITE_TWEET_INDEX_COUNTER, COUNTER_CF, tweetId, se);

	}

	private void incrementCounterForTweet(String tweetId)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.incrementCounter(FAVORITE_TWEET_INDEX_COUNTER, COUNTER_CF, tweetId, 1);
	}

	private void decrementCounterForTweet(String tweetId)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.decrementCounter(FAVORITE_TWEET_INDEX_COUNTER, COUNTER_CF, tweetId, 1);

	}

	private long countUsersForTweet(String tweetId)
	{
		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspaceOperator, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(FAVORITE_TWEET_INDEX_COUNTER).setName(tweetId);
		return counter.execute().get().getValue();

	}

}
