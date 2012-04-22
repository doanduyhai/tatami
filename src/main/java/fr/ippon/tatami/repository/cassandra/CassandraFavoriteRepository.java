package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVLINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;
import fr.ippon.tatami.domain.FavoriteLine;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;

/**
 * @author DuyHai DOAN
 */
public class CassandraFavoriteRepository extends CassandraAbstractRepository implements FavoriteRepository
{

	private final DecimalFormat orderFormatter = new DecimalFormat("000000000");

	@Override
	public void addFavorite(User user, String tweetId)
	{
		FavoriteLine favoriteLine = em.find(FavoriteLine.class, user.getLogin());
		if (favoriteLine == null)
		{
			favoriteLine = new FavoriteLine();
			favoriteLine.setLogin(user.getLogin());
		}

		if (!favoriteLine.getFavorites().contains(tweetId))
		{
			favoriteLine.getFavorites().add(tweetId);
		}

		user.incrementFavoritesCount();
		em.persist(user);
		em.persist(favoriteLine);

	}

	@Override
	public void removeFavorite(User user, String tweetId)
	{
		FavoriteLine favoriteLine = em.find(FavoriteLine.class, user.getLogin());
		if (favoriteLine == null)
		{
			// TODO Functional exception
			return;
		}

		favoriteLine.getFavorites().remove(tweetId);

		user.decrementFavoritesCount();
		em.persist(user);
		em.persist(favoriteLine);

	}

	@Override
	public Collection<String> findFavoritesRangeForUser(User user, int start, int end)
	{
		List<String> tweetIds = new ArrayList<String>();

		if (start > user.getFavoritesCount())
		{
			return Arrays.asList();
		}
		else if (end > user.getFavoritesCount())
		{
			end = (int) user.getFavoritesCount();
		}

		long maxTweetColumn = user.getFavoritesCount() - 1;
		long endTweetColum = maxTweetColumn - start + 1;
		long startTweetColum = maxTweetColumn - end + 1;
		int count = end - start + 1 == 0 ? 1 : end - start + 1;

		List<HColumn<String, Object>> columns = createSliceQuery(keyspaceOperator, se, se, oe).setColumnFamily(FAVLINE_CF).setKey(user.getLogin())
				.setRange("favorites:" + orderFormatter.format(endTweetColum), "favorites:" + orderFormatter.format(startTweetColum), true, count)
				.execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			tweetIds.add((String) column.getValue());
		}
		return tweetIds;
	}

}
