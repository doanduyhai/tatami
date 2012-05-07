package fr.ippon.tatami.service.pipeline;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;

public interface FavoriteHandler
{

	void onAddToFavorite(Tweet tweet) throws FunctionalException;

	void onRemoveFromFavorite(Tweet tweet) throws FunctionalException;
}
