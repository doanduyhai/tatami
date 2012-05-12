package fr.ippon.tatami.service.renderer.tweet;

import org.apache.commons.lang.StringUtils;

import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.pipeline.tweet.rendering.TweetRenderingHandler;
import fr.ippon.tatami.service.user.UserService;

public class DeleteTweetRenderer implements TweetRenderingHandler
{

	private UserService userService;

	@Override
	public void onRender(Tweet tweet)
	{
		User currentUser = this.userService.getCurrentUser();

		if (StringUtils.equals(currentUser.getLogin(), tweet.getLogin()))
		{
			tweet.setDeletable(true);
		}
	}

	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

}
