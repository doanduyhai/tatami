package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.TIMELINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hom.EntityManagerImpl;

import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.service.util.TatamiConstants;

@Repository
public class CassandraTimeLineRepository extends CassandraAbstractRepository implements TimeLineRepository
{

	@Inject
	private Keyspace keyspaceOperator;

	@Inject
	private EntityManagerImpl em;

	@Override
	public void addTweetToTimeline(User user, String tweetId)
	{
		CqlQuery<String, Long, String> cqlQuery = new CqlQuery<String, Long, String>(keyspaceOperator, se, le, se);
		cqlQuery.setQuery("INSERT INTO TimeLine(KEY,'" + user.getTimelineTweetCount() + "') VALUES('" + user.getLogin() + "','" + tweetId + "')");
		cqlQuery.execute();

		user.incrementTimelineTweetCount();

		em.persist(user);

	}

	@Override
	public Collection<String> getTweetsFromTimeline(User user)
	{
		long endTweetColumn = user.getTimelineTweetCount() - 1;
		long startTweetColumn = user.getTimelineTweetCount() - TatamiConstants.DEFAULT_TWEET_LIST_SIZE - 1;

		List<HColumn<Long, String>> columns = createSliceQuery(keyspaceOperator, se, le, se).setColumnFamily(TIMELINE_CF).setKey(user.getLogin())
				.setRange(endTweetColumn, startTweetColumn, true, TatamiConstants.DEFAULT_TWEET_LIST_SIZE).execute().get().getColumns();

		List<String> tweetIds = new ArrayList<String>();
		for (HColumn<Long, String> column : columns)
		{
			tweetIds.add(column.getValue());
		}

		return tweetIds;
	}

	@Override
	public Collection<String> getTweetsRangeFromTimeline(User user, int start, int end)
	{
		List<String> tweetIds = new ArrayList<String>();

		long maxTweetColumn = user.getTimelineTweetCount() - 1;
		long endTweetColumn = maxTweetColumn - start + 1;
		long startTweetColumn = maxTweetColumn - end + 1;
		int count = end - start + 1 == 0 ? 1 : end - start + 1;

		List<HColumn<Long, String>> columns = createSliceQuery(keyspaceOperator, se, le, se).setColumnFamily(TIMELINE_CF).setKey(user.getLogin())
				.setRange(endTweetColumn, startTweetColumn, true, count).execute().get().getColumns();

		for (HColumn<Long, String> column : columns)
		{
			tweetIds.add(column.getValue());
		}
		return tweetIds;
	}

}
