package com.tbd.forkfront;

/**
 * Interface for NetHack game connections.
 * Abstracts local (JNI) and remote (SSH/Telnet) game implementations.
 */
public interface IGameConnection {
    
    /**
     * Connection types supported
     */
    enum ConnectionType {
        LOCAL,      // Native JNI connection to local game
        SSH,        // SSH connection to remote server
        TELNET      // Telnet connection to remote server
    }
    
    /**
     * Start the game connection
     * @param dataDir Data directory for game files
     * @throws GameConnectionException if connection fails
     */
    void start(String dataDir) throws GameConnectionException;
    
    /**
     * Send a keyboard command to the game
     * @param key Character key to send
     */
    void sendKey(char key);
    
    /**
     * Send a directional key command
     * @param key Directional key character
     */
    void sendDirectionalKey(char key);
    
    /**
     * Send a position command (click on map)
     * @param x X coordinate
     * @param y Y coordinate
     */
    void sendPosition(int x, int y);
    
    /**
     * Send a text line (e.g., for naming items, answering questions)
     * @param line Text to send
     */
    void sendLine(String line);
    
    /**
     * Send a menu selection
     * @param selected Array of selected menu items
     */
    void sendMenuSelection(int[] selected);
    
    /**
     * Request to save the game state
     */
    void saveState();
    
    /**
     * Abort the current game
     */
    void abort();
    
    /**
     * Check if the connection is active
     * @return true if connected and ready
     */
    boolean isConnected();
    
    /**
     * Check if the connection is ready for input
     * @return true if ready to accept commands
     */
    boolean isReady();
    
    /**
     * Wait until the connection is ready for input
     */
    void waitReady();
    
    /**
     * Get the connection type
     * @return Connection type (LOCAL, SSH, or TELNET)
     */
    ConnectionType getConnectionType();
    
    /**
     * Disconnect and clean up resources
     */
    void disconnect();
    
    /**
     * Set callback handler for game events
     * @param handler Handler for processing game output
     */
    void setHandler(IGameEventHandler handler);
}
