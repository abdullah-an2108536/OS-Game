
package content;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server
{
	
	public ArrayList<Game> games = new ArrayList<>();
	public ArrayList<Player> players = new ArrayList<>();
	public ArrayList<Ticket> tickets = new ArrayList<>();
	
	private Timer pingTimer; //new change
    private static final int PING_TIME= 6000; // //new change

	
	public Map<String, Integer> leaderboard = new HashMap<>();

	//private PrintWriter toClient;
	private BufferedReader fromClient;

	public Server()
	{
		try
		{
			
			loadTickets();

			// TEMPORARY (print tickets for testing purposes)
			System.out.println("Tickets available :" + tickets.size());
			for (Ticket t : tickets) {System.out.println(t.toString());}

			// Create default empty games
			Game defaultGame = new Game(0, "Default Game");
			Game defaultGame1 = new Game(1, "Test Game");

			games.add(defaultGame);
			games.add(defaultGame1);

			ServerSocket server = new ServerSocket(13337);
			System.out.println("Server is up, waiting for Connections on port " + server.getLocalPort());

			 pingTimer = new Timer(); //new change
	         pingTimer.schedule(new PingTask(), 1000, PING_TIME); //new change
	         
			while (true)
			{
				Socket client = server.accept();
				

				// Create a new Player Thread
				Player player = new Player(client, this);
				players.add(player);
				player.start();
				
				Thread clientThread = new Thread(() -> handleClient(client,player)); //new change
                clientThread.start(); //new change
			}
		}
		catch (IOException e) {System.out.println(e);}
	}
	
	//new change
	 private void handleClient(Socket client,Player player) {
	        // Implementation to handle client connections
		 try {
		        fromClient=new BufferedReader(new InputStreamReader(client.getInputStream()));
		        
		        
		        //toClient = new PrintWriter(client.getOutputStream(), true); //Do we need this? //new change
		        
		 
		        String feed;
		        do {
		          feed = fromClient.readLine();
		          if (feed.equals("ping")) {
		        	System.out.println(player.getName()+" is still present !");
		            }
		        } while (feed != null);
		        player.terminateConnection();
		        players.remove(player);
		        
		      } catch (IOException e) {
		        e.printStackTrace();
		      }
		    }
		 
	 
	//new change
	 private class PingTask extends TimerTask {
	        @Override
	        public void run() {
	            // Send "ping" messages to players in the waitingPlayers list
	            for (Player player : players) {
	            	
	                player.sendMessage("Type ping if you are still there");
	            }
	        }
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
				System.out.println("hi");
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

	public static void main(String args[]) {new Server();}
}