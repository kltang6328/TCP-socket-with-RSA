import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) throws Exception {
        // Initialize scanner
        Scanner keyboard = new Scanner(System.in);

        // Initialize variable quit 
        boolean quit = false;

        // Continue to run program until user chooses to quit 
        while (!quit) {
            // Prompt user to enter the file name that contains their message
            System.out.print("Enter a file name: ");
            String fileName = keyboard.nextLine();

            // Check if user wants to quit
            if(fileName.equalsIgnoreCase("quit")) {
                quit = true;
                break;
            }

            // Initialize a string to hold message
            String message = "";

            try {
                // Read the file 
                File file = new File(fileName);
                Scanner fileReader = new Scanner(file);

                // Print to message string
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
            // Check if user wants to quit
            System.out.println("Type 'quit' to exit program or type anything else to continue with another file.");
            String input = keyboard.nextLine();
            if(input.equalsIgnoreCase("quit"))
                quit = true;
        }
        // Close scanner
        keyboard.close();

        System.out.println("Shutting down...");
    }
}