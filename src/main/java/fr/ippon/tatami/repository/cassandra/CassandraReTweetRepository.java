package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.RETWEETER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.RETWEET_TARGET_USER_CF;
import static fr.ippon.tatami.config.CounterKeys.RETWEETER_COUNTER;
import static fr.ippon.tatami.config.CounterKeys.RETWEET_TARGET_USER_COUNTER;

import java.util.Collection;

import fr.ippon.tatami.repository.ReTweetRepository;

public class CassandraReTweetRepository extends CassandraAbstractRepository implements ReTweetRepository
{

	@Override
	public void addRetweeter(String retweeterLogin, String tweetId)
	{
		this.insertIntoCF(RETWEETER_CF, tweetId, retweeterLogin);
		this.incrementCounter(RETWEETER_COUNTER, tweetId);

	}

	@Override
	public void addTargetUserForRetweet(String targetUserLogin, String tweetId)
	{
		this.insertIntoCF(RETWEET_TARGET_USER_CF, tweetId, targetUserLogin);
		this.incrementCounter(RETWEET_TARGET_USER_COUNTER, tweetId);

	}

	@Override
	public void removeTargetUserForRetweet(String targetUserLogin, String tweetId)
	{
		this.removeFromCF(RETWEET_TARGET_USER_CF, tweetId, targetUserLogin);
		this.decrementCounter(RETWEET_TARGET_USER_COUNTER, tweetId);

	}

	@Override
	public Collection<String> findRetweeterForTweet(String tweetId)
	{
		long retweetCount = this.countRetweeter(tweetId);
		return this.findRangeFromCF(RETWEETER_CF, tweetId, null, false, (int) retweetCount);

	}

	@Override
	public Collection<String> findTartgetUsersForRetweet(String tweetId)
	{
		long targetUserForRetweetCount = this.getCounterValue(RETWEET_TARGET_USER_COUNTER, tweetId);
		return this.findRangeFromCF(RETWEET_TARGET_USER_CF, tweetId, null, false, (int) targetUserForRetweetCount);
	}

	@Override
	public long countRetweeter(String tweetId)
	{
		return this.getCounterValue(RETWEETER_COUNTER, tweetId);
	}

}
