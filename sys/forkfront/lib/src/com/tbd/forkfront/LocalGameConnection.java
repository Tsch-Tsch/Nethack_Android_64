package com.tbd.forkfront;

import android.os.Handler;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Local game connection using JNI to native NetHack library.
 * This wraps the existing NetHackIO JNI functionality.
 */
public class LocalGameConnection implements IGameConnection {
    
    private final Handler mHandler;
    private IGameEventHandler mEventHandler;
    private final Thread mGameThread;
    private final String mLibraryName;
    private final ConcurrentLinkedQueue<Cmd> mCmdQueue;
    private volatile Integer mIsReady = 0;
    private final Object mReadyMonitor = new Object();
    private String mDataDir;
    private boolean mIsConnected = false;
    
    // Command types for the queue
    private enum CmdType {
        KEY,
        DIR_KEY,
        POS,
        LINE,
        SELECT,
        SAVE_STATE,
        ABORT
    }
    
    private interface Cmd {
        CmdType type();
    }
    
    private static class KeyCmd implements Cmd {
        final char key;
        KeyCmd(char key) { this.key = key; }
        public CmdType type() { return CmdType.KEY; }
    }
    
    private static class DirKeyCmd implements Cmd {
        final char key;
        DirKeyCmd(char key) { this.key = key; }
        public CmdType type() { return CmdType.DIR_KEY; }
    }
    
    private static class PosCmd implements Cmd {
        final int x, y;
        PosCmd(int x, int y) { this.x = x; this.y = y; }
        public CmdType type() { return CmdType.POS; }
    }
    
    private static class LineCmd implements Cmd {
        final String line;
        LineCmd(String line) { this.line = line; }
        public CmdType type() { return CmdType.LINE; }
    }
    
    private static class SelectCmd implements Cmd {
        final int[] selected;
        SelectCmd(int[] selected) { this.selected = selected; }
        public CmdType type() { return CmdType.SELECT; }
    }
    
    private static class SaveStateCmd implements Cmd {
        public CmdType type() { return CmdType.SAVE_STATE; }
    }
    
    private static class AbortCmd implements Cmd {
        public CmdType type() { return CmdType.ABORT; }
    }
    
    /**
     * Constructor
     * @param handler Android Handler for UI thread
     * @param libraryName Name of native library to load (e.g., "nethack")
     */
    public LocalGameConnection(Handler handler, String libraryName) {
        this.mHandler = handler;
        this.mLibraryName = libraryName;
        this.mCmdQueue = new ConcurrentLinkedQueue<>();
        this.mGameThread = new Thread(mGameThreadRunnable, "LocalGameThread");
    }
    
    @Override
    public void start(String dataDir) throws GameConnectionException {
        if (mIsConnected) {
            throw new GameConnectionException("Already connected");
        }
        
        this.mDataDir = dataDir;
        
        try {
            mGameThread.start();
            mIsConnected = true;
            
            if (mEventHandler != null) {
                mEventHandler.onConnected();
            }
        } catch (Exception e) {
            throw new GameConnectionException("Failed to start local game", e);
        }
    }
    
    @Override
    public void sendKey(char key) {
        mCmdQueue.add(new KeyCmd(key));
    }
    
    @Override
    public void sendDirectionalKey(char key) {
        mCmdQueue.add(new DirKeyCmd(key));
    }
    
    @Override
    public void sendPosition(int x, int y) {
        mCmdQueue.add(new PosCmd(x, y));
    }
    
    @Override
    public void sendLine(String line) {
        mCmdQueue.add(new LineCmd(line));
    }
    
    @Override
    public void sendMenuSelection(int[] selected) {
        mCmdQueue.add(new SelectCmd(selected));
    }
    
    @Override
    public void saveState() {
        mCmdQueue.add(new SaveStateCmd());
    }
    
    @Override
    public void abort() {
        mCmdQueue.add(new AbortCmd());
    }
    
    @Override
    public boolean isConnected() {
        return mIsConnected && mGameThread.isAlive();
    }
    
    @Override
    public boolean isReady() {
        return mIsReady != 0;
    }
    
    @Override
    public void waitReady() {
        // Flush queue
        long endTime = System.currentTimeMillis() + 1000;
        while (mCmdQueue.peek() != null && endTime - System.currentTimeMillis() > 0) {
            Thread.yield();
        }
        
        synchronized (mReadyMonitor) {
            try {
                do {
                    mReadyMonitor.wait(10);
                } while (mIsReady == 0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.LOCAL;
    }
    
    @Override
    public void disconnect() {
        if (mIsConnected) {
            abort();
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
    
    // Game thread that runs the native code
    private final Runnable mGameThreadRunnable = new Runnable() {
        @Override
        public void run() {
            Log.print("LocalGameConnection: starting native process");
            
            try {
                System.loadLibrary(mLibraryName);
                runNetHackNative(mDataDir);
            } catch (Exception e) {
                Log.print("LocalGameConnection: EXCEPTION: " + e.getMessage());
                if (mEventHandler != null) {
                    mEventHandler.onError("Native game crashed: " + e.getMessage());
                }
                e.printStackTrace();
            }
            
            Log.print("LocalGameConnection: native process finished");
            mIsConnected = false;
            System.exit(0);
        }
    };
    
    // Process commands from the queue (called by native code)
    private char processCommand() {
        Cmd cmd = mCmdQueue.poll();
        if (cmd == null) {
            return 0;
        }
        
        switch (cmd.type()) {
            case KEY:
                return ((KeyCmd) cmd).key;
            case DIR_KEY:
                return ((DirKeyCmd) cmd).key;
            case ABORT:
                return '\033'; // ESC
            default:
                return 0;
        }
    }
    
    // JNI callback: game is ready for input
    private void onGameReady() {
        synchronized (mReadyMonitor) {
            mIsReady = 1;
            mReadyMonitor.notifyAll();
        }
        
        if (mEventHandler != null) {
            mEventHandler.onReady();
        }
    }
    
    // JNI callback: game is busy
    private void onGameBusy() {
        synchronized (mReadyMonitor) {
            mIsReady = 0;
        }
    }
    
    // Native methods (implemented in JNI)
    private native void runNetHackNative(String dataDir);
    private native void saveNetHackState();
}
