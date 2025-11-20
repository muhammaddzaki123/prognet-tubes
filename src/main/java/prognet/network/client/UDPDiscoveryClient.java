package prognet.network.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

public class UDPDiscoveryClient {
    private static final Logger LOGGER = Logger.getLogger(UDPDiscoveryClient.class.getName());
    private static final int UDP_PORT = 5001;
    private static final int TIMEOUT = 5000;

    public static String discoverServer() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setSoTimeout(TIMEOUT);

            // Send discovery request
            byte[] sendData = "DISCOVER_SERVER".getBytes();
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    broadcastAddress,
                    UDP_PORT);

            socket.send(sendPacket);
            LOGGER.info("Sent UDP discovery broadcast");

            // Wait for response
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

            if (response.equals("MEMORY_GAME_SERVER")) {
                String serverIP = receivePacket.getAddress().getHostAddress();
                LOGGER.info("Server discovered at: " + serverIP);
                socket.close();
                return serverIP;
            }

            socket.close();

        } catch (Exception e) {
            LOGGER.warning("Server discovery failed: " + e.getMessage());
        }

        return null;
    }
}
