package form;

import koneksi.Koneksi;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MasterCabang extends JPanel {

    JLabel title = new JLabel("MASTER CABANG");

    JButton btnTambah = new JButton("+ Tambah Cabang");
    JLabel lblSearch = new JLabel("Search");
JTextField txtSearch = new JTextField();
    JButton btnEdit = new JButton("Edit");
    JButton btnHapus = new JButton("Nonaktif");

    JTable table = new JTable();
    JScrollPane scroll = new JScrollPane(table);

    DefaultTableModel model;

    int selectedId = 0;

    public MasterCabang() {

        setLayout(null);
        setBackground(new Color(240, 248, 255));

        // TITLE
        title.setBounds(30, 20, 300, 40);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // BUTTON
        btnTambah.setBounds(30, 80, 160, 35);
        btnEdit.setBounds(210, 80, 100, 35);
        btnHapus.setBounds(330, 80, 100, 35);
        
        //SEARCH
        lblSearch.setBounds(520, 80, 60, 35);
txtSearch.setBounds(580, 80, 150, 35);

        // TABLE
        scroll.setBounds(30, 140, 700, 320);

        table.setRowHeight(25);

        // MODEL
        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Nama Cabang");
        model.addColumn("Alamat");
        model.addColumn("Telepon");
        model.addColumn("Status");

        table.setModel(model);

        // ADD COMPONENT
        add(title);
        add(btnTambah);
        add(btnEdit);
        add(btnHapus);
        add(lblSearch);
        add(txtSearch);
        add(scroll);

        tampilData();

        // ACTION
        btnTambah.addActionListener(e -> formCabang("Tambah"));

        btnEdit.addActionListener(e -> {

            if (selectedId == 0) {

                JOptionPane.showMessageDialog(this,
                        "Pilih data terlebih dahulu");

            } else {

                formCabang("Edit");

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

    // ================= FORM POPUP =================
    private void formCabang(String mode) {

        JDialog dialog = new JDialog();

        dialog.setTitle(mode + " Cabang");
        dialog.setSize(420, 420);
        dialog.setLayout(null);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        JLabel lblNama = new JLabel("Nama Cabang");
        JLabel lblAlamat = new JLabel("Alamat");
        JLabel lblTelepon = new JLabel("Telepon");
        JLabel lblStatus = new JLabel("Status");

        JTextField txtNama = new JTextField();
        JTextArea txtAlamat = new JTextArea();
        JTextField txtTelepon = new JTextField();

        String[] statusList = {"aktif", "nonaktif"};

        JComboBox<String> cbStatus =
                new JComboBox<>(statusList);

        JButton btnSimpan = new JButton("Simpan");

        lblNama.setBounds(30, 40, 120, 30);
        txtNama.setBounds(150, 40, 200, 30);

        lblAlamat.setBounds(30, 90, 120, 30);
        txtAlamat.setBounds(150, 90, 200, 70);

        lblTelepon.setBounds(30, 180, 120, 30);
        txtTelepon.setBounds(150, 180, 200, 30);

        lblStatus.setBounds(30, 230, 120, 30);
        cbStatus.setBounds(150, 230, 200, 30);

        btnSimpan.setBounds(150, 300, 120, 35);

        txtAlamat.setBorder(
                BorderFactory.createLineBorder(Color.GRAY)
        );

        // ================= LOAD DATA EDIT =================
        if (mode.equals("Edit")) {

            try {

                Connection conn = Koneksi.getKoneksi();

                String sql =
                        "SELECT * FROM master_cabang WHERE id_cabang=?";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setInt(1, selectedId);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {

                    txtNama.setText(
                            rs.getString("nama_cabang"));

                    txtAlamat.setText(
                            rs.getString("alamat"));

                    txtTelepon.setText(
                            rs.getString("telepon"));

                    cbStatus.setSelectedItem(
                            rs.getString("status"));
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(dialog, e);

            }
        }

        // ================= SAVE BUTTON =================
        btnSimpan.addActionListener(e -> {

            // VALIDASI
            if (txtNama.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Nama cabang wajib diisi");

                return;
            }

            if (txtAlamat.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Alamat wajib diisi");

                return;
            }

            if (txtTelepon.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Telepon wajib diisi");

                return;
            }

            // VALIDASI ANGKA
            if (!txtTelepon.getText().matches("[0-9]+")) {

                JOptionPane.showMessageDialog(dialog,
                        "Telepon harus angka");

                return;
            }

            try {

                Connection conn = Koneksi.getKoneksi();

                // ================= INSERT =================
                if (mode.equals("Tambah")) {

                    String sql =
                            "INSERT INTO master_cabang "
                            + "(nama_cabang,alamat,telepon,status)"
                            + " VALUES(?,?,?,?)";

                    PreparedStatement pst =
                            conn.prepareStatement(sql);

                    pst.setString(1, txtNama.getText());
                    pst.setString(2, txtAlamat.getText());
                    pst.setString(3, txtTelepon.getText());
                    pst.setString(4,
                            cbStatus.getSelectedItem().toString());

                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(dialog,
                            "Data berhasil disimpan");

                } else {

                    // ================= UPDATE =================
                    String sql =
                            "UPDATE master_cabang SET "
                            + "nama_cabang=?, "
                            + "alamat=?, "
                            + "telepon=?, "
                            + "status=? "
                            + "WHERE id_cabang=?";

                    PreparedStatement pst =
                            conn.prepareStatement(sql);

                    pst.setString(1, txtNama.getText());
                    pst.setString(2, txtAlamat.getText());
                    pst.setString(3, txtTelepon.getText());
                    pst.setString(4,
                            cbStatus.getSelectedItem().toString());

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

        dialog.add(lblNama);
        dialog.add(txtNama);

        dialog.add(lblAlamat);
        dialog.add(txtAlamat);

        dialog.add(lblTelepon);
        dialog.add(txtTelepon);

        dialog.add(lblStatus);
        dialog.add(cbStatus);

        dialog.add(btnSimpan);

        dialog.setVisible(true);
    }

    // ================= TAMPIL DATA =================
    private void tampilData() {

        model.setRowCount(0);

        try {

            Connection conn = Koneksi.getKoneksi();

            String sql = "SELECT * FROM master_cabang";

            PreparedStatement pst = conn.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                    rs.getInt("id_cabang"),
                    rs.getString("nama_cabang"),
                    rs.getString("alamat"),
                    rs.getString("telepon"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }

    private void cariData() {

    model.setRowCount(0);

    try {

        Connection conn = Koneksi.getKoneksi();

        String sql =
                "SELECT * FROM master_cabang "
                + "WHERE nama_cabang ILIKE ?";

        PreparedStatement pst =
                conn.prepareStatement(sql);

        pst.setString(1,
                "%" + txtSearch.getText() + "%");

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {

            model.addRow(new Object[]{
                rs.getInt("id_cabang"),
                rs.getString("nama_cabang"),
                rs.getString("alamat"),
                rs.getString("telepon"),
                rs.getString("status")
            });
        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(this, e);

    }
}
    // ================= HAPUS =================
    private void hapusData() {

    if (selectedId == 0) {

        JOptionPane.showMessageDialog(
                this,
                "Pilih data terlebih dahulu"
        );

        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Nonaktifkan cabang ini?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "UPDATE master_cabang "
                    + "SET status='nonaktif' "
                    + "WHERE id_cabang=?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setInt(1, selectedId);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Cabang berhasil dinonaktifkan"
            );

            tampilData();

            selectedId = 0;

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e
            );

        }
    }
}
}