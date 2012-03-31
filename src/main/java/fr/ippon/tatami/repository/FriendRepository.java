package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.User;

/**
 * The Follower Respository.
 * 
 * @author Julien Dubois
 */
public interface FriendRepository
{

	void addFriend(User user, User friend);

	void removeFriend(User user, User friend);

	Collection<String> findFriendsForUser(User user);
}
