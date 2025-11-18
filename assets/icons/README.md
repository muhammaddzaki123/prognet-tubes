# Animal Icons

## Status

✅ **Game sekarang menggunakan Unicode Emoji!**

Tidak perlu download atau menambahkan file PNG lagi. Game menggunakan built-in emoji dari sistem operasi yang otomatis ter-render.

### Emoji yang Digunakan:

- 🐯 Tiger
- 🦥 Sloth
- 🦜 Toucan
- 🦧 Orangutan
- 🐒 Lemur
- 🦏 Rhino
- 🐊 Crocodile
- 🐼 Red Panda
- 🐗 Warthog
- 🦌 Antelope

### Keuntungan Emoji:

✅ Tidak perlu file eksternal
✅ Otomatis support multi-platform (Windows, Mac, Linux)
✅ Ukuran aplikasi lebih kecil
✅ Render otomatis oleh sistem
✅ Selalu up-to-date dengan OS

---

## (Opsional) Untuk Menambahkan Icon PNG Custom

Jika Anda tetap ingin menggunakan icon gambar PNG custom:

### 1. Simpan icon PNG di folder ini

Format nama file:

- `tiger.png`
- `sloth.png`
- `toucan.png`
- `orangutan.png`
- `lemur.png`
- `rhino.png`
- `crocodile.png`
- `redpanda.png`
- `warthog.png`
- `antelope.png`

### 2. Rekomendasi Ukuran

- Ukuran: 128x128 px atau 256x256 px
- Format: PNG dengan transparent background
- Style: Flat design, kartun, child-friendly

### 3. Sumber Icon Gratis

- [Flaticon](https://www.flaticon.com/) - search "animal flat icon"
- [IconFinder](https://www.iconfinder.com/free_icons)
- [Freepik](https://www.freepik.com/vectors/animal-icon)
- [Icons8](https://icons8.com/icons/set/animal)

### 4. Export dari React Project

Jika Anda ingin convert SVG dari project React:

1. Buka file SVG (contoh: `src/components/animals/TigerIcon.tsx`)
2. Copy SVG path/content
3. Gunakan online converter SVG → PNG:
   - [CloudConvert](https://cloudconvert.com/svg-to-png)
   - [Online-Convert](https://image.online-convert.com/convert-to-png)
4. Set output size 256x256px
5. Download dan rename sesuai nama hewan

### 5. Modifikasi Code untuk Load PNG

Edit file `MemoryCardPanel.java`, method `loadAnimalImage()`:

```java
private void loadAnimalImage() {
    try {
        String iconPath = "assets/icons/" + card.getAnimal() + ".png";
        File iconFile = new File(iconPath);

        if (iconFile.exists()) {
            animalImage = ImageIO.read(iconFile);
            // Resize to 60x60
            Image scaledImage = animalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            animalImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = animalImage.createGraphics();
            g.drawImage(scaledImage, 0, 0, null);
            g.dispose();
        } else {
            // Fallback to text representation
            createTextBasedIcon();
        }
    } catch (IOException e) {
        e.printStackTrace();
        createTextBasedIcon();
    }
}

private void createTextBasedIcon() {
    // Current implementation (text-based)
    // ... existing code ...
}
```

Jangan lupa tambahkan import:

```java
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
```

## Placeholder Icons

Untuk sementara, icon berbasis teks dengan warna berbeda sudah cukup untuk testing gameplay.

Warna yang digunakan:

- 🐯 Tiger: Orange
- 🦥 Sloth: Brown
- 🦜 Toucan: Yellow
- 🦧 Orangutan: Red-Brown
- 🐵 Lemur: Gray
- 🦏 Rhino: Dark Gray
- 🐊 Crocodile: Green
- 🐼 Red Panda: Dark Red
- 🐗 Warthog: Brown
- 🦌 Antelope: Tan
