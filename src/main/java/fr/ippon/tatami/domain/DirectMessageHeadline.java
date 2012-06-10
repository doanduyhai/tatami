package fr.ippon.tatami.domain;

public class DirectMessageHeadline
{
	private String directMessageId;

	private Tweet directMessage;

	private String interlocutorLogin;

	private User interlocutor;

	public DirectMessageHeadline(String directMessageId, String interlocutorLogin) {
		super();
		this.directMessageId = directMessageId;
		this.interlocutorLogin = interlocutorLogin;
	}

	public String getDirectMessageId()
	{
		return directMessageId;
	}

	public void setDirectMessageId(String directMessageId)
	{
		this.directMessageId = directMessageId;
	}

	public Tweet getDirectMessage()
	{
		return directMessage;
	}

	public void setDirectMessage(Tweet directMessage)
	{
		this.directMessage = directMessage;
	}

	public String getInterlocutorLogin()
	{
		return interlocutorLogin;
	}

	public void setInterlocutorLogin(String interlocutorLogin)
	{
		this.interlocutorLogin = interlocutorLogin;
	}

	public User getInterlocutor()
	{
		return interlocutor;
	}

	public void setInterlocutor(User interlocutor)
	{
		this.interlocutor = interlocutor;
	}

}
