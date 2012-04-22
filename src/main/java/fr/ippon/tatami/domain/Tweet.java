package fr.ippon.tatami.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.codehaus.jackson.map.annotate.JsonView;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import fr.ippon.tatami.web.json.view.TweetView;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
@Entity
@Table(name = "Tweet")
@EqualsAndHashCode(of = "tweetId")
@ToString
public class Tweet
{

	private static PeriodFormatter dayFormatter = new PeriodFormatterBuilder().appendDays().appendSuffix("d").toFormatter();

	private static PeriodFormatter hourFormatter = new PeriodFormatterBuilder().appendHours().appendSuffix("h").toFormatter();

	private static PeriodFormatter minuteFormatter = new PeriodFormatterBuilder().appendMinutes().appendSuffix("m").toFormatter();

	private static PeriodFormatter secondFormatter = new PeriodFormatterBuilder().appendSeconds().appendSuffix("s").toFormatter();

	@Id
	@JsonView(TweetView.Full.class)
	private String tweetId;

	@JsonView(TweetView.Full.class)
	@Column(name = "login")
	private String login;

	@JsonView(TweetView.Full.class)
	@NotEmpty(message = "The tweet content should not be empty")
	@Length(max = 140, message = "The tweet content should not exceed 140 characters")
	@Column(name = "content")
	private String content;

	@Column(name = "tweetDate")
	private Date tweetDate;

	@me.prettyprint.hom.annotations.Column(name = "likers")
	private Set<String> likers = new HashSet<String>();

	@Column(name = "likersCount")
	private long likersCount;

	@JsonView(TweetView.Full.class)
	private String firstName;

	@JsonView(TweetView.Full.class)
	private String lastName;

	@JsonView(TweetView.Full.class)
	private String gravatar;

	@Column(name = "removed")
	private Boolean removed = new Boolean(false);

	@JsonView(TweetView.Full.class)
	private Boolean authorFollow = new Boolean(false);

	@JsonView(TweetView.Full.class)
	private Boolean authorForget = new Boolean(false);

	@JsonView(TweetView.Full.class)
	private Boolean addToFavorite = new Boolean(false);

	@JsonView(TweetView.Full.class)
	public String getPrettyPrintTweetDate()
	{
		Duration duration = new Duration(Calendar.getInstance().getTimeInMillis() - tweetDate.getTime());

		Period period = duration.toPeriod();

		if (period.getDays() > 0)
		{
			return dayFormatter.print(duration.toPeriod());
		}
		else if (period.getHours() > 0)
		{
			return hourFormatter.print(duration.toPeriod());
		}
		else if (period.getMinutes() > 0)
		{
			return minuteFormatter.print(duration.toPeriod());
		}
		else
		{
			return secondFormatter.print(duration.toPeriod());
		}
	}

	public String getTweetId()
	{
		return tweetId;
	}

	public void setTweetId(String tweetId)
	{
		this.tweetId = tweetId;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public Date getTweetDate()
	{
		return tweetDate;
	}

	public void setTweetDate(Date tweetDate)
	{
		this.tweetDate = tweetDate;
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

	public String getGravatar()
	{
		return gravatar;
	}

	public void setGravatar(String gravatar)
	{
		this.gravatar = gravatar;
	}

	public Set<String> getLikers()
	{
		return likers;
	}

	public void setLikers(Set<String> likers)
	{
		this.likers = likers;
	}

	public long getLikersCount()
	{
		return likersCount;
	}

	public void setLikersCount(long likersCount)
	{
		this.likersCount = likersCount;
	}

	public Boolean getRemoved()
	{
		return removed;
	}

	public void setRemoved(Boolean removed)
	{
		this.removed = removed;
	}

	public boolean isAuthorFollow()
	{
		return authorFollow;
	}

	public boolean isAuthorForget()
	{
		return authorForget;
	}

	public void setAuthorForget(boolean authorForget)
	{
		this.authorForget = authorForget;
	}

	public void setAuthorFollow(boolean authorFollow)
	{
		this.authorFollow = authorFollow;
	}

	public boolean isAddToFavorite()
	{
		return addToFavorite;
	}

	public void setAddToFavorite(boolean addToFavorite)
	{
		this.addToFavorite = addToFavorite;
	}

	public void resetFlags()
	{
		this.authorFollow = false;
		this.authorForget = false;
		this.addToFavorite = false;
	}
}
