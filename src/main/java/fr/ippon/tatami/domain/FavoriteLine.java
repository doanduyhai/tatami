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
@Table(name = "FavoriteLine")
@EqualsAndHashCode(of = "login")
@ToString(of = "login")
public class FavoriteLine
{

	@NotEmpty
	@Id
	private String login;

	@me.prettyprint.hom.annotations.Column(name = "favorites")
	private Set<String> favorites = new TreeSet<String>();

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public Set<String> getFavorites()
	{
		return favorites;
	}

	public void setFavorites(Set<String> favorites)
	{
		this.favorites = favorites;
	}
}
