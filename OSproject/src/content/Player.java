package content;

import java.io.*;
import java.net.*;

public class Player extends Thread {
	
	private Socket clientSocket;
	private Server server;
	PrintWriter toUser;
	BufferedReader fromUser;
	private String nickname;
	private Ticket ticket;
	private Game game;
	private int points = 5; // Initial points for each player

	public Player(Socket clientSocket, Server server) {
		this.clientSocket = clientSocket;
		this.server = server;

		try {
			toUser = new PrintWriter(clientSocket.getOutputStream(), true);
			fromUser = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Error initializing I/O streams: " + e.getMessage());
		}
	}

	public void run() {
		try {
			String request = fromUser.readLine();

			if (request.startsWith("nickname")) {
				handleNickname(request);
			}

			if (request.startsWith("ticket")) {
				handleTicket(request);
			}

			handleGame();
		} catch (IOException e) {
			System.err.println("Error communicating with client: " + e.getMessage());
		}
	}

	private void handleNickname(String nicknameRequest) {
		String nickname = nicknameRequest.split(" ")[1];

		int maxId = 0;
		for (Ticket ticket : server.tickets) {
			int id = ticket.getId();
			if (id > maxId) {
				maxId = id;
			}
		}

		Ticket ticket = new Ticket(nickname, maxId + 1);
		server.tickets.add(ticket);
		server.saveTickets();

		toUser.println("Ticket issued: " + ticket.getTicket());
	}

	private void handleTicket(String ticketRequest) {
		String ticketData = ticketRequest.substring(7);
		Ticket existingTicket = server.findTicket(ticketData);

		if (existingTicket != null) {
			toUser.println("Welcome back, " + existingTicket.getNickname());
			this.nickname = existingTicket.getNickname();
			this.ticket = existingTicket;
		} else {
			toUser.println("Invalid ticket");
		}
	}

	private void handleGame() {
		toUser.println("Available games:");

		for (Game game : server.games) {
			toUser.println(
					"(" + game.getId() + ") " + game.getGameName() + " : " + game.getPlayers().size() + " players");
		}

		toUser.println("Enter the game ID you want to join, or 'new' to create a new game:");
		String input;
		try {
			input = fromUser.readLine();
			if (input == null) {
				System.err.println("Client disconnected");
				if (game != null) {
					game.removePlayer(this);
				}
			}

			if (input.equals("new")) {
				createNewGame();
			} else {
				int gameId = Integer.parseInt(input);
				joinGame(gameId);
			}
		} catch (IOException e) {
			System.err.println("Error reading user input: " + e.getMessage());
		}
	}

	private void createNewGame() {
		try {
			toUser.println("Enter the name for the new game:");
			String newGameName = fromUser.readLine();
			Game newGame = new Game(server.games.size() + 1, newGameName);
			server.games.add(newGame);
			game = newGame;
			toUser.println("You created a new game with ID " + newGame.getId());
			newGame.addPlayer(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void joinGame(int gameId) {
		Game targetGame = null;

		for (Game game : server.games) {
			if (game.getId() == gameId) {
				targetGame = game;
				break;
			}
		}

		if (targetGame == null) {
			toUser.println("Game not found.");
			return;
		}

		if (targetGame.getPlayers().size() >= 6) {
			toUser.println("Game is full, cannot join.");
			return;
		}

//		if (targetGame.gameInProgress) {
//			toUser.println("Game has already started, cannot join.");
//			return;
//		}

		toUser.println("You joined game " + gameId);
		game = targetGame;
		targetGame.addPlayer(this);
		System.out.println("Hi");
//		if (targetGame.getPlayers().size() >= 2) {
		targetGame.startGame();
//		}
	}

	public void playRound(int number) {
		if (game != null) {
			game.playRound(this, number);
		}
	}

	public void updatePoints(int points) {
		this.points += points;
		toUser.println("Yourcurrent points: " + this.points);
	}

	public String getNickname() {
		return nickname;
	}

	public int getPoints() {
		return points;
	}
}