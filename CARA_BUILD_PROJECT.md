# 🚀 Cara Build & Run Project

## ❗ PENTING: Dependency Baru Telah Ditambahkan!

Dependency **jasperreports-pdf** telah ditambahkan ke `pom.xml` untuk fitur Export PDF.

---

## 📋 Langkah-Langkah Build Project

### Menggunakan NetBeans (Cara Termudah)

1. **Buka NetBeans**
2. **Open Project** → Pilih folder `BozzLaundry`
3. **Tunggu NetBeans memproses dependencies** (ada loading indicator di pojok kanan bawah)
4. **Klik kanan** pada project di panel Projects
5. Pilih **"Clean and Build"**
6. Tunggu sampai build selesai (akan muncul "BUILD SUCCESSFUL")
7. **Run Project**

### Jika Build Berhasil Tapi Masih Error Saat Export PDF

Kemungkinan dependency belum ter-download sempurna. Lakukan:

1. **Tutup NetBeans**
2. **Hapus folder cache Maven:**
   ```bash
   rm -rf ~/.m2/repository/net/sf/jasperreports/jasperreports-pdf/
   ```
3. **Buka kembali NetBeans**
4. **Build ulang project** (Clean and Build)
5. NetBeans akan download ulang dependency yang diperlukan

### Alternatif: Force Download Dependencies

Di NetBeans:
1. Window → Services
2. Maven Repositories → Expand "Local Repository"
3. Klik kanan → "Rebuild Index"
4. Tunggu selesai
5. Build ulang project

---

## 🔍 Mengecek Apakah Dependency Sudah Terinstall

Cek folder ini di Finder atau Terminal:
```bash
ls -la ~/.m2/repository/net/sf/jasperreports/jasperreports-pdf/7.0.6/
```

Jika ada file `jasperreports-pdf-7.0.6.jar` berarti dependency sudah terdownload.

---

## ✅ Cara Test Fitur Export PDF

Setelah build berhasil:

1. **Run aplikasi**
2. **Login** ke sistem
3. Buka menu **Master Pelanggan**
4. Klik tombol **"Export PDF"**
5. Pilih lokasi untuk menyimpan file
6. Klik **Save**
7. PDF akan ter-generate!

---

## 🛠 Troubleshooting

### Error: "missing jasperReports PDF extension"
**Solusi:**
- Build ulang project dengan "Clean and Build"
- Pastikan koneksi internet aktif
- Cek apakah file `.jar` ada di folder `~/.m2/repository/`

### Error: "Font not found"
**Solusi:** 
- Sudah diperbaiki dengan menggunakan font SansSerif (universal)
- Tidak perlu install font tambahan

### Error: "File report tidak ditemukan"
**Solusi:**
- Pastikan file `Pelanggan_report.jrxml` ada di folder:
  `src/main/resources/report/`
- Build ulang project

### Build Gagal di NetBeans
**Solusi:**
1. File → Project Properties → Sources
2. Pastikan Source/Binary Format = JDK 17 atau lebih tinggi
3. Clean and Build ulang

---

## 📦 Dependencies yang Dibutuhkan

✅ mysql-connector-j (8.3.0)  
✅ itextpdf (5.5.13.3)  
✅ jasperreports (7.0.6)  
✅ **jasperreports-pdf (7.0.6)** ← BARU!  
✅ itext (2.1.7)  

Semua dependencies akan otomatis didownload oleh NetBeans/Maven.

---

## 💡 Tips

- **Gunakan NetBeans** untuk development, karena project ini sudah dikonfigurasi untuk NetBeans
- **Pastikan Java 17 atau lebih tinggi** sudah terinstall
- **Koneksi internet** diperlukan untuk download dependencies
- Setelah build berhasil, dependency akan disimpan di cache `~/.m2/repository/` dan tidak perlu download ulang

---

## 🎯 Expected Result

Setelah semua langkah dilakukan:
- ✅ Build SUCCESS
- ✅ Aplikasi berjalan normal
- ✅ Tombol "Export PDF" muncul di form Master Pelanggan
- ✅ Klik "Export PDF" → PDF berhasil di-generate
- ✅ PDF berisi tabel data pelanggan yang rapi

---

**Jika masih ada masalah, pastikan:**
1. Java 17+ terinstall
2. NetBeans up-to-date
3. Koneksi internet stabil
4. Folder project tidak read-only
