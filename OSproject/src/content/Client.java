package content;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    @SuppressWarnings("resource")
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
                if (response.equals("Invalid ticket")) {
                    System.out.println("Invalid Ticket (Terminating Client Class)");
                    System.exit(0);
                }
                System.out.println(response);

            } else {
                System.out.println("Invalid Input");
                System.exit(0);
            }

            // Welcome Message
            System.out.println(fromServer.readLine());

            // Player chooses a game to Join or creates a new Game
            System.out.println(fromServer.readLine());
            System.out.println(fromServer.readLine());
            System.out.println(fromServer.readLine());

            String gameInput = fromUser.readLine();

            if (gameInput.equals("new")) {
                toServer.println("new");

                System.out.println("Enter name of the New Game");
                toServer.println(fromUser.readLine());

                System.out.println(fromServer.readLine());
            } else {
                toServer.println(gameInput);
//                System.out.println(fromServer.readLine());
            }

            
            while (true) {
            	
                String message = fromServer.readLine(); // ask for guess
                
                if (message == null || message.equals("Game over! No winners.") || message.equals("You Lost (Points = 0)")) {
                    System.out.println("Game has ended.");
                    break;
                }
                
                System.out.println(message);
                toServer.println(fromUser.readLine());
                System.out.println(fromServer.readLine()); // Round Output
            }


        } catch (IOException e) {
            System.out.println("IO related error: " + e);
        }
    }
}
