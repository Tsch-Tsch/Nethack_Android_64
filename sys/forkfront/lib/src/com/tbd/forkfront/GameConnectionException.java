package com.tbd.forkfront;

/**
 * Exception thrown when game connection fails
 */
public class GameConnectionException extends Exception {
    
    public GameConnectionException(String message) {
        super(message);
    }
    
    public GameConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
