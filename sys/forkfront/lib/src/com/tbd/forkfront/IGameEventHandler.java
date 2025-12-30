package com.tbd.forkfront;

import android.os.Handler;

/**
 * Interface for handling game events and output.
 * This is implemented by NH_Handler to process game output from any connection type.
 */
public interface IGameEventHandler {
    
    /**
     * Get the Android Handler for posting UI updates
     * @return Android Handler instance
     */
    Handler getHandler();
    
    /**
     * Called when a window is created
     * @param winid Window ID
     * @param type Window type
     */
    void onCreateWindow(int winid, int type);
    
    /**
     * Called when a window is cleared
     * @param winid Window ID
     */
    void onClearWindow(int winid);
    
    /**
     * Called when a window should be displayed
     * @param winid Window ID
     * @param blocking Whether to block until dismissed
     */
    void onDisplayWindow(int winid, boolean blocking);
    
    /**
     * Called when a window is destroyed
     * @param winid Window ID
     */
    void onDestroyWindow(int winid);
    
    /**
     * Called when text is printed to a window
     * @param winid Window ID
     * @param attr Text attributes
     * @param text Text to display
     */
    void onPutString(int winid, int attr, String text);
    
    /**
     * Called when the game is ready for input
     */
    void onReady();
    
    /**
     * Called when an error occurs
     * @param message Error message
     */
    void onError(String message);
    
    /**
     * Called when connection is established
     */
    void onConnected();
    
    /**
     * Called when connection is lost
     */
    void onDisconnected();
}
