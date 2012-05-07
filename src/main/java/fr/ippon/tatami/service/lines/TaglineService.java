package fr.ippon.tatami.service.lines;

import static fr.ippon.tatami.service.util.TatamiConstants.HASHTAG;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.TagLineRepository;
import fr.ippon.tatami.service.pipeline.TweetHandler;
import fr.ippon.tatami.service.util.TatamiConstants;

public class TaglineService extends AbstractlineService implements TweetHandler
{
	private final Logger log = LoggerFactory.getLogger(TaglineService.class);

	private TagLineRepository tagLineRepository;

	private String hashtagDefault;

	private static final Pattern HASHTAG_PATTERN = Pattern.compile(TatamiConstants.HASHTAG_REGEXP);

	public Collection<Tweet> getTaglineRange(String tag, String startTweetId, int count) throws FunctionalException
	{
		if (tag == null || tag.isEmpty())
		{
			tag = hashtagDefault;
		}

		if (startTweetId == null && count < TatamiConstants.DEFAULT_TAG_LIST_SIZE)
		{
			count = TatamiConstants.DEFAULT_TAG_LIST_SIZE;
		}

		Collection<String> tweetIds = tagLineRepository.findTweetsRangeForTag(tag, startTweetId, count);

		User currentUser = userService.getCurrentUser();

		return this.buildTweetsList(currentUser, tweetIds);
	}

	@Override
	public void onTweetPost(Tweet tweet)
	{
		Matcher m = HASHTAG_PATTERN.matcher(tweet.getContent());

		// Set to eliminate multiple additions for same tag in the source tweet
		Set<String> tagSet = new HashSet<String>();
		while (m.find())
		{
			String tag = m.group(1);
			assert tag != null && !tag.isEmpty() && !tag.contains(HASHTAG);
			if (!tagSet.contains(tag))
			{
				tagSet.add(tag);
				log.debug("tag list augmented : " + tag);
				tagLineRepository.addTweet(tag, tweet.getTweetId());
			}

		}

	}

	@Override
	public void onTweetRemove(Tweet tweet) throws FunctionalException
	{
		if (!tweet.isNotification())
		{
			Matcher m = HASHTAG_PATTERN.matcher(tweet.getContent());
			Set<String> tagSet = new HashSet<String>();
			while (m.find())
			{
				String tag = m.group(1);
				assert tag != null && !tag.isEmpty() && !tag.contains(HASHTAG);
				if (!tagSet.contains(tag))
				{
					tagSet.add(tag);
					log.debug("tag list reduced : " + tag);
					tagLineRepository.removeTweet(tag, tweet.getTweetId());
				}
			}
		}
	}

	public void setTagLineRepository(TagLineRepository tagLineRepository)
	{
		this.tagLineRepository = tagLineRepository;
	}

	public void setHashtagDefault(String hashtagDefault)
	{
		this.hashtagDefault = hashtagDefault;
	}

}
