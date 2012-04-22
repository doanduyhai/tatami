package fr.ippon.tatami.domain;

/**
 * @author DuyHai DOAN
 */
import java.util.ArrayList;
import java.util.List;

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
	private List<String> favorites = new ArrayList<String>();

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public List<String> getFavorites()
	{
		return favorites;
	}

	public void setFavorites(List<String> favorites)
	{
		this.favorites = favorites;
	}
}
