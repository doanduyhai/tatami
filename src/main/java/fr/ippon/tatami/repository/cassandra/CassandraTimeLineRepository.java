package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.TIMELINE_CF;

import java.util.Collection;

import fr.ippon.tatami.repository.TimeLineRepository;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class CassandraTimeLineRepository extends CassandraAbstractRepository implements TimeLineRepository
{
	@Override
	public void addTweetToTimeline(String userLogin, String tweetId)
	{
		this.insertIntoCF(TIMELINE_CF, userLogin, tweetId);
	}

	@Override
	public void removeTweetFromTimeline(String userLogin, String tweetId)
	{
		this.removeFromCF(TIMELINE_CF, userLogin, tweetId);
	}

	@Override
	public Collection<String> getTweetsRangeFromTimeline(String userLogin, String startTweetId, int count)
	{
		assert count >= 0 : "Timeline search count should be positive";

		return this.findRangeFromCF(TIMELINE_CF, userLogin, startTweetId, true, count);
	}

}
