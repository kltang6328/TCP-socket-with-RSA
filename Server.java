import java.io.*;
import java.net.*;
import java.security.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;

public class Server {
    public static void main(String[] args) {
        // Initialize variable so its easier to change port number later if necessary
        int port = 12345;

        // Open socket on port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Announce which port the server is on
            System.out.println("Server listening on port " + port);

            // Generate RSA key pair for the server
            KeyPair serverKeyPair = RSA.generateKeyPair();
            PublicKey serverPublicKey = serverKeyPair.getPublic();
            PrivateKey serverPrivateKey = serverKeyPair.getPrivate();

            while (true) {
                // Open connection socket on the server
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Connection from: " + clientSocket.getInetAddress());

                    // Send server's public key to the client
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    byte[] serverPublicKeyBytes = serverPublicKey.getEncoded();
                    String encodedServerPublicKey = Base64.getEncoder().encodeToString(serverPublicKeyBytes);
                    writer.write(encodedServerPublicKey);
                    writer.newLine();
                    writer.flush();

                    // Receive client's public key
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String encodedClientPublicKey = reader.readLine();
                    byte[] clientPublicKeyBytes = Base64.getDecoder().decode(encodedClientPublicKey);
                    PublicKey clientPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientPublicKeyBytes));

                    while (true) {
                        // Read encrypted message from client
                        String encryptedFileData = reader.readLine();
                        byte[] encryptedData = Base64.getDecoder().decode(encryptedFileData);

                        // Decrypt message with server's private key
                        byte[] decryptedFileData = RSA.decrypt(encryptedData, serverPrivateKey);

                        // Print the decrypted message from client
                        String decryptedMessage = new String(decryptedFileData);
                        System.out.println("Decrypted message received from client: " + decryptedMessage);

                        // Read server response from file ServerResponse
                        String serverResponse = new String(Files.readAllBytes(Paths.get("ServerResponse.txt")));

                        // Encrypt server response with client's public key
                        byte[] encryptedServerResponse = RSA.encrypt(serverResponse.getBytes(), clientPublicKey);
                        System.out.print("Encrypted server response:" + Base64.getEncoder().encodeToString(encryptedServerResponse) + "\n");

                        // Send encrypted server response to client
                        writer.write(Base64.getEncoder().encodeToString(encryptedServerResponse));
                        writer.newLine();
                        writer.flush();
                    }
                }
            }
        } catch (Exception e) { // Print error warning message
            e.printStackTrace();
        }
    }
}
