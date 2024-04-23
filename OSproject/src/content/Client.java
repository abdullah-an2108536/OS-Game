package content;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

	public Client() {

	}

	@SuppressWarnings("resource")
	public static void main(String args[]) {
		try {
			Socket server = new Socket("localhost", 13337);

			PrintWriter to_server = new PrintWriter(server.getOutputStream(), true);
			BufferedReader from_user = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader from_server = new BufferedReader(new InputStreamReader(server.getInputStream()));

			System.out.println("Welcome ! Let's establish connection to the Game Server");
			System.out.println("New Player : Enter 1  or  Returning Player : Enter 2");

			// New Player
			if (Integer.parseInt(from_user.readLine()) == 1) {

				System.out.println("Please provide your nickname");

				String nickname = from_user.readLine();
				to_server.println("nickname " + nickname);

				// Receive the Connection Establishment Response from the server
				String response = from_server.readLine();
				System.out.println(response);

			}
			// Returning Player
			else if (Integer.parseInt(from_user.readLine()) == 2) {

				System.out.println("Please provide your ticket in the format : \"nickname id\"");

				String ticket = from_user.readLine();
				to_server.println("ticket " + ticket);

				// Send the ticket to the server
				to_server.println("ticket " + "your_ticket");

				// Receive the Connection Establishment Response from the server
				String response = from_server.readLine();
				System.out.println(response);

			} else {
				System.out.println("Invalid Input");
				System.exit(0);
			}

			String response = from_server.readLine();
			System.out.println(response);

		} catch (IOException ioe) {
			System.out.println("IO related error " + ioe);
		}
	}
}