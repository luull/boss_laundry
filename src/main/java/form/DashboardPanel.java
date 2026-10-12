package form;

import koneksi.Koneksi;
import koneksi.Session;

import ui.Async;
import ui.CardPanel;
import ui.Icons;
import ui.StatCard;
import ui.UIFactory;
import ui.UITheme;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard ringkasan operasional. Seluruh angka berasal dari data nyata
 * Supabase; query dijalankan di latar belakang agar UI tidak freeze. Menangani
 * kondisi database kosong (menampilkan 0 / empty state).
 */
public class DashboardPanel extends JPanel {

    private final StatCard cTotal =
            new StatCard("Total Transaksi", Icons.TRX, UITheme.PRIMARY);
    private final StatCard cProses =
            new StatCard("Sedang Diproses", Icons.CLOCK, UITheme.WARNING);
    private final StatCard cSelesai =
            new StatCard("Selesai", Icons.CHECK, UITheme.SUCCESS);
    private final StatCard cBulan =
            new StatCard("Pendapatan Bulan Ini", Icons.MONEY, UITheme.PRIMARY);
    private final StatCard cHari =
            new StatCard("Pendapatan Hari Ini", Icons.MONEY, UITheme.SUCCESS);
    private final StatCard cPelanggan =
            new StatCard("Pelanggan", Icons.CUSTOMER, UITheme.INFO);
    private final StatCard cLayanan =
            new StatCard("Layanan Aktif", Icons.SERVICE, UITheme.PRIMARY);
    private final StatCard cCabang =
            new StatCard("Cabang Aktif", Icons.BRANCH, UITheme.INFO);

    private final DefaultTableModel recentModel = new DefaultTableModel(
            new Object[]{"Invoice", "Pelanggan", "Layanan", "Total",
                    "Status", "Tanggal Masuk"}, 0);
    private final JTable recentTable = new JTable(recentModel);
    private final JLabel recentEmpty = UIFactory.muted("Belum ada transaksi.");

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG);

        JPanel content = new JPanel();
        content.setBackground(UITheme.BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(buildGreeting());
        content.add(Box.createVerticalStrut(18));
        content.add(buildStatsGrid());
        content.add(Box.createVerticalStrut(18));
        content.add(buildRecentCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        loadData();
    }

    private Component buildGreeting() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel hi = UIFactory.h1("Halo, "
                + (koneksi.Session.username == null ? "Pengguna"
                : koneksi.Session.username) + " \uD83D\uDC4B");
        hi.setAlignmentX(LEFT_ALIGNMENT);
        String month = LocalDate.now().getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        JLabel sub = UIFactory.muted("Ringkasan operasional - "
                + month + " " + LocalDate.now().getYear());
        sub.setAlignmentX(LEFT_ALIGNMENT);
        left.add(hi);
        left.add(Box.createVerticalStrut(2));
        left.add(sub);

        p.add(left, BorderLayout.WEST);
        return p;
    }

    private Component buildStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 4, 16, 16));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.add(cTotal);
        grid.add(cProses);
        grid.add(cSelesai);
        grid.add(cBulan);
        grid.add(cHari);
        grid.add(cPelanggan);
        grid.add(cLayanan);
        grid.add(cCabang);
        for (StatCard c : new StatCard[]{cTotal, cProses, cSelesai, cBulan,
                cHari, cPelanggan, cLayanan, cCabang}) {
            c.setValue("...");
        }
        return grid;
    }

    private Component buildRecentCard() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UIFactory.h3("Transaksi Terbaru"), BorderLayout.WEST);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        card.add(header, BorderLayout.NORTH);

        UIFactory.styleTable(recentTable);
        recentTable.getColumnModel().getColumn(3)
                .setCellRenderer(UIFactory.currencyRenderer());
        recentTable.getColumnModel().getColumn(4)
                .setCellRenderer(UIFactory.statusRenderer());
        JScrollPane sp = UIFactory.tableScroll(recentTable);
        sp.setPreferredSize(new Dimension(10, 320));
        card.add(sp, BorderLayout.CENTER);

        recentEmpty.setHorizontalAlignment(JLabel.CENTER);
        recentEmpty.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(recentEmpty, BorderLayout.SOUTH);
        recentEmpty.setVisible(false);

        return card;
    }

    // ================= DATA =================
    private void loadData() {
        Async.run(this::queryStats,
                stats -> applyStats(stats),
                err -> {
                    for (StatCard c : new StatCard[]{cTotal, cProses, cSelesai,
                            cBulan, cHari, cPelanggan, cLayanan, cCabang}) {
                        c.setValue("-");
                        c.setCaption("gagal memuat");
                    }
                    recentEmpty.setText("Gagal memuat data dashboard.");
                    recentEmpty.setVisible(true);
                });
    }

    private Stats queryStats() throws Exception {
        Connection conn = Koneksi.getKoneksi();
        Stats s = new Stats();

        // Admin: semua cabang (cab = null). Owner: hanya cabangnya (nilai int
        // berasal dari sesi, aman di-inline — bukan input pengguna).
        Integer cab = Session.scopedCabang();
        String whereTrx = cab == null ? "" : " WHERE id_cabang=" + cab;
        String andTrx = cab == null ? "" : " AND id_cabang=" + cab;
        String wherePel = cab == null ? "" : " WHERE id_cabang=" + cab;

        s.totalTrx = scalarLong(conn,
                "SELECT COUNT(*) FROM transaksi_laundry" + whereTrx);
        s.prosesTrx = scalarLong(conn,
                "SELECT COUNT(*) FROM transaksi_laundry "
                + "WHERE status IN ('proses','dicuci','disetrika')" + andTrx);
        s.selesaiTrx = scalarLong(conn,
                "SELECT COUNT(*) FROM transaksi_laundry "
                + "WHERE status IN ('selesai','diambil')" + andTrx);
        s.pendapatanBulan = scalarDouble(conn,
                "SELECT COALESCE(SUM(total_harga),0) FROM transaksi_laundry "
                + "WHERE date_trunc('month', tanggal_masuk) "
                + "= date_trunc('month', CURRENT_DATE)" + andTrx);
        s.pendapatanHari = scalarDouble(conn,
                "SELECT COALESCE(SUM(total_harga),0) FROM transaksi_laundry "
                + "WHERE tanggal_masuk = CURRENT_DATE" + andTrx);
        s.pelanggan = scalarLong(conn,
                "SELECT COUNT(*) FROM master_pelanggan" + wherePel);
        s.layananAktif = scalarLong(conn,
                "SELECT COUNT(*) FROM master_layanan WHERE status='aktif'");
        s.cabangAktif = scalarLong(conn,
                "SELECT COUNT(*) FROM master_cabang WHERE status='aktif'"
                + (cab == null ? "" : " AND id_cabang=" + cab));

        String sql = "SELECT t.kode_invoice, p.nama_pelanggan, l.nama_layanan, "
                + "t.total_harga, t.status, t.tanggal_masuk "
                + "FROM transaksi_laundry t "
                + "JOIN master_pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                + "JOIN master_layanan l ON t.id_layanan = l.id_layanan "
                + (cab == null ? "" : "WHERE t.id_cabang=" + cab + " ")
                + "ORDER BY t.id_transaksi DESC LIMIT 8";
        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                s.recent.add(new Object[]{
                        rs.getString("kode_invoice"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("nama_layanan"),
                        rs.getDouble("total_harga"),
                        rs.getString("status"),
                        rs.getString("tanggal_masuk")
                });
            }
        }
        return s;
    }

    private void applyStats(Stats s) {
        String month = LocalDate.now().getMonth()
                .getDisplayName(TextStyle.SHORT, new Locale("id", "ID"));
        cTotal.setValue(String.valueOf(s.totalTrx));
        cTotal.setCaption("seluruh waktu");
        cProses.setValue(String.valueOf(s.prosesTrx));
        cProses.setCaption("perlu ditindaklanjuti");
        cSelesai.setValue(String.valueOf(s.selesaiTrx));
        cSelesai.setCaption("selesai / diambil");
        cBulan.setValue(UIFactory.rupiah(s.pendapatanBulan));
        cBulan.setCaption("bulan " + month);
        cHari.setValue(UIFactory.rupiah(s.pendapatanHari));
        cHari.setCaption("hari ini");
        cPelanggan.setValue(String.valueOf(s.pelanggan));
        cPelanggan.setCaption("terdaftar");
        cLayanan.setValue(String.valueOf(s.layananAktif));
        cLayanan.setCaption("layanan tersedia");
        cCabang.setValue(String.valueOf(s.cabangAktif));
        cCabang.setCaption("cabang beroperasi");

        recentModel.setRowCount(0);
        for (Object[] row : s.recent) {
            recentModel.addRow(row);
        }
        recentEmpty.setVisible(s.recent.isEmpty());
    }

    private long scalarLong(Connection conn, String sql) throws Exception {
        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    private double scalarDouble(Connection conn, String sql) throws Exception {
        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0d;
        }
    }

    private static final class Stats {
        long totalTrx;
        long prosesTrx;
        long selesaiTrx;
        double pendapatanBulan;
        double pendapatanHari;
        long pelanggan;
        long layananAktif;
        long cabangAktif;
        final List<Object[]> recent = new ArrayList<>();
    }
}
