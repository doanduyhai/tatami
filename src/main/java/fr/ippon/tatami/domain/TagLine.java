package fr.ippon.tatami.domain;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "TagLine")
@EqualsAndHashCode(of = "tag")
@ToString(of = "tag")
public class TagLine
{
	@NotEmpty
	@Id
	private String tag;

	@me.prettyprint.hom.annotations.Column(name = "tweetIds")
	private Set<String> tweetIds = new TreeSet<String>();

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public Set<String> getTweetIds()
	{
		return tweetIds;
	}

	public void setTweetIds(Set<String> tweetIds)
	{
		this.tweetIds = tweetIds;
	}

}
