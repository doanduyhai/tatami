package fr.ippon.tatami.domain;

public class UserTweetStat implements Comparable<UserTweetStat>
{

	private String login;

	private Integer tweetsCount;

	public UserTweetStat(String login, Integer count) {
		assert login != null && count != null;
		this.login = login;
		this.tweetsCount = count;
	}

	@Override
	public int compareTo(UserTweetStat o)
	{
		return this.login.compareToIgnoreCase(o.login);
	}

	@Override
	public String toString()
	{
		return "TweetStat{login='" + this.login + "', tweetsCount=" + this.tweetsCount + "}";
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public Integer getTweetsCount()
	{
		return tweetsCount;
	}

	public void setTweetsCount(Integer count)
	{
		this.tweetsCount = count;
	}
}