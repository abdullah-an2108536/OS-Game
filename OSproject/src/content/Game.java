
package content;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Manages an active game, including game rules and dynamics.

public class Game /*extends Thread*/ {

	Socket client;
	
	private ArrayList<Player> players;

	public Game(Socket c) {
		client = c;
	}

	public Game(int i, String string) {
		// TODO Auto-generated constructor stub
	}

	public void run() {

//      List<Integer> players = new ArrayList<>();
//      List<Integer> points = new ArrayList<>();

		List<Integer> guesses = new ArrayList<>();

		try {
			PrintWriter output = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			Scanner scanner = new Scanner(System.in); // to take guesses from the players
			for (int i = 0; i < 4; i++) {
				System.out.print("Player " + (i + 1) + " enter your guess: ");
				int guess = scanner.nextInt();
				guesses.add(guess);
			}

			// calculate the average and target
			double sum = 0;
			for (int guess : guesses) {
				sum += guess;
			}
			double average = sum / guesses.size();
			double target = (2.0 / 3.0) * average;

			// list to maintain winners
			List<Integer> winners = new ArrayList<>();

			double minDifference = Double.MAX_VALUE;

			for (int guess : guesses) {
				double difference = Math.abs(target - guess);
				if (difference < minDifference) {
					minDifference = difference;
					winners.clear();
					winners.add(guess);
				} else if (difference == minDifference) {
					winners.add(guess);
				}
			}
			scanner.close();

			// list the winners who are close to 2/3 average...
			System.out.println("The average is: " + average);
			System.out.println("The target is: " + target);
			System.out.println("The closest guesses to two-thirds of the average are: " + winners);

			output.println("The average is: " + average); // this sends the result back to the player
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Ticket> getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPlayer(Player player) {
		// TODO Auto-generated method stub
		
	}

	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	public void playRound(Player player, int number) {
		// TODO Auto-generated method stub
		
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void removePlayer(Player player) {
		// TODO Auto-generated method stub
		
	}

	public String getGameName() {
		// TODO Auto-generated method stub
		return null;
	}
}
