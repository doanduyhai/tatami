package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.User;

/**
 * The Follower Respository.
 * 
 * @author Julien Dubois
 */
public interface FollowerRepository
{

	void addFollower(User user, User follower);

	void removeFollower(User user, User follower);

	Collection<String> findFollowersForUser(User user);

}
