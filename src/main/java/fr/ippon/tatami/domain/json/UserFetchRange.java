package fr.ippon.tatami.domain.json;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class UserFetchRange
{
	@Pattern(regexp = "^([a-zA-Z][a-zA-Z0-9]*)?", message = "Start user login for user paging should be blank or contains no special character")
	private String startUser;

	@Min(value = 0, message = "Count value for user paging cannot be negative")
	private int count;

	@NotBlank(message = "Functional key for user paging should not be blank")
	private String functionalKey;

	public String getStartUser()
	{
		return startUser;
	}

	public void setStartUser(String startUser)
	{
		this.startUser = startUser;
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
