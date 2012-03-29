package fr.ippon.tatami.domain;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "UserFriends")
@EqualsAndHashCode(of = "login")
@ToString(of = "login")
public class UserFriends
{
	@NotEmpty
	@Id
	private String login;

	@me.prettyprint.hom.annotations.Column(name = "friends")
	private Set<String> friends = new TreeSet<String>();

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public Set<String> getFriends()
	{
		return friends;
	}

	public void setFriends(Set<String> friends)
	{
		this.friends = friends;
	}

}
