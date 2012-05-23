package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.RETWEETER_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.RETWEETLINE_CF;
import static fr.ippon.tatami.config.CounterKeys.RETWEETER_COUNTER;
import static fr.ippon.tatami.config.CounterKeys.RETWEET_COUNTER;

import java.util.Collection;

import fr.ippon.tatami.repository.ReTweetRepository;

public class CassandraReTweetRepository extends CassandraAbstractRepository implements ReTweetRepository
{
	@Override
	public void addRetweeter(String retweeterLogin, String originalTweetId, String retweetId)
	{
		this.insertIntoCFWithValue(RETWEETER_CF, originalTweetId, retweeterLogin, retweetId);
		this.incrementCounter(RETWEETER_COUNTER, originalTweetId);

	}

	@Override
	public void removeRetweeter(String retweeterLogin, String originalTweetId)
	{
		this.removeFromCF(RETWEETER_CF, originalTweetId, retweeterLogin);
		this.decrementCounter(RETWEETER_COUNTER, originalTweetId);
	}

	@Override
	public String findRetweetIdForRetweeter(String retweeterLogin, String originalTweetId)
	{
		return (String) this.getValueFromCF(RETWEETER_CF, originalTweetId, retweeterLogin);
	}

	@Override
	public Collection<String> findRetweetIdsForTweet(String originalTweetId)
	{
		long retweeterCount = this.getCounterValue(RETWEETER_COUNTER, originalTweetId);
		return this.findRowValuesFromCF(RETWEETER_CF, originalTweetId, false, (int) retweeterCount);
	}

	@Override
	public Collection<String> findRetweetersForTweet(String originalTweetId)
	{
		long retweeterCount = this.getCounterValue(RETWEETER_COUNTER, originalTweetId);
		return this.findRangeFromCF(RETWEETER_CF, originalTweetId, null, false, (int) retweeterCount);
	}

	@Override
	public long countRetweeters(String originalTweetId)
	{
		return this.getCounterValue(RETWEETER_COUNTER, originalTweetId);

	}

	@Override
	public void removeRetweeterIndex(String originalTweetId)
	{
		this.removeRowFromCF(RETWEETER_CF, originalTweetId);
		this.removeCounter(RETWEETER_COUNTER, originalTweetId);
	}

	@Override
	public void addToRetweetLine(String userLogin, String originalTweetId)
	{
		this.insertIntoCF(RETWEETLINE_CF, userLogin, originalTweetId);
		this.incrementCounter(RETWEET_COUNTER, userLogin);
	}

	@Override
	public void removeFromRetweetLine(String userLogin, String originalTweetId)
	{
		this.removeFromCF(RETWEETLINE_CF, userLogin, originalTweetId);
		this.decrementCounter(RETWEET_COUNTER, userLogin);
	}

	@Override
	public boolean isTweetInRetweetLine(String userLogin, String tweetId)
	{
		Object value = this.getValueFromCF(RETWEETLINE_CF, userLogin, tweetId);
		if (value == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

}
