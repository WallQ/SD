package Server;


import Entities.User;
import Models.SynchronizedArrayList;

import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server class is responsible for the main functionality of the server application.
 *
 * @author Carlos Leite
 */
public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final SynchronizedArrayList<User> users = new SynchronizedArrayList<>();
    private static ServerSocket serverSocket;
    private static final int port = 2048;

    /**
     * The main method of the Server class. It creates a ServerSocket on a specified port,
     * starts a ServerThread, and handles any IOExceptions that may occur.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server started on port " + port);

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getCause());
            System.exit(-1);
        }
    }
}
