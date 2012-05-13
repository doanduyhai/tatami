package fr.ippon.tatami.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.repository.UserRepository;
import fr.ippon.tatami.service.pipeline.user.rendering.UserRenderingPipelineManager;

public abstract class AbstractUserService
{
	protected FriendRepository friendRepository;

	protected UserRepository userRepository;

	protected UserRenderingPipelineManager userRenderingPipelineManager;

	public List<User> buildUserList(User currentUser, Collection<String> logins) throws FunctionalException
	{
		List<User> results = new ArrayList<User>();

		User foundUser = null;
		for (String login : logins)
		{
			foundUser = this.userRepository.findUserByLogin(login).duplicate();
			if (foundUser == null)
			{
				throw new FunctionalException("No user found for login '" + login + "'");
			}
			else
			{
				this.userRenderingPipelineManager.onUserRender(foundUser, currentUser);
			}
			results.add(foundUser);

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

	public void setUserRenderingPipelineManager(UserRenderingPipelineManager userRenderingPipelineManager)
	{
		this.userRenderingPipelineManager = userRenderingPipelineManager;
	}

}
