# 📋 Fitur Export PDF Master Layanan

## ✅ Deskripsi
Fitur export PDF telah ditambahkan pada form **Master Layanan** untuk mengekspor seluruh data layanan laundry ke dalam file PDF dengan tampilan profesional yang konsisten.

---

## 📂 File yang Dibuat/Diubah

### 1. **File Baru**
- **`src/main/resources/report/Layanan_report.jrxml`**
  - Template JasperReports untuk laporan data layanan
  - Kolom: ID, Nama Layanan, Kategori, Harga Min, Harga Max, Estimasi, Status
  - Logo dengan **onErrorType="Blank"** (tidak error jika logo tidak ada)
  - Alamat lengkap: Jl. Pancasila II, Cicadas, Kec. Gn. Putri, Kab. Bogor
  - No. Telp: 081292873797
  - Font: SansSerif (universal)

### 2. **File yang Dimodifikasi**
- **`src/main/java/form/MasterLayanan.java`**
  - ✅ Import JasperReports library
  - ✅ Tombol "Export PDF" (hijau, 120x40px)
  - ✅ Method `exportToPDF()`
  - ✅ Layout disesuaikan (tombol Search dipindah ke kanan)

---

## 🎨 Template PDF

### **Header (Clean & Professional):**
```
┌─────────────────────────────────────────┐
│ [LOGO]  Bozz Laundry                   │
│         Jl. Pancasila II, Cicadas      │
│         Kec. Gn. Putri, Kab. Bogor     │
│         081292873797                   │
├─────────────────────────────────────────┤
│                                         │
│       Laporan Data Layanan              │
│                                         │
└─────────────────────────────────────────┘
```

### **Tabel Data:**
| ID | Nama Layanan | Kategori | Harga Min | Harga Max | Estimasi | Status |
|----|-------------|----------|-----------|-----------|----------|--------|
| 1  | Cuci Komplit | KILOAN  | Rp 8,000  | Rp 8,000  | 3 Hari   | AKTIF  |
| 2  | Cuci Setrika | KILOAN  | Rp 10,000 | Rp 10,000 | 2 Hari   | AKTIF  |
| 3  | Seprei       | ADDONS  | Rp 10,000 | Rp 15,000 | 1 Hari   | AKTIF  |

### **Footer:**
```
┌─────────────────────────────────────────┐
│ Tanggal Cetak : Sunday 07 June 2026    │
│                                         │
│   THANKYOU FOR YOUR BUSINESS            │
└─────────────────────────────────────────┘
```

---

## 📊 Detail Kolom Tabel

| Kolom | Width | Format | Alignment |
|-------|-------|--------|-----------|
| **ID** | 35px | Number | Center |
| **Nama Layanan** | 130px | Text | Left |
| **Kategori** | 70px | UPPERCASE | Center |
| **Harga Min** | 80px | Rp #,### | Right |
| **Harga Max** | 80px | Rp #,### | Right |
| **Estimasi** | 80px | "X Hari" | Center |
| **Status** | 80px | UPPERCASE | Center |

---

## 🎯 Query SQL

```sql
SELECT
    id_layanan,
    nama_layanan,
    kategori,
    harga_min,
    harga_max,
    estimasi_hari,
    status
FROM master_layanan
ORDER BY id_layanan
```

---

## 🔘 Layout Tombol Baru

### **Before:**
```
[+ Tambah Layanan] [Edit] [Hapus]          [Search] [______]
     190x40         100x40  100x40                   180x40
```

### **After:**
```
[+ Tambah Layanan] [Edit] [Hapus] [Export PDF]  [Search] [______]
     190x40         100x40  100x40   120x40                180x40
```

### **Posisi & Warna:**
| Tombol | X | Y | Width | Height | Color |
|--------|---|---|-------|--------|-------|
| + Tambah Layanan | 30 | 80 | 190 | 40 | Blue (#2563EB) |
| Edit | 240 | 80 | 100 | 40 | Orange (#F59E0B) |
| Hapus | 360 | 80 | 100 | 40 | Red (#EF4444) |
| **Export PDF** | **480** | **80** | **120** | **40** | **Green (#16A34A)** |
| Search Label | 620 | 80 | 70 | 40 | - |
| Search Field | 690 | 80 | 180 | 40 | - |

---

## 🚀 Cara Menggunakan

1. **Jalankan aplikasi** dan login
2. Buka menu **Master Layanan**
3. Klik tombol **"Export PDF"** (tombol hijau)
4. Dialog **file chooser** akan muncul
5. Pilih lokasi penyimpanan
6. Nama default: `Laporan_Layanan_YYYYMMDD_HHMMSS.pdf`
7. Klik **Save**
8. ✅ PDF ter-generate!
9. Pesan konfirmasi: "Laporan berhasil di-export ke: [path]"

---

## 🎨 Fitur Template

### ✅ **Logo dengan Error Handling**
- Path: `"invoice_logo.png"`
- **onErrorType="Blank"** - tidak akan error jika logo tidak ditemukan
- Ukuran: 65x60px
- Position: Kiri atas

### ✅ **Format Harga**
- Format: `Rp #,###`
- Contoh: Rp 8,000 / Rp 10,000
- Alignment: Right (rata kanan)

### ✅ **Status & Kategori**
- Format: UPPERCASE
- Contoh: AKTIF, KILOAN, ADDONS
- Alignment: Center

### ✅ **Estimasi**
- Format: "[number] Hari"
- Contoh: 3 Hari, 2 Hari
- Alignment: Center

### ✅ **Tanggal Cetak**
- Format: EEEEE dd MMMMM yyyy
- Contoh: Sunday 07 June 2026
- Posisi: Footer kiri atas

### ✅ **Footer Message**
- Text: "THANKYOU FOR YOUR BUSINESS"
- Background: #E6E8E9 (abu-abu muda)
- Font: Serif, 9pt, italic
- Position: Bottom center

---

## 🔧 Dependencies

Sudah ditambahkan di `pom.xml`:
- ✅ JasperReports 7.0.6
- ✅ JasperReports-PDF 7.0.6
- ✅ MySQL Connector 8.3.0
- ✅ iText 5.5.13.3

---

## 📋 Checklist Fitur

- ✅ Export semua data layanan ke PDF
- ✅ Template profesional dengan logo
- ✅ Logo dengan error handling (onErrorType="Blank")
- ✅ Alamat lengkap perusahaan
- ✅ Format harga Rupiah (Rp #,###)
- ✅ Status & Kategori uppercase
- ✅ Estimasi dengan format "X Hari"
- ✅ Tanggal cetak di footer
- ✅ Footer "THANKYOU FOR YOUR BUSINESS"
- ✅ Font SansSerif (universal)
- ✅ File chooser untuk pilih lokasi
- ✅ Nama file otomatis dengan timestamp
- ✅ Error handling lengkap
- ✅ Konfirmasi setelah export berhasil

---

## ⚠️ Troubleshooting

### **1. "File report tidak ditemukan"**
**Solusi:**
- Pastikan file `Layanan_report.jrxml` ada di:
  ```
  src/main/resources/report/Layanan_report.jrxml
  ```
- Build ulang: **Clean and Build** di NetBeans

### **2. "missing jasperReports PDF extension"**
**Solusi:**
- Build ulang project di NetBeans
- Dependency akan otomatis ter-download

### **3. "Font not found"**
**Solusi:**
- ✅ Sudah fixed! Menggunakan **SansSerif** (universal)
- Tidak perlu install font tambahan

### **4. Logo tidak muncul tapi tidak error**
**Ini normal!**
- Logo menggunakan **onErrorType="Blank"**
- Jika logo tidak ditemukan, area logo akan kosong
- PDF tetap ter-generate tanpa error

**Untuk menampilkan logo:**
- Copy logo ke: `src/main/resources/report/invoice_logo.png`
- Build ulang project

### **5. Koneksi database gagal**
**Solusi:**
- Cek koneksi di `koneksi/Koneksi.java`
- Pastikan MySQL running
- Pastikan database `bozz_laundry` ada

---

## 🎯 Hasil Akhir

### **PDF Header:**
- Logo Bozz Laundry (kiri atas) - tidak error jika tidak ada
- Nama perusahaan (19pt, bold)
- Alamat: Jl. Pancasila II, Cicadas
- Kota: Kec. Gn. Putri, Kab. Bogor
- Telepon: 081292873797
- Garis pemisah
- Judul: "Laporan Data Layanan" (center, 16pt, bold)

### **PDF Body:**
- Tabel dengan 7 kolom
- Baris bergantian (striped: abu-abu/putih)
- Border 0.5px solid
- Data terurut berdasarkan ID

### **PDF Footer:**
- Tanggal cetak (kiri atas)
- "THANKYOU FOR YOUR BUSINESS" (center, background abu-abu)

---

## 📝 Catatan

- ✅ PDF akan berisi **semua data layanan** (tidak terfilter berdasarkan search)
- ✅ Logo menggunakan **onErrorType="Blank"** untuk menghindari error
- ✅ Harga otomatis diformat Rupiah dengan thousand separator
- ✅ Kategori dan Status otomatis uppercase untuk konsistensi
- ✅ Estimasi otomatis menambahkan kata "Hari"
- ✅ File .jrxml dikompilasi otomatis saat export (tidak perlu pre-compile)
- ✅ Konsisten dengan report Pelanggan & Transaksi

---

## 🎉 Status

**✅ SELESAI - Siap digunakan!**

Fitur Export PDF Master Layanan telah berhasil diimplementasikan dengan:
- Template profesional ✅
- Error handling lengkap ✅
- Logo tidak akan error ✅
- Layout konsisten ✅
- Format data rapi ✅

**Build project dan test fitur Export PDF sekarang!** 🚀
