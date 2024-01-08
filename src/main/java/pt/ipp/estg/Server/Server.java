package pt.ipp.estg.Server;

import pt.ipp.estg.Entities.Chatroom;
import pt.ipp.estg.Entities.User;
import pt.ipp.estg.Enums.Role;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    private static final int SERVER_PORT = 1024;
    protected ServerSocket serverSocket;
    protected static final Map<ClientHandler, User> clients = new HashMap<>();
    protected static final List<Chatroom> rooms = new ArrayList<>();

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
                clients.put(handler, null);
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
                handleAction();
            } catch (Exception e) {
                System.out.printf("[%s %s] Lost connection!%n", getCurrentTime(), getClientAddress(this.socket));
            } finally {
                close();
            }
        }

        private void authenticateUser() throws IOException {
            boolean isAuthenticated = false;

            while (!isAuthenticated) {
                sendMessageToClient(CommandsMenu.AuthenticationCommands());

                String command = this.bufferedReader.readLine();

                if (command.startsWith("/sign-up")) {
                    String[] commandArgs = command.split("\\s+", 5);

                    if (isInvalidCommand(commandArgs.length, 5)) return;
                    if (isInvalidRole(commandArgs[4])) return;

                    this.user = Auth.signUp(commandArgs[1], commandArgs[2], commandArgs[3], commandArgs[4]);
                } else if (command.startsWith("/sign-in")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) return;

                    this.user = Auth.signIn(commandArgs[1], commandArgs[2]);
                } else {
                    sendMessageToClient("Invalid command. Please try again!");
                    continue;
                }

                if (this.user != null) {
                    synchronized (clients) {
                        clients.put(this, this.user);
                    }
                    isAuthenticated = true;
                    System.out.printf("[%s %s] (%s)%s connected!%n", getCurrentTime(), getClientAddress(this.socket), this.user.getRole(), this.user.getUsername());
                } else {
                    sendMessageToClient("Authentication failed. Please try again!");
                }
            }
        }

        private void handleAction() throws IOException {
            String command;

            sendMessageToClient(CommandsMenu.MessageCommands());

            while ((command = bufferedReader.readLine()) != null) {
                sendMessageToClient(CommandsMenu.MessageCommands());

                if (command.startsWith("/whisper")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) return;

                    unicastMessage(commandArgs[1], commandArgs[2]);
                } else if (command.startsWith("/say")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) return;
                    if (isInvalidRole(commandArgs[1])) return;

                    multicastMessage(Role.valueOf(commandArgs[1]), commandArgs[2]);
                } else if (command.startsWith("/all")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) return;

                    broadcastMessage(commandArgs[1]);
                } else if (command.startsWith("/create-room")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) return;

                    synchronized (rooms) {
                        rooms.add(new Chatroom(commandArgs[1]));
                    }
                } else if (command.startsWith("/join-room")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) return;

                    synchronized (rooms) {
                        for (Chatroom room : rooms) {
                            if (room.getName().equals(commandArgs[1])) {
                                room.addUser(this.user);
                                break;
                            }
                        }
                    }
                } else {
                    sendMessageToClient("Invalid command. Please try again!");
                    return;
                }
            }
        }

        private boolean isInvalidRole(String role) {
            if (!Role.Private.name().equals(role) && !Role.Sergeant.name().equals(role) && !Role.Lieutenant.name().equals(role) && !Role.General.name().equals(role)) {
                sendMessageToClient("Invalid role. Please try again!");
                return true;
            }
            return false;
        }

        private boolean isInvalidCommand(int commandArgsLength, int maxArgsLength) {
            if (commandArgsLength != maxArgsLength) {
                sendMessageToClient("Invalid command. Please try again!");
                return true;
            }
            return false;
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

        private void unicastMessage(String username, String message) {
            synchronized (clients) {
                for (Map.Entry<ClientHandler, User> set : clients.entrySet()) {
                    if (set.getValue().getUsername().equals(username)) {
                        try {
                            set.getKey().bufferedWriter.write("[%s] [Say] (%s)%s: %s%n".formatted(getCurrentTime(), this.user.getRole(), this.user.getUsername(), message));
                            set.getKey().bufferedWriter.newLine();
                            set.getKey().bufferedWriter.flush();
                        } catch (Exception e) {
                            handleException("An unexpected error has occurred during broadcasting message!", e);
                        }
                    }
                }
            }
        }

        private void multicastMessage(Role role, String message) {
            synchronized (clients) {
                for (Map.Entry<ClientHandler, User> set : clients.entrySet()) {
                    if (set.getValue().getRole().equals(role)) {
                        try {
                            set.getKey().bufferedWriter.write("[%s] [Rank] (%s)%s: %s%n".formatted(getCurrentTime(), this.user.getRole(), this.user.getUsername(), message));
                            set.getKey().bufferedWriter.newLine();
                            set.getKey().bufferedWriter.flush();
                        } catch (Exception e) {
                            handleException("An unexpected error has occurred during broadcasting message!", e);
                        }
                    }
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (Map.Entry<ClientHandler, User> set : clients.entrySet()) {
                    if (set.getKey() != this) {
                        try {
                            set.getKey().bufferedWriter.write("[%s] [All] (%s)%s: %s%n".formatted(getCurrentTime(), this.user.getRole(), this.user.getUsername(), message));
                            set.getKey().bufferedWriter.newLine();
                            set.getKey().bufferedWriter.flush();
                        } catch (Exception e) {
                            handleException("An unexpected error has occurred during broadcasting message!", e);
                        }
                    }
                }
            }
        }

        private void close() {
            try {
                if (socket != null) socket.close();
                if (bufferedWriter != null) bufferedWriter.close();
                if (bufferedReader != null) bufferedReader.close();
                synchronized (clients) {
                    clients.remove(this);
                }
            } catch (IOException e) {
                handleException("Couldn't close buffered writer buffered reader & socket!", e);
            } catch (Exception e) {
                handleException("An unexpected error has occurred during closing connection!", e);
            }
        }
    }
}
