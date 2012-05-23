package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.MENTIONLINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.MENTION_TWEET_INDEX_CF;
import static fr.ippon.tatami.config.CounterKeys.MENTION_LINE_COUNTER;
import static fr.ippon.tatami.config.CounterKeys.MENTION_TWEET_INDEX_COUNTER;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;

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

	// Indexing
	@Override
	public void addTweetToIndex(String authorLogin, String mentionedLogin, String tweetId)
	{
		this.insertIntoCF(MENTION_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + mentionedLogin, tweetId);
		this.incrementCounter(MENTION_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + mentionedLogin);
	}

	@Override
	public Collection<String> findTweetsForUserAndMentioner(String authorLogin, String mentionedLogin)
	{
		long indexTweetsCount = this.getCounterValue(MENTION_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + mentionedLogin);
		return this.findRangeFromCF(MENTION_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + mentionedLogin, null, false, (int) indexTweetsCount);
	}

	@Override
	public void removeTweetFromIndex(String authorLogin, String mentionedLogin, String tweetId)
	{
		this.removeFromCF(MENTION_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + mentionedLogin, tweetId);
		this.decrementCounter(MENTION_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + mentionedLogin);
	}

	@Override
	public void removeIndex(String authorLogin, String mentionedLogin)
	{
		this.removeRowFromCF(MENTION_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + mentionedLogin);
		this.removeCounter(MENTION_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + mentionedLogin);

	}
}
