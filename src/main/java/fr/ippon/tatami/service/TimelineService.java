package fr.ippon.tatami.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.joda.time.DateTime;
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

	private static final String DAYLINE_KEY_FORMAT = "yyyyMMdd";
	private static final String WEEKLINE_KEY_FORMAT = "w";
	private static final String MONTHLINE_KEY_FORMAT = "yyyyMM";
	private static final String YEARLINE_KEY_FORMAT = "yyyy";

	public Tweet postTweet(String content)
	{

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
			assert tag != null && !tag.isEmpty() && !tag.contains("#");
			log.debug("tag list augmented : " + tag);
			tagLineRepository.addTweet(tag, tweet.getTweetId());
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

		return this.buildTweetsList(tweetIds);
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

		return this.buildTweetsList(tweetIds);
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

		return this.buildTweetsList(tweetIds);
	}

	public Collection<Tweet> getTimeline(String login, int nbTweets)
	{
		return this.getTimelineRange(login, 1, nbTweets);
	}

	public Collection<Tweet> getTimelineRange(String login, int start, int end)
	{
		User user = null;
		if (login == null || login.isEmpty())
		{
			user = authenticationService.getCurrentUser();
		}
		else
		{
			user = this.userService.getUserByLogin(login);
		}

		Collection<String> tweetIds = timeLineRepository.getTweetsRangeFromTimeline(user, start, end);

		return this.buildTweetsList(tweetIds);
	}

	public Collection<Tweet> getUserline(String login, int nbTweets)
	{
		return this.getUserlineRange(login, 1, nbTweets);
	}

	public Collection<Tweet> getUserlineRange(String login, int start, int end)
	{
		User user = this.userService.getUserByLogin(login);
		if (user == null)
		{
			return Arrays.asList();
		}

		Collection<String> tweetIds = userLineRepository.getTweetsRangeFromUserline(user, start, end);

		return this.buildTweetsList(tweetIds);
	}

	private Collection<Tweet> buildTweetsList(Collection<String> tweetIds)
	{
		Collection<Tweet> tweets = new ArrayList<Tweet>(tweetIds.size());
		for (String tweedId : tweetIds)
		{
			Tweet tweet = tweetRepository.findTweetById(tweedId);
			User tweetUser = userService.getUserByLogin(tweet.getLogin());
			tweet.setFirstName(tweetUser.getFirstName());
			tweet.setLastName(tweetUser.getLastName());
			tweet.setGravatar(tweetUser.getGravatar());
			tweets.add(tweet);
		}
		return tweets;
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
			String content = '@' + currentUser.getLogin() + " liked your tweet<br/><em>_PH_...</em>";
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

	public Collection<Tweet> getFavoritesline(String login)
	{
		return this.getFavoriteslineByRange(login, 1, TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE);
	}

	public Collection<Tweet> getFavoriteslineByRange(String login, int start, int end)
	{
		User currentUser = authenticationService.getCurrentUser();
		User user = null;
		Collection<String> tweetIds = null;
		if (login == null || login.isEmpty())
		{
			// TODO Functional exception
			tweetIds = Arrays.asList();
		}
		else
		{
			user = this.userService.getUserByLogin(login);
			if (!currentUser.equals(user))
			{
				// TODO Functional exception
				tweetIds = Arrays.asList();
			}
			else
			{
				tweetIds = favoriteLineRepository.findFavoritesRangeForUser(user, start, end);
			}

		}
		return this.buildTweetsList(tweetIds);
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