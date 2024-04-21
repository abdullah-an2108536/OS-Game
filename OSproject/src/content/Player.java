package content;
import java.io.*;
import java.net.*;

public class Player implements Runnable{
    public void run() {
        try {
//        	PrintWriter output= new PrintWriter(client.getOutputStream(),true);
            Socket socket = new Socket("localhost", 3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = in.readLine();
            System.out.println("Message from server: " + message);
//            socket.close(); 
//            output.
        } catch (IOException e) {
            System.out.println("I/O Error related: " + e);
        }
    }
}