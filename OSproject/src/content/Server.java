package content;

import java.io.*;
import java.net.*;
import java.util.*;

// Handles incoming connections from clients, manages games, and maintains a leaderboard

public class Server {

	// Keep games, players, and tickets in ArrayLists
	private ArrayList<Game> games;
	private ArrayList<Player> players;
	private ArrayList<Ticket> tickets = new ArrayList<>();

	// store Leaderboard ("PlayerName",wins)
	private Map<String, Integer> leaderboard;

	PrintWriter to_client;
	BufferedReader from_client;

	public Server() {
		try {

			@SuppressWarnings("resource")
			ServerSocket server = new ServerSocket(13337);
			System.out.println("Server is up, waiting for Connections on port " + server.getLocalPort());

			for (;;) {
				Socket client = server.accept();

				to_client = new PrintWriter(client.getOutputStream(), true);
				from_client = new BufferedReader(new InputStreamReader(client.getInputStream()));

				// handle client differently based on if they are providing ticket or nickname

				String request = from_client.readLine();

				if (request.startsWith("nickname")) {
					handleNickname(client, request);
				} else if (request.startsWith("ticket")) {
					handleTicket(client, request);
				}
				handleGame(client);

			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// If client provides a nickname, assign a new Ticket
	private void handleNickname(Socket client, String nickname) {

		try {
			to_client = new PrintWriter(client.getOutputStream(), true);
			from_client = new BufferedReader(new InputStreamReader(client.getInputStream()));

			// create new ticket based on client nickname
			Ticket ticket = new Ticket(nickname.split(" ")[1]);

			// add ticket to the tickets ArrayList
			tickets.add(ticket);

			to_client.println("Ticket issued: " + ticket.getTicket());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Client Provides previously issued Ticket
	private void handleTicket(Socket client, String ticket) {

		try {

			to_client = new PrintWriter(client.getOutputStream(), true);
			from_client = new BufferedReader(new InputStreamReader(client.getInputStream()));

			ticket = ticket.split(" ")[1] + " " + ticket.split(" ")[2];
			System.out.println(ticket);

			Ticket existingTicket = findTicket(ticket);

			if (existingTicket != null) {
				to_client.println("Welcome back, " + existingTicket.getNickname());
			} else {
				to_client.println("Invalid ticket");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleGame(Socket client) {

		try {
			to_client = new PrintWriter(client.getOutputStream(), true);
			from_client = new BufferedReader(new InputStreamReader(client.getInputStream()));

			to_client.println("You are in the Game");

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

	public static void main(String args[]) {
		new Server();
	}
}
