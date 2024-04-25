
package content;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
	public ArrayList<Game> games = new ArrayList<>();
	public ArrayList<Player> players = new ArrayList<>();
	public ArrayList<Ticket> tickets = new ArrayList<>();
	
	public Map<String, Integer> leaderboard = new HashMap<>();

	private PrintWriter toClient;
	private BufferedReader fromClient;

	public Server() {
		try {
			
			loadTickets();

			// TEMPORARY (print tickets for testing purposes)
			System.out.println("Tickets available :" + tickets.size());
			for (Ticket t : tickets) {
				System.out.println(t.toString());
			}

			// Create default empty games
			Game defaultGame = new Game(0, "Default Game");
			Game defaultGame1 = new Game(1, "Test Game");

			games.add(defaultGame);
			games.add(defaultGame1);

			ServerSocket server = new ServerSocket(13337);
			System.out.println("Server is up, waiting for Connections on port " + server.getLocalPort());

			while (true) {
				Socket client = server.accept();

				// Create a new Player Thread
				Player player = new Player(client, this);
				players.add(player);
				player.start();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// Check if the ticket is present in the tickets ArrayList
	public Ticket findTicket(String ticket) {
		String[] parts = ticket.split(" ");

		if (parts.length == 2) {
			String nickname = parts[0];
			int id = Integer.parseInt(parts[1]);

			for (Ticket t : tickets) {
				if (t.getNickname().equals(nickname) && t.getId() == id) {
					return t;
				}
			}
		}
		return null;
	}

	// Load Tickets stored in TicketsStorage.ser
	public void loadTickets() {
		try {
			File file = new File("TicketsStorage.ser");
			if (!file.exists()) {
				System.out.println("hi");
				boolean created = file.createNewFile();
				if (!created) {
					System.out.println("Failed to create 'tickets.ser' file.");
					return; // Return if the file creation failed
				}
			}

			try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
				while (true) {
					try {
						Ticket ticket = (Ticket) objectInputStream.readObject();
						tickets.add(ticket);
					} catch (EOFException e) {
						break;
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error loading tickets from file: " + e.getMessage());
		}
	}

	// Save Tickets stored in TicketsStorage.ser
	public void saveTickets() {
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				new FileOutputStream("TicketsStorage.ser"))) {
			for (Ticket ticket : tickets) {
				objectOutputStream.writeObject(ticket);
			}
		} catch (IOException e) {
			System.out.println("Error saving tickets to file: " + e.getMessage());
		}
	}

	public static void main(String args[]) {
		new Server();
	}
}