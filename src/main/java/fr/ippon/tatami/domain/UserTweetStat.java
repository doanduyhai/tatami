package fr.ippon.tatami.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
@EqualsAndHashCode
@ToString
public class UserTweetStat implements Comparable<UserTweetStat>
{

	private String login;

	private Long tweetsCount;

	public UserTweetStat(String login, Long count) {
		assert login != null && count != null;
		this.login = login;
		this.tweetsCount = count;
	}

	@Override
	public int compareTo(UserTweetStat o)
	{
		if (o != null)
		{
			return this.tweetsCount.compareTo(o.getTweetsCount());
		}
		else
		{
			return 1;
		}
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public Long getTweetsCount()
	{
		return tweetsCount;
	}

	public void setTweetsCount(Long count)
	{
		this.tweetsCount = count;
	}
}