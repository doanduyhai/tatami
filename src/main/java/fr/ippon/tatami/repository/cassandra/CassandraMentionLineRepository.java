package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.MENTIONLINE_CF;
import static fr.ippon.tatami.config.CounterKeys.MENTION_LINE_COUNTER;

import java.util.Collection;

import fr.ippon.tatami.repository.MentionLineRepository;

public class CassandraMentionLineRepository extends CassandraAbstractRepository implements MentionLineRepository
{

	@Override
	public void addTweetToMentionline(String userLogin, String tweetId)
	{
		this.insertIntoCF(MENTIONLINE_CF, userLogin, tweetId);
		this.incrementCounter(MENTION_LINE_COUNTER, userLogin);
	}

	@Override
	public void removeTweetFromMentionline(String userLogin, String tweetId)
	{
		this.removeFromCF(MENTIONLINE_CF, userLogin, tweetId);
		this.decrementCounter(MENTION_LINE_COUNTER, userLogin);
	}

	@Override
	public Collection<String> findMentionTweetsForUser(String userLogin)
	{
		long mentionLineCount = this.getCounterValue(MENTION_LINE_COUNTER, userLogin);
		return this.findRangeFromCF(MENTIONLINE_CF, userLogin, null, true, (int) mentionLineCount);
	}

	@Override
	public Collection<String> findMentionTweetsRangeForUser(String userLogin, String startTweetId, int count)
	{
		assert count >= 0 : "Mentionline search count should be positive";

		return this.findRangeFromCF(MENTIONLINE_CF, userLogin, startTweetId, true, count);

	}

}
