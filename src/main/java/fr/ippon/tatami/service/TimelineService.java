package fr.ippon.tatami.service;

import static fr.ippon.tatami.service.util.TatamiConstants.HASHTAG;
import static fr.ippon.tatami.service.util.TatamiConstants.HASHTAG_REGEXP;
import static fr.ippon.tatami.service.util.TatamiConstants.TAG_LINK_PATTERN;
import static fr.ippon.tatami.service.util.TatamiConstants.USERTAG;
import static fr.ippon.tatami.service.util.TatamiConstants.USER_LINK_PATTERN;
import static fr.ippon.tatami.service.util.TatamiConstants.USER_REGEXP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.owasp.esapi.reference.DefaultEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.StatsRepository;
import fr.ippon.tatami.repository.TagLineRepository;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.repository.UserLineRepository;
import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * Manages the the timeline.
 * 
 * @author Julien Dubois
 */
@Service
public class TimelineService implements InitializingBean
{

	private final Logger log = LoggerFactory.getLogger(TimelineService.class);

	@Inject
	private UserService userService;

	@Inject
	private TweetRepository tweetRepository;

	@Inject
	private FollowerRepository followerRepository;

	@Inject
	private UserLineRepository userLineRepository;

	@Inject
	private TimeLineRepository timeLineRepository;

	@Inject
	private TagLineRepository tagLineRepository;

	@Inject
	private StatsRepository statsRepository;

	@Inject
	private FavoriteRepository favoriteLineRepository;

	@Inject
	private AuthenticationService authenticationService;

	@Inject
	Environment env;

	private String hashtagDefault;

	private static final Pattern HASHTAG_PATTERN = Pattern.compile(TatamiConstants.HASHTAG_REGEXP);

	private static final Pattern USER_PATTERN = Pattern.compile(TatamiConstants.USER_REGEXP);

	private static final String DAYLINE_KEY_FORMAT = "yyyyMMdd";
	private static final String WEEKLINE_KEY_FORMAT = "w";
	private static final String MONTHLINE_KEY_FORMAT = "yyyyMM";
	private static final String YEARLINE_KEY_FORMAT = "yyyy";

	public Tweet postTweet(String content)
	{

		// XSS protection by encoding input data with ESAPI api
		content = DefaultEncoder.getInstance().encodeForHTML(content);

		log.debug("Creating new tweet : {}", content);

		User currentUser = authenticationService.getCurrentUser();

		Tweet tweet = tweetRepository.createTweet(currentUser.getLogin(), content);

		// Timeline & Userline
		userLineRepository.addTweetToUserline(currentUser, tweet.getTweetId());
		timeLineRepository.addTweetToTimeline(currentUser, tweet.getTweetId());

		User follower = null;
		// Followers
		for (String followerLogin : followerRepository.findFollowersForUser(currentUser))
		{
			follower = this.userService.getUserByLogin(followerLogin);
			timeLineRepository.addTweetToTimeline(follower, tweet.getTweetId());
		}

		// Tagline
		Matcher m = HASHTAG_PATTERN.matcher(tweet.getContent());
		while (m.find())
		{
			String tag = m.group(1);
			assert tag != null && !tag.isEmpty() && !tag.contains(HASHTAG);
			log.debug("tag list augmented : " + tag);
			tagLineRepository.addTweet(tag, tweet.getTweetId());
		}

		// Alert for quoted users
		Matcher usermatcher = USER_PATTERN.matcher(tweet.getContent());
		while (usermatcher.find())
		{
			String user = usermatcher.group(1);
			assert user != null;
			User quotedUser = this.userService.getUserByLogin(user);
			if (quotedUser != null)
			{
				Collection<String> userFollowers = this.userService.getFollowersForUser(currentUser.getLogin());
				if (!userFollowers.contains(user))
				{
					log.debug("Add tweet to quoted user " + user + " timeline");
					this.timeLineRepository.addTweetToTimeline(quotedUser, tweet.getTweetId());
				}
			}

		}

		// Stats
		DateTime today = new DateTime(new Date());

		statsRepository.addTweetToDay(tweet.getTweetId(), today.toString(DAYLINE_KEY_FORMAT));
		statsRepository.addTweetToWeek(tweet.getTweetId(), today.toString(WEEKLINE_KEY_FORMAT));
		statsRepository.addTweetToMonth(tweet.getTweetId(), today.toString(MONTHLINE_KEY_FORMAT));
		statsRepository.addTweetToYear(tweet.getTweetId(), today.toString(YEARLINE_KEY_FORMAT));

		tweet.setFirstName(currentUser.getFirstName());
		tweet.setLastName(currentUser.getLastName());
		tweet.setGravatar(currentUser.getGravatar());

		return tweet;
	}

	public Collection<Tweet> getDayline(String date)
	{
		return this.getDaylineRange(date, 1, TatamiConstants.DEFAULT_DAY_LIST_SIZE);
	}

	public Collection<Tweet> getDaylineRange(String date, int start, int end)
	{
		String correctDate = null;
		correctDate = date;
		if (correctDate == null || correctDate.isEmpty() || !correctDate.matches("^\\d{8}$"))
		{
			correctDate = new DateTime(new Date()).toString(DAYLINE_KEY_FORMAT);
		}
		Collection<String> tweetIds = statsRepository.findTweetsRangeForDay(correctDate, start, end);

		return this.buildTweetsList(null, tweetIds);
	}

	public Collection<Tweet> getDayline(Date date)
	{
		return this.getDayline(date, 1, TatamiConstants.DEFAULT_DAY_LIST_SIZE);
	}

	public Collection<Tweet> getDayline(Date date, int start, int end)
	{
		if (date == null)
		{
			date = new Date();
		}

		Collection<String> tweetIds = statsRepository.findTweetsRangeForDay(new DateTime(date).toString(DAYLINE_KEY_FORMAT), start, end);

		return this.buildTweetsList(null, tweetIds);
	}

	public Collection<Tweet> getTagline(String tag, int nbTweets)
	{
		return this.getTaglineRange(tag, 1, nbTweets);
	}

	public Collection<Tweet> getTaglineRange(String tag, int start, int end)
	{
		if (tag == null || tag.isEmpty())
		{
			tag = hashtagDefault;
		}
		Collection<String> tweetIds = tagLineRepository.findTweetsRangeForTag(tag, start, end);

		User currentUser = authenticationService.getCurrentUser();

		return this.buildTweetsList(currentUser, tweetIds);
	}

	public Collection<Tweet> getTimeline(int nbTweets)
	{
		return this.getTimelineRange(1, nbTweets);
	}

	public Collection<Tweet> getTimelineRange(int start, int end)
	{
		User currentUser = authenticationService.getCurrentUser();

		Collection<String> tweetIds = timeLineRepository.getTweetsRangeFromTimeline(currentUser, start, end);

		return this.buildTweetsList(currentUser, tweetIds);
	}

	public Collection<Tweet> getUserline(String login, int nbTweets)
	{
		return this.getUserlineRange(login, 1, nbTweets);
	}

	public Collection<Tweet> getUserlineRange(String login, int start, int end)
	{
		User user = this.userService.getUserByLogin(login);
		User currentUser = this.userService.getCurrentUser();
		if (user == null)
		{
			return Arrays.asList();
		}

		Collection<String> tweetIds = userLineRepository.getTweetsRangeFromUserline(user, start, end);

		return this.buildTweetsList(currentUser, tweetIds);
	}

	public void addFavoriteTweet(String tweetId)
	{
		User currentUser = authenticationService.getCurrentUser();

		log.debug("Adding tweet : {} to favorites for {} ", tweetId, currentUser.getLogin());

		Tweet tweet = tweetRepository.findTweetById(tweetId);
		if (tweet == null)
		{
			// TODO Functional Exception
			return;
		}

		// FavoriteLine
		favoriteLineRepository.addFavorite(currentUser, tweetId);

		// Tweet alert
		if (!currentUser.getLogin().equals(tweet.getLogin()))
		{

			String content = USERTAG + currentUser.getLogin() + " <strong>liked your tweet:</strong><br/><em>_PH_...</em>";

			int maxLength = TatamiConstants.MAX_TWEET_SIZE - content.length() + 4;
			if (tweet.getContent().length() > maxLength)
			{
				content = content.replace("_PH_", tweet.getContent().substring(0, maxLength));
			}
			else
			{
				content = content.replace("_PH_", tweet.getContent());
			}

			Tweet helloTweet = tweetRepository.createTweet(tweet.getLogin(), content); // removable

			User author = this.userService.getUserByLogin(tweet.getLogin());

			timeLineRepository.addTweetToTimeline(author, helloTweet.getTweetId());
		}
	}

	public void removeFavoriteTweet(String tweetId)
	{
		User currentUser = authenticationService.getCurrentUser();

		log.debug("Removing tweet : {} from favorites for {} ", tweetId, currentUser.getLogin());

		Tweet tweet = tweetRepository.findTweetById(tweetId);
		if (tweet == null)
		{
			// TODO Functional Exception
			return;
		}

		// FavoriteLine
		favoriteLineRepository.removeFavorite(currentUser, tweetId);
	}

	public Collection<Tweet> getFavoritesline()
	{
		return this.getFavoriteslineByRange(1, TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE);
	}

	public Collection<Tweet> getFavoriteslineByRange(int start, int end)
	{
		User currentUser = authenticationService.getCurrentUser();
		Collection<String> tweetIds = favoriteLineRepository.findFavoritesRangeForUser(currentUser, start, end);
		return this.buildTweetsList(currentUser, tweetIds);
	}

	public boolean removeTweet(String tweetId)
	{
		log.debug("Removing tweet : {} ", tweetId);

		Tweet tweet = tweetRepository.findTweetById(tweetId);

		User currentUser = authenticationService.getCurrentUser();
		if (tweet.getLogin().equals(currentUser.getLogin()) && !Boolean.TRUE.equals(tweet.getRemoved()))
		{
			tweetRepository.removeTweet(tweet);
			currentUser.decrementTweetCount();
			currentUser.decrementTimelineTweetCount();

			this.userService.updateUser(currentUser);

			return true;
		}
		return false;
	}

	private Collection<Tweet> buildTweetsList(User currentUser, Collection<String> tweetIds)
	{
		Collection<Tweet> tweets = new ArrayList<Tweet>(tweetIds.size());
		for (String tweedId : tweetIds)
		{
			Tweet tweet = tweetRepository.findTweetById(tweedId);
			User tweetUser = userService.getUserByLogin(tweet.getLogin());
			tweet.setFirstName(tweetUser.getFirstName());
			tweet.setLastName(tweetUser.getLastName());
			tweet.setGravatar(tweetUser.getGravatar());
			tweet.resetFlags();
			if (currentUser != null)
			{
				Collection<String> friends = this.userService.getFriendsForUser(currentUser.getLogin());
				Collection<String> favorites = this.favoriteLineRepository.findFavoritesForUser(currentUser);

				if (!StringUtils.equals(currentUser.getLogin(), tweet.getLogin()))
				{
					if (!friends.contains(tweet.getLogin()))
					{
						tweet.setAuthorFollow(true);
					}
					else
					{
						tweet.setAuthorForget(true);
					}
				}

				if (!favorites.contains(tweet.getTweetId()))
				{
					tweet.setAddToFavorite(true);
				}
				else
				{
					tweet.setAddToFavorite(false);
				}

			}

			tweet.setContent(tweet.getContent().replaceAll(USER_REGEXP, USER_LINK_PATTERN).replaceAll(HASHTAG_REGEXP, TAG_LINK_PATTERN));

			tweets.add(tweet);
		}
		return tweets;
	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		this.hashtagDefault = env.getProperty("hashtag.default");

	}
}