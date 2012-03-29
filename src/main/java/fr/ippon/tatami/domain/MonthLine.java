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
@Table(name = "MonthLine")
@EqualsAndHashCode(of = "month")
@ToString(of = "month")
public class MonthLine
{

	public static final String monthFormat = "yyyyMM";

	@NotEmpty
	@Id
	private String month;

	@me.prettyprint.hom.annotations.Column(name = "tweetIds")
	private Set<String> tweetIds = new TreeSet<String>();

	public String getMonth()
	{
		return month;
	}

	public void setMonth(String month)
	{
		this.month = month;
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
		return jodaTime.toString(monthFormat);
	}
}
