
package content;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server
{
	
	public ArrayList<Game> games = new ArrayList<>();
	public ArrayList<Player> players = new ArrayList<>();
	public ArrayList<Ticket> tickets = new ArrayList<>();
	
	public Leaderboard leaderboard;

	private PrintWriter toClient;
	private BufferedReader fromClient;

	public Server()
	{
		try
		{
			
			loadTickets();
			leaderboard = Leaderboard.loadLeaderboard("LeaderboardStorage.ser");
			leaderboard.checkAndUpdateLeaderboard(tickets);

			// TEMPORARY (print tickets for testing purposes)
			System.out.println("Tickets available :" + tickets.size());
			for (Ticket t : tickets) {System.out.println(t.toString());}
			System.out.println(leaderboard.toString());
			

			// Create default empty games
			Game defaultGame = new Game(0, "Default Game", leaderboard);
			Game defaultGame1 = new Game(1, "Test Game", leaderboard);

			games.add(defaultGame);
			games.add(defaultGame1);

			ServerSocket server = new ServerSocket(13337);
			System.out.println("Server is up, waiting for Connections on port " + server.getLocalPort());

			while (true)
			{
				Socket client = server.accept();

				// Create a new Player Thread
				Player player = new Player(client, this);
				//player.sendMessage(leaderboard.toString2());
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				players.add(player);
				player.start();
			}
		}
		catch (IOException e) {System.out.println(e);}
	}

	// Check if the ticket is present in the tickets ArrayList
	public Ticket findTicket(String ticket)
	{
		String[] parts = ticket.split(" ");

		if (parts.length == 2)
		{
			String nickname = parts[0];
			int id = Integer.parseInt(parts[1]);

			for (Ticket t : tickets)
			{
				if (t.getNickname().equals(nickname) && t.getId() == id) {return t;}
			}
		}
		return null;
	}

	// Load Tickets stored in TicketsStorage.ser
	public void loadTickets()
	{
		try
		{
			File file = new File("TicketsStorage.ser");
			if (!file.exists())
			{
				System.out.println("Tickets file does not exist!");
				boolean created = file.createNewFile();
				if (!created)
				{
					System.out.println("Failed to create 'tickets.ser' file.");
					return; // Return if the file creation failed
				}
			}

			try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file)))
			{
				while (true)
				{
					try
					{
						Ticket ticket = (Ticket) objectInputStream.readObject();
						tickets.add(ticket);
					}
					catch (EOFException e) {break;}
				}
			}
		}
		catch (IOException | ClassNotFoundException e) {System.out.println("Error loading tickets from file: " + e.getMessage());}
	}

	// Save Tickets stored in TicketsStorage.ser
	public void saveTickets()
	{
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("TicketsStorage.ser")))
		{
			for (Ticket ticket : tickets) {objectOutputStream.writeObject(ticket);}
		}
		catch (IOException e) {System.out.println("Error saving tickets to file: " + e.getMessage());}
	}
	
	
	// Load Leaderboard stored in LeaderboardStorage.ser
//	public void loadLeaderboard() {
//	    try {
//	        File file = new File("LeaderboardStorage.ser");
//	        if (file.exists()) {
//	            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
//	                leaderboard = (Map<String, Integer>) objectInputStream.readObject();
//	            }
//	        } else {
//	            System.out.println("Leaderboard file not found. Creating a new leaderboard.");
//	            // Initialize leaderboard from tickets if it's empty
//	            if (leaderboard.isEmpty()) {
//	                initializeLeaderboardFromTickets();
//	                saveLeaderboard(); // Save the initialized leaderboard
//	            }
//	        }
//	    } catch (IOException | ClassNotFoundException e) {
//	        System.out.println("Error loading leaderboard from file: " + e.getMessage());
//	    }
//	}
//
//	// Initialize leaderboard from tickets
//    private void initializeLeaderboardFromTickets() {
//        for (Ticket ticket : tickets) {
//            String key = ticket.getNickname() + "_" + ticket.getId();
//            if (!leaderboard.containsKey(key)) {
//                leaderboard.put(key, 0);
//            }
//        }
//        leaderboard.saveLeaderboard("LeaderboardStorage.ser");
//    }

    
    
    // Save Leaderboard stored in LeaderboardStorage.ser
//    public void saveLeaderboard() {
//        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("LeaderboardStorage.ser"))) {
//            objectOutputStream.writeObject(leaderboard);
//        } catch (IOException e) {
//            System.out.println("Error saving leaderboard to file: " + e.getMessage());
//        }
//    }
//
//    // Print the leaderboard
//    public void printLeaderboard() {
//        System.out.println("Leaderboard:");
//        for (Map.Entry<String, Integer> entry : leaderboard.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue() + " wins");
//        }
//    }
	

	public static void main(String args[]) {new Server();}
}