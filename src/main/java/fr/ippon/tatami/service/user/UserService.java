package fr.ippon.tatami.service.user;

import static fr.ippon.tatami.service.util.TatamiConstants.USER_SEARCH_LIMIT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.reference.DefaultEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.exception.FunctionalException;
import fr.ippon.tatami.repository.TweetRepository;
import fr.ippon.tatami.repository.UserIndexRepository;
import fr.ippon.tatami.service.security.AuthenticationService;
import fr.ippon.tatami.service.util.GravatarUtil;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */

public class UserService extends AbstractUserService
{

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	private UserIndexRepository userIndexRepository;

	private AuthenticationService authenticationService;

	TweetRepository tweetRepository;

	public User getUserByLogin(String login) throws FunctionalException
	{
		log.debug("Looking for user with login : {} ", login);

		User user = userRepository.findUserByLogin(login);
		if (user == null)
		{
			throw new FunctionalException("No user found for login '" + login + "'");
		}
		return user.duplicate();
	}

	public void updateUser(User updatedUser)

	{
		User currentUser = authenticationService.getCurrentUser();
		if (currentUser.getLogin().equals(updatedUser.getLogin()))
		{
			if (!StringUtils.equalsIgnoreCase(updatedUser.getFirstName(), currentUser.getFirstName()))
			{
				this.userIndexRepository.removeFirstName(currentUser.getFirstName().toLowerCase(), currentUser.getLogin().toLowerCase());
				this.userIndexRepository.insertFirstName(updatedUser.getFirstName().toLowerCase(), currentUser.getLogin().toLowerCase());
			}

			if (!StringUtils.equalsIgnoreCase(updatedUser.getLastName(), currentUser.getLastName()))
			{
				this.userIndexRepository.removeLastName(currentUser.getLastName().toLowerCase(), currentUser.getLogin().toLowerCase());
				this.userIndexRepository.insertLastName(updatedUser.getLastName().toLowerCase(), currentUser.getLogin().toLowerCase());
			}

			currentUser.setEmail(updatedUser.getEmail());
			currentUser.setGravatar(GravatarUtil.getHash(updatedUser.getEmail()));
			currentUser.setFirstName(updatedUser.getFirstName());
			currentUser.setLastName(updatedUser.getLastName());

			// XSS protection by encoding input data with ESAPI api
			currentUser.setBiography(DefaultEncoder.getInstance().encodeForHTML(updatedUser.getBiography()));
			currentUser.setLocation(DefaultEncoder.getInstance().encodeForHTML(updatedUser.getLocation()));
			currentUser.setWebsite(updatedUser.getWebsite());

			userRepository.updateUser(currentUser);

		}
		else
		{
			log.info("Security alert : user {} tried to update user {} ", currentUser.getLogin(), updatedUser);
		}
	}

	public void updateRandomUser(User user)
	{
		userRepository.updateUser(user);
	}

	public void createUser(User user)
	{
		user.setGravatar(GravatarUtil.getHash(user.getEmail()));
		userRepository.createUser(user);

		// Add user to user index
		userIndexRepository.insertLogin(user.getLogin().toLowerCase());
		userIndexRepository.insertFirstName(user.getFirstName().toLowerCase(), user.getLogin().toLowerCase());
		if (StringUtils.isNotBlank(user.getLastName()))
		{
			userIndexRepository.insertLastName(user.getLastName().toLowerCase(), user.getLogin().toLowerCase());
		}

	}

	public List<User> findUser(String searchString) throws FunctionalException
	{
		return this.findUser(searchString, 1, USER_SEARCH_LIMIT);
	}

	public List<User> findUser(String searchString, int start, int end) throws FunctionalException
	{
		assert end > start : "User search end index should be greater than start index";
		assert start > 0 : "User search start index should be greater than 1";

		start--;

		List<String> logins = null;
		User currentUser = this.getCurrentUser();

		if (searchString.startsWith("@"))
		{
			logins = this.userIndexRepository.findLogin(searchString.substring(1).toLowerCase().trim(), end);
		}
		else
		{
			Set<String> set = new HashSet<String>();

			set.addAll(this.userIndexRepository.findLogin(searchString.toLowerCase().trim(), end));
			set.addAll(this.userIndexRepository.findFirstName(searchString.toLowerCase().trim(), end));
			set.addAll(this.userIndexRepository.findLastName(searchString.toLowerCase().trim(), end));

			logins = new ArrayList<String>(set);

			// Sort by logins
			Collections.sort(logins);

			// Extract search range
			if (start > logins.size())
			{
				logins = Arrays.asList();
			}
			else if (logins.size() > end)
			{
				logins = logins.subList(start, end);
			}
			else
			{
				logins = logins.subList(start, logins.size());
			}

		}
		return this.buildUserList(currentUser, logins);
	}

	public User getCurrentUser()
	{
		return authenticationService.getCurrentUser();
	}

	public void setAuthenticationService(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}

	public void setUserIndexRepository(UserIndexRepository userIndexRepository)
	{
		this.userIndexRepository = userIndexRepository;
	}

	public void setTweetRepository(TweetRepository tweetRepository)
	{
		this.tweetRepository = tweetRepository;
	}

}