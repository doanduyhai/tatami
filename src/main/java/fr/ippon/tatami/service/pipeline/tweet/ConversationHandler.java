package fr.ippon.tatami.service.pipeline.tweet;

import fr.ippon.tatami.domain.Tweet;

public interface ConversationHandler
{

	void onAddToConversation(Tweet tweet, String sourceTweetId);
}
