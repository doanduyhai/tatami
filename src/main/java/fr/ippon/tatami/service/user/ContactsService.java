package fr.ippon.tatami.service.user;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.TimeLineRepository;
import fr.ippon.tatami.service.pipeline.TweetHandler;

public class ContactsService implements TweetHandler
{

	private UserService userService;

	private FollowerRepository followerRepository;

	private TimeLineRepository timeLineRepository;

	@Override
	public void onTweetPost(Tweet tweet) throws FunctionalException
	{
		User currentUser = userService.getCurrentUser();

		User follower = null;

		// Followers
		for (String followerLogin : followerRepository.findFollowersForUser(currentUser))
		{
			follower = this.userService.getUserByLogin(followerLogin);
			timeLineRepository.addTweetToTimeline(follower, tweet.getTweetId());
		}

	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	public void setFollowerRepository(FollowerRepository followerRepository)
	{
		this.followerRepository = followerRepository;
	}

	public void setTimeLineRepository(TimeLineRepository timeLineRepository)
	{
		this.timeLineRepository = timeLineRepository;
	}

}
