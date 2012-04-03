package fr.ippon.tatami.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.codehaus.jackson.map.annotate.JsonView;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import fr.ippon.tatami.web.json.view.UserView;

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
	@JsonView(UserView.Full.class)
	@NotEmpty(message = "User 'login' should not be empty")
	@Id
	private String login;

	@JsonView(UserView.Full.class)
	@Email(message = "User 'email' is invalid")
	@Column(name = "email")
	private String email;

	@JsonView(UserView.Full.class)
	@Column(name = "gravatar")
	private String gravatar;

	@JsonView(UserView.Full.class)
	// (?<!-) is a negative look-behind construct, meaning that the end of line ($) shoud NOT be preceded by a dash -
	@Pattern(regexp = "^[a-zA-Z][ a-zA-Z-]{1,29}(?<!-)$", message = "User 'firstName' should only contains a-z,A-Z,-, should start/finish by a character and should not exceed 30 characters")
	@Column(name = "firstName")
	private String firstName;

	@JsonView(UserView.Full.class)
	// (?<!-) is a negative look-behind construct, meaning that the end of line ($) shoud NOT be preceded by a dash -
	@Pattern(regexp = "^[a-zA-Z][ a-zA-Z-]{1,29}(?<!-)$", message = "User 'lastName' should only contains a-z,A-Z,-, should start/finish by a character and should not exceed 30 characters")
	@Column(name = "lastName")
	private String lastName;

	@JsonView(value =
	{
			UserView.Stats.class,
			UserView.Full.class
	})
	@Column(name = "tweetCount")
	private long tweetCount = 0;

	@Column(name = "timelineTweetCount")
	private long timelineTweetCount = 0;

	@JsonView(value =
	{
			UserView.Stats.class,
			UserView.Full.class
	})
	@Column(name = "friendsCount")
	private long friendsCount = 0;

	@JsonView(value =
	{
			UserView.Stats.class,
			UserView.Full.class
	})
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

	public void incrementTimelineTweetCount()
	{
		this.timelineTweetCount++;
	}

	public void decrementTimelineTweetCount()
	{
		this.timelineTweetCount--;
		if (this.timelineTweetCount < 0)
			this.timelineTweetCount = 0;
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

	public long getTimelineTweetCount()
	{
		return timelineTweetCount;
	}

	public void setTimelineTweetCount(long timelineTweetCount)
	{
		this.timelineTweetCount = timelineTweetCount;
	}

}
