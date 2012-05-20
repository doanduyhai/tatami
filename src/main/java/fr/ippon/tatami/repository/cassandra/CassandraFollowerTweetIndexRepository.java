package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWED_TWEET_INDEX_CF;
import static fr.ippon.tatami.config.CounterKeys.FOLLOWED_TWEET_INDEX_COUNTER;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;

import java.util.Collection;

import fr.ippon.tatami.repository.FollowerTweetIndexRepository;

public class CassandraFollowerTweetIndexRepository extends CassandraAbstractRepository implements FollowerTweetIndexRepository
{

	@Override
	public void addTweetToIndex(String authorLogin, String followerLogin, String tweetId)
	{
		this.insertIntoCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + followerLogin, tweetId);
		this.incrementCounter(FOLLOWED_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + followerLogin);
	}

	@Override
	public Collection<String> findTweetsForUserAndFollower(String authorLogin, String followerLogin)
	{
		long indexTweetsCount = this.getCounterValue(FOLLOWED_TWEET_INDEX_COUNTER, authorLogin + ':' + followerLogin);
		return this.findRangeFromCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + followerLogin, null, false, (int) indexTweetsCount);
	}

	@Override
	public void removeTweetFromIndex(String authorLogin, String followerLogin, String tweetId)
	{
		this.removeFromCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + followerLogin, tweetId);
		this.decrementCounter(FOLLOWED_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + followerLogin);
	}

	@Override
	public void removeIndex(String authorLogin, String followerLogin)
	{
		this.removeRowFromCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + followerLogin);
		this.removeCounter(FOLLOWED_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + followerLogin);

	}
}
