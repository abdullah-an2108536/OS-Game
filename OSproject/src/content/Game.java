package content;

import java.util.*;

public class Game {
    private int id;
    private String gameName;

    private List<Player> playingPlayers;
    private List<Player> waitingPlayers;

    int currentRound;

    private boolean gameInProgress;

    private static final int MAX_PLAYERS = 6;
    private static final int MIN_PLAYERS_TO_START = 2;

    public int getId() {return id;}

    public String getGameName() {return gameName;}

    public List<Player> getPlayers() {return playingPlayers;}

    public Game(int id, String gameName)
    {
        this.id = id;
        this.gameName = gameName;

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
            if (playingPlayers.size() >= MIN_PLAYERS_TO_START) {startGame();}
        }
        System.out.println(player.getNickname() + " added to the game.");
    }

    public void startGame()
    {
        if (playingPlayers.size() >= MIN_PLAYERS_TO_START)
        {
            gameInProgress = true;
            currentRound = 1;
            playRound();
        }
        else{System.out.println("Not enough players to start the game.");}
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
    }

    public void playRound()
    {
        if (playingPlayers.size() < MIN_PLAYERS_TO_START)
        {
            System.out.println("Not enough players to start the round.");
            // Check if there are enough players to continue the game
            if (playingPlayers.size() == 1)
            {
                Player winner = playingPlayers.get(0);
                System.out.println("Game over! " + winner.getNickname() + " wins!");
                gameInProgress = false;
            }
            return;
        }

        // clear roundWinners and roundLosers ArrayLists
        List<Player> roundWinners = new ArrayList<>();
        List<Player> roundLosers = new ArrayList<>();

        // Get the guess from each player
        Map<Player, Integer> playerGuesses = new HashMap<>();
        for (Player player : playingPlayers)
        {
            System.out.println("Getting guess from " + player.getNickname());
            int playerGuess = player.getPlayerGuess();
            playerGuesses.put(player, playerGuess);
        }

        // Calculate the target number (two-thirds of the average)
        int sum = 0;
        for (int guess : playerGuesses.values()) {sum += guess;}
        
        double average = (double) sum / playingPlayers.size();
        int targetNumber = (int) (average * 0.6666);

        // Determine the closest guess to the target number
        Player winner = null;
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

        // Set winner and update points for losers
        for (Map.Entry<Player, Integer> entry : playerGuesses.entrySet())
        {
            Player player = entry.getKey();
            if (player == winner) {roundWinners.add(player);}
            else
            {
                roundLosers.add(player);
                player.updatePoints(-1);
            }
        }

        // Print the round outcome
        String roundOutput = "Round " + currentRound + ": Target number is " + targetNumber + ". ";
        roundOutput += "Winner: " + winner.getNickname() + ". Losers: ";
        
        for (Player loser : roundLosers) {roundOutput += loser.getNickname() + ", ";}
        
        System.out.println(roundOutput);

        // Check for game end condition
        checkEndGame();

        // Start next round if game is still in progress
        if (gameInProgress)
        {
            currentRound++;
            playRound();
        }
    }

    private void checkEndGame()
    {
        if (playingPlayers.size() == 1)
        {
            Player winner = playingPlayers.get(0);
            System.out.println("Game over! " + winner.getNickname() + " wins!");
            gameInProgress = false;
        }
    }
}
