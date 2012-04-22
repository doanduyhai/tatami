package fr.ippon.tatami.domain;

/**
 * @author DuyHai DOAN
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "TagLineCount")
@EqualsAndHashCode(of = "tag")
@ToString(of = "tag")
public class TagLineCount
{
	@NotEmpty
	@Id
	private String tag;

	@Column(name = "tweetCount")
	private long tweetCount = 0;

	public void incrementTweetCount()
	{
		this.tweetCount++;
	}

	public void decrementTweetCount()
	{
		this.tweetCount--;
	}

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public long getTweetCount()
	{
		return tweetCount;
	}

	public void setTweetCount(long tweetCount)
	{
		this.tweetCount = tweetCount;
	}

}
