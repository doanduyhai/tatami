package fr.ippon.tatami.service.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.UserRepository;

/**
 * @author Julien DUBOIS
 * @author DuyHai DOAN
 */
public class AuthenticationService
{

	private UserRepository userRepository;

	public User getCurrentUser()
	{
		SecurityContext securityContext = SecurityContextHolder.getContext();
		org.springframework.security.core.userdetails.User springSecurityUser = (org.springframework.security.core.userdetails.User) securityContext
				.getAuthentication().getPrincipal();

		return userRepository.findUserByLogin(springSecurityUser.getUsername()).duplicate();
	}

	public void setUserRepository(UserRepository userRepository)
	{
		this.userRepository = userRepository;
	}

}