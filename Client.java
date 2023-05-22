import java.io.*;
import java.net.*;
import java.security.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;

public class Client {
    public static void main(String[] args) {
        // Initialize variables so its easier to change later if necessary
        String host = "localhost";
        int port = 12345;

        // Open a new socket
        try (Socket socket = new Socket(host, port)) {
            // Generate RSA key pair for the client
            KeyPair clientKeyPair = RSA.generateKeyPair();
            PublicKey clientPublicKey = clientKeyPair.getPublic();
            PrivateKey clientPrivateKey = clientKeyPair.getPrivate();

            // Send client's public key to the server
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            byte[] clientPublicKeyBytes = clientPublicKey.getEncoded();
            String encodedClientPublicKey = Base64.getEncoder().encodeToString(clientPublicKeyBytes);
            writer.write(encodedClientPublicKey);
            writer.newLine();
            writer.flush();

            // Receive server's public key
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String encodedServerPublicKey = reader.readLine();
            byte[] serverPublicKeyBytes = Base64.getDecoder().decode(encodedServerPublicKey);
            PublicKey serverPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(serverPublicKeyBytes));

            // Prompt user to enter the file name that contains their message and check if the user wants to quit
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the file name or type 'Quit' to exit: ");
            String userInput = userInputReader.readLine();

            while (!userInput.equalsIgnoreCase("Quit")) {
                // Read the file
                String filePath = userInput;
                byte[] fileData = Files.readAllBytes(Paths.get(filePath));

                // Encrypt message with server's public key
                byte[] encryptedFileData = RSA.encrypt(fileData, serverPublicKey);

                // Send encrypted message to server
                writer.write(Base64.getEncoder().encodeToString(encryptedFileData));
                System.out.print("Encrypted message:" + Base64.getEncoder().encodeToString(encryptedFileData) + "\n");
                writer.newLine();
                writer.flush();

                // Read encrypted server's response
                String serverResponse = reader.readLine();
                byte[] encryptedServerResponse = Base64.getDecoder().decode(serverResponse);

                // Decrypt server's response with client's private key
                byte[] decryptedServerResponse = RSA.decrypt(encryptedServerResponse, clientPrivateKey);
                String decryptedMessage = new String(decryptedServerResponse);

                // Print decrypted server's response
                System.out.println("Decrypted server's response: " + decryptedMessage);

                // Prompt user to enter another file name or quit
                System.out.print("Enter the file name or type 'Quit' to exit: ");
                userInput = userInputReader.readLine();
            }
        } catch (Exception e) { // Print error warning message
            e.printStackTrace();
        }
    }
}

