# Download Gson Library

## Langkah 1: Download Gson

Download file `gson-2.10.1.jar` dari Maven Central:
https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

## Langkah 2: Simpan ke Folder lib

Simpan file yang sudah didownload ke folder:
`MemoryGameJava/lib/gson-2.10.1.jar`

## Langkah 3: Verifikasi

Pastikan struktur folder menjadi:

```
MemoryGameJava/
├── lib/
│   └── gson-2.10.1.jar
├── src/
│   └── ...
└── compile.bat
```

## Alternatif: Download via Command Line

### Windows (PowerShell):

```powershell
cd MemoryGameJava/lib
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar" -OutFile "gson-2.10.1.jar"
```

### Windows (curl):

```bash
cd MemoryGameJava/lib
curl -O https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
```

### Linux/Mac:

```bash
cd MemoryGameJava/lib
wget https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
```

Setelah download selesai, Anda bisa lanjut compile dengan `compile.bat`
