package form;

import koneksi.Koneksi;

import ui.Async;
import ui.CardPanel;
import ui.DialogHelper;
import ui.DocListener;
import ui.FormDialog;
import ui.Icons;
import ui.TableDownloader;
import ui.UIFactory;
import ui.UITheme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman Master Layanan: daftar layanan kiloan &amp; add-ons, cari, filter,
 * CRUD, dan unduh PDF berkop surat. Operasi berat dijalankan di latar belakang
 * agar UI tetap responsif.
 */
public class MasterLayanan extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nama Layanan", "Kategori", "Harga Min",
                    "Harga Max", "Estimasi", "Status"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField txtSearch = UIFactory.searchField("Cari layanan...");
    private final JComboBox<String> cbKategori = new JComboBox<>(
            new String[]{"Semua Kategori", "kiloan", "addons"});
    private final JButton btnEdit = UIFactory.warningButton("Edit");
    private final JButton btnHapus = UIFactory.dangerButton("Hapus");
    private final JButton btnExport = UIFactory.successButton("Export PDF");
    private final JLabel statusLabel = UIFactory.muted("");

    private int selectedId = 0;

    public MasterLayanan() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        add(buildTop(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
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
        JLabel title = UIFactory.h2("Master Layanan");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel desc = UIFactory.muted("Kelola layanan kiloan dan add-ons.");
        desc.setFont(UITheme.small());
        desc.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(desc);
        header.add(titleBox, BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        hr.setOpaque(false);
        UIFactory.withIcon(btnExport, Icons.PDF);
        JButton btnTambah = UIFactory.primaryButton("Tambah Layanan");
        UIFactory.withIcon(btnTambah, Icons.PLUS);
        btnTambah.addActionListener(e -> openForm(0));
        hr.add(btnExport);
        hr.add(btnTambah);
        header.add(hr, BorderLayout.EAST);
        top.add(header);
        top.add(Box.createVerticalStrut(14));

        CardPanel toolbar = new CardPanel(new BorderLayout(12, 0));
        toolbar.setAlignmentX(LEFT_ALIGNMENT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        txtSearch.setPreferredSize(new Dimension(240, 38));
        cbKategori.setPreferredSize(new Dimension(160, 38));
        JButton btnReset = UIFactory.secondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbKategori.setSelectedIndex(0);
            loadData();
        });
        left.add(txtSearch);
        left.add(cbKategori);
        left.add(btnReset);
        toolbar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
        UIFactory.withIcon(btnEdit, Icons.EDIT);
        UIFactory.withIcon(btnHapus, Icons.TRASH);
        right.add(btnEdit);
        right.add(btnHapus);
        toolbar.add(right, BorderLayout.EAST);

        top.add(toolbar);
        return top;
    }

    private JPanel buildTable() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(0)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(3)
                .setCellRenderer(UIFactory.currencyRenderer());
        table.getColumnModel().getColumn(4)
                .setCellRenderer(UIFactory.currencyRenderer());
        table.getColumnModel().getColumn(5)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(6).setMaxWidth(120);
        table.getColumnModel().getColumn(6)
                .setCellRenderer(UIFactory.statusRenderer());
        card.add(UIFactory.tableScroll(table), BorderLayout.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 0));
        card.add(statusLabel, BorderLayout.SOUTH);
        return card;
    }

    private void wireEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            boolean sel = r >= 0;
            selectedId = sel ? Integer.parseInt(model.getValueAt(r, 0).toString()) : 0;
            btnEdit.setEnabled(sel);
            btnHapus.setEnabled(sel);
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && selectedId != 0) {
                    openForm(selectedId);
                }
            }
        });
        btnEdit.addActionListener(e -> {
            if (selectedId != 0) {
                openForm(selectedId);
            }
        });
        btnHapus.addActionListener(e -> hapus());
        btnExport.addActionListener(e -> TableDownloader.download(this, table,
                "Laporan Master Layanan", "Laporan_Layanan"));
        txtSearch.addActionListener(e -> loadData());
        txtSearch.getDocument().addDocumentListener(new DocListener(this::loadData));
        cbKategori.addActionListener(e -> loadData());
    }

    // ================= DATA =================
    private void loadData() {
        statusLabel.setText("Memuat data...");
        final String keyword = txtSearch.getText().trim();
        final String kategori = cbKategori.getSelectedIndex() == 0
                ? null : (String) cbKategori.getSelectedItem();
        Async.run(() -> query(keyword, kategori),
                rows -> {
                    model.setRowCount(0);
                    for (Object[] row : rows) {
                        model.addRow(row);
                    }
                    selectedId = 0;
                    btnEdit.setEnabled(false);
                    btnHapus.setEnabled(false);
                    statusLabel.setText(rows.isEmpty()
                            ? "Tidak ada layanan."
                            : rows.size() + " layanan ditemukan.");
                },
                err -> statusLabel.setText("Gagal memuat data layanan."));
    }

    private List<Object[]> query(String keyword, String kategori)
            throws Exception {
        Connection conn = Koneksi.getKoneksi();
        StringBuilder sql = new StringBuilder(
                "SELECT id_layanan, nama_layanan, kategori, harga_min, "
                + "harga_max, estimasi_hari, status FROM master_layanan "
                + "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (!keyword.isEmpty()) {
            sql.append(" AND nama_layanan ILIKE ?");
            params.add("%" + keyword + "%");
        }
        if (kategori != null) {
            sql.append(" AND kategori = ?");
            params.add(kategori);
        }
        sql.append(" ORDER BY id_layanan");
        List<Object[]> rows = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt("id_layanan"),
                            rs.getString("nama_layanan"),
                            rs.getString("kategori"),
                            rs.getDouble("harga_min"),
                            rs.getDouble("harga_max"),
                            rs.getInt("estimasi_hari") + " Hari",
                            rs.getString("status")});
                }
            }
        }
        return rows;
    }

    // ================= FORM =================
    private void openForm(int id) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        boolean edit = id != 0;
        FormDialog dialog = new FormDialog(owner,
                edit ? "Edit Layanan" : "Tambah Layanan",
                edit ? "Perbarui detail layanan."
                        : "Tambahkan layanan baru.");

        JTextField txtNama = UIFactory.textField();
        JComboBox<String> cbFormKategori =
                new JComboBox<>(new String[]{"kiloan", "addons"});
        JTextField txtMin = UIFactory.textField();
        JTextField txtMax = UIFactory.textField();
        JTextField txtEstimasi = UIFactory.textField();
        JComboBox<String> cbFormStatus =
                new JComboBox<>(new String[]{"aktif", "nonaktif"});

        dialog.addRow("Nama Layanan", txtNama);
        dialog.addRow("Kategori", cbFormKategori,
                "Kiloan: harga tetap per kg. Add-ons: rentang harga.");
        dialog.addRow("Harga Minimum", txtMin);
        dialog.addRow("Harga Maksimum", txtMax);
        dialog.addRow("Estimasi (hari)", txtEstimasi);
        dialog.addRow("Status", cbFormStatus);

        Runnable applyKategori = () -> {
            boolean kiloan = "kiloan".equals(cbFormKategori.getSelectedItem());
            if (kiloan) {
                txtMax.setText(txtMin.getText());
                txtMax.setEnabled(false);
            } else {
                txtMax.setEnabled(true);
            }
        };
        cbFormKategori.addActionListener(e -> applyKategori.run());
        txtMin.getDocument().addDocumentListener(new DocListener(() -> {
            if ("kiloan".equals(cbFormKategori.getSelectedItem())) {
                txtMax.setText(txtMin.getText());
            }
        }));
        applyKategori.run();

        if (edit) {
            loadForEdit(id, txtNama, cbFormKategori, txtMin, txtMax,
                    txtEstimasi, cbFormStatus, applyKategori, dialog);
        }

        dialog.onSave(() -> {
            dialog.clearError();
            String nama = txtNama.getText().trim();
            String min = txtMin.getText().trim();
            String max = txtMax.getText().trim();
            String est = txtEstimasi.getText().trim();

            if (nama.isEmpty()) {
                dialog.showError("Nama layanan wajib diisi.");
                return;
            }
            if (!min.matches("\\d+")) {
                dialog.showError("Harga minimum harus berupa angka.");
                return;
            }
            if (!max.matches("\\d+")) {
                dialog.showError("Harga maksimum harus berupa angka.");
                return;
            }
            if (Integer.parseInt(max) < Integer.parseInt(min)) {
                dialog.showError("Harga maksimum tidak boleh kurang dari minimum.");
                return;
            }
            if (!est.matches("\\d+")) {
                dialog.showError("Estimasi hari harus berupa angka.");
                return;
            }

            String kategori = (String) cbFormKategori.getSelectedItem();
            String st = (String) cbFormStatus.getSelectedItem();
            dialog.setSaving(true);
            Async.run(() -> {
                save(id, nama, est, st, kategori, min, max);
                return null;
            }, ok -> {
                dialog.setSaving(false);
                dialog.dispose();
                DialogHelper.success(this, edit
                        ? "Layanan berhasil diperbarui."
                        : "Layanan baru berhasil ditambahkan.");
                loadData();
            }, err -> {
                dialog.setSaving(false);
                dialog.showError("Gagal menyimpan layanan. Coba lagi.");
            });
        });

        dialog.display();
    }

    private void loadForEdit(int id, JTextField nama, JComboBox<String> kategori,
            JTextField min, JTextField max, JTextField est,
            JComboBox<String> status, Runnable applyKategori, FormDialog dialog) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT * FROM master_layanan WHERE id_layanan=?")) {
                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return new String[]{
                                rs.getString("nama_layanan"),
                                rs.getString("kategori"),
                                String.valueOf((int) rs.getDouble("harga_min")),
                                String.valueOf((int) rs.getDouble("harga_max")),
                                String.valueOf(rs.getInt("estimasi_hari")),
                                rs.getString("status")};
                    }
                }
            }
            return null;
        }, d -> {
            if (d != null) {
                nama.setText(d[0]);
                kategori.setSelectedItem(d[1]);
                min.setText(d[2]);
                max.setText(d[3]);
                est.setText(d[4]);
                status.setSelectedItem(d[5]);
                applyKategori.run();
            }
        }, err -> dialog.showError("Gagal memuat data layanan."));
    }

    private void save(int id, String nama, String est, String status,
            String kategori, String min, String max) throws Exception {
        Connection conn = Koneksi.getKoneksi();
        if (id == 0) {
            try (PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO master_layanan (nama_layanan,estimasi_hari,"
                    + "status,kategori,harga_min,harga_max) "
                    + "VALUES(?,?,?,?,?,?)")) {
                pst.setString(1, nama);
                pst.setInt(2, Integer.parseInt(est));
                pst.setString(3, status);
                pst.setString(4, kategori);
                pst.setDouble(5, Double.parseDouble(min));
                pst.setDouble(6, Double.parseDouble(max));
                pst.executeUpdate();
            }
        } else {
            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE master_layanan SET nama_layanan=?, estimasi_hari=?, "
                    + "status=?, kategori=?, harga_min=?, harga_max=? "
                    + "WHERE id_layanan=?")) {
                pst.setString(1, nama);
                pst.setInt(2, Integer.parseInt(est));
                pst.setString(3, status);
                pst.setString(4, kategori);
                pst.setDouble(5, Double.parseDouble(min));
                pst.setDouble(6, Double.parseDouble(max));
                pst.setInt(7, id);
                pst.executeUpdate();
            }
        }
    }

    // ================= HAPUS (dengan penanganan FK) =================
    private void hapus() {
        if (selectedId == 0) {
            return;
        }
        int r = table.getSelectedRow();
        String nama = r >= 0 ? model.getValueAt(r, 1).toString() : "";
        if (!DialogHelper.confirmDanger(this,
                "Hapus layanan \"" + nama + "\" secara permanen?")) {
            return;
        }
        final int id = selectedId;
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM master_layanan WHERE id_layanan=?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }
            return null;
        }, ok -> {
            DialogHelper.success(this, "Layanan berhasil dihapus.");
            loadData();
        }, err -> {
            if (isForeignKeyViolation(err)) {
                DialogHelper.warn(this,
                        "Layanan tidak dapat dihapus karena masih dipakai "
                        + "pada transaksi.\nUbah statusnya menjadi 'nonaktif' "
                        + "agar tidak muncul pada transaksi baru.");
            } else {
                DialogHelper.error(this, "Gagal menghapus layanan. Coba lagi.");
            }
        });
    }

    private boolean isForeignKeyViolation(Throwable err) {
        Throwable c = err;
        while (c != null) {
            if (c instanceof SQLException sq && "23503".equals(sq.getSQLState())) {
                return true;
            }
            c = c.getCause();
        }
        return false;
    }
}
