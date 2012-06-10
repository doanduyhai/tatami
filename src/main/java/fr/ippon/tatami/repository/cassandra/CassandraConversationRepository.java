package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.CONVERSATIONLINE_CF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hom.EntityManagerImpl;
import fr.ippon.tatami.domain.ConversationItem;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.repository.ConversationRepository;

public class CassandraConversationRepository extends CassandraAbstractRepository implements ConversationRepository
{

	@Inject
	EntityManagerImpl em;

	@Override
	public void addTweetToConversation(Tweet tweet, String authorLogin, String sourceTweetId)
	{
		this.insertIntoCFWithValue(CONVERSATIONLINE_CF, sourceTweetId, tweet.getTweetId(), authorLogin);
		tweet.setSourceTweetId(sourceTweetId);
		em.persist(tweet);
	}

	@Override
	public Collection<ConversationItem> getTweetsForConversation(String sourceTweetId)
	{
		Collection<HColumn<String, Object>> columns = this.findColumnsRangeFromCF(CONVERSATIONLINE_CF, sourceTweetId, null, true, Integer.MAX_VALUE);

		List<ConversationItem> result = new ArrayList<ConversationItem>();

		for (HColumn<String, Object> column : columns)
		{

			result.add(new ConversationItem(column.getName(), (String) column.getValue()));
		}
		return result;
	}
}
