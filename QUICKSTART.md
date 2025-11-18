# 🎮 Quick Start Guide

## ⚡ Cepat Mulai (3 Langkah)

### 1️⃣ Download Gson Library

```bash
cd MemoryGameJava\lib
curl -O https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
```

Atau manual download: [Gson 2.10.1](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar)

**Catatan:** Animal icons menggunakan emoji Unicode (🐯🦥🦜) - sudah built-in, tidak perlu download!

### 2️⃣ Compile

```bash
compile.bat
```

### 3️⃣ Jalankan

**Terminal 1 - Server:**

```bash
run-server.bat
```

**Terminal 2 - Client 1 (Host):**

```bash
run-client.bat
```

**Terminal 3 - Client 2 (Guest):**

```bash
run-client.bat
```

## 🌐 Multiplayer di 2 Device Berbeda

**Ingin main dengan teman di laptop/PC berbeda?**

📖 Baca panduan lengkap di: **[MULTIPLAYER_SETUP.md](MULTIPLAYER_SETUP.md)**

**Quick steps:**

1. **Device 1:** Jalankan `run-server.bat` → catat IP (contoh: 192.168.1.105)
2. **Device 1:** Jalankan `run-client.bat` → Create Room → share room code
3. **Device 2:** Jalankan `run-client.bat` → input IP dari step 1 → Join Room

## 🎯 Cara Bermain

### Host (Player 1):

1. Klik **"Create Room"**
2. Masukkan nama Anda
3. Pilih grid size & theme
4. **Copy room code** (6 digit)
5. Share ke Player 2
6. Tunggu Player 2 join
7. Klik **"Start Game"**

### Guest (Player 2):

1. Klik **"Join Room"**
2. Masukkan nama Anda
3. Paste **room code** dari Host
4. Klik **"Join Room"**
5. Tunggu Host start game

### Saat Bermain:

- Giliran Anda? Klik 2 kartu
- Cocok? Dapat poin & main lagi
- Tidak cocok? Giliran lawan
- Yang paling banyak match = MENANG! 🏆

## 🔧 Troubleshooting Cepat

**Client tidak connect?**
→ Pastikan server sudah running di terminal 1

**Port sudah digunakan?**
→ Matikan aplikasi lain yang pakai port 5000/5001

**Compile error?**
→ Cek gson-2.10.1.jar ada di folder `lib/`

## 📋 Requirements

- ✅ Java JDK 8+
- ✅ Gson 2.10.1 library
- ✅ Windows (batch files) atau Linux/Mac (modify scripts)

## 📚 Dokumentasi Lengkap

- **README.md** - Penjelasan detail arsitektur & fitur
- **SETUP.md** - Panduan lengkap setup & troubleshooting
- **lib/DOWNLOAD_GSON.md** - Cara download library

## 🎓 Materi yang Diimplementasikan

✅ **Stream**: BufferedReader, PrintWriter, JSON messaging
✅ **Multithreading**: Thread pool, ClientHandler threads, UI threading
✅ **Socket**: TCP ServerSocket/Socket, UDP DatagramSocket

## 📞 Network Info

- Server TCP: `localhost:5000`
- Server UDP: `localhost:5001`
- Auto-discovery via UDP broadcast

---

**Selamat bermain! 🎉**
