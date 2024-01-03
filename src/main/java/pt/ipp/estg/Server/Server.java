package pt.ipp.estg.Server;

import pt.ipp.estg.Entities.User;
import pt.ipp.estg.Enums.Role;
import pt.ipp.estg.Utils.JSON;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Server {
    private static final int SERVER_PORT = 1024;
    protected ServerSocket serverSocket;
    protected static final ArrayList<ClientHandler> clients = new ArrayList<>();

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    private String getClientAddress(Socket clientSocket) {
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }

    private void start() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.printf("[%s] NEW CONNECTION -> %s%n", getCurrentTime(), getClientAddress(clientSocket));
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.err.println("An unexpected error has occurred during server initialization!\n" + e.getMessage());
            System.exit(1);
        }
    }

    private static User handleSignUp(String username, String email, String password, String role) {
        ArrayList<User> users = JSON.loadUsers();

        User user = new User(UUID.randomUUID(), username, email, password, Role.valueOf(role));

        if (!users.isEmpty()) {
            for (User currentUser : users) {
                if (currentUser.getEmail().equals(user.getEmail())) {
                    return null;
                }
            }
        }

        JSON.saveUser(user);

        return user;
    }

    private static User handleSignIn(String email, String password) {
        ArrayList<User> users = JSON.loadUsers();

        if (users.isEmpty()) return null;

        for (User currentUser : users) {
            if (currentUser.getEmail().equals(email) && currentUser.getPassword().equals(password)) {
                return currentUser;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        try (
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT)
        ) {
            Server server = new Server(serverSocket);
            server.start();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection listen on port " + SERVER_PORT + "!\n" + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("An unexpected error has occurred!\n" + e.getMessage());
            System.exit(1);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            } catch (Exception e) {
                handleException("An unexpected error has occurred during client initialization!", e);
            }
        }

        private String getCurrentTime() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        }

        private String getClientAddress(Socket clientSocket) {
            return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        }

        private void handleException(String message, Exception e) {
            System.err.println(message + "\n" + e.getMessage());
            System.exit(1);
        }

        @Override
        public void run() {
            try {
                boolean isAuthenticated = false;
                User user = null;

                while (!isAuthenticated) {
                    sendMessageToClient("[Auth Menu]");
                    sendMessageToClient("1 - Sign Up");
                    sendMessageToClient("2 - Sign In");

                    String option = bufferedReader.readLine();
                    String email;
                    String password;

                    switch (option) {
                        case "1":
                            sendMessageToClient("Username: ");
                            String username = bufferedReader.readLine();
                            sendMessageToClient("Email: ");
                            email = bufferedReader.readLine();
                            sendMessageToClient("Password: ");
                            password = bufferedReader.readLine();
                            sendMessageToClient("Available Roles: [Private], [Sergeant], [Lieutenant], [General]");
                            String role = bufferedReader.readLine();
                            user = handleSignUp(username, email, password, role);
                            break;
                        case "2":
                            sendMessageToClient("Email: ");
                            email = bufferedReader.readLine();
                            sendMessageToClient("Password: ");
                            password = bufferedReader.readLine();
                            user = handleSignIn(email, password);
                            break;
                        default:
                            sendMessageToClient("Invalid option. Please try again!");
                            break;
                    }

                    if (user != null) {
                        isAuthenticated = true;
                        System.out.printf("[%s %s] %s[%s] connected!%n", getCurrentTime(), getClientAddress(socket), user.getUsername(), user.getRole());
                        broadcastMessage("[SERVER] " + user.getUsername() + "[" + user.getRole() + "] connected!");
                    } else {
                        sendMessageToClient("Authentication failed. Please try again!");
                    }
                }
                String clientMessage;
                while ((clientMessage = bufferedReader.readLine()) != null) {
                    System.out.printf("[%s %s] %s[%s]: %s%n", getCurrentTime(), getClientAddress(socket), user.getUsername(), user.getRole(), clientMessage);
                    broadcastMessage(user.getUsername() + "[" + user.getRole() + "]: " + clientMessage);
                }
            } catch (Exception e) {
                System.out.printf("[%s %s] Client disconnected!%n", getCurrentTime(), getClientAddress(socket));
            } finally {
                close();
            }
        }

        private void sendMessageToClient(String message) {
            try {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (Exception e) {
                handleException("An unexpected error has occurred during sending message to client!", e);
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    try {
                        if (client != this) {
                            client.bufferedWriter.write(message);
                            client.bufferedWriter.newLine();
                            client.bufferedWriter.flush();
                        }
                    } catch (Exception e) {
                        handleException("An unexpected error has occurred during broadcasting message!", e);
                    }
                }
            }
        }

        private void close() {
            try {
                if (socket != null) socket.close();
                if (bufferedWriter != null) bufferedWriter.close();
                if (bufferedReader != null) bufferedReader.close();
                clients.remove(this);
                broadcastMessage("[SERVER] Client disconnected!");
            } catch (IOException e) {
                handleException("Couldn't close buffered writer buffered reader & socket!", e);
            } catch (Exception e) {
                handleException("An unexpected error has occurred during closing connection!", e);
            }
        }
    }
}
