package fr.ippon.tatami.repository;

import fr.ippon.tatami.domain.User;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public interface UserRepository
{

	void createUser(User user);

	void updateUser(User user);

	User findUserByLogin(String login);

}
