package content;

import java.io.*;
import java.net.*;

public class Client {
	public static void main(String[] args) {
		try {

			Socket server = new Socket("localhost", 13337);

			PrintWriter toServer = new PrintWriter(server.getOutputStream(), true);
			BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));

			System.out.println("Welcome! Let's establish connection to the Game Server");
			System.out.println("New Player: Enter 1 or Returning Player: Enter 2");

			int userInput = Integer.parseInt(fromUser.readLine());

			if (userInput == 1) {

				System.out.println("Please provide your nickname");
				String nickname = fromUser.readLine();
				toServer.println("nickname " + nickname);
				System.out.println(fromServer.readLine());

			} else if (userInput == 2) {

				System.out.println("Please provide your ticket in the format: \"nickname id\"");
				String ticket = fromUser.readLine();
				toServer.println("ticket " + ticket);

				String response = fromServer.readLine();
				if (response.equals("Invalid ticket"))
					System.out.println("Invalid Ticket (Terminating Client Class)");
					System.exit(0);

			} else {
				System.out.println("Invalid Input");
				System.exit(0);
			}

			System.out.println(fromServer.readLine());

		} catch (IOException e) {
			System.out.println("IO related error: " + e);
		}
	}
}
