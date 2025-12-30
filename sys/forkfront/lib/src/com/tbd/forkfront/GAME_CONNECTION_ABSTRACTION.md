# ForkFront Game Connection Abstraction Layer

## Overview

This abstraction layer decouples the ForkFront UI from the game implementation, allowing support for both local (JNI) and remote (SSH/Telnet) NetHack games.

## Architecture

```
┌─────────────────────────────────────┐
│      ForkFront Activity             │
│   (UI, Input, Rendering)            │
└────────────┬────────────────────────┘
             │
             │ uses
             ▼
┌─────────────────────────────────────┐
│    IGameConnection Interface        │
│  (sendKey, sendPosition, etc.)      │
└──────┬──────────────────┬───────────┘
       │                  │
       │ implements       │ implements
       │                  │
┌──────▼──────────┐  ┌───▼──────────────┐
│ LocalGame       │  │ RemoteGame       │
│ Connection      │  │ Connection       │
│ (JNI)           │  │ (SSH/Telnet)     │
└─────────────────┘  └──────────────────┘
```

## Components

### 1. IGameConnection Interface
Main abstraction interface defining all game operations:
- `start(dataDir)` - Initialize and start the game
- `sendKey(char)` - Send keyboard input
- `sendDirectionalKey(char)` - Send directional input
- `sendPosition(x, y)` - Send map click
- `sendLine(String)` - Send text input
- `sendMenuSelection(int[])` - Send menu selections
- `isConnected()` - Check connection status
- `isReady()` - Check if ready for input
- `disconnect()` - Close connection

### 2. LocalGameConnection
Implementation for local games via JNI:
- Wraps existing NetHackIO JNI functionality
- Loads native library (nethack, slashem, unnethack)
- Runs game in separate thread
- Command queue for thread-safe communication

### 3. RemoteGameConnection
Stub for remote SSH/Telnet connections:
- Phase 2 implementation (not yet functional)
- Will handle SSH authentication
- Will parse terminal output (VT100)
- Will convert commands to key sequences

### 4. IGameEventHandler Interface
Callback interface for game events:
- `onCreateWindow(winid, type)`
- `onPutString(winid, attr, text)`
- `onReady()`
- `onConnected()`
- `onDisconnected()`
- `onError(message)`

### 5. GameConnectionFactory
Factory class for creating connections:
- `createLocalConnection(handler, libraryName)`
- `createSSHConnection(handler, host, port, username, password)`
- `createConnection(handler, config)` - Create from config object

### 6. GameConnectionException
Exception class for connection errors

## Usage Examples

### Local Connection
```java
// Create handler
Handler handler = new Handler(Looper.getMainLooper());

// Create local connection
IGameConnection connection = GameConnectionFactory.createLocalConnection(
    handler, 
    "nethack"  // or "slashem", "unnethack"
);

// Set event handler
connection.setHandler(myEventHandler);

// Start game
try {
    connection.start("/data/data/com.tbd.NetHack/nethackdir");
} catch (GameConnectionException e) {
    Log.print("Failed to start: " + e.getMessage());
}

// Send commands
connection.sendKey('i');  // inventory
connection.sendPosition(10, 5);  // click on map
```

### Remote Connection (Phase 2)
```java
// Create SSH connection
IGameConnection connection = GameConnectionFactory.createSSHConnection(
    handler,
    "hardfought.org",
    22,
    "player",
    "password"
);

// Start connection
try {
    connection.start("");  // dataDir not needed for remote
} catch (GameConnectionException e) {
    Log.print("Connection failed: " + e.getMessage());
}
```

### Using Configuration
```java
// Create config
GameConnectionFactory.ConnectionConfig config = 
    GameConnectionFactory.ConnectionConfig.forLocal("nethack", "NetHack");

// Create connection from config
IGameConnection connection = 
    GameConnectionFactory.createConnection(handler, config);
```

## Integration with NetHackIO

The existing `NetHackIO` class will be refactored to use `IGameConnection`:

**Before:**
```java
public class NetHackIO {
    private native void RunNetHack(String path);
    // Direct JNI calls
}
```

**After:**
```java
public class NetHackIO {
    private IGameConnection mConnection;
    
    public void setConnection(IGameConnection connection) {
        this.mConnection = connection;
        this.mConnection.setHandler(mNhHandler);
    }
    
    public void sendKeyCmd(char key) {
        mConnection.sendKey(key);
    }
}
```

## Phase 1 Status: ✅ COMPLETE

- ✅ IGameConnection interface defined
- ✅ LocalGameConnection implemented (wraps JNI)
- ✅ RemoteGameConnection stub created
- ✅ GameConnectionFactory created
- ✅ IGameEventHandler interface defined
- ✅ GameConnectionException defined
- ✅ Documentation complete

## Next Steps (Phase 2)

1. Add SSH library dependency (JSch or sshj)
2. Implement RemoteGameConnection:
   - SSH authentication
   - Terminal emulation (VT100 parser)
   - Output → UI event mapping
3. Test with hardfought.org

## Benefits

- ✅ Clean separation of concerns
- ✅ Easy to test (can mock connections)
- ✅ Supports both local and remote games
- ✅ Non-breaking (existing code can be gradually migrated)
- ✅ Extensible (can add Telnet, WebSocket, etc.)

## Files Created

```
sys/forkfront/lib/src/com/tbd/forkfront/
├── IGameConnection.java              (2.3 KB)
├── IGameEventHandler.java            (1.6 KB)
├── GameConnectionException.java      (0.3 KB)
├── LocalGameConnection.java          (7.1 KB)
├── RemoteGameConnection.java         (3.3 KB)
├── GameConnectionFactory.java        (4.2 KB)
└── GAME_CONNECTION_ABSTRACTION.md    (this file)
```

Total: ~18.8 KB of new abstraction code
