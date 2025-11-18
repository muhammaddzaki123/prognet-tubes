# 🌐 Multiplayer Setup Guide - Cross Device

## 📱 Bermain di 2 Device Berbeda

Panduan ini untuk bermain Memory Game di **2 laptop/PC berbeda** yang terhubung ke **jaringan WiFi yang sama**.

---

## 🖥️ Setup Server (Device 1)

### 1. Jalankan Server

**Windows:**

```cmd
cd MemoryGameJava
run-server.bat
```

**Mac/Linux:**

```bash
cd MemoryGameJava
chmod +x run-server.sh
./run-server.sh
```

### 2. Catat IP Address Server

Server akan menampilkan IP address saat startup:

**Contoh output:**

```
===================================
   Memory Game Server
===================================

Starting server on port 5000...
UDP broadcast on port 5001

Server IP addresses:
  - 192.168.1.105    ← CATAT IP INI!

Share above IP with clients to connect
===================================
```

**📝 Catat IP address** (contoh: `192.168.1.105`) dan share ke Player 2!

---

## 💻 Setup Client (Device 2 - Player yang Join)

### 1. Jalankan Client

**Windows:**

```cmd
cd MemoryGameJava
run-client.bat
```

**Mac/Linux:**

```bash
cd MemoryGameJava
chmod +x run-client.sh
./run-client.sh
```

### 2. Input Server IP

Jika auto-connect gagal, akan muncul dialog:

```
❌ Cannot connect to server automatically

Enter Server IP Address:
[192.168.1.   ]

💡 Find server IP with: ipconfig (Windows) or ifconfig (Mac/Linux)
📶 Both devices must be on the same network
```

**Masukkan IP server** yang dicatat di step sebelumnya!

### 3. Join Room

Setelah connect:

1. Klik **"Join Room"**
2. Masukkan **nama Anda**
3. Masukkan **Room Code** dari Host (Player 1)
4. Klik **"Join"**

---

## 🎮 Setup Client di Device 1 (Host/Player 1)

Setelah server running, buka **terminal baru** di Device 1:

**Windows:**

```cmd
cd MemoryGameJava
run-client.bat
```

**Mac/Linux:**

```bash
./run-client.sh
```

Client akan otomatis connect ke `localhost` (server di device yang sama).

### Buat Room:

1. Klik **"Create Room"**
2. Masukkan **nama Anda**
3. Pilih **grid size** (4x4 atau 6x6)
4. Klik **"Create"**
5. **Share Room Code** ke Player 2 (Device 2)

---

## 🔄 Mengubah Server IP (Optional)

Jika ingin ganti server IP tanpa restart client:

1. Di **Home Screen**, klik **"⚙ Change Server IP"**
2. Masukkan IP baru
3. Klik **OK**

---

## 🐛 Troubleshooting

### ❌ "Cannot connect to server"

**Penyebab:** Server belum running atau IP salah

**Solusi:**

1. Pastikan server running di Device 1
2. Cek IP address server benar
3. Pastikan kedua device di WiFi yang sama
4. Coba ping server dari client:
   ```bash
   ping 192.168.1.105
   ```

---

### ❌ "Connection refused"

**Penyebab:** Firewall block port 5000

**Windows Firewall:**

1. Control Panel → Windows Defender Firewall
2. Advanced Settings → Inbound Rules
3. New Rule → Port → TCP → Port 5000
4. Allow connection → Apply

**Mac Firewall:**

```bash
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --add /usr/bin/java
```

**Linux Firewall:**

```bash
sudo ufw allow 5000/tcp
sudo ufw allow 5001/udp
```

---

### ❌ "Room not found"

**Penyebab:** Client connect ke server berbeda atau room expired

**Solusi:**

1. Pastikan kedua client connect ke server yang sama
2. Cek di Home Screen: "📶 Connected to: [IP]"
3. Room code valid selama 1 jam
4. Host harus sudah create room sebelum Guest join

---

### ❓ Cara Cek IP Server?

**Windows:**

```cmd
ipconfig
```

Lihat di **IPv4 Address** (biasanya 192.168.x.x atau 10.0.x.x)

**Mac:**

```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

**Linux:**

```bash
hostname -I
```

---

## 📊 Network Topology

```
        WiFi Router (192.168.1.1)
              /           \
             /             \
    Device 1              Device 2
  (Server + Client)       (Client)
  192.168.1.105          192.168.1.106
       |                      |
   Port 5000                  |
   (Game Server) ←────────────┘
       |
   Port 5001
   (UDP Broadcast)
```

---

## ✅ Testing Checklist

Sebelum bermain, pastikan:

- [ ] ✅ Kedua device terhubung ke WiFi yang sama
- [ ] ✅ Server running di Device 1 (lihat console output)
- [ ] ✅ IP server sudah dicatat dan dishare
- [ ] ✅ Client di Device 2 berhasil connect (cek status di Home Screen)
- [ ] ✅ Client di Device 1 berhasil connect (localhost)
- [ ] ✅ Firewall allow port 5000 dan 5001
- [ ] ✅ Bisa ping antar device
- [ ] ✅ Host create room dan dapat room code
- [ ] ✅ Guest join dengan room code yang benar

---

## 🎯 Example Scenario

**Contoh: Andi (Device 1) vs Budi (Device 2)**

### Device 1 (Andi - Host):

```
1. run-server.bat
   → Server IP: 192.168.1.105

2. run-client.bat (terminal baru)
   → Auto connect to localhost
   → Create Room → "Andi" → 4x4
   → Room Code: ABC123

3. Share ke Budi: "192.168.1.105" dan "ABC123"
```

### Device 2 (Budi - Guest):

```
1. run-client.bat
   → Dialog muncul: Input "192.168.1.105"
   → Connect berhasil!

2. Join Room → "Budi" → "ABC123"
   → Masuk Waiting Room

3. Tunggu Andi klik "Start Game"
```

### Game Mulai:

- Andi (Player 1) main duluan
- Budi (Player 2) nunggu giliran
- Score sync real-time di kedua device
- Winner ditampilkan saat game selesai

---

## 🔒 Security Note

- Server tidak pakai authentication (untuk demo)
- Jangan expose ke internet (LAN only)
- Room code 6 digit = 1 juta kombinasi (cukup aman untuk LAN)

---

## 📚 Related Docs

- `README.md` - Setup lengkap project
- `QUICKSTART.md` - Panduan cepat local
- `ARCHITECTURE.md` - Diagram network
- `SETUP.md` - Troubleshooting detail

---

**🎉 Selamat bermain dengan teman di device berbeda!**
