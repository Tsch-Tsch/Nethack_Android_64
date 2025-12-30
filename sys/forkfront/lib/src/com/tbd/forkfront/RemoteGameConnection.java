package com.tbd.forkfront;

import android.os.Handler;

/**
 * Remote game connection via SSH or Telnet.
 * This is a placeholder for Phase 2 implementation.
 */
public class RemoteGameConnection implements IGameConnection {
    
    private final Handler mHandler;
    private final ConnectionType mConnectionType;
    private IGameEventHandler mEventHandler;
    private boolean mIsConnected = false;
    
    // Connection parameters
    private String mHost;
    private int mPort;
    private String mUsername;
    private String mPassword;
    
    /**
     * Constructor for SSH connection
     */
    public RemoteGameConnection(Handler handler, String host, int port, String username, String password) {
        this.mHandler = handler;
        this.mConnectionType = ConnectionType.SSH;
        this.mHost = host;
        this.mPort = port;
        this.mUsername = username;
        this.mPassword = password;
    }
    
    @Override
    public void start(String dataDir) throws GameConnectionException {
        // TODO Phase 2: Implement SSH connection
        throw new GameConnectionException("Remote connections not yet implemented - coming in Phase 2");
    }
    
    @Override
    public void sendKey(char key) {
        // TODO Phase 2: Send key over SSH channel
        Log.print("RemoteGameConnection.sendKey: not implemented");
    }
    
    @Override
    public void sendDirectionalKey(char key) {
        // TODO Phase 2: Send directional key
        Log.print("RemoteGameConnection.sendDirectionalKey: not implemented");
    }
    
    @Override
    public void sendPosition(int x, int y) {
        // TODO Phase 2: Send position (may need to convert to key sequence)
        Log.print("RemoteGameConnection.sendPosition: not implemented");
    }
    
    @Override
    public void sendLine(String line) {
        // TODO Phase 2: Send text line over SSH
        Log.print("RemoteGameConnection.sendLine: not implemented");
    }
    
    @Override
    public void sendMenuSelection(int[] selected) {
        // TODO Phase 2: Send menu selection (convert to key sequence)
        Log.print("RemoteGameConnection.sendMenuSelection: not implemented");
    }
    
    @Override
    public void saveState() {
        // TODO Phase 2: Send save command
        sendKey('S');
    }
    
    @Override
    public void abort() {
        // TODO Phase 2: Send quit command
        sendKey('\033'); // ESC
    }
    
    @Override
    public boolean isConnected() {
        return mIsConnected;
    }
    
    @Override
    public boolean isReady() {
        // TODO Phase 2: Check if server is ready for input
        return mIsConnected;
    }
    
    @Override
    public void waitReady() {
        // TODO Phase 2: Wait for server prompt
    }
    
    @Override
    public ConnectionType getConnectionType() {
        return mConnectionType;
    }
    
    @Override
    public void disconnect() {
        if (mIsConnected) {
            // TODO Phase 2: Close SSH connection
            mIsConnected = false;
            
            if (mEventHandler != null) {
                mEventHandler.onDisconnected();
            }
        }
    }
    
    @Override
    public void setHandler(IGameEventHandler handler) {
        this.mEventHandler = handler;
    }
}
