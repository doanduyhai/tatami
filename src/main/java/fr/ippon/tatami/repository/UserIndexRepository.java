package fr.ippon.tatami.repository;

import java.util.List;

public interface UserIndexRepository
{

	void insertLogin(String login);

	void insertName(String login, String name, String rowKey);

	void removeName(String login, String name, String rowKey);

	List<String> findLogin(String login, int limit);

	List<String> findExactName(String name, String rowKey);

	List<String> findName(String name, int limit, String rowKey);

	void insertFirstName(String firstName, String login);

	void insertLastName(String lastName, String login);

	void removeFirstName(String firstName, String login);

	void removeLastName(String lastName, String login);

	List<String> findFirstName(String firstName, int limit);

	List<String> findLastName(String LastName, int limit);

}