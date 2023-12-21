package Server;

import java.io.IOException;
import java.net.*;

/**
 * MultiCastSender is a class that extends the Thread class and can be used to send multicast messages.
 * It uses the DatagramSocket class to send the messages, and the InetAddress class to specify the
 * multicast group.
 *
 * @author Carlos Leite
 */
public class MultiCastSender extends Thread {
    private DatagramSocket datagramSocket;
    private String message;
    private String groupPort;

    public MultiCastSender(String message, String groupPort) {
        try {
            this.datagramSocket = new DatagramSocket(4445);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.message = message;
        this.groupPort = groupPort;
    }

    /**
     * The run method for the thread. Creates a DatagramPacket with the specified message, group,
     * and port, and sends it using the DatagramSocket.
     */
    @Override
    public void run () {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] buf = message.getBytes();
        InetAddress group = null;
        try {
            group = InetAddress.getByName(this.groupPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4000);
        try {
            this.datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.datagramSocket.close();
    }
}
