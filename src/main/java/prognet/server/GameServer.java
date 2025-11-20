package prognet.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class GameServer {
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 100;

    private ServerSocket serverSocket;
    private RoomManager roomManager;
    private ExecutorService threadPool;
    private UDPBroadcastServer udpServer;
    private boolean running;

    public GameServer() {
        this.roomManager = new RoomManager();
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.running = true;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            LOGGER.info("Memory Game Server started on port " + PORT);

            // Start UDP broadcast server
            udpServer = new UDPBroadcastServer();
            udpServer.start();

            // Start cleanup thread
            startCleanupThread();

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, roomManager);
                threadPool.execute(clientHandler);
            }

        } catch (IOException e) {
            if (running) {
                LOGGER.severe("Server error: " + e.getMessage());
            }
        } finally {
            shutdown();
        }
    }

    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(60000); // Every minute
                    roomManager.cleanupEmptyRooms();
                    LOGGER.info("Active rooms: " + roomManager.getRoomCount());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    public void shutdown() {
        running = false;

        if (udpServer != null) {
            udpServer.shutdown();
        }

        if (threadPool != null) {
            threadPool.shutdown();
        }

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            LOGGER.severe("Error closing server socket: " + e.getMessage());
        }

        LOGGER.info("Server shutdown complete");
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down server...");
            server.shutdown();
        }));

        server.start();
    }
}
