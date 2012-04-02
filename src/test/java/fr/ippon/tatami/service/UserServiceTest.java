package fr.ippon.tatami.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import me.prettyprint.cassandra.model.CqlQuery;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Tweet;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserFollowers;
import fr.ippon.tatami.domain.UserFriends;
import fr.ippon.tatami.service.util.GravatarUtil;

public class UserServiceTest extends AbstractCassandraTatamiTest
{
	@Test
	public void shouldGetAUserServiceInjected()
	{
		assertThat(userService, notNullValue());

	}

	@Test(dependsOnMethods = "shouldGetAUserServiceInjected")
	public void shouldGetAUserByLogin()
	{
		User jdubois = new User();
		jdubois.setLogin("jdubois");
		jdubois.setEmail("jdubois@ippon.fr");
		jdubois.setFirstName("Julien");
		jdubois.setLastName("DUBOIS");

		this.userRepository.createUser(jdubois);

		User user = userService.getUserByLogin("jdubois");
		assertThat(user, notNullValue());
		assertThat(user.getFirstName(), is("Julien"));
		assertThat(user.getLastName(), is("DUBOIS"));
		assertThat(user.getEmail(), is("jdubois@ippon.fr"));
		assertThat(user.getTweetCount(), is(0L));
		assertThat(user.getTimelineTweetCount(), is(0L));
		assertThat(user.getFollowersCount(), is(0L));
		assertThat(user.getFriendsCount(), is(0L));
		assertThat(user.getFavoritesCount(), is(0L));

	}

	@Test
	public void shouldNotGetAUserByLogin()
	{
		User user = userService.getUserByLogin("unknownUserLogin");
		assertThat(user, nullValue());
	}

	@Test(dependsOnMethods = "shouldGetAUserByLogin")
	public void shouldUpdateUser()
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		jdubois.setEmail("uuser@ippon.fr");
		jdubois.setFirstName("UpdatedFirstName");
		jdubois.setLastName("UpdatedLastName");

		userService.updateUser(jdubois);

		User updatedUser = userService.getUserByLogin("jdubois");

		assertThat(updatedUser.getFirstName(), is("UpdatedFirstName"));
		assertThat(updatedUser.getLastName(), is("UpdatedLastName"));
		assertThat(updatedUser.getGravatar(), is(GravatarUtil.getHash("uuser@ippon.fr")));

	}

	@Test(dependsOnMethods = "shouldUpdateUser")
	public void shouldCreateAUser()
	{
		String login = "userToFollow";
		String firstName = "New";
		String lastName = "User";
		String email = "nuser@ippon.fr";
		String gravatar = "newGravatar";

		User user = new User();
		user.setLogin(login);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setGravatar(gravatar);

		userService.createUser(user);

		/* verify */
		User userToBeTheSame = userService.getUserByLogin(login);
		assertThat(userToBeTheSame.getLogin(), is(user.getLogin()));
		assertThat(userToBeTheSame.getFirstName(), is(user.getFirstName()));
		assertThat(userToBeTheSame.getLastName(), is(user.getLastName()));
		assertThat(userToBeTheSame.getGravatar(), is(user.getGravatar()));
		assertThat(userToBeTheSame.getTweetCount(), is(0L));
		assertThat(userToBeTheSame.getFollowersCount(), is(0L));
		assertThat(userToBeTheSame.getFriendsCount(), is(0L));
	}

	@Test(dependsOnMethods = "shouldCreateAUser")
	public void shouldFollowUser()
	{

		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		// "jdubois" follows "userToFollow"
		userService.followUser("userToFollow");

		User refreshedJdubois = userService.getUserByLogin("jdubois");
		User userToFollow = userService.getUserByLogin("userToFollow");

		UserFriends jduboisFriends = entityManager.find(UserFriends.class, "jdubois");
		UserFollowers userToFollowFollowers = entityManager.find(UserFollowers.class, "userToFollow");

		assertNotNull(jduboisFriends, "jduboisFriends");
		assertTrue(jduboisFriends.getFriends().size() == 1, "jduboisFriends.getFriends().size() == 1");
		assertTrue(jduboisFriends.getFriends().contains("userToFollow"), "jduboisFriends.getFriends().contains('userToFollow')");
		assertThat(refreshedJdubois.getFriendsCount(), is(1L));

		assertNotNull(userToFollowFollowers, "userToFollowFollowers");
		assertTrue(userToFollowFollowers.getFollowers().size() == 1, "userToFollowFollowers.getFollowers().size() == 1");
		assertTrue(userToFollowFollowers.getFollowers().contains("jdubois"), "userToFollowFollowers.getFollowers().contains('jdubois')");
		assertThat(userToFollow.getFollowersCount(), is(1L));

		// "userToFollow" receives an alert tweet
		Collection<String> tweets = timeLineRepository.getTweetsFromTimeline(userToFollow);
		assertTrue(tweets.size() == 1, "tweets.size() == 1");

		Tweet alertTweet = entityManager.find(Tweet.class, tweets.iterator().next());
		assertTrue(alertTweet.getContent().contains("jdubois <strong>is now following you</strong>"),
				"alertTweet contains 'jdubois <strong>is now following you</strong>'");

	}

	@Test(dependsOnMethods = "shouldFollowUser")
	public void shouldNotFollowUserBecauseUserNotExist()
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		userService.followUser("unknownUser");

		User refreshedJdubois = userService.getUserByLogin("jdubois");
		UserFriends jduboisFriends = entityManager.find(UserFriends.class, "jdubois");

		assertNotNull(jduboisFriends, "jduboisFriends");
		assertTrue(jduboisFriends.getFriends().size() == 1, "jduboisFriends.getFriends().size() == 1");
		assertThat(refreshedJdubois.getFriendsCount(), is(1L));
	}

	@Test(dependsOnMethods = "shouldNotFollowUserBecauseUserNotExist")
	public void shouldNotFollowUserBecauseUserAlreadyFollowed() throws Exception
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		userService.followUser("userToFollowFollowers");

		User refreshedJdubois = userService.getUserByLogin("jdubois");
		UserFriends jduboisFriends = entityManager.find(UserFriends.class, "jdubois");

		assertNotNull(jduboisFriends, "jduboisFriends");
		assertTrue(jduboisFriends.getFriends().size() == 1, "jduboisFriends.getFriends().size() == 1");
		assertThat(refreshedJdubois.getFriendsCount(), is(1L));
	}

	@Test(dependsOnMethods = "shouldNotFollowUserBecauseUserAlreadyFollowed")
	public void shouldNotFollowUserBecauseSameUser() throws Exception
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		userService.followUser("jdubois");

		User refreshedJdubois = userService.getUserByLogin("jdubois");
		UserFriends jduboisFriends = entityManager.find(UserFriends.class, "jdubois");

		assertNotNull(jduboisFriends, "jduboisFriends");
		assertTrue(jduboisFriends.getFriends().size() == 1, "jduboisFriends.getFriends().size() == 1");
		assertThat(refreshedJdubois.getFriendsCount(), is(1L));
	}

	@Test(dependsOnMethods = "shouldNotFollowUserBecauseSameUser")
	public void shouldFindFriendsForUser()
	{
		Collection<String> jduboisFriends = this.userService.getFriendsForUser("jdubois");

		assertNotNull(jduboisFriends, "jduboisFriends");
		assertTrue(jduboisFriends.size() == 1, "jduboisFriends.size() == 1");
		assertTrue(jduboisFriends.contains("userToFollow"), "jduboisFriends.contains('userToFollow')");
	}

	@Test(dependsOnMethods = "shouldFindFriendsForUser")
	public void shouldFindFollowersForUser()
	{
		Collection<String> userToFollowFollowers = this.userService.getFollowersForUser("userToFollow");

		assertNotNull(userToFollowFollowers, "userToFollowFollowers");
		assertTrue(userToFollowFollowers.size() == 1, "userToFollowFollowers.size() == 1");
		assertTrue(userToFollowFollowers.contains("jdubois"), "userToFollowFollowers.contains('jdubois')");
	}

	@Test(dependsOnMethods = "shouldFindFollowersForUser")
	public void shouldForgetUser()
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		userService.forgetUser("userToFollow");

		User refreshedJdubois = userService.getUserByLogin("jdubois");
		User userToFollow = userService.getUserByLogin("userToFollow");

		UserFriends jduboisFriends = entityManager.find(UserFriends.class, "jdubois");
		UserFollowers userToFollowFollowers = entityManager.find(UserFollowers.class, "userToFollow");

		assertNotNull(jduboisFriends, "jduboisFriends");
		assertTrue(jduboisFriends.getFriends().size() == 0, "jduboisFriends.getFriends().size() == 0");
		assertThat(refreshedJdubois.getFriendsCount(), is(0L));

		assertNotNull(userToFollowFollowers, "userToFollowFollowers");
		assertTrue(userToFollowFollowers.getFollowers().size() == 0, "userToFollowFollowers.getFollowers().size() == 0");
		assertThat(userToFollow.getFollowersCount(), is(0L));

	}

	@Test(dependsOnMethods = "shouldForgetUser")
	public void shouldNotForgetUserBecauseUserNotExist()
	{
		User jdubois = userService.getUserByLogin("jdubois");
		mockAuthenticatedUser(jdubois);

		userService.forgetUser("userToFollow");

		User refreshedJdubois = userService.getUserByLogin("jdubois");
		UserFriends jduboisFriends = entityManager.find(UserFriends.class, "jdubois");

		assertNotNull(jduboisFriends, "jduboisFriends");
		assertTrue(jduboisFriends.getFriends().size() == 0, "jduboisFriends.getFriends().size() == 0");
		assertThat(refreshedJdubois.getFriendsCount(), is(0L));
	}

	@Test(dependsOnMethods = "shouldNotForgetUserBecauseUserNotExist")
	public void cleanUp()
	{
		CqlQuery<String, String, String> cqlQuery = new CqlQuery<String, String, String>(keyspace, se, se, se);
		cqlQuery.setQuery("truncate User");
		cqlQuery.execute();

		cqlQuery.setQuery("truncate UserFriends");
		cqlQuery.execute();

		cqlQuery.setQuery("truncate UserFollowers");
		cqlQuery.execute();

		User jdubois = userService.getUserByLogin("jdubois");
		User userToFollow = userService.getUserByLogin("userToFollow");
		UserFriends jduboisFriends = entityManager.find(UserFriends.class, "jdubois");
		UserFollowers userToFollowFollowers = entityManager.find(UserFollowers.class, "userToFollow");

		assertNull(jdubois, "jdubois");
		assertNull(userToFollow, "userToFollow");
		assertNull(jduboisFriends, "jduboisFriends");
		assertNull(userToFollowFollowers, "userToFollowFollowers");
	}
}