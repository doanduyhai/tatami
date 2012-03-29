package fr.ippon.tatami.repository.cassandra;

import java.util.Collection;

import javax.inject.Inject;

import me.prettyprint.hom.EntityManagerImpl;

import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.FavoriteLine;
import fr.ippon.tatami.repository.FavoriteRepository;

@Repository
public class CassandraFavoriteRepository implements FavoriteRepository
{

	@Inject
	private EntityManagerImpl em;

	@Override
	public void addFavorite(String login, String tweetId)
	{
		User user = em.find(User.class, login);
		FavoriteLine favoriteLine = em.find(FavoriteLine.class, login);
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
	public void removeFavorite(String login, String tweetId)
	{
		User user = em.find(User.class, login);
		FavoriteLine favoriteLine = em.find(FavoriteLine.class, login);
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
	public Collection<String> findFavoritesForUser(String login)
	{
		FavoriteLine favoriteLine = em.find(FavoriteLine.class, login);
		if (favoriteLine != null)
		{
			return favoriteLine.getFavorites();
		}
		return null;
	}

}
