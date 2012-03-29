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
@Table(name = "UserFollowers")
@EqualsAndHashCode(of = "login")
@ToString(of = "login")
public class UserFollowers
{
	@NotEmpty
	@Id
	private String login;

	@me.prettyprint.hom.annotations.Column(name = "followers")
	private Set<String> followers = new TreeSet<String>();

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public Set<String> getFollowers()
	{
		return followers;
	}

	public void setFollowers(Set<String> followers)
	{
		this.followers = followers;
	}

}
