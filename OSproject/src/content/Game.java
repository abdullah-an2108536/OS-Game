package content;

import java.util.*;

public class Game {
    private int id;
    private String gameName;

    private List<Player> playingPlayers;
    private List<Player> waitingPlayers;

    private List<Player> roundWinners;
    private List<Player> roundLosers;

    int currentRound;

    private boolean gameInProgress;
    private boolean roundInProgress;

    public int getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public List<Player> getPlayers() {
        return playingPlayers;
    }

    public Game(int id, String gameName) {
        this.id = id;
        this.gameName = gameName;

        this.playingPlayers = new ArrayList<>();
        this.waitingPlayers = new ArrayList<>();

        this.currentRound = 1;
        this.gameInProgress = false;
    }

    public void addPlayer(Player player) {
        if (gameInProgress) {
            waitingPlayers.add(player);
        } else {
            playingPlayers.add(player);
            startGame();
        }

        System.out.println(player.getName() + "Added to waiting Players");
    }

    public void startGame() {
        gameInProgress = true;


        currentRound = 1;
        playRound();
    }

    public void removePlayer(Player player) {
        playingPlayers.remove(player);
    }

    public void playRound() {

        int playerCount = playingPlayers.size();
        if (playerCount == 0) {
            System.out.println("No players in the game.");
            return;
        }

        // clear roundWinners ArrayList
        roundWinners = new ArrayList<>();
        roundLosers = new ArrayList<>();
        
        
        

        // Get the guess from each player
        Map<Player, Integer> playerGuesses = new HashMap<>();
        for (Player player : playingPlayers) {
        	System.out.println("getting guess");
            int playerGuess = player.getPlayerGuess();
            playerGuesses.put(player, playerGuess);
//			System.out.println(player.toString()+playerGuess);
        }

        // Calculate the target number (two-thirds of the average)
        int sum = 22; // FOR TESTING PURPOSES WITH ONE PLAYER THE SUM is 22
        for (int guess : playerGuesses.values()) {
            sum += guess;
//            System.out.println("sum + =" + sum);
        }

        double average = (double) sum / playerCount;
        int targetNumber = (int) (average * 0.6666);

        // Determine the outcome of the round
        List<Player> winners = new ArrayList<>();
        int minDifference = Integer.MAX_VALUE;

        for (Map.Entry<Player, Integer> entry : playerGuesses.entrySet()) {

            Player player = entry.getKey();
            int playerGuess = entry.getValue();

            int difference = Math.abs(playerGuess - targetNumber);

            if (difference < minDifference) {
                minDifference = difference;
                winners.clear();
                winners.add(player);
            } else if (difference == minDifference) {
                winners.add(player);
            }

            System.out.println("Player " + player.getNickname() + "'s points: " + player.getPoints());
        }

        // Add winners to the roundWinners list
        roundWinners.addAll(winners);

        // Print the round outcome
        String roundOutput = "Round " + currentRound + ": ";
        for (Map.Entry<Player, Integer> entry : playerGuesses.entrySet()) {
            roundOutput += entry.getKey().getNickname() + "," + entry.getValue() + " ";
        }
        roundOutput += "Target: " + targetNumber + " ";
        for (Map.Entry<Player, Integer> entry : playerGuesses.entrySet()) {
            if (roundWinners.contains(entry.getKey())) {
                roundOutput += "Win ";
                entry.getKey().updatePoints(-1); // TESTING SO SINGLE USER WILL ALWAYS GET POINTS DEDUCTED SO THAT GAME ENDS AT SOME POINT
            } else {
                roundOutput += "Lose ";
                entry.getKey().updatePoints(-1);
            }
        }

        System.out.println(roundOutput);

        for (Player player : playingPlayers) {
        	System.out.println("printing round output");
            player.toUser.println(roundOutput);
        }

        System.out.println();

        // Game Over when no Players Left in the playingPlayers Array
        if (playingPlayers.isEmpty()) {
            System.out.println("Game over! No winners.");
            for (Player player : playingPlayers) {
                player.toUser.println("Game over! No winners.");
            }

            gameInProgress = false;
        } else {
            currentRound++;
            playRound();
        }
    }
}