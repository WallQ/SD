package pt.ipp.estg.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * The {@code Client} class represents a simple client for communication
 * with a server using sockets. It allows sending and receiving messages
 * to and from the server.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class Client {
    /**
     * The socket associated with the client-server communication.
     */
    private Socket socket;
    /**
     * The buffered writer used for sending messages to the server.
     */
    private BufferedWriter bufferedWriter;
    /**
     * The buffered reader used for reading messages from the server.
     */
    private BufferedReader bufferedReader;

    /**
     * Constructs a new {@code Client} object with the specified socket.
     *
     * @param socket The socket to establish the client connection.
     */
    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            handleException("An unexpected error has occurred during client initialization!", e);
        }
    }

    /**
     * The main entry point for the client application.
     *
     * @param args The command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1024)) {
            System.out.println("Connected to server!");
            Client client = new Client(socket);
            client.listenForMessages();
            client.sendMessage();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection listen on host localhost & port 1024!\n" + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("An unexpected error has occurred!\n" + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Sends messages to the server through the established connection.
     */
    public void sendMessage() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            handleException("An unexpected error has occurred during sending message!", e);
        }
    }

    /**
     * Listens for incoming messages from the server in a separate thread.
     */
    public void listenForMessages() {
        new Thread(() -> {
            String messageFromGroupChat;
            while (socket.isConnected()) {
                try {
                    messageFromGroupChat = bufferedReader.readLine();
                    System.out.println(messageFromGroupChat);
                } catch (Exception e) {
                    handleException("An unexpected error has occurred during listening messages!", e);
                }
            }
        }).start();
    }

    /**
     * Handles exceptions by printing an error message and exiting the program.
     *
     * @param message The error message.
     * @param e       The exception that occurred.
     */
    private void handleException(String message, Exception e) {
        System.err.println(message + "\n" + e.getMessage());
        System.exit(1);
    }
}
