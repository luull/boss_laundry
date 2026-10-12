# Cara Install Dependency Baru (jasperreports-pdf)

## ❗ Dependency Baru Telah Ditambahkan

File `pom.xml` telah diupdate dengan dependency baru:
```xml
<dependency>
    <groupId>net.sf.jasperreports</groupId>
    <artifactId>jasperreports-pdf</artifactId>
    <version>7.0.6</version>
</dependency>
```

## 📋 Cara Install Dependency

### Opsi 1: Menggunakan NetBeans (Recommended)
1. **Buka project** di NetBeans
2. **Klik kanan** pada project di panel Projects
3. Pilih **"Build with Dependencies"** atau **"Clean and Build"**
4. NetBeans akan otomatis mendownload dependency yang baru

### Opsi 2: Menggunakan IntelliJ IDEA
1. **Buka project** di IntelliJ IDEA
2. Klik tombol **"Load Maven Changes"** (icon refresh di pojok kanan atas)
3. Atau pilih **Maven → Reload project**
4. IntelliJ akan otomatis mendownload dependency yang baru

### Opsi 3: Menggunakan Maven Command Line
Jika Maven sudah terinstall di sistem:
```bash
cd /Users/macbook/luull/dev/COLLAGE/BozzLaundry
mvn clean install
```

### Opsi 4: Install Maven (Jika belum ada)
Untuk macOS, install dengan Homebrew:
```bash
brew install maven
```

Kemudian jalankan:
```bash
cd /Users/macbook/luull/dev/COLLAGE/BozzLaundry
mvn clean compile
```

## ✅ Verifikasi Installation

Setelah dependency berhasil didownload, coba:
1. **Restart aplikasi** jika sedang berjalan
2. Buka form **Master Pelanggan**
3. Klik tombol **"Export PDF"**
4. PDF seharusnya berhasil di-generate tanpa error

## 🔍 Troubleshooting

### Jika masih error "missing jasperReports PDF extension"
1. Pastikan dependency sudah benar-benar terdownload
2. Check folder: `~/.m2/repository/net/sf/jasperreports/jasperreports-pdf/7.0.6/`
3. Rebuild project: Clean → Build
4. Restart IDE

### Jika build gagal
- Pastikan koneksi internet aktif (untuk download dependency)
- Hapus folder `target/` dan rebuild
- Check apakah ada error di pom.xml

## 📦 Dependency yang Ditambahkan

**jasperreports-pdf** adalah extension untuk JasperReports yang khusus menangani:
- Export ke format PDF
- Font handling untuk PDF
- Layout dan formatting PDF
- Compression dan optimization PDF

Tanpa dependency ini, JasperReports tidak bisa mengekspor ke PDF.

## 🎯 Next Steps

Setelah dependency terinstall:
1. ✅ Fitur Export PDF akan berfungsi normal
2. ✅ PDF dapat di-generate dengan format yang baik
3. ✅ Font SansSerif akan ter-render dengan benar
4. ✅ Tabel data pelanggan akan tampil rapi di PDF

---

**Note:** Dependency ini kompatibel dengan JasperReports 7.0.6 yang sudah ada di project.
