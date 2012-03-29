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
@Table(name = "DayLine")
@EqualsAndHashCode(of = "day")
@ToString(of = "day")
public class DayLine
{

	public static final String dayFormat = "yyyyMMdd";

	@NotEmpty
	@Id
	private String day;

	@me.prettyprint.hom.annotations.Column(name = "tweetIds")
	private Set<String> tweetIds = new TreeSet<String>();

	public String getDay()
	{
		return day;
	}

	public void setDay(String day)
	{
		this.day = day;
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
		return jodaTime.toString(dayFormat);
	}
}
