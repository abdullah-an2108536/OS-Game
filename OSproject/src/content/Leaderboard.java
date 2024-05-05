package content;


import java.io.*;
import java.util.*;

public class Leaderboard implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> leaderboard;

    public Leaderboard() {
        this.leaderboard = new HashMap<>();
        sortLeaderboard();
    }

    public static Leaderboard loadLeaderboard(String filename)
    {
        Leaderboard leaderboard = null;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename)))
        {
            Object obj = ois.readObject();
            if (obj instanceof Leaderboard) {leaderboard = (Leaderboard) obj;}
            else {leaderboard = new Leaderboard();}
        }
        catch (IOException | ClassNotFoundException e) {leaderboard = new Leaderboard();}
        
        return leaderboard;
    }


    public void saveLeaderboard(String filename)
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)))
        {
        	sortLeaderboard();
            oos.writeObject(this);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void updateLeaderboard(ArrayList<Ticket> tickets)
    {
        for (Ticket ticket : tickets)
        {
            String key = ticket.getNickname() + "_" + ticket.getId();
            if (!leaderboard.containsKey(key)) {leaderboard.put(key, 0);}
        }
        saveLeaderboard("LeaderboardStorage.ser");
    }

    public void incrementWins(String nickname, int id)
    {
        String key = nickname + "_" + id;
        if (leaderboard.containsKey(key))
        {
            int wins = leaderboard.get(key);
            leaderboard.put(key, wins + 1);
            saveLeaderboard("LeaderboardStorage.ser");
        }
    }

    public void checkAndUpdateLeaderboard(ArrayList<Ticket> tickets)
    {
        updateLeaderboard(tickets);
        // Check if each ticket is present in the leaderboard, if not, create a new entry with 0 wins
        for (Ticket ticket : tickets)
        {
            String key = ticket.getNickname() + "_" + ticket.getId();
            if (!leaderboard.containsKey(key)) {leaderboard.put(key, 0);}
        }
        saveLeaderboard("LeaderboardStorage.ser");
    }
    
    // Sort the leaderboard in descending order of wins
    private void sortLeaderboard()
    {
        // Convert leaderboard entries to a list
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(leaderboard.entrySet());
        // Sort the list based on values (wins) in descending order
        sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        // Rebuild the leaderboard using the sorted entries
        leaderboard = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {leaderboard.put(entry.getKey(), entry.getValue());}
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nLeaderboard:\n\n");
        sb.append(String.format("%-20s %-10s%n", "Player", "Wins"));
        sb.append("--------------------------------------------------\n");
        for (Map.Entry<String, Integer> entry : leaderboard.entrySet())
        {
            String player = entry.getKey();
            int wins = entry.getValue();
            sb.append(String.format("%-20s %-10d%n", player, wins));
        }
        return sb.toString();
    }
    
    public String toString2() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nLeaderboard:\n\n");
        sb.append(String.format("%-20s %-10s%n", "Player", "Wins"));
        sb.append("--------------------------------------------------\n");
        
        int count = 0; // Counter to limit to first 5 entries
        for (Map.Entry<String, Integer> entry : leaderboard.entrySet()) {
            if (count >= 5) {
                break; // Break the loop after printing the first 5 entries
            }
            
            String player = entry.getKey();
            int wins = entry.getValue();
            sb.append(String.format("%-20s %-10d%n", player, wins));
            
            count++; // Increment the counter
        }
        
        return sb.toString();
    }



}
