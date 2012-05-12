package fr.ippon.tatami.service.pipeline.user.rendering;

import java.util.List;

import fr.ippon.tatami.domain.User;

public class UserRenderingPipelineManager
{
	private List<UserRenderingHandler> userRenderingHandlers;

	public void onUserRender(User user, User currentUser)
	{
		for (UserRenderingHandler handler : userRenderingHandlers)
		{
			handler.onRender(user, currentUser);
		}
	}

	public void setUserRenderingHandlers(List<UserRenderingHandler> userRenderingHandlers)
	{
		this.userRenderingHandlers = userRenderingHandlers;
	}
}
