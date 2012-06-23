package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.ConversationItem;
import fr.ippon.tatami.domain.Tweet;

public interface ConversationRepository
{

	public void addTweetToConversation(Tweet tweet, String sourceTweetId);

	public Collection<ConversationItem> getTweetsForConversation(String sourceTweetId);
}
