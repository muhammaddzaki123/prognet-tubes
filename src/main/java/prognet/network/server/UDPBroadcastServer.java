package prognet.network.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

public class UDPBroadcastServer extends Thread {
    private static final Logger LOGGER = Logger.getLogger(UDPBroadcastServer.class.getName());
    private static final int UDP_PORT = 5001;
    private static final String BROADCAST_MESSAGE = "MEMORY_GAME_SERVER";

    private DatagramSocket socket;
    private boolean running;

    public UDPBroadcastServer() {
        this.running = true;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(UDP_PORT);
            socket.setBroadcast(true);

            LOGGER.info("UDP Broadcast Server started on port " + UDP_PORT);

            byte[] buffer = new byte[1024];

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());

                if (received.equals("DISCOVER_SERVER")) {
                    // Send response back to client
                    byte[] responseData = BROADCAST_MESSAGE.getBytes();
                    InetAddress clientAddress = packet.getAddress();
                    int clientPort = packet.getPort();

                    DatagramPacket response = new DatagramPacket(
                            responseData,
                            responseData.length,
                            clientAddress,
                            clientPort);

                    socket.send(response);
                    LOGGER.info("Sent discovery response to " + clientAddress);
                }
            }

        } catch (Exception e) {
            if (running) {
                LOGGER.severe("UDP Broadcast error: " + e.getMessage());
            }
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    public void shutdown() {
        running = false;
        if (socket != null) {
            socket.close();
        }
    }
}
