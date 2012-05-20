package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITE_INDEX_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITE_TWEET_USER_INDEX_CF;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_INDEX_COUNTER;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_USER_INDEX_COUNTER;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import fr.ippon.tatami.repository.FavoriteTweetIndexRepository;

public class CassandraFavoriteTweetIndexRepository extends CassandraAbstractRepository implements FavoriteTweetIndexRepository
{

	@Override
	public void addTweetToFavoriteIndex(String userLogin, String authorLogin, String tweetId)
	{
		this.insertIntoCF(FAVORITE_INDEX_CF, tweetId, userLogin);
		this.incrementCounter(FAVORITE_TWEET_INDEX_COUNTER, tweetId);
		this.addToTweetUserIndex(userLogin, authorLogin, tweetId);
	}

	private void addToTweetUserIndex(String userLogin, String authorLogin, String tweetId)
	{
		if (!StringUtils.equals(userLogin, authorLogin))
		{
			@SuppressWarnings("unchecked")
			Set<String> favoritesTweetFromUser = (Set<String>) this.getValueFromCF(FAVORITE_TWEET_USER_INDEX_CF, userLogin, authorLogin);
			if (favoritesTweetFromUser == null)
			{
				favoritesTweetFromUser = new HashSet<String>();
			}

			favoritesTweetFromUser.add(tweetId);

			this.insertIntoCFWithValue(FAVORITE_TWEET_USER_INDEX_CF, userLogin, authorLogin, favoritesTweetFromUser);
			this.incrementCounter(FAVORITE_TWEET_USER_INDEX_COUNTER, userLogin + LOGIN_SEPARATOR + authorLogin);
		}
	}

	@Override
	public void removeTweetFromFavoriteIndex(String userLogin, String authorLogin, String tweetId)
	{
		this.removeFromCF(FAVORITE_INDEX_CF, tweetId, userLogin);
		this.decrementCounter(FAVORITE_TWEET_INDEX_COUNTER, tweetId);
		this.removeFromTweetUserIndex(userLogin, authorLogin, tweetId);
	}

	private void removeFromTweetUserIndex(String userLogin, String authorLogin, String tweetId)
	{
		if (!StringUtils.equals(userLogin, authorLogin))
		{
			@SuppressWarnings("unchecked")
			Set<String> favoritesTweetFromUser = (Set<String>) this.getValueFromCF(FAVORITE_TWEET_USER_INDEX_CF, userLogin, authorLogin);
			if (favoritesTweetFromUser != null)
			{
				favoritesTweetFromUser.remove(tweetId);

				if (favoritesTweetFromUser.size() == 0)
				{
					this.removeFromCF(FAVORITE_TWEET_USER_INDEX_CF, userLogin, authorLogin);
					this.removeCounter(FAVORITE_TWEET_USER_INDEX_COUNTER, userLogin + LOGIN_SEPARATOR + authorLogin);
				}
				else
				{
					this.insertIntoCFWithValue(FAVORITE_TWEET_USER_INDEX_CF, userLogin, authorLogin, favoritesTweetFromUser);
					this.decrementCounter(FAVORITE_TWEET_USER_INDEX_COUNTER, userLogin + LOGIN_SEPARATOR + authorLogin);
				}
			}
		}
	}

	@Override
	public Collection<String> getUsersForTweetFromIndex(String tweetId)
	{
		long userCount = this.getCounterValue(FAVORITE_TWEET_INDEX_COUNTER, tweetId);
		return this.findRangeFromCF(FAVORITE_INDEX_CF, tweetId, null, false, (int) userCount);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<String> getTweetsForUserFromIndex(String userLogin, String authorLogin)
	{
		Set<String> favoritesTweetFromUser;
		if (!StringUtils.equals(userLogin, authorLogin))
		{

			favoritesTweetFromUser = (Set<String>) this.getValueFromCF(FAVORITE_TWEET_USER_INDEX_CF, userLogin, authorLogin);
			if (favoritesTweetFromUser == null)
			{
				favoritesTweetFromUser = new HashSet<String>(1);
			}
		}
		else
		{
			favoritesTweetFromUser = new HashSet<String>(1);
		}

		return favoritesTweetFromUser;

	}

	@Override
	public void removeIndexForTweet(String userLogin, String authorLogin, String tweetId)
	{
		this.removeRowFromCF(FAVORITE_INDEX_CF, tweetId);
		this.removeCounter(FAVORITE_TWEET_INDEX_COUNTER, tweetId);
		this.removeFromTweetUserIndex(userLogin, authorLogin, tweetId);
	}

}
