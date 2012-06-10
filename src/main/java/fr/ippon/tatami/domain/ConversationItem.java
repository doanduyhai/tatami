package fr.ippon.tatami.domain;

public class ConversationItem
{
	private String tweetId;
	private String authorLogin;

	public String getTweetId()
	{
		return tweetId;
	}

	public void setTweetId(String tweetId)
	{
		this.tweetId = tweetId;
	}

	public String getAuthorLogin()
	{
		return authorLogin;
	}

	public void setAuthorLogin(String authorLogin)
	{
		this.authorLogin = authorLogin;
	}

	public ConversationItem(String tweetId, String authorLogin) {
		super();
		this.tweetId = tweetId;
		this.authorLogin = authorLogin;
	}

}
