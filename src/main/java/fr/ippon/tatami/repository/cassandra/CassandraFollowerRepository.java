package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWED_TWEET_INDEX_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWERS_CF;
import static fr.ippon.tatami.config.CounterKeys.FOLLOWED_TWEET_INDEX_COUNTER;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;

import java.util.Collection;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FollowerRepository;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class CassandraFollowerRepository extends CassandraAbstractRepository implements FollowerRepository
{

	@Override
	@CacheEvict(value =
	{
			"user-cache",
			"follower-cache"
	}, key = "#user.login")
	public void addFollower(User user, User follower)
	{
		this.insertIntoCF(FOLLOWERS_CF, user.getLogin(), follower.getLogin());

		user.incrementFollowersCount();
		em.persist(user);

	}

	@Override
	@CacheEvict(value =
	{
			"user-cache",
			"follower-cache"
	}, key = "#user.login")
	public void removeFollower(User user, User follower)
	{

		this.removeFromCF(FOLLOWERS_CF, user.getLogin(), follower.getLogin());

		user.decrementFollowersCount();
		em.persist(user);

	}

	@Override
	@Cacheable(value = "follower-cache", key = "#user.login")
	public Collection<String> findFollowersForUser(User user)
	{
		return this.findRangeFromCF(FOLLOWERS_CF, user.getLogin(), null, false, (int) user.getFollowersCount());
	}

	@Override
	public Collection<String> findFollowersForUser(User user, String startUser, int count)
	{
		assert count >= 0 : "Follower search count should be positive";

		return this.findRangeFromCF(FOLLOWERS_CF, user.getLogin(), startUser, false, count);
	}

	@Override
	public void addTweetToIndex(String authorLogin, String followerLogin, String tweetId)
	{
		this.insertIntoCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + followerLogin, tweetId);
		this.incrementCounter(FOLLOWED_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + followerLogin);
	}

	@Override
	public Collection<String> findTweetsForUserAndFollower(String authorLogin, String followerLogin)
	{
		long indexTweetsCount = this.getCounterValue(FOLLOWED_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + followerLogin);
		return this.findRangeFromCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + followerLogin, null, false, (int) indexTweetsCount);
	}

	@Override
	public void removeTweetFromIndex(String authorLogin, String followerLogin, String tweetId)
	{
		this.removeFromCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + followerLogin, tweetId);
		this.decrementCounter(FOLLOWED_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + followerLogin);
	}

	@Override
	public void removeIndex(String authorLogin, String followerLogin)
	{
		this.removeRowFromCF(FOLLOWED_TWEET_INDEX_CF, authorLogin + LOGIN_SEPARATOR + followerLogin);
		this.removeCounter(FOLLOWED_TWEET_INDEX_COUNTER, authorLogin + LOGIN_SEPARATOR + followerLogin);

	}
}
