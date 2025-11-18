# Cara Menjalankan Memory Game

## Persiapan

### 1. Download Gson Library

Lihat instruksi di `lib/DOWNLOAD_GSON.md`

### 2. Compile Project

Jalankan file batch untuk compile:

```bash
compile.bat
```

Atau compile manual:

```bash
javac -d bin -cp "lib/*" src/common/*.java
javac -d bin -cp "lib/*;bin" src/server/*.java
javac -d bin -cp "lib/*;bin" src/client/*.java
javac -d bin -cp "lib/*;bin" src/client/gui/*.java
javac -d bin -cp "lib/*;bin" src/Main.java
```

## Menjalankan Server

### Opsi 1: Menggunakan Batch File

```bash
run-server.bat
```

### Opsi 2: Manual

```bash
java -cp "bin;lib/*" Main server
```

Server akan berjalan di:

- **TCP Port**: 5000 (untuk game communication)
- **UDP Port**: 5001 (untuk server discovery)

## Menjalankan Client

**PENTING**: Jalankan 2 instance client untuk multiplayer!

### Client 1 (Host):

1. Jalankan: `run-client.bat`
2. Pilih "Create Room"
3. Atur nama, grid size, dan theme
4. Share room code ke pemain lain
5. Tunggu Player 2 join
6. Klik "Start Game"

### Client 2 (Guest):

1. Jalankan: `run-client.bat` (di terminal/CMD baru)
2. Pilih "Join Room"
3. Masukkan room code dari Host
4. Tunggu host memulai game

## Troubleshooting

### Server tidak bisa start

- Pastikan port 5000 dan 5001 tidak digunakan aplikasi lain
- Cek firewall settings
- Jalankan dengan admin privileges jika perlu

### Client tidak bisa connect

- Pastikan server sudah running
- Jika localhost gagal, coba masukkan IP manual
- Untuk LAN: pastikan di jaringan WiFi yang sama
- Disable antivirus/firewall sementara untuk testing

### Compile error

- Pastikan gson-2.10.1.jar ada di folder `lib/`
- Pastikan menggunakan JDK 8 atau lebih baru
- Check JAVA_HOME environment variable

### Game lag atau disconnect

- Check network connection
- Jangan minimize/background client saat bermain
- Restart server jika terjadi masalah

## Testing Checklist

- [ ] Server start successfully
- [ ] Client auto-discover server (UDP)
- [ ] Create room → generate 6-digit code
- [ ] Join room dengan code valid
- [ ] Waiting room menampilkan kedua player
- [ ] Game start → cards ter-shuffle
- [ ] Flip card → animasi berjalan
- [ ] Match detection bekerja
- [ ] Turn switching otomatis
- [ ] Score update real-time
- [ ] Game over → result screen
- [ ] Play again → kembali ke home

## Fitur yang Diimplementasikan

### Stream (File I/O & Network)

✅ BufferedReader/PrintWriter untuk JSON messages
✅ ObjectInputStream/ObjectOutputStream ready (jika perlu)
✅ Logging ke console

### Multithreading

✅ Thread pool di server untuk handle multiple clients
✅ ClientHandler thread per connection
✅ UDP broadcast thread terpisah
✅ SwingUtilities.invokeLater() untuk UI updates

### Socket Programming

✅ ServerSocket untuk accept connections (TCP)
✅ Socket untuk client-server communication
✅ DatagramSocket untuk UDP discovery
✅ Error handling & timeout

## Network Ports

- **5000**: TCP - Game communication
- **5001**: UDP - Server discovery broadcast

## Struktur Message (JSON)

### Create Room

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

### Flip Card

```json
{
	"type": "FLIP_CARD",
	"data": {
		"cardId": 5
	}
}
```

Lihat dokumentasi lengkap di README.md
