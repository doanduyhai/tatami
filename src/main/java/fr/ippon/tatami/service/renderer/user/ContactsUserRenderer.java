package fr.ippon.tatami.service.renderer.user;

import java.util.Collection;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FollowerRepository;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.service.pipeline.user.rendering.UserRenderingHandler;

public class ContactsUserRenderer implements UserRenderingHandler
{

	private FriendRepository friendRepository;
	private FollowerRepository followerRepository;

	@Override
	public void onRender(User user, User currentUser)
	{
		Collection<String> userFriends = this.friendRepository.findFriendsForUser(currentUser);
		Collection<String> userFollowers = this.followerRepository.findFollowersForUser(currentUser);

		if (userFriends.contains(user.getLogin()))
		{
			user.setFollow(false);
		}
		else
		{
			user.setFollow(true);
		}

		if (userFollowers.contains(user.getLogin()))
		{
			user.setDirectMessage(true);
		}
		else
		{
			user.setDirectMessage(false);
		}
	}

	public void setFriendRepository(FriendRepository friendRepository)
	{
		this.friendRepository = friendRepository;
	}

	public void setFollowerRepository(FollowerRepository followerRepository)
	{
		this.followerRepository = followerRepository;
	}

}
