package form;

import com.formdev.flatlaf.FlatClientProperties;

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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman Master User: kelola akun pengguna (admin / kasir / owner) beserta
 * cabangnya. Menambahkan pemeriksaan username unik dan mencegah penghapusan
 * akun yang sedang login.
 */
public class MasterUser extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Username", "Role", "Cabang"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField txtSearch = UIFactory.searchField("Cari username...");
    private final JComboBox<String> cbRoleFilter = new JComboBox<>(
            new String[]{"Semua Role", "admin", "kasir", "owner"});
    private final JButton btnEdit = UIFactory.warningButton("Edit");
    private final JButton btnHapus = UIFactory.dangerButton("Hapus");
    private final JLabel statusLabel = UIFactory.muted("");

    private int selectedId = 0;

    public MasterUser() {
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
        JLabel title = UIFactory.h2("Master User");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel desc = UIFactory.muted("Kelola akun dan hak akses pengguna.");
        desc.setFont(UITheme.small());
        desc.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(desc);
        header.add(titleBox, BorderLayout.WEST);

        JButton btnDownload = UIFactory.successButton("Download PDF");
        UIFactory.withIcon(btnDownload, Icons.PDF);
        btnDownload.addActionListener(e -> TableDownloader.download(this, table,
                "Daftar User", "Daftar_User"));
        JButton btnTambah = UIFactory.primaryButton("Tambah User");
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
        cbRoleFilter.setPreferredSize(new Dimension(150, 38));
        JButton btnReset = UIFactory.secondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbRoleFilter.setSelectedIndex(0);
            loadData();
        });
        left.add(txtSearch);
        left.add(cbRoleFilter);
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
        table.getColumnModel().getColumn(2).setMaxWidth(120);
        table.getColumnModel().getColumn(2)
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
        txtSearch.addActionListener(e -> loadData());
        txtSearch.getDocument().addDocumentListener(new DocListener(this::loadData));
        cbRoleFilter.addActionListener(e -> loadData());
    }

    // ================= DATA =================
    private void loadData() {
        statusLabel.setText("Memuat data...");
        final String keyword = txtSearch.getText().trim();
        final String role = cbRoleFilter.getSelectedIndex() == 0
                ? null : (String) cbRoleFilter.getSelectedItem();
        Async.run(() -> query(keyword, role),
                rows -> {
                    model.setRowCount(0);
                    for (Object[] row : rows) {
                        model.addRow(row);
                    }
                    selectedId = 0;
                    btnEdit.setEnabled(false);
                    btnHapus.setEnabled(false);
                    statusLabel.setText(rows.isEmpty()
                            ? "Tidak ada user."
                            : rows.size() + " user ditemukan.");
                },
                err -> statusLabel.setText("Gagal memuat data user."));
    }

    private List<Object[]> query(String keyword, String role) throws Exception {
        Connection conn = Koneksi.getKoneksi();
        StringBuilder sql = new StringBuilder(
                "SELECT u.id_user, u.username, u.role, c.nama_cabang "
                + "FROM master_user u "
                + "LEFT JOIN master_cabang c ON u.id_cabang = c.id_cabang "
                + "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (!keyword.isEmpty()) {
            sql.append(" AND u.username ILIKE ?");
            params.add("%" + keyword + "%");
        }
        if (role != null) {
            sql.append(" AND u.role = ?");
            params.add(role);
        }
        sql.append(" ORDER BY u.id_user");
        List<Object[]> rows = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            rs.getString("role"),
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
                edit ? "Edit User" : "Tambah User",
                edit ? "Perbarui akun pengguna." : "Buat akun pengguna baru.");

        JTextField txtUsername = UIFactory.textField();
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.body());
        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton:true");
        JComboBox<String> cbRole =
                new JComboBox<>(new String[]{"admin", "kasir", "owner"});
        JComboBox<ComboItem> cbCabang = new JComboBox<>();

        dialog.addRow("Username", txtUsername);
        dialog.addRow("Password", txtPassword);
        dialog.addRow("Role", cbRole);
        dialog.addRow("Cabang", cbCabang);

        loadCabang(cbCabang, () -> {
            if (edit) {
                loadForEdit(id, txtUsername, txtPassword, cbRole, cbCabang, dialog);
            }
        }, dialog);

        dialog.onSave(() -> {
            dialog.clearError();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty()) {
                dialog.showError("Username wajib diisi.");
                return;
            }
            if (username.length() < 3) {
                dialog.showError("Username minimal 3 karakter.");
                return;
            }
            if (password.isEmpty()) {
                dialog.showError("Password wajib diisi.");
                return;
            }
            if (cbCabang.getSelectedItem() == null) {
                dialog.showError("Cabang wajib dipilih.");
                return;
            }
            int idCabang = ((ComboItem) cbCabang.getSelectedItem()).getId();
            String role = (String) cbRole.getSelectedItem();

            dialog.setSaving(true);
            Async.run(() -> {
                if (usernameTaken(username, id)) {
                    throw new IllegalStateException("DUP");
                }
                save(id, username, password, role, idCabang);
                return null;
            }, ok -> {
                dialog.setSaving(false);
                dialog.dispose();
                DialogHelper.success(this, edit
                        ? "User berhasil diperbarui."
                        : "User baru berhasil ditambahkan.");
                loadData();
            }, err -> {
                dialog.setSaving(false);
                if (err instanceof IllegalStateException
                        && "DUP".equals(err.getMessage())) {
                    dialog.showError("Username sudah digunakan. Pilih yang lain.");
                } else {
                    dialog.showError("Gagal menyimpan user. Coba lagi.");
                }
            });
        });

        dialog.display();
    }

    private boolean usernameTaken(String username, int excludeId)
            throws Exception {
        Connection conn = Koneksi.getKoneksi();
        try (PreparedStatement pst = conn.prepareStatement(
                "SELECT COUNT(*) FROM master_user "
                + "WHERE LOWER(username)=LOWER(?) AND id_user<>?")) {
            pst.setString(1, username);
            pst.setInt(2, excludeId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getLong(1) > 0;
            }
        }
    }

    private void loadCabang(JComboBox<ComboItem> combo, Runnable after,
            FormDialog dialog) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            List<ComboItem> items = new ArrayList<>();
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT id_cabang, nama_cabang FROM master_cabang "
                    + "ORDER BY nama_cabang");
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

    private void loadForEdit(int id, JTextField username,
            JPasswordField password, JComboBox<String> role,
            JComboBox<ComboItem> cabang, FormDialog dialog) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT * FROM master_user WHERE id_user=?")) {
                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        Object[] d = new Object[4];
                        d[0] = rs.getString("username");
                        d[1] = rs.getString("password");
                        d[2] = rs.getString("role");
                        d[3] = rs.getInt("id_cabang");
                        return d;
                    }
                }
            }
            return null;
        }, d -> {
            if (d != null) {
                username.setText(d[0] == null ? "" : d[0].toString());
                password.setText(d[1] == null ? "" : d[1].toString());
                if (d[2] != null) {
                    role.setSelectedItem(d[2].toString());
                }
                int idc = (int) d[3];
                for (int i = 0; i < cabang.getItemCount(); i++) {
                    if (cabang.getItemAt(i).getId() == idc) {
                        cabang.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }, err -> dialog.showError("Gagal memuat data user."));
    }

    private void save(int id, String username, String password, String role,
            int idCabang) throws Exception {
        Connection conn = Koneksi.getKoneksi();
        if (id == 0) {
            try (PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO master_user (username,password,role,id_cabang) "
                    + "VALUES(?,?,?,?)")) {
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);
                pst.setInt(4, idCabang);
                pst.executeUpdate();
            }
        } else {
            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE master_user SET username=?, password=?, role=?, "
                    + "id_cabang=? WHERE id_user=?")) {
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);
                pst.setInt(4, idCabang);
                pst.setInt(5, id);
                pst.executeUpdate();
            }
        }
    }

    // ================= HAPUS =================
    private void hapus() {
        if (selectedId == 0) {
            return;
        }
        if (selectedId == Session.idUser) {
            DialogHelper.warn(this,
                    "Anda tidak dapat menghapus akun yang sedang digunakan.");
            return;
        }
        int r = table.getSelectedRow();
        String uname = r >= 0 ? model.getValueAt(r, 1).toString() : "";
        if (!DialogHelper.confirmDanger(this,
                "Hapus user \"" + uname + "\" secara permanen?")) {
            return;
        }
        final int id = selectedId;
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM master_user WHERE id_user=?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }
            return null;
        }, ok -> {
            DialogHelper.success(this, "User berhasil dihapus.");
            loadData();
        }, err -> DialogHelper.error(this, "Gagal menghapus user. Coba lagi."));
    }
}
