import java.io.*;
import java.net.*;

public class Server {
    public static void main(String args[]) throws Exception {
        // Open socket on same port as client
        ServerSocket welcome = new ServerSocket(1337);

        while(true) {
            // Open connection socket on the server
            Socket connectionSocket = welcome.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            // Read message from client
            String clientMessage = inFromClient.readLine();

            // Print the message from client
            System.out.println("Message received: " + clientMessage);

            // Close socket
            connectionSocket.close();
        }
    }
}
