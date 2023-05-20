import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) throws Exception {
        // Prompt user to enter the file name that contains their message
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter a file name: ");
        String fileName = keyboard.nextLine();

        // Initialize a string to hold message
        String message = "";

        // Read the file and print to message string
        try {
            File file = new File(fileName);
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                message = fileReader.nextLine();
            }
            fileReader.close();

            // Open a new socket 
            Socket clientSocket = new Socket("localhost", 1337);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            // Send message to server
            outToServer.writeBytes(message + '\n');

            // Let client know the message was sent
            System.out.println("Message sent!");

            // Close socket
            clientSocket.close();
        } catch (FileNotFoundException e) { // If file could not be found, print error warning message
            System.out.println("WARNING-- File '" + fileName + "' could not be found.");
        }

        // Close scanner
        keyboard.close();
    }
}