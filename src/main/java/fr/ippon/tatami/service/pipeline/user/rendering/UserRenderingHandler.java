package fr.ippon.tatami.service.pipeline.user.rendering;

import fr.ippon.tatami.domain.User;

public interface UserRenderingHandler
{
	public void onRender(User user, User currentUser);
}
