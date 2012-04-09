package fr.ippon.tatami.domain;

import javax.validation.constraints.Pattern;

public class UserSearch
{

	@Pattern(regexp = "^(?:@)?[a-zA-Z-]+(?<!-)$", message = "The search string should respect the following pattern: [@] a-z,A-Z,- and should end by a character")
	private String searchString;

	public String getSearchString()
	{
		return searchString;
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

}
