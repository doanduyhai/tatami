package fr.ippon.tatami.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.CounterRepository;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.service.util.TatamiConstants;

/**
 * Manages the the timeline.
 * 
 * @author Julien Dubois
 */
@Service
public class TimelineService
{

	private final Logger log = LoggerFactory.getLogger(TimelineService.class);

	@Inject
	private UserService userService;

	@Inject
	private TweetRepository tweetRepository;

	@Inject
	private CounterRepository counterRepository;

	@Inject
	private FollowerRepository followerRepository;

	@Inject
	private AuthenticationService authenticationService;

	@Value("${hashtag.default}")
	private String hashtagDefault;

	private static final SimpleDateFormat DAYLINE_KEY_FORMAT = new SimpleDateFormat("ddMMyyyy");

	public Tweet postTweet(String content)
	{

		log.debug("Creating new tweet : {}", content);

		User currentUser = authenticationService.getCurrentUser();

		Tweet tweet = tweetRepository.createTweet(currentUser.getLogin(), content);
		tweetRepository.addTweetToDayline(tweet, DAYLINE_KEY_FORMAT.format(tweet.getTweetDate()));
		tweetRepository.addTweetToUserline(tweet);
		tweetRepository.addTweetToTimeline(currentUser.getLogin(), tweet);
		for (String followerLogin : followerRepository.findFollowersForUser(currentUser.getLogin()))
		{
			tweetRepository.addTweetToTimeline(followerLogin, tweet);
		}
		tweetRepository.addTweetToTagline(tweet);
		counterRepository.incrementTweetCounter(currentUser.getLogin());

		tweet.setFirstName(currentUser.getFirstName());
		tweet.setLastName(currentUser.getLastName());
		tweet.setGravatar(currentUser.getGravatar());

		return tweet;
	}

	/**
	 * The dayline contains a day's tweets
	 * 
	 * @param date
	 *            the day to retrieve the tweets of
	 * @return a tweets list
	 */
	public Collection<Tweet> getDayline(String date)
	{
		if (date == null || date.isEmpty() || !date.matches("^\\d{8}$"))
		{
			date = DAYLINE_KEY_FORMAT.format(new Date());
		}
		Collection<String> tweetIds = tweetRepository.getDayline(date);

		return this.buildTweetsList(tweetIds);
	}

	/**
	 * The tagline contains a tag's tweets
	 * 
	 * @param tag
	 *            the tag to retrieve the timeline of
	 * @param nbTweets
	 *            the number of tweets to retrieve, starting from most recent ones
	 * @return a tweets list
	 */
	public Collection<Tweet> getTagline(String tag, int nbTweets)
	{
		if (tag == null || tag.isEmpty())
		{
			tag = hashtagDefault;
		}
		Collection<String> tweetIds = tweetRepository.getTagline(tag, nbTweets);

		return this.buildTweetsList(tweetIds);
	}

	/**
	 * The timeline contains the user's tweets merged with his friends tweets
	 * 
	 * @param login
	 *            the user to retrieve the timeline of
	 * @param nbTweets
	 *            the number of tweets to retrieve, starting from most recent ones
	 * @return a tweets list
	 */
	public Collection<Tweet> getTimeline(String login, int nbTweets)
	{
		if (login == null || login.isEmpty())
		{
			User currentUser = authenticationService.getCurrentUser();
			login = currentUser.getLogin();
		}
		Collection<String> tweetIds = tweetRepository.getTimeline(login, nbTweets);

		return this.buildTweetsList(tweetIds);
	}

	/**
	 * The userline contains the user's own tweets
	 * 
	 * @param login
	 *            the user to retrieve the userline of
	 * @param nbTweets
	 *            the number of tweets to retrieve, starting from most recent ones
	 * @return a tweets list
	 */
	public Collection<Tweet> getUserline(String login, int nbTweets)
	{
		if (login == null || login.isEmpty())
		{
			User currentUser = authenticationService.getCurrentUser();
			login = currentUser.getLogin();
		}
		Collection<String> tweetIds = tweetRepository.getUserline(login, nbTweets);

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
		log.debug("Marking tweet : {}", tweetId);

		Tweet tweet = tweetRepository.findTweetById(tweetId);

		// registering
		User currentUser = authenticationService.getCurrentUser();
		tweetRepository.addTweetToFavoritesline(tweet, currentUser.getLogin());

		// alerting
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
			tweetRepository.addTweetToTimeline(tweet.getLogin(), helloTweet);
		}
	}

	/**
	 * The favline contains the user's favorites tweets
	 * 
	 * @param login
	 *            the user to retrieve the favline of
	 * @return a tweets list
	 */
	public Collection<Tweet> getFavoritesline(String login)
	{
		if (login == null || login.isEmpty())
		{
			User currentUser = authenticationService.getCurrentUser();
			login = currentUser.getLogin();
		}
		Collection<String> tweetIds = tweetRepository.getFavoritesline(login);

		return this.buildTweetsList(tweetIds);
	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}
}