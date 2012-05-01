package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.TIMELINE_CF;

import java.util.Collection;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.TimeLineRepository;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class CassandraTimeLineRepository extends CassandraAbstractRepository implements TimeLineRepository
{
	@Override
	public void addTweetToTimeline(User user, String tweetId)
	{
		this.insertIntoCF(TIMELINE_CF, user.getLogin(), tweetId);

		user.incrementTimelineTweetCount();
		em.persist(user);

	}

	@Override
	public void removeTweetFromTimeline(User user, String tweetId)
	{
		this.removeFromCF(TIMELINE_CF, user.getLogin(), tweetId);

		user.decrementTimelineTweetCount();
		em.persist(user);

	}

	@Override
	public Collection<String> getTweetsRangeFromTimeline(User user, String startTweetId, int count)
	{
		assert count >= 0 : "Timeline search count should be positive";

		return this.findRangeFromCF(TIMELINE_CF, user.getLogin(), startTweetId, true, count);
	}

}
