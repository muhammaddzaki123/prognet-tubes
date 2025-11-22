package prognet;

import prognet.network.server.GameServer;

/**
 * Standalone Server Launcher Run this class to start the game server separately
 * from the client application
 */
public class ServerApp {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Memory Game Server - Standalone Mode  ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();

        GameServer server = new GameServer();

        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SERVER] Shutting down gracefully...");
            server.shutdown();
        }));

        // Start the server
        server.start();
    }
}
