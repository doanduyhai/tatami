package fr.ippon.tatami.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.repository.UserRepository;

public abstract class AbstractUserService
{
	protected FriendRepository friendRepository;

	protected UserRepository userRepository;

	public List<User> buildUserList(User currentUser, Collection<String> logins)
	{
		List<User> results = new ArrayList<User>();

		Collection<String> userFriends = this.friendRepository.findFriendsForUser(currentUser);

		User foundUser = null;
		for (String login : logins)
		{
			foundUser = this.userRepository.findUserByLogin(login);
			if (foundUser != null)
			{
				if (userFriends.contains(login))
				{
					foundUser.setFollow(false);
				}
				else
				{
					foundUser.setFollow(true);
				}
				results.add(foundUser);
			}
		}

		return results;
	}

	public void setFriendRepository(FriendRepository friendRepository)
	{
		this.friendRepository = friendRepository;
	}

	public void setUserRepository(UserRepository userRepository)
	{
		this.userRepository = userRepository;
	}

}
