package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.COUNTER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWED_TWEET_INDEX_CF;

import java.util.Collection;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.thrift.ThriftCounterColumnQuery;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.CounterQuery;
import fr.ippon.tatami.repository.FollowedTweetIndexRepository;

public class CassandraFollowedTweetIndexRepository extends CassandraAbstractRepository implements FollowedTweetIndexRepository
{

	public static final String FOLLOWED_TWEET_COUNTER = "FollowedTweetCounter";

	@Override
	public void addTweetToIndex(String authorLogin, String followerLogin, String tweetId)
	{
		this.insertIntoCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + ":" + followerLogin, tweetId);
		this.incrementCounterForUserAndFollower(authorLogin, followerLogin);

	}

	@Override
	public Collection<String> findTweetsForUserAndFollower(String authorLogin, String followerLogin)
	{
		long indexTweetsCount = this.countIndexedTweetsForUserAndFollower(authorLogin, followerLogin);

		return this.findRangeFromCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + ':' + followerLogin, null, false, (int) indexTweetsCount);
	}

	@Override
	public void removeTweetFromIndex(String authorLogin, String followerLogin, String tweetId)
	{
		this.removeFromCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + ":" + followerLogin, tweetId);
		this.decrementCounterForUserAndFollower(authorLogin, followerLogin);
	}

	@Override
	public void removeIndex(String authorLogin, String followerLogin)
	{
		CqlQuery<String, String, Object> cqlQuery = new CqlQuery<String, String, Object>(keyspaceOperator, se, se, oe);

		cqlQuery.setQuery(" DELETE FROM " + FOLLOWED_TWEET_INDEX_CF + " WHERE KEY = '" + authorLogin + ':' + followerLogin + "';");
		cqlQuery.execute();
		this.removeCounterForUserAndFollower(authorLogin, followerLogin);

	}

	private void incrementCounterForUserAndFollower(String authorLogin, String followerLogin)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.incrementCounter(FOLLOWED_TWEET_COUNTER, COUNTER_CF, authorLogin + ':' + followerLogin, 1);
	}

	private void decrementCounterForUserAndFollower(String authorLogin, String followerLogin)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.decrementCounter(FOLLOWED_TWEET_COUNTER, COUNTER_CF, authorLogin + ':' + followerLogin, 1);

	}

	private void removeCounterForUserAndFollower(String authorLogin, String followerLogin)
	{
		Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, se);
		mutator.delete(FOLLOWED_TWEET_COUNTER, COUNTER_CF, authorLogin + ':' + followerLogin, se);
	}

	private long countIndexedTweetsForUserAndFollower(String authorLogin, String followerLogin)
	{
		CounterQuery<String, String> counter = new ThriftCounterColumnQuery<String, String>(keyspaceOperator, se, se);

		counter.setColumnFamily(COUNTER_CF).setKey(FOLLOWED_TWEET_COUNTER).setName(authorLogin + ':' + followerLogin);
		return counter.execute().get().getValue();

	}

}
