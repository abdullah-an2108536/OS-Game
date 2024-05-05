package content;

import java.util.*;

public class Game {
    private int id;
    private String gameName;

    private List<Player> playingPlayers;
    private List<Player> waitingPlayers;

    int currentRound;

    private boolean gameInProgress;
    private Leaderboard leaderboard;

    private static final int MAX_PLAYERS = 6;
    private static final int MIN_PLAYERS_TO_START = 2;
    private static final int START_DELAY_SECONDS = 30;

    public int getId() {return id;}

    public String getGameName() {return gameName;}

    public List<Player> getPlayers() {return playingPlayers;}

    public Game(int id, String gameName, Leaderboard leaderboard)
    {
        this.id = id;
        this.gameName = gameName;
        this.leaderboard = leaderboard;

        this.playingPlayers = new ArrayList<>();
        this.waitingPlayers = new ArrayList<>();

        this.currentRound = 1;
        this.gameInProgress = false;
    }

    public synchronized void addPlayer(Player player)
    {
        if (gameInProgress || playingPlayers.size() >= MAX_PLAYERS) {waitingPlayers.add(player);}
        else
        {
            playingPlayers.add(player);
            if (playingPlayers.size() >= MIN_PLAYERS_TO_START)
            {
                if (!starting)
                {
                    starting = true;
                    Thread startCountdownThread = new Thread(() -> startCountdown());
                    startCountdownThread.start();
                }
            }
        }
        System.out.println(player.getNickname() + " added to the game.");
    }

    private boolean starting = false;

    private void startCountdown()
    {
        try
        {
            System.out.println("\n\nStarting the game in " + START_DELAY_SECONDS + " seconds...");
            Thread.sleep((START_DELAY_SECONDS-10) * 1000);
            System.out.println("Starting the game in 10 seconds...");
            Thread.sleep(10000);
        }
        catch (InterruptedException e) {e.printStackTrace();}

        synchronized (this)
        {
            if (playingPlayers.size() >= MIN_PLAYERS_TO_START)
            {
                gameInProgress = true;
                currentRound = 1;
                playRound();
            }
            else
            {
                System.out.println("Not enough players to start the game.");
                starting = false;
            }
        }
    }

    public synchronized void joinDuringStart(Player player)
    {
        if (starting && canJoin && !gameInProgress)
        {
            playingPlayers.add(player);
            System.out.println(player.getNickname() + " joined the game during starting phase.");
        }
    }

    public synchronized void removePlayer(Player player)
    {
        playingPlayers.remove(player);
        waitingPlayers.remove(player);
        
        if (playingPlayers.size() == 1)
        {
            Player winner = playingPlayers.get(0);
            System.out.println("Game over! " + winner.getNickname() + " wins!");
            gameInProgress = false;
        }
        if (playingPlayers.size() < MIN_PLAYERS_TO_START) {starting = false;}
    }

    public void startGame()
    {
        if (playingPlayers.size() >= MIN_PLAYERS_TO_START)
        {
            if (!starting)
            {
                starting = true;
                Thread startCountdownThread = new Thread(() -> startCountdown());
                startCountdownThread.start();
            }
        }
        else {System.out.println("Not enough players to start the game.");}
    }

    public void playRound()
    {    	
        if (playingPlayers.size() < MIN_PLAYERS_TO_START)
        {
            System.out.println("Not enough players to start the round.");
            if (playingPlayers.size() == 1)
            {
                Player winner = playingPlayers.get(0);
                System.out.println("Game over! " + winner.getNickname() + " wins!");
                gameInProgress = false;
            }
            return;
        }

        List<Player> roundWinners = new ArrayList<>();
        List<Player> roundLosers = new ArrayList<>();

        Map<Player, Integer> playerGuesses = new HashMap<>();
        
        // Check if at least every player except one has one point
        int playersWithOnePoint = 0;
        for (Player player : playingPlayers)
        {
        	if (player.getPoints() == 1) {playersWithOnePoint++;}
        	
            System.out.println("\nGetting guess from " + player.getNickname());
            int playerGuess = player.getPlayerGuess();
            playerGuesses.put(player, playerGuess);
        }
        
        Player winner=playingPlayers.get(0);
        int targetNumber=0;
        //if the number of remaining players are 2, each has 1 point only
        if (playersWithOnePoint >= playingPlayers.size() - 1)
        {
        	
        	int sum = 0;
        	for (int guess : playerGuesses.values()) {sum += guess;}

        	double average = (double) sum / playingPlayers.size();
        	targetNumber = (int) (average * 0.6666);

        	
        	int closestDifference = Integer.MAX_VALUE;

        	for (Map.Entry<Player, Integer> entry : playerGuesses.entrySet())
        	{
        		Player player = entry.getKey();
        		int playerGuess = entry.getValue();
        		
        		if (playerGuess == 0)
        		{
        			player.sendMessage("You entered 0! How could you!");
        			player.setPoints(1);
        			roundLosers.add(player);
        		}
        		else
        		{
        			int difference = Math.abs(playerGuess - targetNumber);

            		if (difference < closestDifference)
            		{
            			closestDifference = difference;
            			winner = player;
            		}
        		}
        	}

        	for (Map.Entry<Player, Integer> entry : playerGuesses.entrySet())
        	{
        		Player player = entry.getKey();

        		if (player == winner) {roundWinners.add(player);}
        		else
        		{
        			player.updatePoints(-1);
        			roundLosers.add(player);
        			
        		}
        	}
        }
        
        else {
        	
        	int sum = 0;
        	for (int guess : playerGuesses.values()) {sum += guess;}

        	double average = (double) sum / playingPlayers.size();
        	targetNumber = (int) (average * 0.6666);

        	
        	int closestDifference = Integer.MAX_VALUE;

        	for (Map.Entry<Player, Integer> entry : playerGuesses.entrySet())
        	{
        		Player player = entry.getKey();
        		int playerGuess = entry.getValue();
        		int difference = Math.abs(playerGuess - targetNumber);

        		if (difference < closestDifference)
        		{
        			closestDifference = difference;
        			winner = player;
        		}
        	}

        	for (Map.Entry<Player, Integer> entry : playerGuesses.entrySet())
        	{
        		Player player = entry.getKey();

        		if (player == winner) {roundWinners.add(player);}
        		else
        		{
        			player.updatePoints(-1);
        			roundLosers.add(player);
        			
        		}
        	}
        }
        
        Iterator<Player> iterator = playingPlayers.iterator();
    	
    	while (iterator.hasNext())
    	{
    	    Player p = iterator.next();
    	    if (p.getPoints() <= 0)
    	    {
    	        iterator.remove();
    	        waitingPlayers.add(p);
    	        p.sendMessage("\n\nYou are now a spectator [DO NOT INPUT]\n\n");
    	    }
    	}

        String roundOutput = "\nRound: " + currentRound + "\tTarget number was: " + targetNumber + ".\n";
        roundOutput += "Winner: " + winner.getNickname() + "[" + winner.getPoints() + "].\nLosers: ";

        for (Player loser : roundLosers) {roundOutput += loser.getNickname() + "[" + loser.getPoints() + "], ";}

        System.out.println(roundOutput+"\n");
        
        sendAnnouncement(playingPlayers, roundOutput);
        sendAnnouncement(waitingPlayers, roundOutput);

//        if (playingPlayers.size() == 1)
//        {
//            Player winnerPlayer = playingPlayers.get(0);
//            String message = "\n\nGame over!\t" + winnerPlayer.getNickname() + " wins!";
//            System.out.println(message);
//            
//            sendAnnouncement(playingPlayers, message);
//            sendAnnouncement(waitingPlayers, message);
//            
//            gameInProgress = false;
//        }
        
        checkEndGame();

        if (gameInProgress)
        {
            currentRound++;
            playRound();
        }
    }

    private boolean canJoin = true;

    private void checkEndGame()
    {
    	
    	if (playingPlayers.size() == 1)
    	{
            Player winnerPlayer = playingPlayers.get(0);
            leaderboard.incrementWins(winnerPlayer.getNickname(), winnerPlayer.getID());
            String message = "\n\nGame over!\t" + winnerPlayer.getNickname() + " wins!";
            System.out.println(message);
            
            sendAnnouncement(playingPlayers, message);
            sendAnnouncement(waitingPlayers, message);
            
            // Remove all players from the game
            for (Player p : playingPlayers)
            {
            	p.sendMessage("The game has ended, goodbye!");
            	p.terminateConnection();
            }
            playingPlayers.clear();
            
            for (Player p : waitingPlayers)
            {
            	p.sendMessage("The game has ended, goodbye!");
            	p.terminateConnection();
            }
            waitingPlayers.clear();
            
            gameInProgress = false;
    	}
    }
    
    private void sendAnnouncement(List<Player> players, String message)
    { for (Player p : players) {p.sendMessage(message);} }
}
