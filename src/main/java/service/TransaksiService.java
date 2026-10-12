package service;

import koneksi.Koneksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Logika bisnis transaksi laundry yang dipakai bersama oleh TransaksiLaundry
 * (admin/owner) dan KasirDashboard (kasir). Dengan memusatkan perhitungan harga
 * dan penyimpanan di sini, tidak ada lagi duplikasi rumus harga di banyak form.
 *
 * <p>Harga add-ons dipertahankan sama persis dengan implementasi lama agar
 * perilaku bisnis tidak berubah. Tanggal selesai dihitung dari estimasi_hari
 * layanan yang dipilih (data nyata), bukan angka tetap.
 */
public final class TransaksiService {

    private TransaksiService() {
    }

    /** Daftar add-ons beserta harga satuan (sesuai perilaku aplikasi lama). */
    public enum Addon {
        SEPREI("Seprei", 10000),
        SELIMUT("Selimut", 10000),
        BED_COVER("Bed Cover", 20000),
        BONEKA("Boneka", 10000),
        TAS("Tas", 20000),
        KARPET("Karpet", 30000);

        private final String label;
        private final int harga;

        Addon(String label, int harga) {
            this.label = label;
            this.harga = harga;
        }

        public String getLabel() {
            return label;
        }

        public int getHarga() {
            return harga;
        }
    }

    /** Hitung total: (harga layanan x qty) + total add-ons. */
    public static double hitungTotal(double harga, double qty,
            Map<Addon, Integer> addons) {
        double total = harga * qty;
        if (addons != null) {
            for (Map.Entry<Addon, Integer> e : addons.entrySet()) {
                total += (double) e.getKey().getHarga() * e.getValue();
            }
        }
        return total;
    }

    /** Rangkai deskripsi add-ons, mis. "Seprei x1, Tas x2, ". */
    public static String deskripsiAddon(Map<Addon, Integer> addons) {
        StringBuilder sb = new StringBuilder();
        if (addons != null) {
            for (Map.Entry<Addon, Integer> e : addons.entrySet()) {
                sb.append(e.getKey().getLabel()).append(" x")
                        .append(e.getValue()).append(", ");
            }
        }
        return sb.toString();
    }

    /** Daftar metode pembayaran yang didukung. */
    public static String[] metodeList() {
        return new String[]{"Tunai", "QRIS", "Transfer", "Debit"};
    }

    /** Hanya metode "Tunai" yang memerlukan input uang &amp; kembalian. */
    public static boolean isTunai(String metode) {
        return metode != null && metode.equalsIgnoreCase("Tunai");
    }

    /** Hitung kembalian untuk pembayaran tunai (0 untuk non-tunai). */
    public static double hitungKembalian(String metode, double total,
            double uangDiterima) {
        if (!isTunai(metode)) {
            return 0d;
        }
        return Math.max(0d, uangDiterima - total);
    }

    /**
     * Simpan transaksi baru beserta metode pembayaran. Tanggal selesai dihitung
     * otomatis dari estimasi layanan. Untuk pembayaran tunai, {@code uangDiterima}
     * adalah uang yang dibayarkan pelanggan dan kembalian dihitung otomatis;
     * untuk non-tunai (QRIS/Transfer/Debit) dianggap dibayar pas tanpa kembalian.
     * Mengembalikan kode invoice yang dihasilkan.
     *
     * <p>Operasi ini terdiri dari satu INSERT tunggal yang bersifat atomik,
     * sehingga tidak memerlukan blok transaksi multi-query.
     */
    public static String simpan(int idPelanggan, int idLayanan, double berat,
            double harga, Map<Addon, Integer> addons, int idCabang,
            String status, String metode, double uangDiterima)
            throws Exception {

        Connection conn = Koneksi.getKoneksi();
        int estimasi = ambilEstimasiHari(conn, idLayanan);
        double total = hitungTotal(harga, berat, addons);
        String desc = deskripsiAddon(addons);
        String invoice = "INV" + System.currentTimeMillis();
        LocalDate masuk = LocalDate.now();
        LocalDate selesai = masuk.plusDays(Math.max(0, estimasi));

        boolean tunai = isTunai(metode);
        double bayar = tunai ? uangDiterima : total;
        double kembalian = tunai ? Math.max(0d, uangDiterima - total) : 0d;

        String sql = "INSERT INTO transaksi_laundry (kode_invoice,tanggal_masuk,"
                + "tanggal_selesai,id_pelanggan,id_layanan,berat,total_harga,"
                + "status,id_cabang,addons,metode_bayar,bayar,kembalian) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, invoice);
            pst.setDate(2, java.sql.Date.valueOf(masuk));
            pst.setDate(3, java.sql.Date.valueOf(selesai));
            pst.setInt(4, idPelanggan);
            pst.setInt(5, idLayanan);
            pst.setDouble(6, berat);
            pst.setDouble(7, total);
            pst.setString(8, status);
            pst.setInt(9, idCabang);
            pst.setString(10, desc);
            pst.setString(11, metode == null ? "Tunai" : metode);
            pst.setDouble(12, bayar);
            pst.setDouble(13, kembalian);
            pst.executeUpdate();
        }
        return invoice;
    }

    /** Perbarui status transaksi berdasarkan kode invoice. */
    public static void updateStatus(String invoice, String status)
            throws Exception {
        Connection conn = Koneksi.getKoneksi();
        try (PreparedStatement pst = conn.prepareStatement(
                "UPDATE transaksi_laundry SET status=? WHERE kode_invoice=?")) {
            pst.setString(1, status);
            pst.setString(2, invoice);
            pst.executeUpdate();
        }
    }

    private static int ambilEstimasiHari(Connection conn, int idLayanan)
            throws Exception {
        try (PreparedStatement pst = conn.prepareStatement(
                "SELECT estimasi_hari FROM master_layanan WHERE id_layanan=?")) {
            pst.setInt(1, idLayanan);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("estimasi_hari");
                }
            }
        }
        return 0;
    }

    /** Status transaksi yang valid, urut sesuai alur kerja. */
    public static String[] statusList() {
        return new String[]{"proses", "dicuci", "disetrika", "selesai", "diambil"};
    }

    public static Map<Addon, Integer> newAddonMap() {
        return new LinkedHashMap<>();
    }
}
