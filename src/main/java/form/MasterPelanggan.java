package form;

import koneksi.Koneksi;
import koneksi.Session;

import ui.Async;
import ui.CardPanel;
import ui.ComboItem;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman Master Pelanggan: daftar, cari, filter member, CRUD, dan export PDF.
 * Menghormati otorisasi (kasir hanya melihat pelanggan cabangnya). Operasi
 * database dijalankan di latar belakang.
 */
public class MasterPelanggan extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nama", "Alamat", "No HP", "Member", "Cabang"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField txtSearch = UIFactory.searchField("Cari nama pelanggan...");
    private final JComboBox<String> cbMemberFilter = new JComboBox<>(
            new String[]{"Semua Member", "regular", "vip"});
    private final JButton btnEdit = UIFactory.warningButton("Edit");
    private final JButton btnHapus = UIFactory.dangerButton("Hapus");
    private final JButton btnExport = UIFactory.successButton("Export PDF");
    private final JLabel statusLabel = UIFactory.muted("");

    private int selectedId = 0;

    public MasterPelanggan() {
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
        JLabel title = UIFactory.h2("Master Pelanggan");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel desc = UIFactory.muted("Kelola data pelanggan laundry.");
        desc.setFont(UITheme.small());
        desc.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(desc);
        header.add(titleBox, BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        hr.setOpaque(false);
        UIFactory.withIcon(btnExport, Icons.PDF);
        JButton btnTambah = UIFactory.primaryButton("Tambah Pelanggan");
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
        cbMemberFilter.setPreferredSize(new Dimension(150, 38));
        JButton btnReset = UIFactory.secondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbMemberFilter.setSelectedIndex(0);
            loadData();
        });
        left.add(txtSearch);
        left.add(cbMemberFilter);
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
        table.getColumnModel().getColumn(4).setMaxWidth(110);
        table.getColumnModel().getColumn(4)
                .setCellRenderer(UIFactory.centerRenderer());
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
                "Laporan Master Pelanggan", "Laporan_Pelanggan"));
        txtSearch.addActionListener(e -> loadData());
        txtSearch.getDocument().addDocumentListener(new DocListener(this::loadData));
        cbMemberFilter.addActionListener(e -> loadData());
    }

    private boolean isPrivileged() {
        // Hanya admin yang melihat pelanggan seluruh cabang;
        // owner/kasir dibatasi pada cabangnya sendiri.
        return Session.canAccessAllCabang();
    }

    // ================= DATA =================
    private void loadData() {
        statusLabel.setText("Memuat data...");
        final String keyword = txtSearch.getText().trim();
        final String member = cbMemberFilter.getSelectedIndex() == 0
                ? null : (String) cbMemberFilter.getSelectedItem();
        Async.run(() -> query(keyword, member),
                rows -> {
                    model.setRowCount(0);
                    for (Object[] row : rows) {
                        model.addRow(row);
                    }
                    selectedId = 0;
                    btnEdit.setEnabled(false);
                    btnHapus.setEnabled(false);
                    statusLabel.setText(rows.isEmpty()
                            ? "Tidak ada pelanggan."
                            : rows.size() + " pelanggan ditemukan.");
                },
                err -> statusLabel.setText("Gagal memuat data pelanggan."));
    }

    private List<Object[]> query(String keyword, String member)
            throws Exception {
        Connection conn = Koneksi.getKoneksi();
        StringBuilder sql = new StringBuilder(
                "SELECT p.id_pelanggan, p.nama_pelanggan, p.alamat, p.no_hp, "
                + "p.jenis_member, c.nama_cabang "
                + "FROM master_pelanggan p "
                + "LEFT JOIN master_cabang c ON p.id_cabang = c.id_cabang "
                + "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (!isPrivileged()) {
            sql.append(" AND p.id_cabang = ?");
            params.add(Session.idCabang);
        }
        if (!keyword.isEmpty()) {
            sql.append(" AND p.nama_pelanggan ILIKE ?");
            params.add("%" + keyword + "%");
        }
        if (member != null) {
            sql.append(" AND p.jenis_member = ?");
            params.add(member);
        }
        sql.append(" ORDER BY p.id_pelanggan");
        List<Object[]> rows = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt("id_pelanggan"),
                            rs.getString("nama_pelanggan"),
                            rs.getString("alamat"),
                            rs.getString("no_hp"),
                            rs.getString("jenis_member"),
                            rs.getString("nama_cabang")});
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
                edit ? "Edit Pelanggan" : "Tambah Pelanggan",
                edit ? "Perbarui data pelanggan." : "Tambahkan pelanggan baru.");

        JTextField txtNama = UIFactory.textField();
        JTextArea txtAlamat = new JTextArea(3, 20);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setFont(UITheme.body());
        JScrollPane alamatScroll = new JScrollPane(txtAlamat);
        alamatScroll.setPreferredSize(new Dimension(260, 72));
        alamatScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        JTextField txtHp = UIFactory.textField();
        JComboBox<String> cbMember =
                new JComboBox<>(new String[]{"regular", "vip"});
        JComboBox<ComboItem> cbCabang = new JComboBox<>();

        dialog.addRow("Nama", txtNama);
        dialog.addRow("Alamat", alamatScroll);
        dialog.addRow("No HP", txtHp, "Hanya angka, mis. 081234567890");
        dialog.addRow("Jenis Member", cbMember);
        dialog.addRow("Cabang", cbCabang);

        loadCabang(cbCabang, () -> {
            if (edit) {
                loadForEdit(id, txtNama, txtAlamat, txtHp, cbMember,
                        cbCabang, dialog);
            }
        }, dialog);

        dialog.onSave(() -> {
            dialog.clearError();
            String nama = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String hp = txtHp.getText().trim();

            if (nama.isEmpty()) {
                dialog.showError("Nama pelanggan wajib diisi.");
                return;
            }
            if (hp.isEmpty() || !hp.matches("[0-9]{6,20}")) {
                dialog.showError("No HP harus berupa 6-20 digit angka.");
                return;
            }
            if (cbCabang.getSelectedItem() == null) {
                dialog.showError("Cabang wajib dipilih.");
                return;
            }
            int idCabang = ((ComboItem) cbCabang.getSelectedItem()).getId();
            String member = (String) cbMember.getSelectedItem();

            dialog.setSaving(true);
            Async.run(() -> {
                save(id, nama, alamat, hp, member, idCabang);
                return null;
            }, ok -> {
                dialog.setSaving(false);
                dialog.dispose();
                DialogHelper.success(this, edit
                        ? "Data pelanggan berhasil diperbarui."
                        : "Pelanggan baru berhasil ditambahkan.");
                loadData();
            }, err -> {
                dialog.setSaving(false);
                dialog.showError("Gagal menyimpan pelanggan. Coba lagi.");
            });
        });

        dialog.display();
    }

    private void loadCabang(JComboBox<ComboItem> combo, Runnable after,
            FormDialog dialog) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            List<ComboItem> items = new ArrayList<>();
            // Owner/kasir hanya boleh menetapkan cabangnya sendiri.
            StringBuilder sql = new StringBuilder(
                    "SELECT id_cabang, nama_cabang FROM master_cabang "
                    + "WHERE status='aktif'");
            if (!isPrivileged()) {
                sql.append(" AND id_cabang = ").append(Session.idCabang);
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
            combo.removeAllItems();
            for (ComboItem it : items) {
                combo.addItem(it);
            }
            if (after != null) {
                after.run();
            }
        }, err -> dialog.showError("Gagal memuat daftar cabang."));
    }

    private void loadForEdit(int id, JTextField nama, JTextArea alamat,
            JTextField hp, JComboBox<String> member,
            JComboBox<ComboItem> cabang, FormDialog dialog) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT * FROM master_pelanggan WHERE id_pelanggan=?")) {
                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        Object[] d = new Object[5];
                        d[0] = rs.getString("nama_pelanggan");
                        d[1] = rs.getString("alamat");
                        d[2] = rs.getString("no_hp");
                        d[3] = rs.getString("jenis_member");
                        d[4] = rs.getInt("id_cabang");
                        return d;
                    }
                }
            }
            return null;
        }, d -> {
            if (d != null) {
                nama.setText(d[0] == null ? "" : d[0].toString());
                alamat.setText(d[1] == null ? "" : d[1].toString());
                hp.setText(d[2] == null ? "" : d[2].toString());
                if (d[3] != null) {
                    member.setSelectedItem(d[3].toString());
                }
                int idc = (int) d[4];
                for (int i = 0; i < cabang.getItemCount(); i++) {
                    if (cabang.getItemAt(i).getId() == idc) {
                        cabang.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }, err -> dialog.showError("Gagal memuat data pelanggan."));
    }

    private void save(int id, String nama, String alamat, String hp,
            String member, int idCabang) throws Exception {
        Connection conn = Koneksi.getKoneksi();
        if (id == 0) {
            try (PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO master_pelanggan (nama_pelanggan,alamat,no_hp,"
                    + "jenis_member,id_cabang) VALUES(?,?,?,?,?)")) {
                pst.setString(1, nama);
                pst.setString(2, alamat);
                pst.setString(3, hp);
                pst.setString(4, member);
                pst.setInt(5, idCabang);
                pst.executeUpdate();
            }
        } else {
            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE master_pelanggan SET nama_pelanggan=?, alamat=?, "
                    + "no_hp=?, jenis_member=?, id_cabang=? "
                    + "WHERE id_pelanggan=?")) {
                pst.setString(1, nama);
                pst.setString(2, alamat);
                pst.setString(3, hp);
                pst.setString(4, member);
                pst.setInt(5, idCabang);
                pst.setInt(6, id);
                pst.executeUpdate();
            }
        }
    }

    // ================= HAPUS (jaga integritas transaksi) =================
    private void hapus() {
        if (selectedId == 0) {
            return;
        }
        int r = table.getSelectedRow();
        String nama = r >= 0 ? model.getValueAt(r, 1).toString() : "";
        if (!DialogHelper.confirmDanger(this,
                "Hapus pelanggan \"" + nama + "\"?\n"
                + "Pelanggan yang masih memiliki transaksi tidak dapat dihapus.")) {
            return;
        }
        final int id = selectedId;
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM master_pelanggan WHERE id_pelanggan=?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }
            return null;
        }, ok -> {
            DialogHelper.success(this, "Pelanggan berhasil dihapus.");
            loadData();
        }, err -> {
            if (isForeignKeyViolation(err)) {
                DialogHelper.warn(this,
                        "Pelanggan memiliki riwayat transaksi dan tidak dapat "
                        + "dihapus. Riwayat transaksi perlu dipertahankan untuk "
                        + "keperluan laporan.");
            } else {
                DialogHelper.error(this, "Gagal menghapus pelanggan. Coba lagi.");
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
