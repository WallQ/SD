package pt.ipp.estg.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            handleException("An unexpected error has occurred during client initialization!", e);
        }
    }

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

    private void handleException(String message, Exception e) {
        System.err.println(message + "\n" + e.getMessage());
        System.exit(1);
    }

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
}
