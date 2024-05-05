// Client.java
package content;

import java.io.*;
import java.net.*;
//import java.util.Scanner;

public class Client
{

    @SuppressWarnings("resource")
    public static void main(String[] args)
    {
        try
        {
            Socket server = new Socket("localhost", 13337);

            PrintWriter toServer = new PrintWriter(server.getOutputStream(), true);
            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));

            System.out.println("Welcome! Let's establish connection to the Game Server\n");
            System.out.println("New Player [Enter 1]\nReturning Player [Enter 2]\nEnter: ");

            int userInput = Integer.parseInt(fromUser.readLine());

            if (userInput == 1)
            {

                System.out.println("Please provide your nickname: ");
                String nickname = fromUser.readLine();
                toServer.println("nickname " + nickname);
                System.out.println(fromServer.readLine());

            }
            else if (userInput == 2)
            {

                System.out.println("Please provide your ticket in the format: \"nickname id\": ");
                String ticket = fromUser.readLine();
                toServer.println("ticket " + ticket);

                String response = fromServer.readLine();
                if (response.equals("Invalid ticket"))
                {
                    System.out.println("Invalid Ticket (Terminating Client Class)");
                    System.exit(0);
                    return;
                }
                System.out.println(response);

            }
            else
            {
                System.out.println("Invalid Input");
                System.exit(0);
            }

            // Welcome Message
            System.out.println(fromServer.readLine());

            // Player chooses a game to Join or creates a new Game
            String message = "";
            while ( !(message.startsWith("Enter the game ID you want to join")) )
            {
            	message = fromServer.readLine();
            	System.out.println(message);
            }

            String gameInput = fromUser.readLine();

            if (gameInput.equals("new"))
            {
                toServer.println("new");

                System.out.println("Enter name of the New Game");
                toServer.println(fromUser.readLine());

                System.out.println(fromServer.readLine());
            }
            else {toServer.println(gameInput);}

            while (true)
            {
                message = fromServer.readLine(); // receive round results or prompt for next action
                if (message == null || message.equals("Game over! No winners."))
                {
                    System.out.println("Game has ended.");
                    break;
                }
                System.out.println(message);
                
                // Check if the server is asking for user input
                if (message.startsWith("Leaderboard:")) {
                	// Print the leaderboard
                    System.out.println(message);
                    
                    // Continue reading messages until there is no more content
                    while (!message.isEmpty())
                    {
                        System.out.println(message);
                    }
                } else if (message.endsWith("Enter your guess (0-100):")) {
                    // Prompt for user input
                    toServer.println(fromUser.readLine());
                }
            }


        }
        catch (IOException e) {System.out.println("IO related error: " + e);}
    }
}
