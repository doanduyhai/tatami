package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.DIRECTMESSAGELINE_CF;
import static fr.ippon.tatami.config.ColumnFamilyKeys.DIRECTMESSAGE_INDEX_CF;
import static fr.ippon.tatami.service.util.TatamiConstants.LOGIN_SEPARATOR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import me.prettyprint.hector.api.beans.HColumn;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.DirectMessageHeadline;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;

public class DirectMessageRepositoryTest extends AbstractCassandraTatamiTest
{
	Tweet dm1, dm2, dm3, dm4, dm5, dm6, dm7;

	@Test
	public void testAddDirectMessageWithUserInRepository()
	{
		dm1 = this.tweetRepository.createTweet("jdubois", "Hi duyhai", false);
		dm2 = this.tweetRepository.createTweet("jdubois", "conversation with duyhai", false);
		dm3 = this.tweetRepository.createTweet("jdubois", "another dm to duyhai", false);
		dm4 = this.tweetRepository.createTweet("jdubois", "last dm to duyhai", false);

		dm5 = this.tweetRepository.createTweet("jdubois", "dm to tescolan", false);

		this.directMessageRepository.addDirectMessageWithUser("jdubois", "duyhai", dm1.getTweetId());
		this.directMessageRepository.addDirectMessageWithUser("jdubois", "duyhai", dm2.getTweetId());
		this.directMessageRepository.addDirectMessageWithUser("jdubois", "duyhai", dm3.getTweetId());
		this.directMessageRepository.addDirectMessageWithUser("jdubois", "duyhai", dm4.getTweetId());

		this.directMessageRepository.addDirectMessageWithUser("jdubois", "tescolan", dm5.getTweetId());

		Collection<String> jduboisDMduyhai = this.findRangeFromCF(DIRECTMESSAGELINE_CF, "jdubois" + LOGIN_SEPARATOR + "duyhai", null, true, 10);
		assertEquals(jduboisDMduyhai.size(), 4, "jdubois has 4 dm with duyhai");
		Collection<String> jduboisDMtescolan = this.findRangeFromCF(DIRECTMESSAGELINE_CF, "jdubois" + LOGIN_SEPARATOR + "tescolan", null, true, 10);
		assertEquals(jduboisDMtescolan.size(), 1, "jdubois has 1 dm with tescolan");

		Collection<String> duyhaiDMjdubois = this.findRangeFromCF(DIRECTMESSAGELINE_CF, "duyhai" + LOGIN_SEPARATOR + "jdubois", null, true, 10);
		assertEquals(duyhaiDMjdubois.size(), 4, "duyhai has 4 dm with jdubois");
		Collection<String> tescolanDMjdubois = this.findRangeFromCF(DIRECTMESSAGELINE_CF, "tescolan" + LOGIN_SEPARATOR + "jdubois", null, true, 10);
		assertEquals(tescolanDMjdubois.size(), 1, "tescolan has 1 dm with jdubois");

		Collection<HColumn<String, Object>> jduboisDMIndex = this.findInclusiveColumnsRangeFromCF(DIRECTMESSAGE_INDEX_CF, "jdubois", null, true, 10);
		assertEquals(jduboisDMIndex.size(), 2, "jdubois has 2 conversations so far");
		Iterator<HColumn<String, Object>> iter = jduboisDMIndex.iterator();
		assertEquals(iter.next().getValue(), "tescolan", "jdubois has conversation with tescolan");
		assertEquals(iter.next().getValue(), "duyhai", "jdubois has conversation with duyhai");

		Collection<HColumn<String, Object>> duyhaiDMIndex = this.findInclusiveColumnsRangeFromCF(DIRECTMESSAGE_INDEX_CF, "duyhai", null, true, 10);
		assertEquals(duyhaiDMIndex.size(), 1, "duyhai has 1 conversation so far");
		assertEquals(duyhaiDMIndex.iterator().next().getValue(), "jdubois", "duyhai has conversation with jdubois");
		assertEquals(duyhaiDMIndex.iterator().next().getName(), dm4.getTweetId(), "last dm between duyhai & jdubois is dm4");

		Collection<HColumn<String, Object>> tescolanDMIndex = this
				.findInclusiveColumnsRangeFromCF(DIRECTMESSAGE_INDEX_CF, "tescolan", null, true, 10);
		assertEquals(tescolanDMIndex.size(), 1, "tescolan has 1 conversation so far");
		assertEquals(tescolanDMIndex.iterator().next().getValue(), "jdubois", "tescolan has conversation with jdubois");
		assertEquals(tescolanDMIndex.iterator().next().getName(), dm5.getTweetId(), "last dm between tescolan & jdubois is dm5");

	}

	@Test(dependsOnMethods = "testAddDirectMessageWithUserInRepository")
	public void testAddNewDirectMessageWithUserInRepository()
	{
		dm6 = this.tweetRepository.createTweet("duyhai", "new message to jdubois", false);

		this.directMessageRepository.addDirectMessageWithUser("duyhai", "jdubois", dm6.getTweetId());

		Collection<String> jduboisDMduyhai = this.findRangeFromCF(DIRECTMESSAGELINE_CF, "jdubois" + LOGIN_SEPARATOR + "duyhai", null, true, 10);
		assertEquals(jduboisDMduyhai.size(), 5, "jdubois has 5 dm with duyhai");

		Collection<String> duyhaiDMjdubois = this.findRangeFromCF(DIRECTMESSAGELINE_CF, "duyhai" + LOGIN_SEPARATOR + "jdubois", null, true, 10);
		assertEquals(duyhaiDMjdubois.size(), 5, "duyhai has 5 dm with jdubois");

		Collection<HColumn<String, Object>> jduboisDMIndex = this.findInclusiveColumnsRangeFromCF(DIRECTMESSAGE_INDEX_CF, "jdubois", null, true, 10);
		assertEquals(jduboisDMIndex.iterator().next().getName(), dm6.getTweetId(), "last dm between jdubois & duyhai is dm6");

		Collection<HColumn<String, Object>> duyhaiDMIndex = this.findInclusiveColumnsRangeFromCF(DIRECTMESSAGE_INDEX_CF, "duyhai", null, true, 10);
		assertEquals(duyhaiDMIndex.iterator().next().getName(), dm6.getTweetId(), "last dm between duyhai & jdubois is dm6");

	}

	@Test(dependsOnMethods = "testAddNewDirectMessageWithUserInRepository")
	public void testAddDirectMessageWithNewUserInRepository()
	{
		dm7 = this.tweetRepository.createTweet("jdbois", "new dm to uncleBob", false);
		this.directMessageRepository.addDirectMessageWithUser("jdubois", "uncleBob", dm7.getTweetId());

		Collection<String> jduboisDMuncleBob = this.findRangeFromCF(DIRECTMESSAGELINE_CF, "jdubois" + LOGIN_SEPARATOR + "uncleBob", null, true, 10);
		assertEquals(jduboisDMuncleBob.size(), 1, "jdubois has 1 dm with uncleBob");

		Collection<HColumn<String, Object>> jduboisDMIndex = this.findInclusiveColumnsRangeFromCF(DIRECTMESSAGE_INDEX_CF, "jdubois", null, true, 10);
		assertEquals(jduboisDMIndex.size(), 3, "jdubois has 3 conversations now");
		Iterator<HColumn<String, Object>> iter = jduboisDMIndex.iterator();
		assertEquals(iter.next().getValue(), "uncleBob", "jdubois has conversation with uncleBob");
		assertEquals(iter.next().getValue(), "duyhai", "jdubois has conversation with duyhai");
		assertEquals(iter.next().getValue(), "tescolan", "jdubois has conversation with tescolan");
	}

	@Test(dependsOnMethods = "testAddDirectMessageWithNewUserInRepository")
	public void testFindDirectMessageRangeForUserInRepository()
	{
		Collection<DirectMessageHeadline> dmHeadlines = this.directMessageRepository.findDirectMessageHeadlineRangeForUser("jdubois", null, 10);

		assertEquals(dmHeadlines.size(), 3, "jdubois has 3 direct message headlines");
		Iterator<DirectMessageHeadline> iter = dmHeadlines.iterator();

		DirectMessageHeadline dmHeadline = iter.next();
		assertEquals(dmHeadline.getDirectMessageId(), dm7.getTweetId());
		assertEquals(dmHeadline.getInterlocutorLogin(), "uncleBob");

		dmHeadline = iter.next();
		assertEquals(dmHeadline.getDirectMessageId(), dm6.getTweetId());
		assertEquals(dmHeadline.getInterlocutorLogin(), "duyhai");

		dmHeadline = iter.next();
		assertEquals(dmHeadline.getDirectMessageId(), dm5.getTweetId());
		assertEquals(dmHeadline.getInterlocutorLogin(), "tescolan");

	}

	@Test(dependsOnMethods = "testFindDirectMessageRangeForUserInRepository")
	public void testRemoveDirectMessageFromUserInRepository() throws FunctionalException
	{
		this.directMessageRepository.removeDirectMessageFromUser("jdubois", "duyhai", dm3.getTweetId());

		Collection<String> jduboiDmWithduyhai = this.directMessageRepository.findDirectMessageRangeForUser("jdubois", "duyhai", null, 10);

		assertEquals(jduboiDmWithduyhai.size(), 4, "jdubois has now only 4 dms with duyhai");
		assertFalse(jduboiDmWithduyhai.contains(dm3.getTweetId()), "jdubois no longer  has dm3 in his direct message line");

		Collection<String> duyhaiDmWithjdubois = this.directMessageRepository.findDirectMessageRangeForUser("duyhai", "jdubois", null, 10);

		assertEquals(duyhaiDmWithjdubois.size(), 5, "duyhai still has 5 dms with jdubois");
		assertTrue(duyhaiDmWithjdubois.contains(dm3.getTweetId()), "duyhai still has dm3 in his direct message line");

	}

	@Test(dependsOnMethods = "testRemoveDirectMessageFromUserInRepository")
	public void testRemoveDirectMessageFromUserAndFromTweetLineInRepository() throws FunctionalException
	{
		this.directMessageRepository.removeDirectMessageFromUser("duyhai", "jdubois", dm3.getTweetId());

		Collection<String> duyhaiDmWithjdubois = this.directMessageRepository.findDirectMessageRangeForUser("duyhai", "jdubois", null, 10);

		assertEquals(duyhaiDmWithjdubois.size(), 4, "duyhai still has 4 dms with jdubois");
		assertFalse(duyhaiDmWithjdubois.contains(dm3.getTweetId()), "duyhai still has dm3 in his direct message line");

		Tweet deletedDm = this.entityManager.find(Tweet.class, dm3.getTweetId());
		assertNull(deletedDm, "dm3 has been removed from Tweet line");
	}

	@Test(dependsOnMethods = "testRemoveDirectMessageFromUserAndFromTweetLineInRepository")
	public void testCompleteRemoveDirectMessageFromUserInRepository() throws FunctionalException
	{

		this.directMessageRepository.removeDirectMessageFromUser("jdubois", "tescolan", dm5.getTweetId());

		Collection<DirectMessageHeadline> dmHeadlines = this.directMessageRepository.findDirectMessageHeadlineRangeForUser("jdubois", null, 10);

		assertEquals(dmHeadlines.size(), 2, "jdubois has 2 direct message headlines");
		Iterator<DirectMessageHeadline> iter = dmHeadlines.iterator();

		DirectMessageHeadline dmHeadline = iter.next();
		assertEquals(dmHeadline.getDirectMessageId(), dm7.getTweetId());
		assertEquals(dmHeadline.getInterlocutorLogin(), "uncleBob");

		dmHeadline = iter.next();
		assertEquals(dmHeadline.getDirectMessageId(), dm6.getTweetId());
		assertEquals(dmHeadline.getInterlocutorLogin(), "duyhai");

		Collection<String> jduboisDmWithtescolan = this.directMessageRepository.findDirectMessageRangeForUser("jdubois", "tescolan", null, 10);
		assertTrue(jduboisDmWithtescolan.isEmpty(), "jdubois has no dm with tescolan");
	}

	@Test(dependsOnMethods = "testCompleteRemoveDirectMessageFromUserInRepository")
	public void testRemoveLastDirectMessageFromUserInRepository() throws FunctionalException
	{
		this.directMessageRepository.removeDirectMessageFromUser("jdubois", "duyhai", dm6.getTweetId());
		Collection<DirectMessageHeadline> dmHeadlines = this.directMessageRepository.findDirectMessageHeadlineRangeForUser("jdubois", null, 10);

		assertEquals(dmHeadlines.size(), 2, "jdubois has 2 direct message headlines");
		Iterator<DirectMessageHeadline> iter = dmHeadlines.iterator();

		iter.next();
		DirectMessageHeadline dmHeadline = iter.next();

		assertEquals(dmHeadline.getDirectMessageId(), dm4.getTweetId());
		assertEquals(dmHeadline.getInterlocutorLogin(), "duyhai");

	}
}
