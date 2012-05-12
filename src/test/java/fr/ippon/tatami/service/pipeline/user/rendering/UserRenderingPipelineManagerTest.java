package fr.ippon.tatami.service.pipeline.user.rendering;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.service.user.UserService;

public class UserRenderingPipelineManagerTest extends AbstractCassandraTatamiTest
{
	@Test
	public void testOnUserRender()
	{
		User jdubois = new User();
		jdubois.setLogin("jdubois");

		UserService mockUserService = mock(UserService.class);
		when(mockUserService.getCurrentUser()).thenReturn(jdubois);

		FriendRepository mockFriendRepository = mock(FriendRepository.class);
		when(mockFriendRepository.findFriendsForUser(jdubois)).thenReturn(Arrays.asList(""));

		this.contactsUserRenderer.setFriendRepository(mockFriendRepository);

		User duyhai = new User();
		duyhai.setLogin("duyhai");

		this.userRenderingPipelineManager.onUserRender(duyhai, jdubois);

		assertTrue(duyhai.isFollow(), "Can follow duyhai");

	}
}
