package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.User;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public interface FollowerRepository
{

	void addFollower(User user, User follower);

	void removeFollower(User user, User follower);

	Collection<String> findFollowersForUser(User user);

	Collection<String> findFollowersForUser(User user, String startUser, int end);

	// Indexing
	void addTweetToIndex(String authorLogin, String followerLogin, String tweetId);

	Collection<String> findTweetsForUserAndFollower(String authorLogin, String followerLogin);

	void removeTweetFromIndex(String authorLogin, String followerLogin, String tweetId);

	void removeIndex(String authorLogin, String followerLogin);
}
