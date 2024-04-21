package content;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ServiceServer extends Thread{

	Socket client;
	public ServiceServer(Socket c){
		client=c;
	}
//	public void run() {
//	 
//		try {
//			PrintWriter output= new PrintWriter(client.getOutputStream(),true);
//			Scanner input= new Scanner(client.getInputStream());
//			
//			output.println("welcome to my game");
//			
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

}
