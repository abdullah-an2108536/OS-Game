package content;

public class Ticket {

	private static int idCount = 0;
	private int id;
	private String nickname;

	public Ticket(String nickname) {

		idCount++;

		this.id = idCount + 1;
		this.nickname = nickname;
	}
	
	public Ticket(String nickname,int id) {


		this.id = id;
		this.nickname = nickname;
	}


	public int getId() {
		return id;
	}

	public String getNickname() {
		return nickname;
	}

	public String getTicket() {
		return nickname+" "+id;
	}
}