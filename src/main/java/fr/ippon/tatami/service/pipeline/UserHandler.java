package fr.ippon.tatami.service.pipeline;

import fr.ippon.tatami.exception.FunctionalException;

public interface UserHandler
{
	void onUserFollow(String userLoginToFollow) throws FunctionalException;

	void onUserForget(String userLoginToForget) throws FunctionalException;
}
