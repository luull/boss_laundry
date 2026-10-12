package form;

import koneksi.Koneksi;
import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import koneksi.Session;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class MasterPelanggan extends JPanel {

    JLabel title = new JLabel("MASTER PELANGGAN");

    JButton btnTambah = new JButton("+ Tambah Pelanggan");
    JLabel lblSearch = new JLabel("Search");
JTextField txtSearch = new JTextField();
    JButton btnEdit = new JButton("Edit");
    JButton btnHapus = new JButton("Hapus");
    JButton btnExportPDF = new JButton("Export PDF");

    JTable table = new JTable();
    JScrollPane scroll = new JScrollPane(table);

    DefaultTableModel model;

    int selectedId = 0;

    public MasterPelanggan() {

        setLayout(null);
        setBackground(new Color(240, 248, 255));

        title.setBounds(30, 20, 350, 40);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        btnTambah.setBounds(30, 80, 190, 35);
        btnEdit.setBounds(240, 80, 100, 35);
        btnHapus.setBounds(360, 80, 100, 35);
        btnExportPDF.setBounds(480, 80, 120, 35);
        lblSearch.setBounds(620, 80, 60, 35);
txtSearch.setBounds(680, 80, 150, 35);

        scroll.setBounds(30, 140, 800, 320);

        table.setRowHeight(25);

        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Nama");
        model.addColumn("Alamat");
        model.addColumn("No HP");
        model.addColumn("Member");

        table.setModel(model);

        add(title);
        add(btnTambah);
        add(btnEdit);
        add(btnHapus);
        add(btnExportPDF);
        add(lblSearch);
add(txtSearch);
        
        add(scroll);

        tampilData();

        btnTambah.addActionListener(e -> formPelanggan("Tambah"));

        btnEdit.addActionListener(e -> {

            if (selectedId == 0) {

                JOptionPane.showMessageDialog(this,
                        "Pilih data terlebih dahulu");

            } else {

                formPelanggan("Edit");

            }

        });

        btnHapus.addActionListener(e -> hapusData());
        
        btnExportPDF.addActionListener(e -> exportToPDF());
        
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
    private void formPelanggan(String mode) {

        JDialog dialog = new JDialog();

        dialog.setTitle(mode + " Pelanggan");
        dialog.setSize(420, 450);
        dialog.setLayout(null);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        JLabel lblNama = new JLabel("Nama");
        JLabel lblAlamat = new JLabel("Alamat");
        JLabel lblHp = new JLabel("No HP");
        JLabel lblMember = new JLabel("Jenis Member");
        JLabel lblCabang = new JLabel("Cabang");

        JTextField txtNama = new JTextField();
        JTextArea txtAlamat = new JTextArea();
        JTextField txtHp = new JTextField();

        String[] memberList = {"regular", "vip"};

        JComboBox<String> cbMember =
                new JComboBox<>(memberList);
        JComboBox<String> cbCabang =
        new JComboBox<>();

        JButton btnSimpan = new JButton("Simpan");

        lblNama.setBounds(30, 40, 120, 30);
        txtNama.setBounds(150, 40, 200, 30);

        lblAlamat.setBounds(30, 90, 120, 30);
        txtAlamat.setBounds(150, 90, 200, 70);

        lblHp.setBounds(30, 180, 120, 30);
        txtHp.setBounds(150, 180, 200, 30);

        lblMember.setBounds(30, 230, 120, 30);
        cbMember.setBounds(150, 230, 200, 30);

        lblCabang.setBounds(30, 280, 120, 30);
        cbCabang.setBounds(150, 280, 200, 30);

        btnSimpan.setBounds(150, 340, 120, 35);

        txtAlamat.setBorder(
                BorderFactory.createLineBorder(Color.GRAY)
        );
        
        // ================= LOAD CABANG =================
try {

    Connection conn =
            Koneksi.getKoneksi();

    String sql =
            "SELECT * FROM master_cabang "
            + "WHERE status='aktif'";

    PreparedStatement pst =
            conn.prepareStatement(sql);

    ResultSet rs =
            pst.executeQuery();

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
                        "SELECT * FROM master_pelanggan "
                        + "WHERE id_pelanggan=?";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setInt(1, selectedId);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {

                    txtNama.setText(
                            rs.getString("nama_pelanggan"));

                    txtAlamat.setText(
                            rs.getString("alamat"));

                    txtHp.setText(
                            rs.getString("no_hp"));

                    cbMember.setSelectedItem(
                            rs.getString("jenis_member"));
                    int idCabang =
        rs.getInt("id_cabang");

for (int i = 0;
        i < cbCabang.getItemCount();
        i++) {

    String item =
            cbCabang.getItemAt(i);

    if (item.startsWith(idCabang + " -")) {

        cbCabang.setSelectedIndex(i);

        break;
    }
}
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(dialog, e);

            }
        }

        // ================= BUTTON SIMPAN =================
        btnSimpan.addActionListener(e -> {

            if (txtNama.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Nama wajib diisi");

                return;
            }

            if (txtAlamat.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "Alamat wajib diisi");

                return;
            }

            if (txtHp.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog,
                        "No HP wajib diisi");

                return;
            }

            if (!txtHp.getText().matches("[0-9]+")) {

                JOptionPane.showMessageDialog(dialog,
                        "No HP harus angka");

                return;
            }

            try {

                Connection conn = Koneksi.getKoneksi();

                // ================= INSERT =================
                if (mode.equals("Tambah")) {

                    String sql =
                            "INSERT INTO master_pelanggan "
                            + "(nama_pelanggan,alamat,no_hp,jenis_member,id_cabang)"
                            + " VALUES(?,?,?,?,?)";

                    PreparedStatement pst =
                            conn.prepareStatement(sql);

                    pst.setString(1, txtNama.getText());
                    pst.setString(2, txtAlamat.getText());
                    pst.setString(3, txtHp.getText());
                    pst.setString(4,
                            cbMember.getSelectedItem().toString());
               String cabang =
        cbCabang.getSelectedItem()
                .toString();

int idCabang =
        Integer.parseInt(
                cabang.split(" - ")[0]
        );

pst.setInt(5, idCabang);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(dialog,
                            "Data berhasil disimpan");

                } else {

                    // ================= UPDATE =================
                    String sql =
                            "UPDATE master_pelanggan SET "
                            + "nama_pelanggan=?, "
                            + "alamat=?, "
                            + "no_hp=?, "
                            + "jenis_member=?, "
                            + "id_cabang=? "
                            + "WHERE id_pelanggan=?";

                    PreparedStatement pst =
                            conn.prepareStatement(sql);

                    pst.setString(1, txtNama.getText());
                    pst.setString(2, txtAlamat.getText());
                    pst.setString(3, txtHp.getText());
                    pst.setString(4,
                            cbMember.getSelectedItem().toString());

                    String cabang =
        cbCabang.getSelectedItem()
                .toString();

int idCabang =
        Integer.parseInt(
                cabang.split(" - ")[0]
        );

pst.setInt(5, idCabang);

pst.setInt(6, selectedId);

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

        dialog.add(lblHp);
        dialog.add(txtHp);

        dialog.add(lblMember);
        dialog.add(cbMember);
        
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

        String sql;

        PreparedStatement pst;

        // ================= ADMIN & OWNER =================
        if (Session.role.equals("admin")
                || Session.role.equals("owner")) {

            sql = "SELECT * FROM master_pelanggan";

            pst = conn.prepareStatement(sql);

        } else {

            // ================= KASIR =================
            sql =
                    "SELECT * FROM master_pelanggan "
                    + "WHERE id_cabang=?";

            pst = conn.prepareStatement(sql);

            pst.setInt(1, Session.idCabang);

        }

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {

            model.addRow(new Object[]{
                rs.getInt("id_pelanggan"),
                rs.getString("nama_pelanggan"),
                rs.getString("alamat"),
                rs.getString("no_hp"),
                rs.getString("jenis_member")
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
                "SELECT * FROM master_pelanggan "
                + "WHERE nama_pelanggan LIKE ?";

        PreparedStatement pst =
                conn.prepareStatement(sql);

        pst.setString(1,
                "%" + txtSearch.getText() + "%");

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {

            model.addRow(new Object[]{
                rs.getInt("id_pelanggan"),
                rs.getString("nama_pelanggan"),
                rs.getString("alamat"),
                rs.getString("no_hp"),
                rs.getString("jenis_member")
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
            "Yakin ingin menghapus pelanggan beserta transaksi?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            // ================= HAPUS TRANSAKSI =================
            String sqlTransaksi =
                    "DELETE FROM transaksi_laundry "
                    + "WHERE id_pelanggan=?";

            PreparedStatement pstTransaksi =
                    conn.prepareStatement(sqlTransaksi);

            pstTransaksi.setInt(1, selectedId);

            pstTransaksi.executeUpdate();

            // ================= HAPUS PELANGGAN =================
            String sqlPelanggan =
                    "DELETE FROM master_pelanggan "
                    + "WHERE id_pelanggan=?";

            PreparedStatement pstPelanggan =
                    conn.prepareStatement(sqlPelanggan);

            pstPelanggan.setInt(1, selectedId);

            pstPelanggan.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Pelanggan berhasil dihapus"
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

    // ================= EXPORT PDF =================
    private void exportToPDF() {

        try {

            // ================= LOAD REPORT =================
            InputStream reportStream = 
                    getClass().getResourceAsStream(
                            "/report/Pelanggan_report.jrxml"
                    );

            if (reportStream == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "File report tidak ditemukan"
                );

                return;
            }

            // ================= COMPILE REPORT =================
            JasperReport jasperReport =
                    JasperCompileManager.compileReport(
                            reportStream
                    );

            // ================= FILL REPORT =================
            Connection conn = Koneksi.getKoneksi();

            HashMap<String, Object> parameters =
                    new HashMap<>();

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport,
                            parameters,
                            conn
                    );

            // ================= CHOOSE SAVE LOCATION =================
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogTitle("Simpan Laporan PDF");

            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyyMMdd_HHmmss");

            String defaultFileName =
                    "Laporan_Pelanggan_"
                    + sdf.format(new java.util.Date())
                    + ".pdf";

            fileChooser.setSelectedFile(
                    new java.io.File(defaultFileName)
            );

            int userSelection =
                    fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {

                String filePath =
                        fileChooser.getSelectedFile()
                                .getAbsolutePath();

                // ================= ADD .pdf EXTENSION =================
                if (!filePath.toLowerCase().endsWith(".pdf")) {

                    filePath += ".pdf";
                }

                // ================= EXPORT TO PDF =================
                JasperExportManager.exportReportToPdfFile(
                        jasperPrint,
                        filePath
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Laporan berhasil di-export ke:\n" + filePath
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error export PDF: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}