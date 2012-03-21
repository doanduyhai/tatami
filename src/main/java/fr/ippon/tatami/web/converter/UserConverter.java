package fr.ippon.tatami.web.converter;

import javax.inject.Inject;

import org.springframework.core.convert.converter.Converter;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.service.UserService;

public class UserConverter implements Converter<String, User>
{

	@Inject
	private UserService userService;

	public UserConverter() {}

	@Override
	public User convert(String login)
	{
		return userService.getUserProfileByLogin(login);
	}

}
