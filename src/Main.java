import server.GameServer;
import client.gui.MainFrame;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            // Start server
            System.out.println("Starting Memory Game Server...");
            GameServer server = new GameServer();

            // Shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                server.shutdown();
            }));

            server.start();
        } else {
            // Start client
            System.out.println("Starting Memory Game Client...");
            MainFrame.main(args);
        }
    }
}
