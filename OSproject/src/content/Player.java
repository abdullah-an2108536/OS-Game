package content;


import java.io.*;
import java.net.*;


public class Player extends Thread
{

    private Socket clientSocket;
    private Server server;

    PrintWriter toUser;
    BufferedReader fromUser;

    private String nickname;

    // Ticket for this player
    private Ticket ticket;
    private Leaderboard leaderboard;

    private Game game;
    
    private int points = 5; // Initial points for each player

    // Player Thread will have access to the Client and everything in the Server
    public Player(Socket clientSocket, Server server)
    {

        this.clientSocket = clientSocket;
        this.server = server;
        
        this.leaderboard = Leaderboard.loadLeaderboard("LeaderboardStorage.ser");

        try
        {
            toUser = new PrintWriter( clientSocket.getOutputStream(), true );
            fromUser = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()) );
            toUser.println(leaderboard.toString2());
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public void run()
    {
        try
        {
        	
            String request = fromUser.readLine();

            // New Client
            if (request.startsWith("nickname")) {handleNickname(request);}

            // Returning Client who provides a ticket
            if (request.startsWith("ticket")) {handleTicket(request);}

            // The Client has logged in (has a ticket)
            handleGame();

            // The Client is in a Game

//          System.out.println("Before adding Player in the game");

            game.addPlayer(this);
//          System.out.println("After calling game.addPlayer()");
//          if (targetGame.getPlayers().size() >= 2) {
            game.startGame();

        }
        catch (IOException e) {System.err.println("Error communicating with client: " + e.getMessage());}
    }

    // Create new Ticket for the new client who provided a nickname
    private void handleNickname(String nicknameRequest)
    {
        String nickname = nicknameRequest.split(" ")[1];

        int maxId = 0;
        for (Ticket ticket : server.tickets)
        {
            int id = ticket.getId();
            if (id > maxId) {maxId = id;}
        }
        
     // Increment the maxId within synchronized block to ensure atomicity
        synchronized (this) {
            Ticket ticket = new Ticket(nickname, maxId + 1);
            server.tickets.add(ticket);
            this.ticket = ticket;
            server.saveTickets();
            toUser.println("Ticket issued: " + ticket.getTicket());
        }
    }

    // Check if Ticket is Valid
    private void handleTicket(String ticketRequest)
    {
        String ticketData = ticketRequest.substring(7);

        Ticket existingTicket = server.findTicket(ticketData);

        if (existingTicket != null)
        {
            toUser.println("Welcome back, " + existingTicket.getNickname());
            this.nickname = existingTicket.getNickname();

            this.ticket = existingTicket;
        }
        else {toUser.println("Invalid ticket");}
    }

    public void handleGame()
    {
    	//toUser.println(leaderboard.toString());
    	

        // Display Available Games to the Client
        toUser.println("Available games:");

        for (Game game : server.games)
        {toUser.println("(" + game.getId() + ") " + game.getGameName() + " : " + game.getPlayers().size() + " players");}

        toUser.println("Enter the game ID you want to join, or 'new' to create a new game:");
        String input;

        // Client can create a new Game or join an existing Game
        try
        {
            input = fromUser.readLine();
//            if (input == null)
//            {
//                System.err.println("Client disconnected");
//                if (game != null) {game.removePlayer(this);}
//            }

            if (input.equals("new")) {createNewGame();}
            else
            {
                int gameId = Integer.parseInt(input);
                joinGame(gameId);
            }
        }
        catch (IOException e) {System.err.println("Error reading user input: " + e.getMessage());}
    }

    // Create new game after getting details from the client
    private void createNewGame()
    {
        try
        {
            toUser.println("Enter the name for the new game:");
            String newGameName = fromUser.readLine();   
            synchronized (server.games) {
                // Increment the game ID based on the size of the games list
                int gameId = server.games.size() + 1;
                Game newGame = new Game(gameId, newGameName, leaderboard);
                server.games.add(newGame);
                game = newGame;
                toUser.println("You created a new game with ID " + newGame.getId());
                joinGame(game.getId());
            }
        }
        catch (IOException e)
        {e.printStackTrace();}
    }

    // Join Game
    private void joinGame(int gameId)
    {
        Game targetGame = null;
        
//        if (gameId == (-1)) {handleGame(); return;}
//        else
//        {
	        // Find game in the ArrayList
	        for (Game game : server.games)
	        {
	            if (game.getId() == gameId)
	            {
	                targetGame = game;
	                break;
	            }
	        }
	
	        if (targetGame == null)
	        {
	            toUser.println("Game not found.");
	            handleGame();
	        }
	
	        // Make sure game is not full (Probably should move this code to the Game class)
	        if ( (targetGame.getPlayers().size() >= 6) || (targetGame.getGameInProgress()))
	        {
	            toUser.println("\n\nGame is full, cannot join.\n\n");
	            handleGame();
	        }
	
	//      if (targetGame.gameInProgress) {
	//          toUser.println("Game has already started, cannot join.");
	//          return;
	//      }
	
	//      toUser.println("You joined game " + gameId);
	        game = targetGame;
	//      }
        }
    //}

    // Increment or Decrement the Player Points based on Round Result
    public void updatePoints(int points)
    {
        this.points += points;
//      toUser.println("Your current points: " + this.points);
        
        
        if(this.points<=0)
        {
            toUser.println("You lost all your points...");
            //game.getPlayers().remove(this);
        }
    }

    public String getNickname() {return nickname;}
    
    public int getID() {return ticket.getID();}

    public int getPoints() {return points;}
    
    public void setPoints(int points) {this.points = points;}

    public int getPlayerGuess()
    {
        try
        {

            toUser.println("Current Round : " + game.currentRound+" Enter your guess (0-100):");
            
            String guessInput = fromUser.readLine();

            int guess = Integer.parseInt(guessInput);
            System.out.println("guess input :"+guess);
            return guess;

        }
        catch (Exception e) {System.err.println("Error reading user input: " + e.getMessage());}

        return 0;

    }
    
    // Method to terminate player connection
    public void terminateConnection()
    {
        try
        {
            // Close the PrintWriter
            if (toUser != null) {toUser.close();}
            
            // Close the socket
            if (clientSocket != null && !clientSocket.isClosed()) {clientSocket.close();}
        }
        catch (IOException e) {e.printStackTrace();}
    }
    
    // Method to send a message to the player
    public void sendMessage(String message)
    {
    	toUser.flush();
    	toUser.println(message);
    }
    
    // Method to send the leaderboard to the player
    public void sendLeaderboard() {
        if (server != null && server.leaderboard != null) {
            toUser.println(server.leaderboard.toString());
        }
    }
}
