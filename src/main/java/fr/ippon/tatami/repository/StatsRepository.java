package fr.ippon.tatami.repository;

import java.util.Collection;

public interface StatsRepository
{
	void addTweetToDay(String tweetId, String day);

	void removeTweetFromDay(String tweetId, String day);

	Collection<String> findTweetsForDay(String day);

	Collection<String> findTweetsRangeForDay(String day, int start, int end);

	void addTweetToWeek(String tweetId, String week);

	void removeTweetFromWeek(String tweetId, String week);

	Collection<String> findTweetsForWeek(String week);

	Collection<String> findTweetsRangeForWeek(String day, int start, int end);

	void addTweetToMonth(String tweetId, String month);

	void removeTweetFromMonth(String tweetId, String month);

	Collection<String> findTweetsForMonth(String month);

	Collection<String> findTweetsRangeForMonth(String day, int start, int end);

	void addTweetToYear(String tweetId, String year);

	void removeTweetFromYear(String tweetId, String year);

	Collection<String> findTweetsForYear(String year);

	Collection<String> findTweetsRangeForYear(String day, int start, int end);
}
