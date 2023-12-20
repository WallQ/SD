package Server;

import Entities.User;
import Models.SynchronizedArrayList;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * The BroadcastSender class is a thread that sends broadcast messages to all connected clients.
 * <p>
 * It uses the DatagramSocket class to send the messages and sets the broadcast address and port number.
 * <p>
 * It also has a constructor that takes in a SynchronizedArrayList of UserManagerThreads as input.
 *
 * @author Sérgio Moreira
 */
public class BroadcastSender extends Thread {
    private final String BROADCAST_ADDRESS = "230.0.0.1";
    private final int PORT_NUMBER = 3000;
    private DatagramSocket broadcastSocket;
    //Aqui o User é o UserManagerThread que tem que criar a thread para o user
    private SynchronizedArrayList<User> listUsers;

    /**
     Constructor for the BroadcastSender class.
     @param users a SynchronizedArrayList of UserManagerThreads representing the connected clients.
     */
    public BroadcastSender(SynchronizedArrayList<User> users) {
        try {
            this.broadcastSocket = new DatagramSocket();
            this.broadcastSocket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.listUsers = users;
    }

    /**
     The run method for the thread. Sends a broadcast message to all connected clients if the list of users is not empty.
     It uses the DatagramPacket class to package the message and sends it via the DatagramSocket.
     */
    @Override
    public void run() {
        System.out.println("BroadCast Thread Iniciada!");
        if (!listUsers.isEmpty()) {
            try {
                String serverMsg = "Suspensão GLOBAL do tráfego da rede!";
                byte[] buf = serverMsg.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(BROADCAST_ADDRESS), PORT_NUMBER);
                this.broadcastSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.broadcastSocket.close();
    }
}
