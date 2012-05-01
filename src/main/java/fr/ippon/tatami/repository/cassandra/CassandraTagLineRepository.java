package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.TAGLINE_CF;

import java.util.Collection;

import fr.ippon.tatami.repository.TagLineRepository;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class CassandraTagLineRepository extends CassandraAbstractRepository implements TagLineRepository
{
	@Override
	public void addTweet(String tag, String tweetId)
	{
		this.insertIntoCF(TAGLINE_CF, tag, tweetId);

	}

	@Override
	public void removeTweet(String tag, String tweetId)
	{
		this.removeFromCF(TAGLINE_CF, tag, tweetId);
	}

	@Override
	public Collection<String> findTweetsRangeForTag(String tag, String startTweetId, int count)
	{
		assert count >= 0 : "Tag search count should be positive";

		return this.findRangeFromCF(TAGLINE_CF, tag, startTweetId, true, count);
	}
}
