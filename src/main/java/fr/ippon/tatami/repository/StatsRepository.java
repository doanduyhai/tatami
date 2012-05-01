package fr.ippon.tatami.repository;

import java.util.Map;

/**
 * 
 * @author DuyHai DOAN
 */
public interface StatsRepository
{
	void addTweetToDay(String login, String day);

	void removeTweetFromDay(String login, String day);

	Map<String, Long> findTweetsForDay(String day);

	void addTweetToWeek(String login, String week);

	void removeTweetFromWeek(String login, String week);

	Map<String, Long> findTweetsForWeek(String week);

	void addTweetToMonth(String login, String month);

	void removeTweetFromMonth(String login, String month);

	Map<String, Long> findTweetsForMonth(String month);

	void addTweetToYear(String login, String year);

	void removeTweetFromYear(String login, String year);

	Map<String, Long> findTweetsForYear(String year);

}
