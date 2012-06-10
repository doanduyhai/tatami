package fr.ippon.tatami.domain;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.codehaus.jackson.map.annotate.JsonView;
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

	// Source tweet id for conversation feature
	@JsonView(TweetView.Full.class)
	@Column(name = "originalTweetId")
	private String sourceTweetId;

	// Original tweet id for retweet feature
	@JsonView(TweetView.Full.class)
	@Column(name = "originalTweetId")
	private String originalTweetId;

	@JsonView(TweetView.Full.class)
	@Column(name = "login")
	private String login;

	// Original tweet author login for retweet feature
	@JsonView(TweetView.Full.class)
	@Column(name = "originalAuthorLogin")
	private String originalAuthorLogin;

	@JsonView(TweetView.Full.class)
	@NotEmpty(message = "The tweet content should not be empty")
	@Column(name = "content")
	private String content;

	@Column(name = "tweetDate")
	private Date tweetDate;

	@Column(name = "notification")
	private boolean notification = false;

	@JsonView(TweetView.Full.class)
	private String firstName;

	@JsonView(TweetView.Full.class)
	private String lastName;

	@JsonView(TweetView.Full.class)
	private String gravatar;

	@JsonView(TweetView.Full.class)
	private Boolean deletable = new Boolean(false);

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

	public Tweet duplicate()
	{
		Tweet clone = new Tweet();
		clone.setAddToFavorite(this.addToFavorite);
		clone.setContent(this.content);
		clone.setDeletable(this.deletable);
		clone.setFirstName(this.firstName);
		clone.setGravatar(this.gravatar);
		clone.setLastName(this.lastName);
		clone.setLogin(this.login);
		clone.setOriginalAuthorLogin(this.originalAuthorLogin);
		clone.setNotification(this.notification);
		clone.setTweetDate(this.tweetDate);
		clone.setTweetId(this.tweetId);
		clone.setOriginalTweetId(this.originalTweetId);
		return clone;
	}

	public String getTweetId()
	{
		return tweetId;
	}

	public void setTweetId(String tweetId)
	{
		this.tweetId = tweetId;
	}

	public String getOriginalTweetId()
	{
		return originalTweetId;
	}

	public void setOriginalTweetId(String originalTweetId)
	{
		this.originalTweetId = originalTweetId;
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

	public boolean isNotification()
	{
		return notification;
	}

	public void setNotification(boolean notification)
	{
		this.notification = notification;
	}

	public String getOriginalAuthorLogin()
	{
		return originalAuthorLogin;
	}

	public void setOriginalAuthorLogin(String originalAuthorLogin)
	{
		this.originalAuthorLogin = originalAuthorLogin;
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

	public boolean isAddToFavorite()
	{
		return addToFavorite;
	}

	public void setAddToFavorite(boolean addToFavorite)
	{
		this.addToFavorite = addToFavorite;
	}

	public Boolean getDeletable()
	{
		return deletable;
	}

	public void setDeletable(Boolean deletable)
	{
		this.deletable = deletable;
	}

	public String getSourceTweetId()
	{
		return sourceTweetId;
	}

	public void setSourceTweetId(String sourceTweetId)
	{
		this.sourceTweetId = sourceTweetId;
	}

}
