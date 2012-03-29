package fr.ippon.tatami.domain;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

@Entity
@Table(name = "WeekLine")
@EqualsAndHashCode(of = "week")
@ToString(of = "week")
public class WeekLine
{

	public static final String weekFormat = "ww";

	@NotEmpty
	@Id
	private String week;

	@me.prettyprint.hom.annotations.Column(name = "tweetIds")
	private Set<String> tweetIds = new TreeSet<String>();

	public String getWeek()
	{
		return week;
	}

	public void setWeek(String week)
	{
		this.week = week;
	}

	public Set<String> getTweetIds()
	{
		return tweetIds;
	}

	public void setTweetIds(Set<String> tweetIds)
	{
		this.tweetIds = tweetIds;
	}

	public static final String getTodayDayId()
	{
		DateTime jodaTime = new DateTime(new Date());
		return jodaTime.toString(weekFormat);
	}
}
