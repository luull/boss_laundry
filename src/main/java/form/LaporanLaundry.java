package form;

import koneksi.Koneksi;
import koneksi.Session;

import ui.Async;
import ui.CardPanel;
import ui.ComboItem;
import ui.DialogHelper;
import ui.Icons;
import ui.TableDownloader;
import ui.UIFactory;
import ui.UITheme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman Laporan: menampilkan transaksi berdasarkan filter cabang dan rentang
 * tanggal, menghitung total pendapatan, serta mengekspor / melihat laporan PDF
 * berkop surat. Filter yang sama diterapkan pada tampilan tabel maupun PDF.
 */
public class LaporanLaundry extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Invoice", "Pelanggan", "Layanan", "Berat", "Total",
                    "Status", "Tanggal Masuk", "Cabang"}, 0);
    private final JTable table = new JTable(model);
    private final JComboBox<ComboItem> cbCabang = new JComboBox<>();
    private final JTextField txtDari = UIFactory.textField();
    private final JTextField txtSampai = UIFactory.textField();
    private final JLabel lblTotal = UIFactory.h3("Rp 0");
    private final JLabel statusLabel = UIFactory.muted("");
    private final JButton btnPreview = UIFactory.secondaryButton("Pratinjau");
    private final JButton btnPdf = UIFactory.successButton("Export PDF");

    public LaporanLaundry() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        add(buildTop(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        loadCabang();
        wireEvents();
        loadData();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(LEFT_ALIGNMENT);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel title = UIFactory.h2("Laporan Transaksi");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel desc = UIFactory.muted("Rekap transaksi dan pendapatan laundry.");
        desc.setFont(UITheme.small());
        desc.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(desc);
        header.add(titleBox, BorderLayout.WEST);

        CardPanel totalCard = new CardPanel(new BorderLayout(10, 0));
        totalCard.fill(UITheme.SUCCESS_SOFT).border(UITheme.SUCCESS_SOFT)
                .shadow(false);
        totalCard.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JPanel tc = new JPanel();
        tc.setOpaque(false);
        tc.setLayout(new BoxLayout(tc, BoxLayout.Y_AXIS));
        JLabel cap = UIFactory.muted("TOTAL PENDAPATAN");
        cap.setFont(UITheme.smallBold());
        cap.setForeground(UITheme.SUCCESS_HOVER);
        lblTotal.setForeground(UITheme.SUCCESS_HOVER);
        tc.add(cap);
        tc.add(lblTotal);
        totalCard.add(tc, BorderLayout.CENTER);
        header.add(totalCard, BorderLayout.EAST);
        top.add(header);
        top.add(Box.createVerticalStrut(14));

        CardPanel toolbar = new CardPanel(new BorderLayout(12, 0));
        toolbar.setAlignmentX(LEFT_ALIGNMENT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        cbCabang.setPreferredSize(new Dimension(190, 38));
        txtDari.setPreferredSize(new Dimension(130, 38));
        txtSampai.setPreferredSize(new Dimension(130, 38));
        UITheme.setPlaceholder(txtDari, "Dari (yyyy-mm-dd)");
        UITheme.setPlaceholder(txtSampai, "Sampai (yyyy-mm-dd)");
        JButton btnFilter = UIFactory.primaryButton("Terapkan");
        UIFactory.withIcon(btnFilter, Icons.FILTER);
        JButton btnReset = UIFactory.secondaryButton("Reset");
        btnFilter.addActionListener(e -> loadData());
        btnReset.addActionListener(e -> {
            cbCabang.setSelectedIndex(0);
            txtDari.setText("");
            txtSampai.setText("");
            loadData();
        });
        left.add(new JLabel("Cabang:"));
        left.add(cbCabang);
        left.add(txtDari);
        left.add(txtSampai);
        left.add(btnFilter);
        left.add(btnReset);
        toolbar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        UIFactory.withIcon(btnPreview, Icons.EYE);
        UIFactory.withIcon(btnPdf, Icons.PDF);
        right.add(btnPreview);
        right.add(btnPdf);
        toolbar.add(right, BorderLayout.EAST);

        top.add(toolbar);
        return top;
    }

    private JPanel buildTable() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(3).setMaxWidth(90);
        table.getColumnModel().getColumn(3)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(4)
                .setCellRenderer(UIFactory.currencyRenderer());
        table.getColumnModel().getColumn(5).setMaxWidth(120);
        table.getColumnModel().getColumn(5)
                .setCellRenderer(UIFactory.statusRenderer());
        card.add(UIFactory.tableScroll(table), BorderLayout.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 0));
        card.add(statusLabel, BorderLayout.SOUTH);
        return card;
    }

    private void wireEvents() {
        btnPreview.addActionListener(e -> {
            if (validateDates()) {
                TableDownloader.preview(this, table, "Laporan Transaksi",
                        subtitles());
            }
        });
        btnPdf.addActionListener(e -> {
            if (validateDates()) {
                TableDownloader.download(this, table, "Laporan Transaksi",
                        "Laporan_Transaksi", subtitles());
            }
        });
    }

    /** Keterangan di bawah judul laporan: filter aktif dan total pendapatan. */
    private String[] subtitles() {
        ComboItem it = (ComboItem) cbCabang.getSelectedItem();
        String cabang = it == null ? "Semua Cabang" : it.getLabel();
        String dari = txtDari.getText().trim();
        String sampai = txtSampai.getText().trim();
        String periode;
        if (dari.isEmpty() && sampai.isEmpty()) {
            periode = "Semua periode";
        } else {
            periode = (dari.isEmpty() ? "awal" : dari) + " s/d "
                    + (sampai.isEmpty() ? "sekarang" : sampai);
        }
        return new String[]{
            "Cabang: " + cabang + "     Periode: " + periode,
            "Total Pendapatan: " + lblTotal.getText()
        };
    }

    private void loadCabang() {
        if (Session.canAccessAllCabang()) {
            cbCabang.addItem(new ComboItem(0, "Semua Cabang"));
        }
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            List<ComboItem> items = new ArrayList<>();
            // Owner hanya melihat cabangnya sendiri pada filter laporan.
            StringBuilder sql = new StringBuilder(
                    "SELECT id_cabang, nama_cabang FROM master_cabang");
            if (!Session.canAccessAllCabang()) {
                sql.append(" WHERE id_cabang = ").append(Session.idCabang);
            }
            sql.append(" ORDER BY nama_cabang");
            try (PreparedStatement pst = conn.prepareStatement(sql.toString());
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    items.add(new ComboItem(rs.getInt("id_cabang"),
                            rs.getString("nama_cabang")));
                }
            }
            return items;
        }, items -> {
            items.forEach(cbCabang::addItem);
            if (cbCabang.getItemCount() > 0) {
                cbCabang.setSelectedIndex(0);
            }
        }, err -> { });
    }

    // ================= FILTER PARSING =================
    private Integer selectedCabangId() {
        ComboItem it = (ComboItem) cbCabang.getSelectedItem();
        return (it == null || it.getId() == 0) ? null : it.getId();
    }

    private java.sql.Date parseDate(String s) {
        s = s.trim();
        if (s.isEmpty()) {
            return null;
        }
        return java.sql.Date.valueOf(LocalDate.parse(s));
    }

    private boolean validateDates() {
        try {
            parseDate(txtDari.getText());
            parseDate(txtSampai.getText());
            return true;
        } catch (Exception ex) {
            DialogHelper.warn(this,
                    "Format tanggal tidak valid. Gunakan format yyyy-mm-dd, "
                    + "mis. 2026-05-01.");
            return false;
        }
    }

    // ================= DATA (TABEL) =================
    private void loadData() {
        if (!validateDates()) {
            return;
        }
        statusLabel.setText("Memuat data...");
        // Owner/kasir dipaksa hanya melihat cabangnya sendiri.
        final Integer idCabang = Session.canAccessAllCabang()
                ? selectedCabangId() : Session.idCabang;
        final java.sql.Date dari = parseDate(txtDari.getText());
        final java.sql.Date sampai = parseDate(txtSampai.getText());

        Async.run(() -> query(idCabang, dari, sampai),
                result -> {
                    model.setRowCount(0);
                    double total = 0;
                    for (Object[] row : result) {
                        model.addRow(row);
                        total += (double) row[8];
                    }
                    lblTotal.setText(UIFactory.rupiah(total));
                    statusLabel.setText(result.isEmpty()
                            ? "Tidak ada transaksi pada filter ini."
                            : result.size() + " transaksi ditemukan.");
                },
                err -> {
                    statusLabel.setText("Gagal memuat data laporan.");
                    lblTotal.setText("Rp 0");
                });
    }

    private List<Object[]> query(Integer idCabang, java.sql.Date dari,
            java.sql.Date sampai) throws Exception {
        Connection conn = Koneksi.getKoneksi();
        StringBuilder sql = new StringBuilder(
                "SELECT t.kode_invoice, p.nama_pelanggan, l.nama_layanan, "
                + "t.berat, t.total_harga, t.status, t.tanggal_masuk, "
                + "c.nama_cabang FROM transaksi_laundry t "
                + "JOIN master_pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                + "JOIN master_layanan l ON t.id_layanan = l.id_layanan "
                + "JOIN master_cabang c ON t.id_cabang = c.id_cabang "
                + "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (idCabang != null) {
            sql.append(" AND t.id_cabang = ?");
            params.add(idCabang);
        }
        if (dari != null) {
            sql.append(" AND t.tanggal_masuk >= ?");
            params.add(dari);
        }
        if (sampai != null) {
            sql.append(" AND t.tanggal_masuk <= ?");
            params.add(sampai);
        }
        sql.append(" ORDER BY t.tanggal_masuk DESC, t.id_transaksi DESC");

        List<Object[]> rows = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getString("kode_invoice"),
                            rs.getString("nama_pelanggan"),
                            rs.getString("nama_layanan"),
                            (int) rs.getDouble("berat") + " Kg",
                            rs.getString("status"),
                            rs.getString("tanggal_masuk"),
                            rs.getString("nama_cabang"),
                            rs.getDouble("total_harga")});
                }
            }
        }
        // Susun ulang agar sesuai urutan kolom tabel (Total di index 4).
        List<Object[]> ordered = new ArrayList<>();
        for (Object[] r : rows) {
            ordered.add(new Object[]{r[0], r[1], r[2], r[3], r[7], r[4],
                    r[5], r[6], r[7]});
        }
        return ordered;
    }

}
