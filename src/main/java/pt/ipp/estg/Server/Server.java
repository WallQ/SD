package pt.ipp.estg.Server;

import pt.ipp.estg.Entities.User;
import pt.ipp.estg.Enums.Role;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int SERVER_PORT = 1024;
    protected ServerSocket serverSocket;
    protected static final Map<ClientHandler, User> users = new HashMap<>();

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
                System.out.printf("[%s %s] New connection!%n", getCurrentTime(), getClientAddress(clientSocket));
                ClientHandler handler = new ClientHandler(clientSocket);
                users.put(handler, null);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.err.println("An unexpected error has occurred during server initialization!\n" + e.getMessage());
            System.exit(1);
        }
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
        private User user;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                this.user = null;
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
                authenticateUser();
//                String clientMessage;
//                while ((clientMessage = bufferedReader.readLine()) != null) {
//                    System.out.printf("[%s %s] [%s]%s: %s%n", getCurrentTime(), getClientAddress(socket), user.getUsername(), user.getRole(), clientMessage);
//                    broadcastMessage(user.getUsername() + "[" + user.getRole() + "]: " + clientMessage);
//                }
            } catch (Exception e) {
                System.out.printf("[%s %s] Lost connection!%n", getCurrentTime(), getClientAddress(this.socket));
            } finally {
                close();
            }
        }

        private void authenticateUser() throws IOException {
            boolean isAuthenticated = false;

            while (!isAuthenticated) {
                sendMessageToClient("[Authentication Commands]\n/sign-up {username} {email} {password} {role (Private, Sergeant, Lieutenant, General)}\n/sign-in {email} {password}");

                String command = this.bufferedReader.readLine();

                if (command.startsWith("/sign-up")) {
                    String[] commandArgs = command.split("\\s+", 5);
                    if (commandArgs.length != 5) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }
                    if (!commandArgs[4].equals("Private") && !commandArgs[4].equals("Sergeant") && !commandArgs[4].equals("Lieutenant") && !commandArgs[4].equals("General")) {
                        sendMessageToClient("Invalid role. Please try again!");
                        continue;
                    }
                    this.user = Auth.signUp(commandArgs[1], commandArgs[2], commandArgs[3], commandArgs[4]);
                } else if (command.startsWith("/sign-in")) {
                    String[] commandArgs = command.split("\\s+", 3);
                    if (commandArgs.length != 3) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }
                    this.user = Auth.signIn(commandArgs[1], commandArgs[2]);
                } else {
                    sendMessageToClient("Invalid command. Please try again!");
                    continue;
                }

                if (this.user != null) {
                    synchronized (users) {
                        users.put(this, this.user);
                    }
                    isAuthenticated = true;
                    System.out.printf("[%s %s] [%s] %s connected!%n", getCurrentTime(), getClientAddress(this.socket), this.user.getRole(), this.user.getUsername());
                    broadcastMessage("[SERVER] [" + this.user.getRole() + "] " + this.user.getUsername() + " connected!");
                } else {
                    sendMessageToClient("Authentication failed. Please try again!");
                }
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

        private void sendMessage(String message, Map.Entry<ClientHandler, User> set) {
            try {
                set.getKey().bufferedWriter.write("[%s %s] [%s] %s: %s%n".formatted(getCurrentTime(), getClientAddress(this.socket), this.user.getUsername(), this.user.getRole(), message));
                set.getKey().bufferedWriter.newLine();
                set.getKey().bufferedWriter.flush();
            } catch (Exception e) {
                handleException("An unexpected error has occurred during broadcasting message!", e);
            }
        }

        private void unicastMessage(String username, String message) {
            synchronized (users) {
                for (Map.Entry<ClientHandler, User> set : users.entrySet()) {
                    if (set.getValue().getUsername().equals(username)) {
                        sendMessage(message, set);
                    }
                }
            }
        }

        private void multicastMessage(Role role, String message) {
            synchronized (users) {
                for (Map.Entry<ClientHandler, User> set : users.entrySet()) {
                    if (set.getValue().getRole().equals(role)) {
                        sendMessage(message, set);
                    }
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (users) {
                for (Map.Entry<ClientHandler, User> set : users.entrySet()) {
                    if (set.getKey() != this) {
                        sendMessage(message, set);
                    }
                }
            }
        }

        private void close() {
            try {
                if (socket != null) socket.close();
                if (bufferedWriter != null) bufferedWriter.close();
                if (bufferedReader != null) bufferedReader.close();
                users.remove(this);
                broadcastMessage("[SERVER] Client disconnected!");
            } catch (IOException e) {
                handleException("Couldn't close buffered writer buffered reader & socket!", e);
            } catch (Exception e) {
                handleException("An unexpected error has occurred during closing connection!", e);
            }
        }
    }
}
