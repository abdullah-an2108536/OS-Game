package content;

import java.io.*;
import java.net.*;

public class Server {
	
	// Handles incoming connections from clients, manages games, and maintains a leaderboard

	public Server() {
		try {

			@SuppressWarnings("resource")
			ServerSocket server = new ServerSocket(13337);
			System.out.println("Server is up, waiting for Connections on port " + server.getLocalPort());

			Socket client;

			// for now, Game.java is acting as ServiceServer, in order to test connectivity
			// and exact output
			// later change it to ServiceServer itself ...
			for (;;) {

				client = server.accept();
				new Game(client).start();
			}

		} catch (IOException ioe) {
			System.out.println("I/O Error related:" + ioe);
		}
	}

	public static void main(String args[]) {
		new Server();
	}
}