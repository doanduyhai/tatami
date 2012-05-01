package fr.ippon.tatami.service.tweet;

import org.owasp.esapi.reference.DefaultEncoder;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.service.pipeline.TweetHandler;

public class XssEncodingService implements TweetHandler
{

	@Override
	public void onTweetPost(Tweet tweet)
	{
		// XSS protection by encoding input data with ESAPI api
		tweet.setContent(DefaultEncoder.getInstance().encodeForHTML(tweet.getContent()));
	}

}
