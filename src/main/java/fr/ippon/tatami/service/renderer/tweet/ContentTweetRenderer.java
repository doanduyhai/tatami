package fr.ippon.tatami.service.renderer.tweet;

import static fr.ippon.tatami.service.util.TatamiConstants.HTML_ENCODED_HASHTAG_REGEXP;
import static fr.ippon.tatami.service.util.TatamiConstants.HTML_ENCODED_USER_REGEXP;
import static fr.ippon.tatami.service.util.TatamiConstants.LINK_PATTERN;
import static fr.ippon.tatami.service.util.TatamiConstants.LINK_PROTOCOL_PREFIX;
import static fr.ippon.tatami.service.util.TatamiConstants.LINK_REGEXP;
import static fr.ippon.tatami.service.util.TatamiConstants.TAG_LINK_PATTERN;
import static fr.ippon.tatami.service.util.TatamiConstants.USER_LINK_PATTERN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.reference.DefaultEncoder;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.service.pipeline.tweet.rendering.TweetRenderingHandler;

public class ContentTweetRenderer implements TweetRenderingHandler
{

	private Pattern linkPattern = Pattern.compile(LINK_REGEXP);

	@Override
	public void onRender(Tweet tweet)
	{
		String content = tweet.getContent().replaceAll(HTML_ENCODED_USER_REGEXP, USER_LINK_PATTERN)
				.replaceAll(HTML_ENCODED_HASHTAG_REGEXP, TAG_LINK_PATTERN);

		String decodedContent = DefaultEncoder.getInstance().decodeForHTML(content);

		Matcher matcher = linkPattern.matcher(decodedContent);

		String url;
		String decodedUrl;
		String newUrl;
		while (matcher.find())
		{
			decodedUrl = matcher.group(1);
			url = DefaultEncoder.getInstance().encodeForHTML(decodedUrl);

			newUrl = LINK_PATTERN.replaceAll("_URL_", decodedUrl).replaceAll("_SHORT-URL_",
					StringUtils.abbreviate(decodedUrl.replaceAll(LINK_PROTOCOL_PREFIX, ""), 13));

			content = content.replaceAll(url, newUrl);
		}

		tweet.setContent(content);

	}

}
