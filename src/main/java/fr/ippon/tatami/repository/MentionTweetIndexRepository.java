package fr.ippon.tatami.repository;

import java.util.Collection;

public interface MentionTweetIndexRepository
{

	void addTweetToIndex(String authorLogin, String mentionedLogin, String tweetId);

	Collection<String> findTweetsForUserAndMentioner(String authorLogin, String mentionedLogin);

	void removeTweetFromIndex(String authorLogin, String mentionedLogin, String tweetId);

	void removeIndex(String authorLogin, String mentionedLogin);
}
