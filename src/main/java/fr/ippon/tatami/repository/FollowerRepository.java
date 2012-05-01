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
}
