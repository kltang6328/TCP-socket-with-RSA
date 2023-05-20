import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) throws Exception {
        // Prompt user to enter a message
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter a message: ");
        String message = keyboard.nextLine();

        // Open a new socket 
        Socket clientSocket = new Socket("localhost", 1337);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        // Send message to server
        outToServer.writeBytes(message + '\n');

        // Let client know the message was sent
        System.out.println("Message sent!");

        // Close socket and scanner
        clientSocket.close();
        keyboard.close();
    }
}