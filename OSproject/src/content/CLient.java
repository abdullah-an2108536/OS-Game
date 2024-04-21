package content;

import java.io.*;
import java.net.*;
import java.util.*;

public class CLient {
	public CLient(){

		Socket client = null;

		try{
			client = new Socket("localhost",13337);
			PrintWriter output= new PrintWriter(client.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			System.out.println(in.readLine()); // receives back the result of guesses
		}catch(IOException ioe){
			System.out.println("IO related error "+ioe);
		}
	}
	public static void main(String srgs[]){
		new CLient();
	}
}
