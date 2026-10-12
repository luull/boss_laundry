package form;

import koneksi.Koneksi;
import koneksi.Session;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Element;
public class KasirDashboard extends JFrame {

    JPanel header = new JPanel();

    JPanel formPanel = new JPanel();

    JPanel layananPanel = new JPanel();

    JPanel addonsPanel = new JPanel();

    JLabel title = new JLabel();

    JLabel lblPelanggan =
            new JLabel("Pelanggan");

    JLabel lblLayanan =
            new JLabel("Layanan Kiloan");

    JLabel lblAddons =
            new JLabel("Add Ons");

    JLabel lblBerat =
            new JLabel("Qty / Kg");

    JLabel lblHarga =
            new JLabel("Harga");

    JComboBox<String> cbPelanggan =
            new JComboBox<>();

    JComboBox<String> cbQtyType =
            new JComboBox<>(
                    new String[]{
                        "Kg",
                        "Pcs"
                    }
            );

    JTextField txtBerat =
            new JTextField();

    JTextField txtHarga =
            new JTextField();

    JLabel lblTotal =
            new JLabel("Rp 0");

    JTextArea txtPreview =
            new JTextArea();

    JButton btnHitung =
            new JButton("HITUNG");

    JButton btnSimpan =
            new JButton("SIMPAN TRANSAKSI");

    JButton btnStatus =
            new JButton("UPDATE STATUS");
    JButton btnCetak =
        new JButton("CETAK STRUK");

    JButton btnTambahPelanggan =
            new JButton("+ Pelanggan Baru");

    JCheckBox cbSeprei =
            new JCheckBox("Seprei");

    JCheckBox cbSelimut =
            new JCheckBox("Selimut");

    JCheckBox cbBedCover =
            new JCheckBox("Bed Cover");

    JCheckBox cbBoneka =
            new JCheckBox("Boneka");

    JCheckBox cbTas =
            new JCheckBox("Tas");

    JCheckBox cbKarpet =
            new JCheckBox("Karpet");
    
    JTextField qtySeprei =
        new JTextField("1");

JTextField qtySelimut =
        new JTextField("1");

JTextField qtyBedCover =
        new JTextField("1");

JTextField qtyBoneka =
        new JTextField("1");

JTextField qtyTas =
        new JTextField("1");

JTextField qtyKarpet =
        new JTextField("1");

    int selectedLayanan = 0;

    double selectedHarga = 0;

    double selectedHargaMax = 0;

    String addons = "";

    JTable table = new JTable();

    JScrollPane scroll =
            new JScrollPane(table);

    DefaultTableModel model;

    public KasirDashboard() {

        setTitle("Kasir BOZZ Laundry");

        setSize(1250, 900);

        setLayout(null);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(
                new Color(248,250,252)
        );

        // ================= HEADER =================
        header.setBounds(0, 0, 1250, 70);

        header.setLayout(null);

        header.setBackground(
                new Color(37,99,235)
        );

        title.setText(
                "BOZZ LAUNDRY - "
                + Session.username.toUpperCase()
        );

        title.setBounds(30, 15, 600, 40);

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        header.add(title);

        // ================= FORM PANEL =================
        formPanel.setBounds(30, 90, 500, 760);

        formPanel.setLayout(null);

        formPanel.setBackground(Color.WHITE);

        formPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220,220,220)
                )
        );

        // ================= PELANGGAN =================
        lblPelanggan.setBounds(30, 30, 150, 30);

        lblPelanggan.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        cbPelanggan.setBounds(30, 65, 400, 40);

        cbPelanggan.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        cbPelanggan.setBackground(Color.WHITE);

        cbPelanggan.setForeground(Color.BLACK);

        btnTambahPelanggan.setBounds(
                30,
                115,
                180,
                35
        );

        btnTambahPelanggan.setBackground(
                new Color(59,130,246)
        );

        btnTambahPelanggan.setForeground(Color.BLACK);

        btnTambahPelanggan.setFocusPainted(false);

        btnTambahPelanggan.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        // ================= LAYANAN =================
        lblLayanan.setBounds(30, 170, 200, 30);

        lblLayanan.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        layananPanel.setBounds(30, 210, 400, 55);

        layananPanel.setLayout(
                new GridLayout(
                        0,
                        3,
                        5,
                        5
                )
        );

        layananPanel.setBackground(Color.WHITE);

        // ================= ADDONS =================
        lblAddons.setBounds(30, 290, 200, 30);

        lblAddons.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        addonsPanel.setBounds(30, 325, 420, 100);

        addonsPanel.setLayout(
                new GridLayout(3,2,10,10)
        );

        addonsPanel.setBackground(Color.WHITE);

        JCheckBox[] addonsList = {
            cbSeprei,
            cbSelimut,
            cbBedCover,
            cbBoneka,
            cbTas,
            cbKarpet
        };

        for (JCheckBox cb : addonsList) {
            JTextField[] qtyFields = {
    qtySeprei,
    qtySelimut,
    qtyBedCover,
    qtyBoneka,
    qtyTas,
    qtyKarpet
};

for (JTextField qty : qtyFields) {

    qty.setHorizontalAlignment(
            JTextField.CENTER
    );

    qty.setFont(
            new Font("Arial", Font.BOLD, 12)
    );
}

            cb.setBackground(Color.WHITE);

            cb.setForeground(Color.BLACK);

            cb.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            13
                    )
            );

            cb.addActionListener(e -> {

                if (cb.isSelected()) {

                    cb.setBackground(
                            new Color(220,252,231)
                    );

                    cb.setBorder(
                            BorderFactory.createLineBorder(
                                    new Color(34,197,94),
                                    2
                            )
                    );

                } else {

                    cb.setBackground(Color.WHITE);

                    cb.setBorder(null);

                }

            });

addonsPanel.add(cb);

if (cb == cbSeprei) {

    addonsPanel.add(qtySeprei);

} else if (cb == cbSelimut) {

    addonsPanel.add(qtySelimut);

} else if (cb == cbBedCover) {

    addonsPanel.add(qtyBedCover);

} else if (cb == cbBoneka) {

    addonsPanel.add(qtyBoneka);

} else if (cb == cbTas) {

    addonsPanel.add(qtyTas);

} else if (cb == cbKarpet) {

    addonsPanel.add(qtyKarpet);
}

        }

        // ================= BERAT =================
        lblBerat.setBounds(30, 450, 150, 30);

        lblBerat.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        cbQtyType.setBounds(30, 485, 100, 38);

        txtBerat.setBounds(140, 485, 290, 38);

        txtBerat.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        // ================= HARGA =================
        lblHarga.setBounds(30, 545, 150, 30);

        lblHarga.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        txtHarga.setBounds(30, 580, 400, 40);

        txtHarga.setEnabled(false);

        txtHarga.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        // ================= TOTAL =================
        lblTotal.setBounds(30, 620, 400, 60);

        lblTotal.setFont(
                new Font("Arial", Font.BOLD, 42)
        );

        lblTotal.setForeground(
                new Color(22,163,74)
        );

        // ================= PREVIEW =================
        txtPreview.setBounds(30, 680, 400, 45);

        txtPreview.setEditable(false);

        txtPreview.setBackground(
                new Color(248,250,252)
        );

        txtPreview.setForeground(Color.BLACK);

        txtPreview.setFont(
                new Font("Monospaced", Font.PLAIN, 12)
        );

        txtPreview.setBorder(
                BorderFactory.createLineBorder(
                        new Color(203,213,225),
                        2
                )
        );

        // ================= BUTTON =================
        btnHitung.setBackground(
        new Color(59,130,246)
        );

        btnHitung.setForeground(Color.WHITE);

        btnHitung.setFocusPainted(false);

        btnHitung.setBorderPainted(false);

        btnHitung.setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        btnHitung.setBounds(320, 635, 110, 40);
        

        btnSimpan.setBounds(560, 90, 180, 45);

        btnStatus.setBounds(750, 90, 180, 45);
        
        btnCetak.setBounds(940, 90, 180, 45);

btnCetak.setBackground(
        new Color(168,85,247)
);

btnCetak.setForeground(Color.BLACK);

btnCetak.setFocusPainted(false);

btnCetak.setBorderPainted(false);

        btnHitung.setBackground(
                new Color(59,130,246)
        );

        btnHitung.setForeground(Color.WHITE);

        btnHitung.setOpaque(true);

        btnHitung.setContentAreaFilled(true);

        btnHitung.setBorderPainted(false);

        btnSimpan.setBackground(
        new Color(34,197,94)
        );

        btnSimpan.setForeground(Color.WHITE);

        btnSimpan.setOpaque(true);

        btnSimpan.setContentAreaFilled(true);

        btnSimpan.setBorderPainted(false);

        btnStatus.setBackground(
                new Color(249,115,22)
        );

        btnStatus.setForeground(Color.WHITE);

        btnStatus.setOpaque(true);

        btnStatus.setContentAreaFilled(true);

        btnStatus.setBorderPainted(false);

        // ================= TABLE =================
        scroll.setBounds(560, 160, 630, 690);

        table.setRowHeight(32);

        table.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        model = new DefaultTableModel();

        model.addColumn("Invoice");
        model.addColumn("Pelanggan");
        model.addColumn("Layanan");
        model.addColumn("Qty");
        model.addColumn("Add Ons");
        model.addColumn("Total");
        model.addColumn("Status");

        table.setModel(model);

        // ================= ADD =================
        formPanel.add(lblPelanggan);
        formPanel.add(cbPelanggan);
        formPanel.add(btnTambahPelanggan);

        formPanel.add(lblLayanan);
        formPanel.add(layananPanel);

        formPanel.add(lblAddons);
        formPanel.add(addonsPanel);

        formPanel.add(lblBerat);
        formPanel.add(cbQtyType);
        formPanel.add(txtBerat);

        formPanel.add(lblHarga);
        formPanel.add(txtHarga);

        formPanel.add(lblTotal);
        formPanel.add(txtPreview);

        add(header);
        add(formPanel);

        add(btnHitung);
        add(btnSimpan);
        add(btnStatus);
        add(btnCetak);

        add(scroll);

        loadPelanggan();

        loadLayanan();

        tampilData();

        // ================= ACTION =================
        btnHitung.addActionListener(e -> {

            hitungTotal();

        });

        btnSimpan.addActionListener(e -> {

            simpanTransaksi();

        });
        btnStatus.addActionListener(e -> {

        if (table.getSelectedRow() == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Pilih transaksi dulu"
                );

                return;
            }

            ubahStatus();

        });
        
        btnCetak.addActionListener(e -> {

    cetakStruk();

});

        btnTambahPelanggan.addActionListener(e -> {

            String nama =
                    JOptionPane.showInputDialog(
                            this,
                            "Masukkan Nama Pelanggan"
                    );

            if (nama == null || nama.trim().isEmpty()) {

                return;
            }

            try {

                Connection conn =
                        Koneksi.getKoneksi();

                String sql =
                        "INSERT INTO master_pelanggan "
                        + "(nama_pelanggan,id_cabang) "
                        + "VALUES(?,?)";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setString(1, nama);

                pst.setInt(2, Session.idCabang);

                pst.executeUpdate();

                cbPelanggan.removeAllItems();

                loadPelanggan();

                JOptionPane.showMessageDialog(
                        this,
                        "Pelanggan berhasil ditambahkan"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex
                );
            }

        });

        setVisible(true);
    }
    private void ubahStatus() {

    try {

        int row =
                table.getSelectedRow();

        String invoice =
                table.getValueAt(row, 0)
                        .toString();

        String[] statusList = {
            "proses",
            "dicuci",
            "disetrika",
            "selesai",
            "diambil"
        };

        String status =
                (String) JOptionPane.showInputDialog(
                        this,
                        "Pilih Status",
                        "Update Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        statusList,
                        statusList[0]
                );

        if (status == null) {

            return;
        }

        Connection conn =
                Koneksi.getKoneksi();

        String sql =
                "UPDATE transaksi_laundry "
                + "SET status=? "
                + "WHERE kode_invoice=?";

        PreparedStatement pst =
                conn.prepareStatement(sql);

        pst.setString(1, status);

        pst.setString(2, invoice);

        pst.executeUpdate();

        JOptionPane.showMessageDialog(
                this,
                "Status berhasil diupdate"
        );

        tampilData();

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e
        );

    }

}

    // ================= RESET =================
    private void resetLayanan() {

        java.awt.Component[] comps =
                layananPanel.getComponents();

        for (java.awt.Component c : comps) {

            if (c instanceof JButton) {

                JButton btn = (JButton) c;

                btn.setBackground(
                        new Color(241,245,249)
                );

                btn.setForeground(Color.BLACK);

            }
        }
    }

    // ================= LOAD LAYANAN =================
    private void loadLayanan() {

        try {

            layananPanel.removeAll();

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT * FROM master_layanan "
                    + "WHERE kategori='kiloan' "
                    + "AND status='aktif'";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                int id =
                        rs.getInt("id_layanan");

                double harga =
                        rs.getDouble("harga_min");

                double hargaMax =
                        rs.getDouble("harga_max");

                JButton btn =
                        new JButton(
                                "<html><center>"
                                + rs.getString("nama_layanan")
                                + "<br>Rp "
                                + (int) harga
                                + "</center></html>"
                        );

                    btn.setBackground(
                   new Color(226,232,240)
                );

                btn.setForeground(Color.BLACK);

                btn.setOpaque(true);

                btn.setContentAreaFilled(true);

                btn.setBorderPainted(false);

                btn.setFocusPainted(false);

                btn.setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                11
                        )
                );

                btn.addActionListener(e -> {

                    selectedLayanan = id;

                    selectedHarga = harga;

                    selectedHargaMax = hargaMax;

                    resetLayanan();

                btn.setBackground(
                        new Color(34,197,94)
                );

                btn.setForeground(Color.BLACK);

                btn.setOpaque(true);

                btn.setContentAreaFilled(true);

                    txtHarga.setText(
                            String.valueOf(
                                    (int) harga
                            )
                    );

                    txtHarga.setEnabled(
                            harga != hargaMax
                    );

                });

                layananPanel.add(btn);

            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }

    // ================= LOAD PELANGGAN =================
    private void loadPelanggan() {

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT * FROM master_pelanggan "
                    + "WHERE id_cabang=?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setInt(1, Session.idCabang);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                cbPelanggan.addItem(
                        rs.getInt("id_pelanggan")
                        + " - "
                        + rs.getString("nama_pelanggan")
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }

    // ================= HITUNG =================
private void hitungTotal() {

    try {

        double harga =
                Double.parseDouble(
                        txtHarga.getText()
                );

        double berat =
                Double.parseDouble(
                        txtBerat.getText()
                );

        double total =
                harga * berat;

        String preview =
                "Laundry : "
                + ((int) berat)
                + " "
                + cbQtyType.getSelectedItem();

        if (cbSeprei.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtySeprei.getText()
                    );

            total += 10000 * qty;

            preview +=
                    "\nSeprei x" + qty;
        }

        if (cbSelimut.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtySelimut.getText()
                    );

            total += 10000 * qty;

            preview +=
                    "\nSelimut x" + qty;
        }

        if (cbBedCover.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtyBedCover.getText()
                    );

            total += 20000 * qty;

            preview +=
                    "\nBedCover x" + qty;
        }

        if (cbBoneka.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtyBoneka.getText()
                    );

            total += 10000 * qty;

            preview +=
                    "\nBoneka x" + qty;
        }

        if (cbTas.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtyTas.getText()
                    );

            total += 20000 * qty;

            preview +=
                    "\nTas x" + qty;
        }

        if (cbKarpet.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtyKarpet.getText()
                    );

            total += 30000 * qty;

            preview +=
                    "\nKarpet x" + qty;
        }

        txtPreview.setText(preview);

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        lblTotal.setText(
                rupiah.format(total)
        );

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                "Hitung gagal"
        );
    }
}
private void cetakStruk() {

    if (table.getSelectedRow() == -1) {

        JOptionPane.showMessageDialog(
                this,
                "Pilih transaksi dulu"
        );

        return;
    }

    try {

        int row =
                table.getSelectedRow();

        String invoice =
                table.getValueAt(row, 0)
                        .toString();

        Connection conn =
                Koneksi.getKoneksi();

        String sql =
                "SELECT t.*, "
                + "p.nama_pelanggan, "
                + "l.nama_layanan, "
                + "c.nama_cabang "
                + "FROM transaksi_laundry t "
                + "JOIN master_pelanggan p "
                + "ON t.id_pelanggan=p.id_pelanggan "
                + "JOIN master_layanan l "
                + "ON t.id_layanan=l.id_layanan "
                + "JOIN master_cabang c "
                + "ON t.id_cabang=c.id_cabang "
                + "WHERE t.kode_invoice=?";

        PreparedStatement pst =
                conn.prepareStatement(sql);

        pst.setString(1, invoice);

        ResultSet rs =
                pst.executeQuery();

        if (rs.next()) {

            String fileName =
                    "struk_"
                    + rs.getString("kode_invoice")
                    + ".pdf";

            // ================= UKURAN STRUK =================
            com.itextpdf.text.Rectangle pageSize =
                    new com.itextpdf.text.Rectangle(
                            226f,
                            600f
                    );

            Document document =
                    new Document(
                            pageSize,
                            10,
                            10,
                            10,
                            10
                    );

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(fileName)
            );

            document.open();

// ================= LOGO =================
try {

    java.net.URL logoUrl =
            getClass().getResource("/image/logo.png");

    Image logo =
            (logoUrl != null)
                    ? Image.getInstance(logoUrl)
                    : Image.getInstance("logo.png");

    logo.scaleToFit(70, 70);
    logo.setAlignment(Element.ALIGN_CENTER);

    document.add(logo);

} catch (Exception logoEx) {

    // Logo opsional: struk tetap dicetak walau logo tidak ditemukan.
}

// ================= TITLE =================
Paragraph title = new Paragraph(
        "BOZZ LAUNDRY\n",
        new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA,
                14,
                com.itextpdf.text.Font.BOLD
        )
);

title.setAlignment(Element.ALIGN_CENTER);

document.add(title);

// ================= ALAMAT =================
Paragraph alamat = new Paragraph(
        "Jl. Pancasila II,\n"
      + "Cicadas, Kec. Gn. Putri,\n"
      + "Kabupaten Bogor,\n"
      + "Jawa Barat\n"
      + "081292873797\n\n"
);

alamat.setAlignment(Element.ALIGN_CENTER);

document.add(alamat);

document.add(
        new Paragraph(
                "============================="
        )
);

// ================= DATA =================
document.add(
        new Paragraph(
                String.format(
                        "Invoice : %s",
                        rs.getString("kode_invoice")
                )
        )
);

document.add(
        new Paragraph(
                "Pelanggan : "
                + rs.getString("nama_pelanggan")
        )
);

document.add(
        new Paragraph(
                "Layanan : "
                + rs.getString("nama_layanan")
        )
);

document.add(
        new Paragraph(
                "Qty / Kg : "
                + ((int) rs.getDouble("berat"))
        )
);

document.add(
        new Paragraph(
                "Add Ons : "
                + rs.getString("addons")
        )
);

NumberFormat rupiah =
        NumberFormat.getCurrencyInstance(
                new Locale("id", "ID")
        );

document.add(
        new Paragraph(
                "Total : "
                + rupiah.format(
                        rs.getDouble("total_harga")
                )
        )
);

document.add(
        new Paragraph(
                "Status : "
                + rs.getString("status")
        )
);

document.add(
        new Paragraph(
                "Cabang : "
                + rs.getString("nama_cabang")
        )
);

document.add(
        new Paragraph(
                "Tanggal Masuk : "
                + rs.getString("tanggal_masuk")
        )
);

document.add(
        new Paragraph(
                "Tanggal Selesai : "
                + rs.getString("tanggal_selesai")
        )
);

// ================= FOOTER =================
document.add(
        new Paragraph(
                "\n============================="
        )
);

Paragraph thanks = new Paragraph(
        "Terima Kasih\n"
      + "Laundry Anda Kepuasan Kami\n\n"
      + "Barang yang tidak diambil\n"
      + "lebih dari 30 hari bukan\n"
      + "tanggung jawab kami."
);

thanks.setAlignment(Element.ALIGN_CENTER);

document.add(thanks);

            document.close();

            // ================= AUTO OPEN =================
            java.awt.Desktop.getDesktop().open(
                    new java.io.File(fileName)
            );
        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e
        );
    }
}

// ================= SIMPAN =================
private void simpanTransaksi() {

    try {

        String pelanggan =
                cbPelanggan.getSelectedItem()
                        .toString();

        int idPelanggan =
                Integer.parseInt(
                        pelanggan.split(" - ")[0]
                );

        double harga =
                Double.parseDouble(
                        txtHarga.getText()
                );

        double berat =
                Double.parseDouble(
                        txtBerat.getText()
                );

        double total =
                harga * berat;

        addons = "";

        // ================= ADDONS =================
        if (cbSeprei.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtySeprei.getText()
                    );

            addons +=
                    "Seprei x"
                    + qty
                    + ", ";

            total += 10000 * qty;
        }

        if (cbSelimut.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtySelimut.getText()
                    );

            addons +=
                    "Selimut x"
                    + qty
                    + ", ";

            total += 10000 * qty;
        }

        if (cbBedCover.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtyBedCover.getText()
                    );

            addons +=
                    "Bed Cover x"
                    + qty
                    + ", ";

            total += 20000 * qty;
        }

        if (cbBoneka.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtyBoneka.getText()
                    );

            addons +=
                    "Boneka x"
                    + qty
                    + ", ";

            total += 10000 * qty;
        }

        if (cbTas.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtyTas.getText()
                    );

            addons +=
                    "Tas x"
                    + qty
                    + ", ";

            total += 20000 * qty;
        }

        if (cbKarpet.isSelected()) {

            int qty =
                    Integer.parseInt(
                            qtyKarpet.getText()
                    );

            addons +=
                    "Karpet x"
                    + qty
                    + ", ";

            total += 30000 * qty;
        }

        // ================= KONEKSI =================
        Connection conn =
                Koneksi.getKoneksi();

        String sql =
                "INSERT INTO transaksi_laundry "
                + "("
                + "kode_invoice,"
                + "tanggal_masuk,"
                + "tanggal_selesai,"
                + "id_pelanggan,"
                + "id_layanan,"
                + "berat,"
                + "total_harga,"
                + "status,"
                + "id_cabang,"
                + "addons"
                + ") "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement pst =
                conn.prepareStatement(sql);

        // ================= INVOICE =================
        pst.setString(
                1,
                "INV"
                + System.currentTimeMillis()
        );

        // ================= TANGGAL MASUK =================
        pst.setDate(
                2,
                new java.sql.Date(
                        System.currentTimeMillis()
                )
        );

        // ================= TANGGAL SELESAI =================
        pst.setDate(
                3,
                new java.sql.Date(
                        System.currentTimeMillis()
                )
        );

        // ================= PELANGGAN =================
        pst.setInt(4, idPelanggan);

        // ================= LAYANAN =================
        pst.setInt(5, selectedLayanan);

        // ================= BERAT / QTY =================
        pst.setDouble(6, berat);

        // ================= TOTAL =================
        pst.setDouble(7, total);

        // ================= STATUS =================
        pst.setString(8, "proses");

        // ================= CABANG =================
        pst.setInt(9, Session.idCabang);

        // ================= ADDONS =================
        pst.setString(10, addons);

        pst.executeUpdate();

        JOptionPane.showMessageDialog(
                this,
                "Transaksi berhasil disimpan"
        );

        // ================= REFRESH =================
        tampilData();

        // ================= RESET FORM =================
        txtBerat.setText("");

        txtHarga.setText("");

        txtPreview.setText("");

        lblTotal.setText("Rp 0");

        cbSeprei.setSelected(false);

        cbSelimut.setSelected(false);

        cbBedCover.setSelected(false);

        cbBoneka.setSelected(false);

        cbTas.setSelected(false);

        cbKarpet.setSelected(false);

        qtySeprei.setText("1");

        qtySelimut.setText("1");

        qtyBedCover.setText("1");

        qtyBoneka.setText("1");

        qtyTas.setText("1");

        qtyKarpet.setText("1");

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e
        );
    }
}

    // ================= TABLE =================
    private void tampilData() {

        model.setRowCount(0);

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT t.*, "
                    + "p.nama_pelanggan, "
                    + "l.nama_layanan "
                    + "FROM transaksi_laundry t "
                    + "JOIN master_pelanggan p "
                    + "ON t.id_pelanggan=p.id_pelanggan "
                    + "JOIN master_layanan l "
                    + "ON t.id_layanan=l.id_layanan "
                    + "WHERE t.id_cabang=?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setInt(1, Session.idCabang);

            ResultSet rs =
                    pst.executeQuery();
NumberFormat rupiah =
        NumberFormat.getCurrencyInstance(
                new Locale("id", "ID")
        );
            while (rs.next()) {

                model.addRow(new Object[]{

                    rs.getString("kode_invoice"),

                    rs.getString("nama_pelanggan"),

                    rs.getString("nama_layanan"),

                    ((int) rs.getDouble("berat"))
                    + " Kg",

                    rs.getString("addons"),

                    rupiah.format(
                            rs.getDouble("total_harga")
                    ),

                    rs.getString("status")

                });

            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }
}