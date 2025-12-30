package com.tbd.forkfront;

import android.os.Handler;

/**
 * Factory for creating game connections.
 * Centralizes the creation of local and remote game connections.
 */
public class GameConnectionFactory {
    
    /**
     * Create a local game connection
     * @param handler Android Handler for UI updates
     * @param libraryName Native library name (e.g., "nethack", "slashem", "unnethack")
     * @return Local game connection
     */
    public static IGameConnection createLocalConnection(Handler handler, String libraryName) {
        return new LocalGameConnection(handler, libraryName);
    }
    
    /**
     * Create a remote SSH connection
     * @param handler Android Handler for UI updates
     * @param host Server hostname
     * @param port SSH port (typically 22)
     * @param username Username for authentication
     * @param password Password for authentication
     * @return Remote game connection
     */
    public static IGameConnection createSSHConnection(
            Handler handler, 
            String host, 
            int port, 
            String username, 
            String password) {
        return new RemoteGameConnection(handler, host, port, username, password);
    }
    
    /**
     * Configuration class for connection parameters
     */
    public static class ConnectionConfig {
        public IGameConnection.ConnectionType type;
        public String libraryName;  // For local connections
        public String host;         // For remote connections
        public int port;            // For remote connections
        public String username;     // For remote connections
        public String password;     // For remote connections
        public String variant;      // Game variant (nethack, slashem, etc.)
        
        public static ConnectionConfig forLocal(String libraryName, String variant) {
            ConnectionConfig config = new ConnectionConfig();
            config.type = IGameConnection.ConnectionType.LOCAL;
            config.libraryName = libraryName;
            config.variant = variant;
            return config;
        }
        
        public static ConnectionConfig forSSH(
                String host, 
                int port, 
                String username, 
                String password,
                String variant) {
            ConnectionConfig config = new ConnectionConfig();
            config.type = IGameConnection.ConnectionType.SSH;
            config.host = host;
            config.port = port;
            config.username = username;
            config.password = password;
            config.variant = variant;
            return config;
        }
    }
    
    /**
     * Create a connection from configuration
     * @param handler Android Handler for UI updates
     * @param config Connection configuration
     * @return Game connection
     * @throws GameConnectionException if configuration is invalid
     */
    public static IGameConnection createConnection(Handler handler, ConnectionConfig config) 
            throws GameConnectionException {
        
        switch (config.type) {
            case LOCAL:
                if (config.libraryName == null || config.libraryName.isEmpty()) {
                    throw new GameConnectionException("Library name required for local connection");
                }
                return createLocalConnection(handler, config.libraryName);
                
            case SSH:
                if (config.host == null || config.host.isEmpty()) {
                    throw new GameConnectionException("Host required for SSH connection");
                }
                return createSSHConnection(
                    handler, 
                    config.host, 
                    config.port > 0 ? config.port : 22,
                    config.username != null ? config.username : "",
                    config.password != null ? config.password : ""
                );
                
            case TELNET:
                throw new GameConnectionException("Telnet connections not yet supported");
                
            default:
                throw new GameConnectionException("Unknown connection type: " + config.type);
        }
    }
}
