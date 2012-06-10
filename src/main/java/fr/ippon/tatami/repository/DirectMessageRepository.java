package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.DirectMessageHeadline;
import fr.ippon.tatami.exception.FunctionalException;

public interface DirectMessageRepository
{
	void addDirectMessageWithUser(String userLogin, String interlocutorLogin, String directMessageId);

	void removeDirectMessageFromUser(String userLogin, String interlocutorLogin, String directMessageId) throws FunctionalException;

	Collection<DirectMessageHeadline> findDirectMessageHeadlineRangeForUser(String userLogin, String startDirectMessageId, int count);

	Collection<String> findDirectMessageRangeForUser(String userLogin, String interlocutorLogin, String startDirectMessageId, int count);

}
