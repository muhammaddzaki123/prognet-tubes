# Multiplayer Memory Game - Java Implementation

Implementasi multiplayer memory game menggunakan Java Socket Programming dengan GUI Swing/AWT.

## Fitur

- ✅ Server-client architecture dengan multithreading
- ✅ Room-based multiplayer system (6-digit room code)
- ✅ UDP broadcast untuk auto-discovery di WiFi yang sama
- ✅ GUI lengkap 7 layar (Swing/AWT)
- ✅ Protokol komunikasi JSON over Socket
- ✅ Turn-based game logic dengan sinkronisasi real-time
- ✅ 10 animal emoji Unicode (🐯🦥🦜🦧🐒🦏🐊🐼🐗🦌) - tidak perlu file eksternal!
- ✅ Error handling dan logging lengkap

## Teknologi

- Java Socket (TCP) untuk komunikasi game
- Java UDP untuk server discovery
- Multithreading untuk concurrent clients
- ObjectInputStream/ObjectOutputStream untuk data transfer
- Gson untuk JSON serialization
- Swing/AWT untuk GUI

## Cara Menjalankan

### 1. Download Dependencies

Download `gson-2.10.1.jar` dari [Maven Central](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar) dan simpan di folder `lib/`

**Catatan:** Icon hewan menggunakan Unicode emoji built-in, tidak perlu download file eksternal!

### 2. Compile

```bash
compile.bat
```

### 3. Jalankan Server

```bash
run-server.bat
```

### 4. Jalankan Client (2 instance untuk multiplayer)

```bash
run-client.bat
```

## Arsitektur

### Server Components

- **GameServer**: Main server socket (port 5000)
- **RoomManager**: Kelola rooms dan room codes
- **Room**: State management per room (grid, scores, turn)
- **ClientHandler**: Thread per client connection
- **UDPBroadcastServer**: Broadcast server info ke LAN (port 5001)

### Client Components

- **GameClient**: Socket connection ke server
- **UDPDiscoveryClient**: Scan local network untuk server
- **MainFrame**: CardLayout untuk navigasi layar
- **7 Screen Panels**: Home, CreateRoom, JoinRoom, WaitingRoom, GameBoard, GameResult, HowToPlay

### Protokol Komunikasi

JSON message format:

```json
{
	"type": "CREATE_ROOM",
	"data": {
		"playerName": "Player1",
		"gridSize": "4x4",
		"theme": "jungle"
	}
}
```

Message Types: CREATE_ROOM, JOIN_ROOM, START_GAME, FLIP_CARD, GAME_UPDATE, PLAYER_LEFT, ERROR

## Materi Pemrograman Internet yang Diimplementasikan

### 1. Stream

- `ObjectInputStream` / `ObjectOutputStream` untuk transfer data
- `BufferedReader` / `PrintWriter` untuk JSON messages
- File I/O untuk logging

### 2. Multithreading

- `Thread` per client di server (ClientHandler)
- `ExecutorService` untuk thread pool management
- `synchronized` untuk thread-safe room operations
- Concurrent updates ke GUI via `SwingUtilities.invokeLater()`

### 3. Socket

- `ServerSocket` untuk accept connections
- `Socket` untuk TCP communication
- `DatagramSocket` untuk UDP broadcast/discovery
- Error handling: connection timeout, socket closed, etc.

## Testing Checklist

- [ ] Server start & UDP broadcast
- [ ] Client discovery via UDP
- [ ] Create room → generate 6-digit code
- [ ] Join room → validate code
- [ ] Waiting room sync (2 players)
- [ ] Game start → shuffle cards
- [ ] Turn-based flip logic
- [ ] Match detection & score update
- [ ] Game completion & result screen
- [ ] Player disconnect handling
- [ ] Rejoin/Play again

## Struktur Kode

```
Server (port 5000) ← TCP Socket → Client GUI
       ↓
   RoomManager
       ↓
   Room (game state)
       ↓
   Broadcast → All clients in room
```

## Author

Created for Internet Programming course - Semester 7
