package fr.ippon.tatami.domain.json;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

public class TweetFetchRange
{
	// UUID pattern
	@Pattern(regexp = "^([0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{8}-[0-9A-Fa-f]{12}-[0-9A-Fa-f]{4})?$", message = "The startTweetId for tweet paging is not a valid UUID")
	private String startTweetId;

	@Min(value = 0, message = "Count value for tweet paging cannot be negative")
	private int count;

	private String functionalKey;

	public String getStartTweetId()
	{
		return startTweetId;
	}

	public void setStartTweetId(String startTweetId)
	{
		this.startTweetId = startTweetId;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public String getFunctionalKey()
	{
		return functionalKey;
	}

	public void setFunctionalKey(String functionalKey)
	{
		this.functionalKey = functionalKey;
	}

}
