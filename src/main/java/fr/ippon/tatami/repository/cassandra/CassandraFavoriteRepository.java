package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVLINE_CF;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hom.EntityManagerImpl;

import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.FavoriteLine;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.service.util.TatamiConstants;

@Repository
public class CassandraFavoriteRepository extends CassandraAbstractRepository implements FavoriteRepository
{

	@Inject
	private EntityManagerImpl em;

	@Inject
	private Keyspace keyspaceOperator;

	@Override
	public void addFavorite(User user, String tweetId)
	{
		FavoriteLine favoriteLine = em.find(FavoriteLine.class, user.getLogin());
		if (favoriteLine == null)
		{
			favoriteLine = new FavoriteLine();
			favoriteLine.setLogin(user.getLogin());
		}

		favoriteLine.getFavorites().add(tweetId);
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
	public Collection<String> findFavoritesForUser(User user)
	{
		FavoriteLine favoriteLine = em.find(FavoriteLine.class, user.getLogin());
		if (favoriteLine != null)
		{
			List<String> list = new ArrayList<String>(favoriteLine.getFavorites());
			Collections.reverse(list);

			// Return default nb of favorite tweets and not all
			if (list.size() < TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE)
			{
				return list;
			}
			else
			{
				return list.subList(0, TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE - 1);
			}

		}

		return Arrays.asList();
	}

	@Override
	public Collection<String> findFavoritesRangeForUser(User user, int start, int end)
	{
		List<String> tweetIds = new ArrayList<String>();

		if (start < 1)
		{
			start = 1;
		}
		if (end > user.getFavoritesCount())
		{
			end = (int) user.getFavoritesCount();
		}

		long maxTweetColumn = user.getFavoritesCount() - 1;
		long endTweetColum = maxTweetColumn - start + 1;
		long startTweetColum = maxTweetColumn - end + 1;

		if (startTweetColum < 0)
		{
			startTweetColum = 0;
		}

		if (endTweetColum <= 0)
		{
			endTweetColum = 0;
		}

		List<HColumn<String, Object>> columns = createSliceQuery(keyspaceOperator, se, se, oe).setColumnFamily(FAVLINE_CF).setKey(user.getLogin())
				.setRange("favorites:" + endTweetColum, "favorites:" + startTweetColum, true, end - start + 1).execute().get().getColumns();

		for (HColumn<String, Object> column : columns)
		{
			tweetIds.add((String) column.getValue());
		}
		return tweetIds;
	}

}
