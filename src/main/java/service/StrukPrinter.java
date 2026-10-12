package service;

import koneksi.Koneksi;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Pembuat struk PDF bergaya thermal receipt: lebar sempit (80mm), font
 * monospace (Courier) berukuran kecil, header di tengah, dan nominal rata
 * kanan dengan garis pemisah putus-putus — seperti struk kasir pada umumnya.
 */
public final class StrukPrinter {

    /** Jumlah karakter per baris (Courier 8pt pada lebar 80mm). */
    private static final int CHARS = 40;

    private static final DecimalFormat RUPIAH = new DecimalFormat("#,###",
            new DecimalFormatSymbols(new Locale("id", "ID")));

    private static final Font F_NAME =
            new Font(Font.FontFamily.COURIER, 12, Font.BOLD);
    private static final Font F_SMALL =
            new Font(Font.FontFamily.COURIER, 7.5f, Font.NORMAL);
    private static final Font F_BODY =
            new Font(Font.FontFamily.COURIER, 8.2f, Font.NORMAL);
    private static final Font F_BODY_B =
            new Font(Font.FontFamily.COURIER, 8.2f, Font.BOLD);
    private static final Font F_TOTAL =
            new Font(Font.FontFamily.COURIER, 9, Font.BOLD);

    /** Lebar kolom untuk baris total (font lebih besar => karakter lebih sedikit). */
    private static final int CHARS_TOTAL = 36;

    private StrukPrinter() {
    }

    public static File cetak(String invoice) throws Exception {
        Connection conn = Koneksi.getKoneksi();
        String sql = "SELECT t.*, p.nama_pelanggan, l.nama_layanan, "
                + "c.nama_cabang FROM transaksi_laundry t "
                + "JOIN master_pelanggan p ON t.id_pelanggan=p.id_pelanggan "
                + "JOIN master_layanan l ON t.id_layanan=l.id_layanan "
                + "JOIN master_cabang c ON t.id_cabang=c.id_cabang "
                + "WHERE t.kode_invoice=?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, invoice);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Transaksi tidak ditemukan.");
                }

                Path dir = Path.of("struk");
                Files.createDirectories(dir);
                File file = dir.resolve("struk_" + sanitize(
                        rs.getString("kode_invoice")) + ".pdf").toFile();

                // 80mm ~= 226pt. Tinggi dibuat cukup untuk satu struk.
                Document doc = new Document(new Rectangle(226f, 560f),
                        12, 12, 12, 12);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    PdfWriter.getInstance(doc, fos);
                    doc.open();

                    addLogo(doc);
                    line(doc, "BOSS LAUNDRY", F_NAME, Element.ALIGN_CENTER);
                    line(doc, "Jl. Pancasila II, Cicadas", F_SMALL,
                            Element.ALIGN_CENTER);
                    line(doc, "Kec. Gn. Putri, Kab. Bogor", F_SMALL,
                            Element.ALIGN_CENTER);
                    line(doc, "Telp: 081292873797", F_SMALL,
                            Element.ALIGN_CENTER);

                    sep(doc);
                    String waktu = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    String kasir = koneksi.Session.username == null
                            ? "-" : koneksi.Session.username;
                    body(doc, kv("Invoice", rs.getString("kode_invoice")));
                    body(doc, kv("Tanggal", waktu));
                    body(doc, kv("Kasir", kasir));
                    body(doc, kv("Pelanggan", clip(rs.getString("nama_pelanggan"))));
                    body(doc, kv("Cabang", clip(rs.getString("nama_cabang"))));

                    sep(doc);
                    body(doc, kv("Layanan", clip(rs.getString("nama_layanan"))));
                    body(doc, kv("Qty/Kg", String.valueOf(
                            (int) rs.getDouble("berat"))));
                    String addons = rs.getString("addons");
                    if (addons != null && !addons.isBlank()) {
                        body(doc, "Add-Ons:");
                        for (String a : addons.split(",")) {
                            String s = a.trim();
                            if (!s.isEmpty()) {
                                body(doc, "  - " + s);
                            }
                        }
                    }

                    sep(doc);
                    line(doc, kv("TOTAL", rupiah(rs.getDouble("total_harga")),
                            CHARS_TOTAL), F_TOTAL, Element.ALIGN_LEFT);
                    String metode = rs.getString("metode_bayar");
                    body(doc, kv("Metode", metode == null ? "Tunai" : metode));
                    if (metode != null && metode.equalsIgnoreCase("Tunai")) {
                        body(doc, kv("Bayar", rupiah(rs.getDouble("bayar"))));
                        body(doc, kv("Kembalian",
                                rupiah(rs.getDouble("kembalian"))));
                    }

                    sep(doc);
                    body(doc, kv("Status", cap(rs.getString("status"))));
                    body(doc, kv("Masuk", String.valueOf(
                            rs.getString("tanggal_masuk"))));
                    body(doc, kv("Selesai", String.valueOf(
                            rs.getString("tanggal_selesai"))));

                    sep(doc);
                    line(doc, "Terima Kasih", F_BODY_B, Element.ALIGN_CENTER);
                    line(doc, "Laundry Anda, Kepuasan Kami", F_SMALL,
                            Element.ALIGN_CENTER);
                    blank(doc);
                    line(doc, "Barang yang tidak diambil lebih", F_SMALL,
                            Element.ALIGN_CENTER);
                    line(doc, "dari 30 hari di luar tanggung", F_SMALL,
                            Element.ALIGN_CENTER);
                    line(doc, "jawab kami.", F_SMALL, Element.ALIGN_CENTER);
                } finally {
                    if (doc.isOpen()) {
                        doc.close();
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (java.io.IOException ignore) {
                            // sudah ditutup PdfWriter.
                        }
                    }
                }
                return file;
            }
        }
    }

    // ------------------------------------------------------------------

    private static void addLogo(Document doc) {
        try {
            java.net.URL logoUrl =
                    StrukPrinter.class.getResource("/image/logo.png");
            Image logo = (logoUrl != null)
                    ? Image.getInstance(logoUrl)
                    : Image.getInstance("logo.png");
            logo.scaleToFit(46, 46);
            logo.setAlignment(Element.ALIGN_CENTER);
            doc.add(logo);
        } catch (Exception ignore) {
            // Logo opsional.
        }
    }

    private static void line(Document doc, String text, Font font, int align)
            throws Exception {
        Paragraph p = new Paragraph(text, font);
        p.setLeading(font.getSize() + 2.2f);
        p.setAlignment(align);
        doc.add(p);
    }

    private static void body(Document doc, String text) throws Exception {
        line(doc, text, F_BODY, Element.ALIGN_LEFT);
    }

    private static void sep(Document doc) throws Exception {
        line(doc, "-".repeat(CHARS), F_BODY, Element.ALIGN_LEFT);
    }

    private static void blank(Document doc) throws Exception {
        line(doc, " ", F_SMALL, Element.ALIGN_LEFT);
    }

    /** Baris "label ........... nilai" rata kiri-kanan dalam CHARS kolom. */
    private static String kv(String key, String value) {
        return kv(key, value, CHARS);
    }

    private static String kv(String key, String value, int width) {
        String k = key + " :";
        int space = width - k.length() - value.length();
        if (space < 1) {
            space = 1;
        }
        return k + " ".repeat(space) + value;
    }

    private static String rupiah(double v) {
        return "Rp " + RUPIAH.format(v);
    }

    private static String clip(String s) {
        if (s == null) {
            return "-";
        }
        int max = 28;
        return s.length() > max ? s.substring(0, max - 1) + "." : s;
    }

    private static String cap(String s) {
        if (s == null || s.isEmpty()) {
            return "-";
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String sanitize(String s) {
        return s == null ? "unknown" : s.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
