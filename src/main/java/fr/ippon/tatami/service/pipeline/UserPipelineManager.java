package fr.ippon.tatami.service.pipeline;

import java.util.List;

import fr.ippon.tatami.exception.FunctionalException;

public class UserPipelineManager
{
	private List<UserHandler> userHandlers;

	public void onFollow(String userLoginToFollow) throws FunctionalException
	{
		for (UserHandler handler : userHandlers)
		{
			handler.onUserFollow(userLoginToFollow);
		}
	}

	public void onForget(String userLoginToForget) throws FunctionalException
	{
		for (UserHandler handler : userHandlers)
		{
			handler.onUserForget(userLoginToForget);
		}
	}

	public void setUserHandlers(List<UserHandler> userHandlers)
	{
		this.userHandlers = userHandlers;
	}

}
