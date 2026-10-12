package ui;

import koneksi.Session;
import service.TableExporter;

import java.awt.Component;
import java.io.File;
import java.text.SimpleDateFormat;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Alur "Unduh / Pratinjau PDF" untuk sebuah {@link JTable}: mengambil cuplikan
 * teks yang tampil (sesuai renderer), lalu menulis PDF berkop surat di latar
 * belakang. Nama pencetak diambil dari sesi pengguna yang sedang login.
 */
public final class TableDownloader {

    private TableDownloader() {
    }

    public static void download(Component parent, JTable table, String title,
            String filePrefix) {
        download(parent, table, title, filePrefix, null);
    }

    /** Unduh dengan dialog simpan; {@code subtitles} = keterangan di bawah judul. */
    public static void download(Component parent, JTable table, String title,
            String filePrefix, String[] subtitles) {

        String[] columns = columns(table);
        String[][] data = snapshot(table);
        if (data.length == 0) {
            DialogHelper.warn(parent, "Tidak ada data untuk diunduh.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan " + title + " (PDF)");
        String def = filePrefix + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(new java.util.Date()) + ".pdf";
        chooser.setSelectedFile(new File(def));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String path = chooser.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith(".pdf")) {
            path += ".pdf";
        }
        final File out = new File(path);
        final String by = Session.username;

        Async.run(() -> {
            TableExporter.writePdf(title, subtitles, columns, data, by, out);
            return out;
        }, f -> {
            DialogHelper.success(parent, "Laporan berhasil diunduh ke:\n"
                    + f.getAbsolutePath());
            open(f);
        }, err -> DialogHelper.error(parent, "Gagal membuat berkas PDF."));
    }

    /** Pratinjau: tulis PDF ke berkas sementara lalu buka. */
    public static void preview(Component parent, JTable table, String title,
            String[] subtitles) {
        String[] columns = columns(table);
        String[][] data = snapshot(table);
        if (data.length == 0) {
            DialogHelper.warn(parent, "Tidak ada data untuk ditampilkan.");
            return;
        }
        final String by = Session.username;
        Async.run(() -> {
            File tmp = File.createTempFile("laporan_", ".pdf");
            tmp.deleteOnExit();
            TableExporter.writePdf(title, subtitles, columns, data, by, tmp);
            return tmp;
        }, TableDownloader::open,
                err -> DialogHelper.error(parent, "Gagal membuat pratinjau PDF."));
    }

    private static void open(File f) {
        try {
            java.awt.Desktop.getDesktop().open(f);
        } catch (Exception ignore) {
            // Membuka berkas bersifat opsional.
        }
    }

    public static String[] columns(JTable table) {
        int cols = table.getColumnCount();
        String[] names = new String[cols];
        for (int i = 0; i < cols; i++) {
            names[i] = table.getColumnName(i);
        }
        return names;
    }

    /**
     * Ambil teks yang tampil di setiap sel (via renderer) agar format Rupiah
     * dan status ikut terbawa. Harus dipanggil di Event Dispatch Thread.
     */
    public static String[][] snapshot(JTable table) {
        int rows = table.getRowCount();
        int cols = table.getColumnCount();
        String[][] data = new String[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r][c] = displayText(table, r, c);
            }
        }
        return data;
    }

    private static String displayText(JTable table, int row, int col) {
        try {
            Component comp = table.prepareRenderer(
                    table.getCellRenderer(row, col), row, col);
            if (comp instanceof JLabel label) {
                String t = label.getText();
                if (t != null) {
                    return t;
                }
            }
        } catch (Exception ignore) {
            // Jatuh ke nilai mentah.
        }
        Object v = table.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }
}
