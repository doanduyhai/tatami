package fr.ippon.tatami.service.tweet;

import static fr.ippon.tatami.service.util.TatamiConstants.LINK_REGEXP;
import static fr.ippon.tatami.service.util.TatamiConstants.LINK_SHORT_LENGTH;
import static fr.ippon.tatami.service.util.TatamiConstants.MAX_CHARACTERS_PER_TWEET;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.service.pipeline.tweet.TweetHandler;

public class TweetContentValidationService implements TweetHandler
{

	Pattern linkPattern = Pattern.compile(LINK_REGEXP);

	@Override
	public void onTweetPost(Tweet tweet) throws FunctionalException
	{
		int tweetSize = tweet.getContent().length();

		Matcher matcher = linkPattern.matcher(tweet.getContent());

		while (matcher.find())
		{
			String url = matcher.group(1);

			tweetSize = tweetSize - url.length() + LINK_SHORT_LENGTH;
		}
		if (tweetSize > MAX_CHARACTERS_PER_TWEET)
		{
			throw new FunctionalException("The tweet must has less than " + MAX_CHARACTERS_PER_TWEET + " characters");
		}
	}

	@Override
	public void onTweetRemove(Tweet tweet) throws FunctionalException
	{
		// Do nothing

	}

}
