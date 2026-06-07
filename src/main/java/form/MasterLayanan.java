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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MasterLayanan extends JPanel {

    JLabel title = new JLabel("MASTER LAYANAN");

    JLabel lblSearch = new JLabel("Search");
    JTextField txtSearch = new JTextField();

    JButton btnTambah = new JButton("+ Tambah Layanan");
    JButton btnEdit = new JButton("Edit");
    JButton btnHapus = new JButton("Hapus");

    JTable table = new JTable();
    JScrollPane scroll = new JScrollPane(table);

    DefaultTableModel model;

    int selectedId = 0;

    public MasterLayanan() {

        setLayout(null);
        setBackground(new Color(240, 248, 255));

        // ================= TITLE =================
        title.setBounds(30, 20, 400, 40);

        title.setFont(new Font("Arial", Font.BOLD, 26));

        // ================= BUTTON =================
        btnTambah.setBounds(30, 80, 190, 40);

        btnEdit.setBounds(240, 80, 100, 40);

        btnHapus.setBounds(360, 80, 100, 40);

        btnTambah.setBackground(new Color(37,99,235));
        btnTambah.setForeground(Color.BLACK);

        btnEdit.setBackground(new Color(245,158,11));
        btnEdit.setForeground(Color.BLACK);

        btnHapus.setBackground(new Color(239,68,68));
        btnHapus.setForeground(Color.BLACK);

        btnTambah.setFocusPainted(false);
        btnEdit.setFocusPainted(false);
        btnHapus.setFocusPainted(false);

        // ================= SEARCH =================
        lblSearch.setBounds(540, 80, 70, 40);

        lblSearch.setFont(new Font("Arial", Font.BOLD, 15));

        txtSearch.setBounds(610, 80, 180, 40);

        txtSearch.setFont(new Font("Arial", Font.PLAIN, 15));

        // ================= TABLE =================
        scroll.setBounds(30, 150, 980, 470);

        table.setRowHeight(25);

        table.setDefaultEditor(Object.class, null);

        // ================= MODEL =================
        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Nama Layanan");
        model.addColumn("Kategori");
        model.addColumn("Harga Min");
        model.addColumn("Harga Max");
        model.addColumn("Estimasi");
        model.addColumn("Status");

        table.setModel(model);

        // ================= ADD =================
        add(title);

        add(btnTambah);
        add(btnEdit);
        add(btnHapus);

        add(lblSearch);
        add(txtSearch);

        add(scroll);

        tampilData();

        // ================= ACTION =================
        btnTambah.addActionListener(e -> formLayanan("Tambah"));

        btnEdit.addActionListener(e -> {

            if (selectedId == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Pilih data terlebih dahulu"
                );

            } else {

                formLayanan("Edit");

            }

        });

        btnHapus.addActionListener(e -> hapusData());

        txtSearch.addKeyListener(
                new java.awt.event.KeyAdapter() {

            public void keyReleased(
                    java.awt.event.KeyEvent evt) {

                cariData();

            }

        });

        table.addMouseListener(
                new java.awt.event.MouseAdapter() {

            public void mouseClicked(
                    java.awt.event.MouseEvent evt) {

                int row = table.getSelectedRow();

                selectedId = Integer.parseInt(
                        table.getValueAt(row, 0)
                                .toString()
                );

            }

        });
    }

    // ================= FORM =================
    private void formLayanan(String mode) {

        JDialog dialog = new JDialog();

        dialog.setTitle(mode + " Layanan");

        dialog.setSize(470, 520);

        dialog.setLayout(null);

        dialog.setLocationRelativeTo(null);

        dialog.setModal(true);

        dialog.getContentPane().setBackground(Color.WHITE);

        // ================= LABEL =================
        JLabel lblNama =
                new JLabel("Nama Layanan");

        JLabel lblKategori =
                new JLabel("Kategori");

        JLabel lblHargaMin =
                new JLabel("Harga Min");

        JLabel lblHargaMax =
                new JLabel("Harga Max");

        JLabel lblEstimasi =
                new JLabel("Estimasi Hari");

        JLabel lblStatus =
                new JLabel("Status");

        // ================= FIELD =================
        JTextField txtNama =
                new JTextField();

        JTextField txtHargaMin =
                new JTextField();

        JTextField txtHargaMax =
                new JTextField();

        JTextField txtEstimasi =
                new JTextField();

        String[] kategoriList = {
            "kiloan",
            "addons"
        };

        JComboBox<String> cbKategori =
                new JComboBox<>(kategoriList);

        String[] statusList = {
            "aktif",
            "nonaktif"
        };

        JComboBox<String> cbStatus =
                new JComboBox<>(statusList);

        JButton btnSimpan =
                new JButton("SIMPAN");

        // ================= STYLE =================
        JLabel[] labels = {
            lblNama,
            lblKategori,
            lblHargaMin,
            lblHargaMax,
            lblEstimasi,
            lblStatus
        };

        for (JLabel lbl : labels) {

            lbl.setFont(
                    new Font("Arial",
                            Font.BOLD,
                            14)
            );

        }

        JTextField[] fields = {
            txtNama,
            txtHargaMin,
            txtHargaMax,
            txtEstimasi
        };

        for (JTextField txt : fields) {

            txt.setFont(
                    new Font("Arial",
                            Font.PLAIN,
                            15)
            );

            txt.setBorder(
                    BorderFactory.createLineBorder(
                            new Color(203,213,225),
                            2
                    )
            );

        }

        cbKategori.setFont(
                new Font("Arial",
                        Font.PLAIN,
                        15)
        );

        cbStatus.setFont(
                new Font("Arial",
                        Font.PLAIN,
                        15)
        );

        btnSimpan.setBackground(
                new Color(37,99,235)
        );

        btnSimpan.setForeground(Color.BLACK);

        btnSimpan.setFocusPainted(false);

        btnSimpan.setFont(
                new Font("Arial",
                        Font.BOLD,
                        15)
        );

        // ================= POSITION =================
        lblNama.setBounds(30, 30, 120, 30);
        txtNama.setBounds(170, 30, 220, 35);

        lblKategori.setBounds(30, 90, 120, 30);
        cbKategori.setBounds(170, 90, 220, 35);

        lblHargaMin.setBounds(30, 150, 120, 30);
        txtHargaMin.setBounds(170, 150, 220, 35);

        lblHargaMax.setBounds(30, 210, 120, 30);
        txtHargaMax.setBounds(170, 210, 220, 35);

        lblEstimasi.setBounds(30, 270, 120, 30);
        txtEstimasi.setBounds(170, 270, 220, 35);

        lblStatus.setBounds(30, 330, 120, 30);
        cbStatus.setBounds(170, 330, 220, 35);

        btnSimpan.setBounds(170, 410, 160, 40);

        // ================= AUTO UX =================
        cbKategori.addActionListener(e -> {

            String kategori =
                    cbKategori.getSelectedItem()
                            .toString();

            if (kategori.equals("kiloan")) {

                txtHargaMax.setEnabled(false);

                txtHargaMax.setText(
                        txtHargaMin.getText()
                );

            } else {

                txtHargaMax.setEnabled(true);

            }

        });

        txtHargaMin.addKeyListener(
                new java.awt.event.KeyAdapter() {

            public void keyReleased(
                    java.awt.event.KeyEvent evt) {

                if (cbKategori
                        .getSelectedItem()
                        .toString()
                        .equals("kiloan")) {

                    txtHargaMax.setText(
                            txtHargaMin.getText()
                    );
                }

            }

        });

        // ================= EDIT MODE =================
        if (mode.equals("Edit")) {

            try {

                Connection conn =
                        Koneksi.getKoneksi();

                String sql =
                        "SELECT * FROM master_layanan "
                        + "WHERE id_layanan=?";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setInt(1, selectedId);

                ResultSet rs =
                        pst.executeQuery();

                if (rs.next()) {

                    txtNama.setText(
                            rs.getString(
                                    "nama_layanan"
                            )
                    );

                    cbKategori.setSelectedItem(
                            rs.getString(
                                    "kategori"
                            )
                    );

                    txtHargaMin.setText(
                            rs.getString(
                                    "harga_min"
                            )
                    );

                    txtHargaMax.setText(
                            rs.getString(
                                    "harga_max"
                            )
                    );

                    txtEstimasi.setText(
                            rs.getString(
                                    "estimasi_hari"
                            )
                    );

                    cbStatus.setSelectedItem(
                            rs.getString(
                                    "status"
                            )
                    );

                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        dialog,
                        e
                );

            }
        }

        // ================= SAVE =================
        btnSimpan.addActionListener(e -> {

            if (txtNama.getText()
                    .trim()
                    .isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Nama layanan wajib diisi"
                );

                return;
            }

            if (txtHargaMin.getText()
                    .trim()
                    .isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Harga min wajib diisi"
                );

                return;
            }

            if (txtHargaMax.getText()
                    .trim()
                    .isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Harga max wajib diisi"
                );

                return;
            }

            if (txtEstimasi.getText()
                    .trim()
                    .isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Estimasi wajib diisi"
                );

                return;
            }

            if (!txtHargaMin.getText()
                    .matches("[0-9]+")) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Harga min harus angka"
                );

                return;
            }

            if (!txtHargaMax.getText()
                    .matches("[0-9]+")) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Harga max harus angka"
                );

                return;
            }

            if (!txtEstimasi.getText()
                    .matches("[0-9]+")) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Estimasi harus angka"
                );

                return;
            }

            try {

                Connection conn =
                        Koneksi.getKoneksi();

                // ================= INSERT =================
                if (mode.equals("Tambah")) {

                    String sql =
                            "INSERT INTO "
                            + "master_layanan "
                            + "(nama_layanan,"
                            + "estimasi_hari,"
                            + "status,"
                            + "kategori,"
                            + "harga_min,"
                            + "harga_max)"
                            + " VALUES(?,?,?,?,?,?)";

                    PreparedStatement pst =
                            conn.prepareStatement(sql);

                    pst.setString(
                            1,
                            txtNama.getText()
                    );

                    pst.setString(
                            2,
                            txtEstimasi.getText()
                    );

                    pst.setString(
                            3,
                            cbStatus
                                    .getSelectedItem()
                                    .toString()
                    );

                    pst.setString(
                            4,
                            cbKategori
                                    .getSelectedItem()
                                    .toString()
                    );

                    pst.setString(
                            5,
                            txtHargaMin.getText()
                    );

                    pst.setString(
                            6,
                            txtHargaMax.getText()
                    );

                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Data berhasil disimpan"
                    );

                } else {

                    // ================= UPDATE =================
                    String sql =
                            "UPDATE master_layanan SET "
                            + "nama_layanan=?, "
                            + "estimasi_hari=?, "
                            + "status=?, "
                            + "kategori=?, "
                            + "harga_min=?, "
                            + "harga_max=? "
                            + "WHERE id_layanan=?";

                    PreparedStatement pst =
                            conn.prepareStatement(sql);

                    pst.setString(
                            1,
                            txtNama.getText()
                    );

                    pst.setString(
                            2,
                            txtEstimasi.getText()
                    );

                    pst.setString(
                            3,
                            cbStatus
                                    .getSelectedItem()
                                    .toString()
                    );

                    pst.setString(
                            4,
                            cbKategori
                                    .getSelectedItem()
                                    .toString()
                    );

                    pst.setString(
                            5,
                            txtHargaMin.getText()
                    );

                    pst.setString(
                            6,
                            txtHargaMax.getText()
                    );

                    pst.setInt(
                            7,
                            selectedId
                    );

                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Data berhasil diupdate"
                    );

                }

                tampilData();

                dialog.dispose();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        ex
                );

            }

        });

        // ================= ADD =================
        dialog.add(lblNama);
        dialog.add(txtNama);

        dialog.add(lblKategori);
        dialog.add(cbKategori);

        dialog.add(lblHargaMin);
        dialog.add(txtHargaMin);

        dialog.add(lblHargaMax);
        dialog.add(txtHargaMax);

        dialog.add(lblEstimasi);
        dialog.add(txtEstimasi);

        dialog.add(lblStatus);
        dialog.add(cbStatus);

        dialog.add(btnSimpan);

        dialog.setVisible(true);
    }

    // ================= TAMPIL DATA =================
    private void tampilData() {

        model.setRowCount(0);

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT * FROM master_layanan";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{

                    rs.getInt("id_layanan"),

                    rs.getString("nama_layanan"),

                    rs.getString("kategori"),

                    rs.getString("harga_min"),

                    rs.getString("harga_max"),

                    rs.getString("estimasi_hari")
                    + " Hari",

                    rs.getString("status")

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

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT * FROM master_layanan "
                    + "WHERE nama_layanan LIKE ?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setString(
                    1,
                    "%"
                    + txtSearch.getText()
                    + "%"
            );

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{

                    rs.getInt("id_layanan"),

                    rs.getString("nama_layanan"),

                    rs.getString("kategori"),

                    rs.getString("harga_min"),

                    rs.getString("harga_max"),

                    rs.getString("estimasi_hari")
                    + " Hari",

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

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Yakin ingin menghapus data?",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm
                == JOptionPane.YES_OPTION) {

            try {

                Connection conn =
                        Koneksi.getKoneksi();

                String sql =
                        "DELETE FROM master_layanan "
                        + "WHERE id_layanan=?";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setInt(1, selectedId);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Data berhasil dihapus"
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