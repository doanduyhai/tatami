package fr.ippon.tatami.service.renderer.user;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FriendRepository;
import fr.ippon.tatami.service.user.UserService;

public class ContactsUserRendererTest extends AbstractCassandraTatamiTest
{
	private User jdubois;
	private UserService mockUserService;
	private FriendRepository mockFriendRepository;
	private ContactsUserRenderer contactsUserRenderer;

	@Test
	public void initContactsUserRendererTest()
	{
		jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");

		mockUserService = mock(UserService.class);
		when(mockUserService.getCurrentUser()).thenReturn(jdubois);

		mockFriendRepository = mock(FriendRepository.class);

		contactsUserRenderer = new ContactsUserRenderer();
		contactsUserRenderer.setFriendRepository(mockFriendRepository);
	}

	@Test(dependsOnMethods = "initContactsUserRendererTest")
	public void testOnRenderFollow()
	{
		User duyhai = new User();
		duyhai.setLogin("duyhai");

		when(mockFriendRepository.findFriendsForUser(jdubois)).thenReturn(Arrays.asList("tescolan"));

		this.contactsUserRenderer.onRender(duyhai, jdubois);

		assertTrue(duyhai.isFollow(), "Can follow duyhai");
	}

	@Test(dependsOnMethods = "initContactsUserRendererTest")
	public void testOnRenderUnFollow()
	{
		User duyhai = new User();
		duyhai.setLogin("duyhai");

		when(mockFriendRepository.findFriendsForUser(jdubois)).thenReturn(Arrays.asList("duyhai"));

		this.contactsUserRenderer.onRender(duyhai, jdubois);

		assertFalse(duyhai.isFollow(), "Cannot follow duyhai");
	}
}
