package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.DIRECTMESSAGELINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.DIRECTMESSAGE_INDEX_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.TWEET_CF;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;

import org.apache.commons.lang.StringUtils;

import fr.ippon.tatami.domain.DirectMessageHeadline;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.DirectMessageRepository;

public class CassandraDirectMessageRepository extends CassandraAbstractRepository implements DirectMessageRepository
{

	@Override
	public void addDirectMessageWithUser(String userLogin, String interlocutorLogin, String directMessageId)
	{
		// Identify the last directMessageId with interlocutor
		Collection<String> directMessageIds = this.findRangeFromCF(DIRECTMESSAGELINE_CF, userLogin + LOGIN_SEPARATOR + interlocutorLogin, null, true,
				1);

		// If existing conversation, remove the last value in index
		if (!directMessageIds.isEmpty())
		{
			String lastDirectMessageId = directMessageIds.iterator().next();
			this.removeFromCF(DIRECTMESSAGE_INDEX_CF, userLogin, lastDirectMessageId);
		}

		this.insertIntoCFWithValue(DIRECTMESSAGE_INDEX_CF, userLogin, directMessageId, interlocutorLogin);
		this.insertIntoCF(DIRECTMESSAGELINE_CF, userLogin + LOGIN_SEPARATOR + interlocutorLogin, directMessageId);

		// Identify the last directMessageId with userLogin
		Collection<String> reverseDirectMessageIds = this.findRangeFromCF(DIRECTMESSAGELINE_CF, interlocutorLogin + LOGIN_SEPARATOR + userLogin,
				null, true, 1);

		// If existing conversation, remove the last value in index
		if (!reverseDirectMessageIds.isEmpty())
		{
			String reverseLastDirectMessageId = directMessageIds.iterator().next();
			this.removeFromCF(DIRECTMESSAGE_INDEX_CF, interlocutorLogin, reverseLastDirectMessageId);
		}

		this.insertIntoCFWithValue(DIRECTMESSAGE_INDEX_CF, interlocutorLogin, directMessageId, userLogin);
		this.insertIntoCF(DIRECTMESSAGELINE_CF, interlocutorLogin + LOGIN_SEPARATOR + userLogin, directMessageId);
	}

	@Override
	public void removeDirectMessageFromUser(String userLogin, String interlocutorLogin, String directMessageId) throws FunctionalException
	{
		// Identify the last directMessageId with interlocutor
		Collection<String> directMessageIds = this.findRangeFromCF(DIRECTMESSAGELINE_CF, userLogin + LOGIN_SEPARATOR + interlocutorLogin, null, true,
				2);

		// Only one direct message with interlocutor, remove all the line & index
		if (directMessageIds.size() == 1)
		{
			String lastDirectMessageId = directMessageIds.iterator().next();
			if (StringUtils.equals(lastDirectMessageId, directMessageId))
			{
				this.removeRowFromCF(DIRECTMESSAGELINE_CF, userLogin + LOGIN_SEPARATOR + interlocutorLogin);
				this.removeFromCF(DIRECTMESSAGE_INDEX_CF, userLogin, directMessageId);
			}
			else
			{
				throw new FunctionalException("Error with Direct Message index. Cannot remove message");
			}

		}
		else
		{
			Iterator<String> iter = directMessageIds.iterator();
			String lastDirectMessageId = iter.next();
			if (StringUtils.equals(lastDirectMessageId, directMessageId))
			{
				this.removeFromCF(DIRECTMESSAGE_INDEX_CF, userLogin, lastDirectMessageId);
				String newLastDirectMessageId = iter.next();
				this.insertIntoCFWithValue(DIRECTMESSAGE_INDEX_CF, userLogin, newLastDirectMessageId, interlocutorLogin);
			}
			this.removeFromCF(DIRECTMESSAGELINE_CF, userLogin + LOGIN_SEPARATOR + interlocutorLogin, directMessageId);
		}

		Collection<String> directMessageFromInterlocutor = this.findRangeFromCF(DIRECTMESSAGELINE_CF,
				interlocutorLogin + LOGIN_SEPARATOR + userLogin, directMessageId, true, 1);

		if (!directMessageFromInterlocutor.isEmpty())
		{
			this.removeRowFromCF(TWEET_CF, directMessageId);
		}

	}

	@Override
	public Collection<DirectMessageHeadline> findDirectMessageHeadlineRangeForUser(String userLogin, String startDirectMessageId, int count)
	{
		Collection<HColumn<String, Object>> columns = this.findColumnsRangeFromCF(DIRECTMESSAGE_INDEX_CF, userLogin, null, true, count);

		List<DirectMessageHeadline> result = new ArrayList<DirectMessageHeadline>();

		for (HColumn<String, Object> column : columns)
		{
			result.add(new DirectMessageHeadline(column.getName(), (String) column.getValue()));
		}

		return result;
	}

	@Override
	public Collection<String> findDirectMessageRangeForUser(String userLogin, String interlocutorLogin, String startDirectMessageId, int count)
	{
		return this.findRangeFromCF(DIRECTMESSAGELINE_CF, userLogin + LOGIN_SEPARATOR + interlocutorLogin, null, false, count);
	}

}
