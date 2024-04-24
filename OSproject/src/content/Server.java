package content;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	private ArrayList<Game> games;
	private ArrayList<Player> players;
	private ArrayList<Ticket> tickets = new ArrayList<>();
	private Map<String, Integer> leaderboard;

	private PrintWriter toClient;
	private BufferedReader fromClient;

	public Server() {
		try {
			loadTickets();
			
			System.out.println("Tickets available :" + tickets.size());
			for (Ticket t : tickets) {
				System.out.println(t.toString());
			}

			ServerSocket server = new ServerSocket(13337);
			System.out.println("Server is up, waiting for Connections on port " + server.getLocalPort());

			while (true) {
				Socket client = server.accept();
				toClient = new PrintWriter(client.getOutputStream(), true);
				fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

				String request = fromClient.readLine();

				if (request.startsWith("nickname")) {
					handleNickname(client, request);
				} else if (request.startsWith("ticket")) {
					handleTicket(client, request);
				}
				handleGame(client);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private void handleNickname(Socket client, String nicknameRequest) {
		try {
			toClient = new PrintWriter(client.getOutputStream(), true);
			fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

			String nickname = nicknameRequest.split(" ")[1];

			int maxId = 0;

			for (Ticket ticket : tickets) {
				int id = ticket.getId();
				if (id > maxId) {
					maxId = id;
				}
			}

			Ticket ticket = new Ticket(nickname, maxId + 1);
			tickets.add(ticket);
			saveTickets();

			toClient.println("Ticket issued: " + ticket.getTicket());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleTicket(Socket client, String ticketRequest) {
		try {
			toClient = new PrintWriter(client.getOutputStream(), true);
			fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

			String ticketData = ticketRequest.substring(7);
			Ticket existingTicket = findTicket(ticketData);

			if (existingTicket != null) {
				toClient.println("Welcome back, " + existingTicket.getNickname());
			} else {
				toClient.println("Invalid ticket");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleGame(Socket client) {
		try {
			toClient = new PrintWriter(client.getOutputStream(), true);
			fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

			toClient.println("You are in the Game");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Ticket findTicket(String ticket) {
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

	private void loadTickets() {
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

	private void saveTickets() {
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("TicketsStorage.ser"))) {
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
