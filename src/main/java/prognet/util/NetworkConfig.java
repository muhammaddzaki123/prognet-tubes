package prognet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * NetworkConfig manages network configuration settings for the game client.
 * Settings are persisted to a properties file in the user's home directory.
 */
public class NetworkConfig {

    private static final Logger LOGGER = Logger.getLogger(NetworkConfig.class.getName());
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".prognet";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "network.properties";

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    private static NetworkConfig instance;
    private Properties properties;

    private NetworkConfig() {
        properties = new Properties();
        loadConfig();
    }

    /**
     * Get the singleton instance of NetworkConfig
     */
    public static synchronized NetworkConfig getInstance() {
        if (instance == null) {
            instance = new NetworkConfig();
        }
        return instance;
    }

    /**
     * Load configuration from file, or create default if doesn't exist
     */
    private void loadConfig() {
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                LOGGER.info("Network configuration loaded from: " + CONFIG_FILE);
            } catch (IOException e) {
                LOGGER.warning("Failed to load network config, using defaults: " + e.getMessage());
                setDefaults();
            }
        } else {
            LOGGER.info("Network configuration file not found, creating defaults");
            setDefaults();
            saveConfig();
        }
    }

    /**
     * Set default configuration values
     */
    private void setDefaults() {
        properties.setProperty("server.host", DEFAULT_HOST);
        properties.setProperty("server.port", String.valueOf(DEFAULT_PORT));
    }

    /**
     * Save configuration to file
     */
    public void saveConfig() {
        File configDir = new File(CONFIG_DIR);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Prognet Network Configuration");
            LOGGER.info("Network configuration saved to: " + CONFIG_FILE);
        } catch (IOException e) {
            LOGGER.severe("Failed to save network config: " + e.getMessage());
        }
    }

    /**
     * Get server host address
     */
    public String getServerHost() {
        return properties.getProperty("server.host", DEFAULT_HOST);
    }

    /**
     * Set server host address
     */
    public void setServerHost(String host) {
        if (host == null || host.trim().isEmpty()) {
            host = DEFAULT_HOST;
        }
        properties.setProperty("server.host", host.trim());
    }

    /**
     * Get server port
     */
    public int getServerPort() {
        try {
            return Integer.parseInt(properties.getProperty("server.port", String.valueOf(DEFAULT_PORT)));
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid port number in config, using default: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    /**
     * Set server port
     */
    public void setServerPort(int port) {
        if (port < 1 || port > 65535) {
            LOGGER.warning("Invalid port number: " + port + ", using default: " + DEFAULT_PORT);
            port = DEFAULT_PORT;
        }
        properties.setProperty("server.port", String.valueOf(port));
    }

    /**
     * Reset to default configuration
     */
    public void resetToDefaults() {
        setDefaults();
        saveConfig();
        LOGGER.info("Network configuration reset to defaults");
    }
}
