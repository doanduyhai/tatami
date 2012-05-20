package fr.ippon.tatami.service.renderer.tweet;

import java.util.Collection;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.service.pipeline.tweet.rendering.TweetRenderingHandler;
import fr.ippon.tatami.service.user.UserService;

public class FavoriteTweetRenderer implements TweetRenderingHandler
{

	private UserService userService;

	private FavoriteRepository favoriteLineRepository;

	@Override
	public void onRender(Tweet tweet)
	{
		User currentUser = this.userService.getCurrentUser();

		if (currentUser != null)
		{
			Collection<String> favorites = this.favoriteLineRepository.findFavoritesForUser(currentUser.getLogin());

			if (!favorites.contains(tweet.getTweetId()))
			{
				tweet.setAddToFavorite(true);
			}
			else
			{
				tweet.setAddToFavorite(false);
			}
		}

	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	public void setFavoriteLineRepository(FavoriteRepository favoriteLineRepository)
	{
		this.favoriteLineRepository = favoriteLineRepository;
	}

}
