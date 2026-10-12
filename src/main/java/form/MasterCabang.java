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
 * Halaman Master Cabang: daftar, cari, filter status, tambah, edit, dan
 * nonaktifkan cabang. Responsif (BorderLayout) dan memuat data di latar
 * belakang agar UI tidak freeze.
 */
public class MasterCabang extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nama Cabang", "Alamat", "Telepon", "Status"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField txtSearch = UIFactory.searchField("Cari nama cabang...");
    private final JComboBox<String> cbStatus =
            new JComboBox<>(new String[]{"Semua Status", "aktif", "nonaktif"});
    private final JButton btnEdit = UIFactory.warningButton("Edit");
    private final JButton btnNonaktif = UIFactory.dangerButton("Nonaktif");
    private final JLabel statusLabel = UIFactory.muted("");

    private int selectedId = 0;

    public MasterCabang() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG);
        setOpaque(false);

        add(buildTop(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        wireEvents();
        loadData();
    }

    // ================= HEADER + TOOLBAR =================
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
        JLabel title = UIFactory.h2("Master Cabang");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel desc = UIFactory.muted("Kelola data cabang laundry.");
        desc.setFont(UITheme.small());
        desc.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(desc);
        header.add(titleBox, BorderLayout.WEST);

        JButton btnDownload = UIFactory.successButton("Download PDF");
        UIFactory.withIcon(btnDownload, Icons.PDF);
        btnDownload.addActionListener(e -> TableDownloader.download(this, table,
                "Daftar Cabang", "Daftar_Cabang"));
        JButton btnTambah = UIFactory.primaryButton("Tambah Cabang");
        UIFactory.withIcon(btnTambah, Icons.PLUS);
        btnTambah.addActionListener(e -> openForm(0));
        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        hr.setOpaque(false);
        hr.add(btnDownload);
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
        cbStatus.setPreferredSize(new Dimension(150, 38));
        JButton btnReset = UIFactory.secondaryButton("Reset");
        left.add(txtSearch);
        left.add(cbStatus);
        left.add(btnReset);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbStatus.setSelectedIndex(0);
            loadData();
        });
        toolbar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        btnEdit.setEnabled(false);
        btnNonaktif.setEnabled(false);
        UIFactory.withIcon(btnEdit, Icons.EDIT);
        right.add(btnEdit);
        right.add(btnNonaktif);
        toolbar.add(right, BorderLayout.EAST);

        top.add(toolbar);
        return top;
    }

    private JPanel buildTable() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(0)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(3).setMaxWidth(160);
        table.getColumnModel().getColumn(4).setMaxWidth(130);
        table.getColumnModel().getColumn(4)
                .setCellRenderer(UIFactory.statusRenderer());
        card.add(UIFactory.tableScroll(table), BorderLayout.CENTER);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 0));
        card.add(statusLabel, BorderLayout.SOUTH);
        return card;
    }

    private void wireEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                selectedId = Integer.parseInt(model.getValueAt(r, 0).toString());
                btnEdit.setEnabled(true);
                btnNonaktif.setEnabled(true);
            } else {
                selectedId = 0;
                btnEdit.setEnabled(false);
                btnNonaktif.setEnabled(false);
            }
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
        btnNonaktif.addActionListener(e -> nonaktifkan());

        txtSearch.addActionListener(e -> loadData());
        txtSearch.getDocument().addDocumentListener(
                new DocListener(this::loadData));
        cbStatus.addActionListener(e -> loadData());
    }

    // ================= DATA =================
    private void loadData() {
        statusLabel.setText("Memuat data...");
        final String keyword = txtSearch.getText().trim();
        final String status = cbStatus.getSelectedIndex() == 0
                ? null : (String) cbStatus.getSelectedItem();

        Async.run(() -> query(keyword, status),
                rows -> {
                    model.setRowCount(0);
                    for (Object[] row : rows) {
                        model.addRow(row);
                    }
                    selectedId = 0;
                    btnEdit.setEnabled(false);
                    btnNonaktif.setEnabled(false);
                    statusLabel.setText(rows.isEmpty()
                            ? "Tidak ada data cabang."
                            : rows.size() + " cabang ditemukan.");
                },
                err -> statusLabel.setText("Gagal memuat data cabang."));
    }

    private List<Object[]> query(String keyword, String status)
            throws Exception {
        Connection conn = Koneksi.getKoneksi();
        StringBuilder sql = new StringBuilder(
                "SELECT id_cabang, nama_cabang, alamat, telepon, status "
                + "FROM master_cabang WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (!keyword.isEmpty()) {
            sql.append(" AND nama_cabang ILIKE ?");
            params.add("%" + keyword + "%");
        }
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY id_cabang");

        List<Object[]> rows = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt("id_cabang"),
                            rs.getString("nama_cabang"),
                            rs.getString("alamat"),
                            rs.getString("telepon"),
                            rs.getString("status")});
                }
            }
        }
        return rows;
    }

    // ================= FORM TAMBAH / EDIT =================
    private void openForm(int id) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        boolean edit = id != 0;
        FormDialog dialog = new FormDialog(owner,
                edit ? "Edit Cabang" : "Tambah Cabang",
                edit ? "Perbarui informasi cabang."
                        : "Lengkapi data cabang baru.");

        JTextField txtNama = UIFactory.textField();
        JTextArea txtAlamat = new JTextArea(3, 20);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setFont(UITheme.body());
        JScrollPane alamatScroll = new JScrollPane(txtAlamat);
        alamatScroll.setPreferredSize(new Dimension(260, 72));
        alamatScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        JTextField txtTelepon = UIFactory.textField();
        JComboBox<String> cbFormStatus =
                new JComboBox<>(new String[]{"aktif", "nonaktif"});

        dialog.addRow("Nama Cabang", txtNama);
        dialog.addRow("Alamat", alamatScroll);
        dialog.addRow("Telepon", txtTelepon, "Hanya angka, mis. 081234567890");
        dialog.addRow("Status", cbFormStatus);

        if (edit) {
            loadForEdit(id, txtNama, txtAlamat, txtTelepon, cbFormStatus, dialog);
        }

        dialog.onSave(() -> {
            dialog.clearError();
            String nama = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String telepon = txtTelepon.getText().trim();

            if (nama.isEmpty()) {
                dialog.showError("Nama cabang wajib diisi.");
                txtNama.requestFocusInWindow();
                return;
            }
            if (alamat.isEmpty()) {
                dialog.showError("Alamat wajib diisi.");
                txtAlamat.requestFocusInWindow();
                return;
            }
            if (telepon.isEmpty()) {
                dialog.showError("Telepon wajib diisi.");
                txtTelepon.requestFocusInWindow();
                return;
            }
            if (!telepon.matches("[0-9]{6,20}")) {
                dialog.showError("Telepon harus berupa 6-20 digit angka.");
                txtTelepon.requestFocusInWindow();
                return;
            }

            String st = (String) cbFormStatus.getSelectedItem();
            dialog.setSaving(true);
            Async.run(() -> {
                save(id, nama, alamat, telepon, st);
                return null;
            }, ok -> {
                dialog.setSaving(false);
                dialog.dispose();
                DialogHelper.success(this, edit
                        ? "Data cabang berhasil diperbarui."
                        : "Cabang baru berhasil ditambahkan.");
                loadData();
            }, err -> {
                dialog.setSaving(false);
                dialog.showError("Gagal menyimpan data. Coba lagi.");
            });
        });

        dialog.display();
    }

    private void loadForEdit(int id, JTextField nama, JTextArea alamat,
            JTextField telepon, JComboBox<String> status, FormDialog dialog) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT * FROM master_cabang WHERE id_cabang=?")) {
                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return new String[]{rs.getString("nama_cabang"),
                                rs.getString("alamat"), rs.getString("telepon"),
                                rs.getString("status")};
                    }
                }
            }
            return null;
        }, data -> {
            if (data != null) {
                nama.setText(data[0]);
                alamat.setText(data[1]);
                telepon.setText(data[2]);
                status.setSelectedItem(data[3]);
            }
        }, err -> dialog.showError("Gagal memuat data cabang."));
    }

    private void save(int id, String nama, String alamat, String telepon,
            String status) throws Exception {
        Connection conn = Koneksi.getKoneksi();
        if (id == 0) {
            try (PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO master_cabang "
                    + "(nama_cabang,alamat,telepon,status) VALUES(?,?,?,?)")) {
                pst.setString(1, nama);
                pst.setString(2, alamat);
                pst.setString(3, telepon);
                pst.setString(4, status);
                pst.executeUpdate();
            }
        } else {
            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE master_cabang SET nama_cabang=?, alamat=?, "
                    + "telepon=?, status=? WHERE id_cabang=?")) {
                pst.setString(1, nama);
                pst.setString(2, alamat);
                pst.setString(3, telepon);
                pst.setString(4, status);
                pst.setInt(5, id);
                pst.executeUpdate();
            }
        }
    }

    // ================= NONAKTIFKAN =================
    private void nonaktifkan() {
        if (selectedId == 0) {
            return;
        }
        int r = table.getSelectedRow();
        String nama = r >= 0 ? model.getValueAt(r, 1).toString() : "";
        if (!DialogHelper.confirmDanger(this,
                "Nonaktifkan cabang \"" + nama + "\"?\n"
                + "Cabang tidak akan muncul pada pilihan aktif.")) {
            return;
        }
        final int id = selectedId;
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE master_cabang SET status='nonaktif' "
                    + "WHERE id_cabang=?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }
            return null;
        }, ok -> {
            DialogHelper.success(this, "Cabang berhasil dinonaktifkan.");
            loadData();
        }, err -> DialogHelper.error(this,
                "Gagal menonaktifkan cabang. Coba lagi."));
    }
}
