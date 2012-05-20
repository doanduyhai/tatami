package fr.ippon.tatami.repository;

import java.util.Collection;

public interface BlockedUserRepository
{
	void addUserToBlockRepository(String userLogin, String blockedUserlogin);

	void removeUserFromBlockRepository(String userLogin, String blockedUserlogin);

	Collection<String> getUsersFromBlockRepository(String userLogin);
}
