# Fitur Export PDF Master Pelanggan

## Deskripsi
Fitur export PDF telah ditambahkan pada form Master Pelanggan untuk mengekspor seluruh data pelanggan ke dalam file PDF dengan tampilan yang sama dengan Laporan Transaksi.

## File yang Dibuat/Diubah

### 1. File Baru
- **`src/main/resources/report/Pelanggan_report.jrxml`**
  - Template JasperReports untuk laporan data pelanggan
  - Berisi layout tabel dengan kolom: ID, Nama, Alamat, No HP, Member, dan Cabang
  - Menggunakan style dan format yang sama dengan Transactions_report.jrxml
  - **Logo**: invoice_logo.png (onErrorType="Blank" - tidak akan error jika logo tidak ada)
  - **Alamat**: Jl. Pancasila II, Cicadas, Kec. Gn. Putri, Kab. Bogor
  - **No. Telp**: 081292873797
  - **Font**: SansSerif (universal, tidak perlu install)

### 2. File yang Dimodifikasi
- **`src/main/java/form/MasterPelanggan.java`**
  - Menambahkan tombol "Export PDF"
  - Menambahkan import untuk JasperReports
  - Menambahkan method `exportToPDF()`
  - Menyesuaikan layout untuk menampung tombol baru

## Cara Menggunakan

1. Buka form **Master Pelanggan** di aplikasi
2. Klik tombol **"Export PDF"** yang berada di sebelah tombol Hapus
3. Dialog file chooser akan muncul
4. Pilih lokasi penyimpanan dan nama file (default: `Laporan_Pelanggan_[tanggal]_[waktu].pdf`)
5. Klik **Save**
6. File PDF akan di-generate dan pesan konfirmasi akan muncul

## Detail Teknis

### Template PDF mencakup (SAMA dengan Transactions Report):
- **Header**: 
  - Logo Bozz Laundry (kiri atas, 65x60px)
  - Nama: "Bozz Laundry"
  - Alamat: "Jl. Pancasila II, Cicadas"
  - Kota: "Kec. Gn. Putri, Kab. Bogor"
  - Telepon: "081292873797"
  - Tanggal cetak (kanan atas)
- **Garis pemisah horizontal**
- **Judul**: "Laporan Data Pelanggan" (center, bold, 16pt)
- **Tabel Data** dengan kolom:
  - ID Pelanggan (40px)
  - Nama Pelanggan (110px)
  - Alamat (130px)
  - No HP (85px)
  - Jenis Member - Regular/VIP uppercase (90px)
  - Nama Cabang (100px)
- **Footer**: "THANKYOU FOR YOUR BUSINESS" (center, italic, background abu-abu)
- **Page Size**: A4 (595 x 842)
- **Margins**: 20px semua sisi

### Query SQL yang Digunakan:
```sql
SELECT
    mp.id_pelanggan,
    mp.nama_pelanggan,
    mp.alamat,
    mp.no_hp,
    mp.jenis_member,
    mc.nama_cabang
FROM master_pelanggan mp
LEFT JOIN master_cabang mc ON mp.id_cabang = mc.id_cabang
ORDER BY mp.id_pelanggan
```

### Dependencies:
- JasperReports 7.0.6 (sudah ada di pom.xml)
- JasperReports-PDF 7.0.6 (BARU - sudah ditambahkan)
- MySQL Connector
- iText (untuk PDF generation)

## Fitur Export PDF:
✅ Export semua data pelanggan ke PDF
✅ Format tabel yang rapi dan profesional
✅ Tampilan SAMA dengan Laporan Transaksi
✅ Logo Bozz Laundry (onErrorType="Blank")
✅ Alamat lengkap sesuai data asli
✅ Font SansSerif (universal, tidak perlu install)
✅ Pemilihan lokasi penyimpanan file
✅ Nama file otomatis dengan timestamp
✅ Validasi file dan error handling

## Layout Tombol (dari kiri ke kanan):
1. **+ Tambah Pelanggan** (30, 80, 190x35)
2. **Edit** (240, 80, 100x35)
3. **Hapus** (360, 80, 100x35)
4. **Export PDF** (480, 80, 120x35) ← BARU!
5. **Search** label (620, 80, 60x35)
6. **Search** text field (680, 80, 150x35)

## Catatan:
- File report (.jrxml) akan dikompilasi secara otomatis saat export
- Tidak perlu pre-compile menjadi .jasper
- PDF akan berisi semua data pelanggan di database (tidak terfilter berdasarkan pencarian)
- Format tanggal menggunakan format: EEEEE dd MMMMM yyyy (contoh: Sunday 07 June 2026)
- Jenis member ditampilkan dalam huruf kapital (REGULAR/VIP)
- Logo menggunakan onErrorType="Blank" jadi tidak akan error jika file logo tidak ditemukan
- Tabel menggunakan style dengan baris bergantian (abu-abu dan putih)

## Perbandingan dengan Transactions Report:
| Fitur | Transactions Report | Pelanggan Report |
|-------|---------------------|------------------|
| Logo | ✅ invoice_logo.png | ✅ invoice_logo.png |
| Alamat | ✅ Sama | ✅ Sama |
| Telepon | ✅ Sama | ✅ Sama |
| Font | SansSerif | SansSerif |
| Layout Header | ✅ Sama | ✅ Sama |
| Garis Pemisah | ✅ Sama | ✅ Sama |
| Footer Style | ✅ Sama | ✅ Sama |
| Page Size | A4 | A4 |
| Table Style | Striped rows | Striped rows |

## Troubleshooting:
1. **"File report tidak ditemukan"**: Pastikan file `Pelanggan_report.jrxml` ada di folder `src/main/resources/report/`
2. **"missing jasperReports PDF extension"**: Jalankan "Clean and Build" di NetBeans untuk download dependency
3. **"Font not found"**: Sudah fixed dengan SansSerif (universal)
4. **"Logo not found"**: Logo set dengan onErrorType="Blank", tidak akan error
5. **Koneksi database gagal**: Periksa koneksi database di class `Koneksi.java`

