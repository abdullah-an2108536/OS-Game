package content;


import java.io.Serializable;


public class Ticket implements Serializable
{
	private static final long serialVersionUID = 0;

	private int id;
	private String nickname;

	public Ticket(String nickname, int id)
	{
		this.id = id;
		this.nickname = nickname;
	}

	public int getId() {return id;}

	public String getNickname() {return nickname;}
	
	public int getID() {return this.id;}

	public String getTicket() {return nickname + " " + id;}

	@Override
	public String toString() {return "Ticket [id=" + id + ", nickname=" + nickname + "]";}
}