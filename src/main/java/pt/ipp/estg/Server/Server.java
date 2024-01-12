package pt.ipp.estg.Server;

import pt.ipp.estg.Entities.Request;
import pt.ipp.estg.Entities.User;
import pt.ipp.estg.Enums.Role;
import pt.ipp.estg.Utils.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The {@code Server} class represents a simple chat server that handles client connections,
 * user authentication, and communication between clients. It utilizes multithreading and
 * timers to manage various tasks such as monitoring active members and displaying pending requests.
 * <p>
 * This class is designed to be used within a chat application.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class Server {
    /**
     * A mapping of room names to lists of client handlers in each room.
     */
    protected static final Map<String, List<ClientHandler>> rooms = new HashMap<>();
    /**
     * A mapping of client handlers to associated user information.
     */
    protected static final Map<ClientHandler, User> clients = new HashMap<>();
    /**
     * A mapping of request IDs to request objects.
     */
    protected static final Map<UUID, Request> requests = new HashMap<>();
    /**
     * A mapping of usernames to lists of offline messages for each user.
     */
    protected static final Map<String, List<String>> offlineMessages = new HashMap<>();
    /**
     * An object used for synchronization.
     */
    protected static final Object lock = new Object();
    /**
     * The port on which the server listens for incoming connections.
     */
    private static final int SERVER_PORT = 1024;
    /**
     * The count of requests accepted by the server.
     */
    protected static int requestsAccepted = 0;
    /**
     * The count of requests rejected by the server.
     */
    protected static int requestsRejected = 0;
    /**
     * The server socket used for accepting client connections.
     */
    protected ServerSocket serverSocket;
    /**
     * The multicast socket used for handling multicast communication.
     */
    protected MulticastSocket multicastSocket;
    /**
     * The multicast group address.
     */
    protected InetAddress group;

    /**
     * Constructs a new {@code Server} with the specified server socket.
     *
     * @param serverSocket The server socket used for accepting client connections.
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * The main method to start the server. It initializes the server socket and starts the server.
     *
     * @param args The command-line arguments (not used in this application).
     */
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

    /**
     * Gets the current time in the "dd/MM/yyyy HH:mm:ss" format.
     *
     * @return The current time as a formatted string.
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    /**
     * Gets the client address based on the client socket's IP address and port.
     *
     * @param clientSocket The client socket.
     * @return The formatted client address.
     */
    private String getClientAddress(Socket clientSocket) {
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }

    /**
     * Initializes and starts timers for monitoring active members and displaying pending requests.
     */
    private void initializeTimers() {
        Timer activeMembersTimer = new Timer(true);
        activeMembersTimer.scheduleAtFixedRate(new ActiveMembersTask(), 0, 150000);

        Timer requestsTimer = new Timer(true);
        requestsTimer.scheduleAtFixedRate(new RequestsTask(), 0, 300000);
    }

    /**
     * Accepts client connections and spawns a new thread for each connected client.
     */
    private void acceptConnections() {
        while (!this.serverSocket.isClosed()) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                Logger.log(getClientAddress(clientSocket), "Connection", "New connection established.");
                System.out.printf("[%s %s] New connection!%n", getCurrentTime(), getClientAddress(clientSocket));
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.put(handler, null);
                new Thread(handler).start();
            } catch (Exception e) {
                System.err.println("An unexpected error has occurred during server initialization!\n" + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Starts the server by initializing timers and accepting client connections.
     */
    private void start() {
        try {
            initializeTimers();
            acceptConnections();
        } catch (Exception e) {
            System.err.println("An unexpected error has occurred during server initialization!\n" + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * The {@code ClientHandler} class represents a thread responsible for handling the communication
     * with a connected client. It manages user authentication, message handling, and other client-related
     * actions within the server.
     * This class is designed to be used within a server application.
     *
     * @author Your Name
     * @version 1.0
     */
    private static class ClientHandler implements Runnable {
        /**
         * The socket associated with the connected client.
         */
        private final Socket socket;
        /**
         * The buffered writer used for sending messages to the client.
         */
        private BufferedWriter bufferedWriter;
        /**
         * The buffered reader used for reading messages from the client.
         */
        private BufferedReader bufferedReader;
        /**
         * The user associated with the connected client.
         */
        private User user;

        /**
         * Constructs a new {@code ClientHandler} for the specified client socket.
         *
         * @param socket The client socket.
         */
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

        /**
         * Gets the current time in the format "dd/MM/yyyy HH:mm:ss".
         *
         * @return The current time as a formatted string.
         */
        private String getCurrentTime() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        }

        /**
         * Gets the client address based on the client socket's IP address and port.
         *
         * @param clientSocket The client socket.
         * @return The formatted client address.
         */
        private String getClientAddress(Socket clientSocket) {
            return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        }

        /**
         * Handles an exception by printing an error message and exiting the application.
         *
         * @param message The error message to display.
         * @param e       The exception that occurred.
         */
        private void handleException(String message, Exception e) {
            System.err.println(message + "\n" + e.getMessage());
            System.exit(1);
        }

        /**
         * Executes the client handling logic, including user authentication and message handling.
         */
        @Override
        public void run() {
            try {
                authenticateUser();
                handleAction();
            } catch (Exception e) {
                Logger.log(getClientAddress(this.socket), "Disconnection", "Connection was lost.");
                System.out.printf("[%s %s] Lost connection!%n", getCurrentTime(), getClientAddress(this.socket));
            } finally {
                close();
            }
        }

        /**
         * Authenticates the user by processing sign-up or sign-in commands until the user
         * is successfully authenticated. Invalid commands result in appropriate error messages.
         *
         * @throws IOException If an I/O error occurs during communication.
         */
        private void authenticateUser() throws IOException {
            boolean isAuthenticated = false;

            while (!isAuthenticated) {
                sendMessageToClient(CommandsMenu.AuthenticationCommands());
                String command = this.bufferedReader.readLine();

                if (command.startsWith("/sign-up")) {
                    handleSignUp(command);
                } else if (command.startsWith("/sign-in")) {
                    handleSignIn(command);
                } else {
                    sendMessageToClient("Invalid command. Please try again!");
                    continue;
                }

                if (this.user != null) {
                    handleSuccessfulAuthentication();
                    isAuthenticated = true;
                } else {
                    sendMessageToClient("Authentication failed. Please try again!");
                }
            }
        }

        /**
         * Handles a sign-up command by extracting command arguments, validating them,
         * and invoking the sign-up method. If the command is invalid, it is ignored.
         *
         * @param command The sign-up command from the client.
         */
        private void handleSignUp(String command) {
            String[] commandArgs = command.split("\\s+", 5);

            if (isInvalidCommand(commandArgs.length, 5)) return;
            if (isInvalidRole(commandArgs[4])) return;

            this.user = Auth.signUp(commandArgs[1], commandArgs[2], commandArgs[3], commandArgs[4]);
        }

        /**
         * Handles a sign-in command by extracting command arguments, validating them,
         * and invoking the sign-in method. If the command is invalid, it is ignored.
         *
         * @param command The sign-in command from the client.
         */
        private void handleSignIn(String command) {
            String[] commandArgs = command.split("\\s+", 3);

            if (isInvalidCommand(commandArgs.length, 3)) return;

            this.user = Auth.signIn(commandArgs[1], commandArgs[2]);
        }

        /**
         * Handles successful user authentication by updating the clients map, logging
         * the authentication event, and displaying a connection message.
         */
        private void handleSuccessfulAuthentication() {
            synchronized (clients) {
                clients.put(this, this.user);
            }
            Logger.log(getClientAddress(this.socket), "Authentication", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " authenticated.");
            System.out.printf("[%s %s] (%s)%s connected!%n", getCurrentTime(), getClientAddress(this.socket), this.user.getRole(), this.user.getUsername());
            handleOfflineMessages();
        }

        /**
         * Handles offline messages for the client. Sends stored messages to the client if any
         * are present in the offlineMessages map, and removes the messages from the map.
         */
        private void handleOfflineMessages() {
            synchronized (offlineMessages) {
                if (offlineMessages.containsKey(this.user.getUsername())) {
                    List<String> messages = offlineMessages.get(this.user.getUsername());
                    messages.forEach(this::sendMessageToClient);
                    offlineMessages.remove(this.user.getUsername());
                }
            }
        }

        /**
         * Handles various user actions, such as sending private, role-specific, and global messages,
         * creating, joining, and leaving chat rooms, launching missiles, managing requests, and promoting/demoting users.
         *
         * @throws IOException If an I/O error occurs during communication.
         */
        private void handleAction() throws IOException {
            String command;

            sendMessageToClientCommands();

            while ((command = bufferedReader.readLine()) != null) {
                sendMessageToClientCommands();

                if (command.startsWith("/whisper")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    Logger.log(getClientAddress(this.socket), "Message", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " sent a message to " + commandArgs[1] + ".");
                    unicastMessage(commandArgs[1], commandArgs[2]);
                } else if (command.startsWith("/say")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    if (isInvalidRole(commandArgs[1])) {
                        sendMessageToClient("Invalid role. Please try again!");
                        continue;
                    }

                    Logger.log(getClientAddress(this.socket), "Message", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " sent a message to role " + commandArgs[1] + ".");
                    multicastMessage(Role.valueOf(commandArgs[1]), commandArgs[2]);
                } else if (command.startsWith("/all")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    Logger.log(getClientAddress(this.socket), "Message", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " sent a message to everyone.");
                    broadcastMessage(commandArgs[1]);
                } else if (command.startsWith("/room")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    Logger.log(getClientAddress(this.socket), "Message", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " sent a message to room " + commandArgs[1] + ".");
                    broadcastMessageRoom(commandArgs[1], commandArgs[2]);
                } else if (command.startsWith("/create-room")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    synchronized (rooms) {
                        if (!rooms.containsKey(commandArgs[1])) {
                            rooms.put(commandArgs[1], new ArrayList<>());
                            rooms.get(commandArgs[1]).add(this);
                            Logger.log(getClientAddress(this.socket), "Creation", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " created room " + commandArgs[1] + ".");
                        } else {
                            sendMessageToClient("The room name already exists. Please try again!");
                        }
                    }
                } else if (command.startsWith("/join-room")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    synchronized (rooms) {
                        if (rooms.containsKey(commandArgs[1]) && !rooms.get(commandArgs[1]).contains(this)) {
                            rooms.get(commandArgs[1]).add(this);
                            Logger.log(getClientAddress(this.socket), "Joining", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " joined room " + commandArgs[1] + ".");
                        } else {
                            sendMessageToClient("The room doesn't exist. Please try again!");
                        }
                    }
                } else if (command.startsWith("/leave-room")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    synchronized (rooms) {
                        if (rooms.containsKey(commandArgs[1]) && rooms.get(commandArgs[1]).contains(this)) {
                            rooms.get(commandArgs[1]).remove(this);
                            if (rooms.get(commandArgs[1]).isEmpty()) {
                                rooms.remove(commandArgs[1]);
                            }
                            Logger.log(getClientAddress(this.socket), "Leave", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " left room " + commandArgs[1] + ".");
                        } else {
                            sendMessageToClient("The room doesn't exist. Please try again!");
                        }
                    }
                } else if (command.startsWith("/list-room")) {
                    String[] commandArgs = command.split("\\s+", 1);

                    if (isInvalidCommand(commandArgs.length, 1)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    synchronized (rooms) {
                        for (Map.Entry<String, List<ClientHandler>> set : rooms.entrySet()) {
                            sendMessageToClient("[Available Rooms]\nRoom: " + set.getKey() + "\nUsers: " + set.getValue().size());
                            Logger.log(getClientAddress(this.socket), "Listing", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " listed room " + set.getKey() + ".");
                        }
                    }
                } else if (command.startsWith("/launch-missile")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    if (this.user.getRole().equals(Role.Private)) {
                        synchronized (requests) {
                            requests.put(UUID.randomUUID(), new Request(this.user, commandArgs[1], commandArgs[2], Role.Sergeant));
                            multicastMessage(Role.Sergeant, "I've sent you a request for a new missile launch to " + commandArgs[1] + " with reason: " + commandArgs[2]);
                        }
                    } else if (this.user.getRole().equals(Role.Sergeant)) {
                        synchronized (requests) {
                            requests.put(UUID.randomUUID(), new Request(this.user, commandArgs[1], commandArgs[2], Role.Lieutenant));
                            multicastMessage(Role.Lieutenant, "I've sent you a request for a new missile launch to " + commandArgs[1] + " with reason: " + commandArgs[2]);
                        }
                    } else if (this.user.getRole().equals(Role.Lieutenant)) {
                        synchronized (requests) {
                            requests.put(UUID.randomUUID(), new Request(this.user, commandArgs[1], commandArgs[2], Role.General));
                            multicastMessage(Role.General, "I've sent you a request for a new missile launch to " + commandArgs[1] + " with reason: " + commandArgs[2]);
                        }
                    } else if (this.user.getRole().equals(Role.General)) {
                        broadcastMessage("Missile launched to " + commandArgs[1] + " with reason: " + commandArgs[2]);
                    }

                    Logger.log(getClientAddress(this.socket), "Attack", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " launched a missile to " + commandArgs[1] + " with reason: " + commandArgs[2] + ".");
                } else if (command.startsWith("/list-requests")) {
                    String[] commandArgs = command.split("\\s+", 1);

                    if (isInvalidCommand(commandArgs.length, 1)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    if (this.user.getRole().equals(Role.Private)) {
                        sendMessageToClient("You don't have permission to list requests. Please try again!");
                        continue;
                    }

                    synchronized (requests) {
                        for (Map.Entry<UUID, Request> set : requests.entrySet()) {
                            if (Objects.equals(set.getValue().getApproval(), this.user.getRole()) || this.user.getRole().equals(Role.General)) {
                                sendMessageToClient("[Available Requests]\nID: " + set.getKey() + "\nUser: " + set.getValue().getUser().getUsername() + "\nLocation: " + set.getValue().getLocation() + "\nReason: " + set.getValue().getReason());
                                Logger.log(getClientAddress(this.socket), "Listing", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " listed requests " + set.getKey() + ".");
                            }
                        }
                    }
                } else if (command.startsWith("/accept-request")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    if (this.user.getRole().equals(Role.Private)) {
                        sendMessageToClient("You don't have permission to accept requests. Please try again!");
                        continue;
                    }

                    synchronized (requests) {
                        for (Map.Entry<UUID, Request> set : requests.entrySet()) {
                            if (set.getKey().toString().equals(commandArgs[1])) {
                                if (set.getValue().getApproval().equals(this.user.getRole()) || this.user.getRole().equals(Role.General)) {
                                    synchronized (lock) {
                                        requestsAccepted++;
                                    }
                                    unicastMessage(set.getValue().getUser().getUsername(), "Your missile launch request to " + set.getValue().getLocation() + " with reason: " + set.getValue().getReason() + " has been accepted!");
                                    broadcastMessage("Missile by " + set.getValue().getUser().getUsername() + " launched to " + set.getValue().getLocation() + " with reason: " + set.getValue().getReason());
                                    Logger.log(getClientAddress(this.socket), "Accept", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " accepted request " + set.getKey() + ".");
                                } else {
                                    sendMessageToClient("You don't have permission to accept this request. Please try again!");
                                }
                            }
                        }
                    }
                } else if (command.startsWith("/reject-request")) {
                    String[] commandArgs = command.split("\\s+", 2);

                    if (isInvalidCommand(commandArgs.length, 2)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    if (this.user.getRole().equals(Role.Private)) {
                        sendMessageToClient("You don't have permission to reject requests. Please try again!");
                        continue;
                    }

                    synchronized (requests) {
                        for (Map.Entry<UUID, Request> set : requests.entrySet()) {
                            if (set.getKey().toString().equals(commandArgs[1])) {
                                if (set.getValue().getApproval().equals(this.user.getRole()) || this.user.getRole().equals(Role.General)) {
                                    synchronized (lock) {
                                        requestsRejected++;
                                    }
                                    unicastMessage(set.getValue().getUser().getUsername(), "Your missile launch request to " + set.getValue().getLocation() + " with reason: " + set.getValue().getReason() + " has been rejected!");
                                    Logger.log(getClientAddress(this.socket), "Reject", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " rejected request " + set.getKey() + ".");
                                } else {
                                    sendMessageToClient("You don't have permission to reject this request. Please try again!");
                                }
                            }
                        }
                    }
                } else if (command.startsWith("/promote")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    if (isInvalidRole(commandArgs[2])) {
                        sendMessageToClient("Invalid role. Please try again!");
                        continue;
                    }

                    if (!this.user.getRole().equals(Role.General)) {
                        sendMessageToClient("You don't have permission to promote. Please try again!");
                        continue;
                    }

                    synchronized (clients) {
                        for (Map.Entry<ClientHandler, User> entry : clients.entrySet()) {
                            if (entry.getValue().getUsername().equals(commandArgs[1])) {
                                entry.getValue().setRole(Role.valueOf(commandArgs[2]));
                                Logger.log(getClientAddress(this.socket), "Promote", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " promoted user " + commandArgs[1] + " to " + commandArgs[2] + ".");
                            }
                        }
                    }
                } else if (command.startsWith("/demote")) {
                    String[] commandArgs = command.split("\\s+", 3);

                    if (isInvalidCommand(commandArgs.length, 3)) {
                        sendMessageToClient("Invalid command. Please try again!");
                        continue;
                    }

                    if (isInvalidRole(commandArgs[2])) {
                        sendMessageToClient("Invalid role. Please try again!");
                        continue;
                    }

                    if (!this.user.getRole().equals(Role.General)) {
                        sendMessageToClient("You don't have permission to demote. Please try again!");
                        continue;
                    }

                    synchronized (clients) {
                        for (Map.Entry<ClientHandler, User> entry : clients.entrySet()) {
                            if (entry.getValue().getUsername().equals(commandArgs[1])) {
                                entry.getValue().setRole(Role.valueOf(commandArgs[2]));
                                Logger.log(getClientAddress(this.socket), "Demote", "User (" + this.user.getRole() + ")" + this.user.getUsername() + " demoted user " + commandArgs[1] + " to " + commandArgs[2] + ".");
                            }
                        }
                    }
                } else {
                    sendMessageToClient("Invalid command. Please try again!");
                }
            }
        }

        /**
         * Sends available commands related to messages, offensive actions, and management to the client.
         */
        private void sendMessageToClientCommands() {
            sendMessageToClient(CommandsMenu.MessageCommands());
            sendMessageToClient(CommandsMenu.OffensiveCommands());
            sendMessageToClient(CommandsMenu.ManagementCommands());
        }

        /**
         * Checks if the provided role string is valid.
         *
         * @param role The role string to check.
         * @return {@code true} if the role is valid, {@code false} otherwise.
         */
        private boolean isInvalidRole(String role) {
            return !Role.Private.name().equals(role) && !Role.Sergeant.name().equals(role) && !Role.Lieutenant.name().equals(role) && !Role.General.name().equals(role);
        }

        /**
         * Checks if the command arguments length is valid.
         *
         * @param commandArgsLength The length of command arguments.
         * @param maxArgsLength     The maximum allowed length of command arguments.
         * @return {@code true} if the length is invalid, {@code false} otherwise.
         */
        private boolean isInvalidCommand(int commandArgsLength, int maxArgsLength) {
            return commandArgsLength != maxArgsLength;
        }

        /**
         * Sends a message to the connected client.
         *
         * @param message The message to send.
         */
        private void sendMessageToClient(String message) {
            try {
                this.bufferedWriter.write(message);
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            } catch (Exception e) {
                handleException("An unexpected error has occurred during sending message to client!", e);
            }
        }

        /**
         * Handles sending private messages to a specific user.
         *
         * @param username The username of the recipient.
         * @param message  The message to send.
         */
        private void unicastMessage(String username, String message) {
            synchronized (clients) {
                boolean userOnline = false;

                for (Map.Entry<ClientHandler, User> entry : clients.entrySet()) {
                    if (entry.getValue().getUsername().equals(username)) {
                        userOnline = true;
                        try {
                            entry.getKey().bufferedWriter.write("[%s] [Say] (%s)%s: %s%n".formatted(getCurrentTime(), this.user.getRole(), this.user.getUsername(), message));
                            entry.getKey().bufferedWriter.newLine();
                            entry.getKey().bufferedWriter.flush();
                        } catch (Exception e) {
                            handleException("An unexpected error has occurred during broadcasting message!", e);
                        }
                    }
                }

                if (!userOnline) {
                    synchronized (offlineMessages) {
                        if (!offlineMessages.containsKey(username)) {
                            offlineMessages.put(username, new ArrayList<>());
                        }
                        offlineMessages.get(username).add("[%s] [Say] (%s)%s: %s%n".formatted(getCurrentTime(), this.user.getRole(), this.user.getUsername(), message));
                    }
                }
            }
        }

        /**
         * Handles broadcasting messages to users with a specific role.
         *
         * @param role    The role of the recipients.
         * @param message The message to broadcast.
         */
        private void multicastMessage(Role role, String message) {
            synchronized (clients) {
                for (Map.Entry<ClientHandler, User> entry : clients.entrySet()) {
                    if (entry.getValue().getRole().equals(role)) {
                        try {
                            entry.getKey().bufferedWriter.write("[%s] [Rank %s] (%s)%s: %s%n".formatted(getCurrentTime(), role.toString(), this.user.getRole(), this.user.getUsername(), message));
                            entry.getKey().bufferedWriter.newLine();
                            entry.getKey().bufferedWriter.flush();
                        } catch (Exception e) {
                            handleException("An unexpected error has occurred during broadcasting message!", e);
                        }
                    }
                }
            }
        }

        /**
         * Handles broadcasting messages to all connected clients except the sender.
         *
         * @param message The message to broadcast.
         */
        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (Map.Entry<ClientHandler, User> entry : clients.entrySet()) {
                    if (entry.getKey() != this) {
                        try {
                            entry.getKey().bufferedWriter.write("[%s] [All] (%s)%s: %s%n".formatted(getCurrentTime(), this.user.getRole(), this.user.getUsername(), message));
                            entry.getKey().bufferedWriter.newLine();
                            entry.getKey().bufferedWriter.flush();
                        } catch (Exception e) {
                            handleException("An unexpected error has occurred during broadcasting message!", e);
                        }
                    }
                }
            }
        }

        /**
         * Handles broadcasting messages to all clients in a specific chat room.
         *
         * @param roomName The name of the chat room.
         * @param message  The message to broadcast.
         */
        private void broadcastMessageRoom(String roomName, String message) {
            synchronized (rooms) {
                if (rooms.containsKey(roomName) && rooms.get(roomName).contains(this)) {
                    for (ClientHandler client : rooms.get(roomName)) {
                        if (client != this) {
                            try {
                                client.bufferedWriter.write("[%s] [Room %s] (%s)%s: %s%n".formatted(getCurrentTime(), roomName, this.user.getRole(), this.user.getUsername(), message));
                                client.bufferedWriter.newLine();
                                client.bufferedWriter.flush();
                            } catch (Exception e) {
                                handleException("An unexpected error has occurred during broadcasting message!", e);
                            }
                        }
                    }
                } else {
                    sendMessageToClient("The room doesn't exist. Please try again!");
                }
            }
        }

        /**
         * Closes the client handler by closing the socket, buffered writer, and buffered reader.
         */
        private void close() {
            try {
                if (this.socket != null) this.socket.close();
                if (this.bufferedWriter != null) this.bufferedWriter.close();
                if (this.bufferedReader != null) this.bufferedReader.close();
                synchronized (rooms) {
                    for (Map.Entry<String, List<ClientHandler>> set : rooms.entrySet()) {
                        set.getValue().remove(this);
                    }
                }
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

    /**
     * The {@code ActiveMembersTask} class is a TimerTask used for periodically notifying
     * active users, specifically users with the role of General, about the total number
     * of active users on the server.
     * It runs at a specified interval to provide updates.
     * This class is designed to be used within a server application.
     *
     * @author Your Name
     * @version 1.0
     */
    private class ActiveMembersTask extends TimerTask {
        /**
         * Executes the task, notifying active users about the total number of active users.
         */
        @Override
        public void run() {
            synchronized (clients) {
                for (Map.Entry<ClientHandler, User> entry : clients.entrySet()) {
                    if (entry.getValue() != null && entry.getValue().getRole().equals(Role.General)) {
                        try {
                            entry.getKey().bufferedWriter.write("[%s] [SERVER] Active users: %s%n".formatted(getCurrentTime(), clients.size()));
                            entry.getKey().bufferedWriter.newLine();
                            entry.getKey().bufferedWriter.flush();
                        } catch (Exception e) {
                            System.err.println("An unexpected error has occurred during broadcasting message!\n" + e.getMessage());
                            System.exit(1);
                        }
                    }
                }
            }
        }
    }

    /**
     * The {@code RequestsTask} class is a TimerTask used for periodically notifying
     * users about the number of pending, accepted, and rejected requests on the server.
     * It runs at a specified interval to provide updates.
     * This class is designed to be used within a server application.
     *
     * @author Your Name
     * @version 1.0
     */
    private class RequestsTask extends TimerTask {
        /**
         * Executes the task, notifying users about the number of pending, accepted,
         * and rejected requests on the server.
         */
        @Override
        public void run() {
            synchronized (clients) {
                for (Map.Entry<ClientHandler, User> entry : clients.entrySet()) {
                    try {
                        entry.getKey().bufferedWriter.write("[%s] [SERVER] Requests pending: %s, Requests accepted: %s, Requests rejected: %s%n".formatted(getCurrentTime(), requests.size(), requestsAccepted, requestsRejected));
                        entry.getKey().bufferedWriter.newLine();
                        entry.getKey().bufferedWriter.flush();
                    } catch (Exception e) {
                        System.err.println("An unexpected error has occurred during broadcasting message!\n" + e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
    }
}
