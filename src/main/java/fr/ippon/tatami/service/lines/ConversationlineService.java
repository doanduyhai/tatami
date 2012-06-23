package fr.ippon.tatami.service.lines;

import java.util.Collection;

import fr.ippon.tatami.domain.ConversationItem;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.ConversationRepository;
import fr.ippon.tatami.service.pipeline.tweet.ConversationHandler;

public class ConversationlineService extends AbstractlineService implements ConversationHandler
{

	private ConversationRepository conversationRepository;

	@Override
	public void onAddToConversation(Tweet tweet, String sourceTweetId)
	{
		this.conversationRepository.addTweetToConversation(tweet, sourceTweetId);
	}

	public Collection<ConversationItem> getTweetsForConversation(String sourceTweetId) throws FunctionalException
	{

		Collection<ConversationItem> conversationItems = this.conversationRepository.getTweetsForConversation(sourceTweetId);

		User currentUser = this.userService.getCurrentUser();

		this.buildConversationTweetsList(currentUser, conversationItems);

		return conversationItems;
	}

	public void setConversationRepository(ConversationRepository conversationRepository)
	{
		this.conversationRepository = conversationRepository;
	}

}
