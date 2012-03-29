package fr.ippon.tatami.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * A user.
 * 
 * @author Julien Dubois
 */
@Entity
@Table(name = "User")
@EqualsAndHashCode(of = "login")
@ToString
public class User
{

	@NotEmpty
	@Id
	private String login;

	@Column(name = "email")
	private String email;

	@Column(name = "gravatar")
	private String gravatar;

	@Column(name = "firstName")
	private String firstName;

	@Column(name = "lastName")
	private String lastName;

	@Column(name = "tweetCount")
	private long tweetCount = 0;

	@Column(name = "friendsCount")
	private long friendsCount = 0;

	@Column(name = "followersCount")
	private long followersCount = 0;

	@Column(name = "favoritesCount")
	private long favoritesCount = 0;

	public void incrementTweetCount()
	{
		this.tweetCount++;
	}

	public void decrementTweetCount()
	{
		this.tweetCount--;
		if (this.tweetCount < 0)
			this.tweetCount = 0;
	}

	public void incrementFriendsCount()
	{
		this.friendsCount++;
	}

	public void decrementFriendsCount()
	{
		this.friendsCount--;
		if (this.friendsCount < 0)
			this.friendsCount = 0;
	}

	public void incrementFollowersCount()
	{
		this.followersCount++;
	}

	public void decrementFollowersCount()
	{
		this.followersCount--;
		if (this.followersCount < 0)
			this.followersCount = 0;
	}

	public void incrementFavoritesCount()
	{
		this.favoritesCount++;
	}

	public void decrementFavoritesCount()
	{
		this.favoritesCount--;
		if (this.favoritesCount < 0)
			this.favoritesCount = 0;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getGravatar()
	{
		return gravatar;
	}

	public void setGravatar(String gravatar)
	{
		this.gravatar = gravatar;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public long getTweetCount()
	{
		return tweetCount;
	}

	public void setTweetCount(long tweetCount)
	{
		this.tweetCount = tweetCount;
	}

	public long getFriendsCount()
	{
		return friendsCount;
	}

	public void setFriendsCount(long friendsCount)
	{
		this.friendsCount = friendsCount;
	}

	public long getFollowersCount()
	{
		return followersCount;
	}

	public void setFollowersCount(long followersCount)
	{
		this.followersCount = followersCount;
	}

	public long getFavoritesCount()
	{
		return favoritesCount;
	}

	public void setFavoritesCount(long favoritesCount)
	{
		this.favoritesCount = favoritesCount;
	}

}
