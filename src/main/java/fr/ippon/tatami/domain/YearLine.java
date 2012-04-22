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

/**
 * 
 * @author DuyHai DOAN
 */
@Entity
@Table(name = "YearLine")
@EqualsAndHashCode(of = "year")
@ToString(of = "year")
public class YearLine
{

	public static final String yearFormat = "yyyy";

	@NotEmpty
	@Id
	private String year;

	@me.prettyprint.hom.annotations.Column(name = "tweetIds")
	private Set<String> tweetIds = new TreeSet<String>();

	public String getYear()
	{
		return year;
	}

	public void setYear(String year)
	{
		this.year = year;
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
		return jodaTime.toString(yearFormat);
	}
}
