package fr.ippon.tatami.repository;

import java.util.Collection;

public interface StatsRepository
{
	void addTweetToDay(String tweetId, String day);

	void removeTweetFromDay(String tweetId, String day);

	Collection<String> findTweetsForDay(String day);

	void addTweetToWeek(String tweetId, String week);

	void removeTweetFromWeek(String tweetId, String week);

	Collection<String> findTweetsForWeek(String week);

	void addTweetToMonth(String tweetId, String month);

	void removeTweetFromMonth(String tweetId, String month);

	Collection<String> findTweetsForMonth(String month);

	void addTweetToYear(String tweetId, String year);

	void removeTweetFromYear(String tweetId, String year);

	Collection<String> findTweetsForYear(String year);
}
