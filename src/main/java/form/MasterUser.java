package form;

import koneksi.Koneksi;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MasterUser extends JPanel {

    JLabel title = new JLabel("MASTER USER");

    JLabel lblSearch = new JLabel("Search");
    JTextField txtSearch = new JTextField();

    JButton btnTambah = new JButton("+ Tambah User");
    JButton btnEdit = new JButton("Edit");
    JButton btnHapus = new JButton("Hapus");

    JTable table = new JTable();
    JScrollPane scroll = new JScrollPane(table);

    DefaultTableModel model;

    int selectedId = 0;

    public MasterUser() {

        setLayout(null);
        setBackground(new Color(240, 248, 255));

        // TITLE
        title.setBounds(30, 20, 300, 40);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // BUTTON
        btnTambah.setBounds(30, 80, 170, 35);
        btnEdit.setBounds(220, 80, 100, 35);
        btnHapus.setBounds(340, 80, 100, 35);

        lblSearch.setBounds(520, 80, 60, 35);
        txtSearch.setBounds(580, 80, 150, 35);

        // TABLE
        scroll.setBounds(30, 140, 700, 320);

        table.setRowHeight(25);

        // MODEL
        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Role");
        model.addColumn("Cabang");

        table.setModel(model);

        // ADD
        add(title);

        add(btnTambah);
        add(btnEdit);
        add(btnHapus);

        add(lblSearch);
        add(txtSearch);

        add(scroll);

        tampilData();

        // ACTION
        btnTambah.addActionListener(e -> formUser("Tambah"));

        btnEdit.addActionListener(e -> {

            if (selectedId == 0) {

                JOptionPane.showMessageDialog(this,
                        "Pilih data terlebih dahulu");

            } else {

                formUser("Edit");

            }

        });

        btnHapus.addActionListener(e -> hapusData());

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyReleased(java.awt.event.KeyEvent evt) {

                cariData();

            }

        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {

                int row = table.getSelectedRow();

                selectedId = Integer.parseInt(
                        table.getValueAt(row, 0).toString()
                );

            }

        });
    }

    // ================= FORM =================
    private void formUser(String mode) {

        JDialog dialog = new JDialog();

        dialog.setTitle(mode + " User");
        dialog.setSize(420, 450);
        dialog.setLayout(null);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        JLabel lblUsername = new JLabel("Username");
        JLabel lblPassword = new JLabel("Password");
        JLabel lblRole = new JLabel("Role");
        JLabel lblCabang = new JLabel("Cabang");

        JTextField txtUsername = new JTextField();
        JTextField txtPassword = new JTextField();

        String[] roleList = {"admin", "kasir", "owner"};

        JComboBox<String> cbRole =
                new JComboBox<>(roleList);

        JComboBox<String> cbCabang =
                new JComboBox<>();

        JButton btnSimpan = new JButton("Simpan");

        lblUsername.setBounds(30, 40, 120, 30);
        txtUsername.setBounds(150, 40, 200, 30);

        lblPassword.setBounds(30, 100, 120, 30);
        txtPassword.setBounds(150, 100, 200, 30);

        lblRole.setBounds(30, 160, 120, 30);
        cbRole.setBounds(150, 160, 200, 30);

        lblCabang.setBounds(30, 220, 120, 30);
        cbCabang.setBounds(150, 220, 200, 30);

        btnSimpan.setBounds(150, 300, 120, 35);

        // ================= LOAD CABANG =================
        try {

            Connection conn = Koneksi.getKoneksi();

            String sql = "SELECT * FROM master_cabang";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                cbCabang.addItem(
                        rs.getInt("id_cabang")
                        + " - "
                        + rs.getString("nama_cabang")
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(dialog, e);

        }

        // ================= EDIT MODE =================
        if (mode.equals("Edit")) {

            try {

                Connection conn = Koneksi.getKoneksi();

                String sql =
                        "SELECT * FROM master_user "
                        + "WHERE id_user=?";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setInt(1, selectedId);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {

                    txtUsername.setText(
                            rs.getString("username"));

                    txtPassword.setText(
                            rs.getString("password"));

                    cbRole.setSelectedItem(
                            rs.getString("role"));

                    // set cabang
                    int idCabang =
                            rs.getInt("id_cabang");

                    for (int i = 0;
                            i < cbCabang.getItemCount();
                            i++) {

                        String item =
                                cbCabang.getItemAt(i);

                        if (item.startsWith(
                                idCabang + " -")) {

                            cbCabang.setSelectedIndex(i);
                            break;
                        }
                    }
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(dialog, e);

            }
        }

        // ================= SAVE =================
        btnSimpan.addActionListener(e -> {

            if (txtUsername.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Username wajib diisi");

                return;
            }

            if (txtPassword.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Password wajib diisi");

                return;
            }

            try {

                Connection conn = Koneksi.getKoneksi();

                String cabang =
                        cbCabang.getSelectedItem().toString();

                int idCabang =
                        Integer.parseInt(
                                cabang.split(" - ")[0]
                        );

                // INSERT
                if (mode.equals("Tambah")) {

                    String sql =
                            "INSERT INTO master_user "
                            + "(username,password,role,id_cabang)"
                            + " VALUES(?,?,?,?)";

                    PreparedStatement pst =
                            conn.prepareStatement(sql);

                    pst.setString(1, txtUsername.getText());
                    pst.setString(2, txtPassword.getText());

                    pst.setString(3,
                            cbRole.getSelectedItem().toString());

                    pst.setInt(4, idCabang);

                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(dialog,
                            "Data berhasil disimpan");

                } else {

                    // UPDATE
                    String sql =
                            "UPDATE master_user SET "
                            + "username=?, "
                            + "password=?, "
                            + "role=?, "
                            + "id_cabang=? "
                            + "WHERE id_user=?";

                    PreparedStatement pst =
                            conn.prepareStatement(sql);

                    pst.setString(1, txtUsername.getText());
                    pst.setString(2, txtPassword.getText());

                    pst.setString(3,
                            cbRole.getSelectedItem().toString());

                    pst.setInt(4, idCabang);

                    pst.setInt(5, selectedId);

                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(dialog,
                            "Data berhasil diupdate");
                }

                tampilData();

                dialog.dispose();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(dialog, ex);

            }

        });

        dialog.add(lblUsername);
        dialog.add(txtUsername);

        dialog.add(lblPassword);
        dialog.add(txtPassword);

        dialog.add(lblRole);
        dialog.add(cbRole);

        dialog.add(lblCabang);
        dialog.add(cbCabang);

        dialog.add(btnSimpan);

        dialog.setVisible(true);
    }

    // ================= TAMPIL DATA =================
    private void tampilData() {

        model.setRowCount(0);

        try {

            Connection conn = Koneksi.getKoneksi();

            String sql =
                    "SELECT u.*, c.nama_cabang "
                    + "FROM master_user u "
                    + "JOIN master_cabang c "
                    + "ON u.id_cabang = c.id_cabang";

            PreparedStatement pst = conn.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("nama_cabang")
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }

    // ================= SEARCH =================
    private void cariData() {

        model.setRowCount(0);

        try {

            Connection conn = Koneksi.getKoneksi();

            String sql =
                    "SELECT u.*, c.nama_cabang "
                    + "FROM master_user u "
                    + "JOIN master_cabang c "
                    + "ON u.id_cabang = c.id_cabang "
                    + "WHERE username ILIKE ?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setString(1,
                    "%" + txtSearch.getText() + "%");

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("nama_cabang")
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }

    // ================= HAPUS =================
    private void hapusData() {

        if (selectedId == 0) {

            JOptionPane.showMessageDialog(this,
                    "Pilih data terlebih dahulu");

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus data?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {

                Connection conn = Koneksi.getKoneksi();

                String sql =
                        "DELETE FROM master_user "
                        + "WHERE id_user=?";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setInt(1, selectedId);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Data berhasil dihapus");

                tampilData();

                selectedId = 0;

            } catch (Exception e) {

                JOptionPane.showMessageDialog(this, e);

            }
        }
    }
}