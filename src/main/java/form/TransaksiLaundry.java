package form;

import koneksi.Koneksi;
import koneksi.Session;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Element;

public class TransaksiLaundry extends JPanel {

    JLabel title =
            new JLabel("TRANSAKSI LAUNDRY");

    JLabel lblSearch =
            new JLabel("Search");

    JTextField txtSearch =
            new JTextField();

    JButton btnTambah =
            new JButton("+ Transaksi");

    JButton btnHapus =
            new JButton("Hapus");
    JButton btnCetak =
        new JButton("Cetak Struk");

    JTable table =
            new JTable();

    JScrollPane scroll =
            new JScrollPane(table);

    DefaultTableModel model;

    int selectedId = 0;

    public TransaksiLaundry() {

        setLayout(null);

        setBackground(new Color(240, 248, 255));

        // ================= TITLE =================
        title.setBounds(30, 20, 400, 40);

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        // ================= BUTTON =================
        btnTambah.setBounds(30, 80, 170, 35);

        btnTambah.setBackground(
                new Color(37,99,235)
        );

        btnTambah.setForeground(Color.BLACK);

        btnTambah.setFocusPainted(false);

        btnHapus.setBounds(220, 80, 100, 35);
        btnCetak.setBounds(340, 80, 140, 35);

btnCetak.setBackground(
        new Color(22,163,74)
);

btnCetak.setForeground(Color.BLACK);

btnCetak.setFocusPainted(false);

        btnHapus.setBackground(
                new Color(220,38,38)
        );

        btnHapus.setForeground(Color.BLACK);

        btnHapus.setFocusPainted(false);

        // ================= SEARCH =================
        lblSearch.setBounds(500, 80, 100, 35);

        txtSearch.setBounds(550, 80, 180, 35);

        // ================= TABLE =================
        scroll.setBounds(30, 140, 1120, 500);

        table.setRowHeight(28);

        table.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        // ================= MODEL =================
        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Invoice");
        model.addColumn("Pelanggan");
        model.addColumn("Layanan");
        model.addColumn("Qty");
        model.addColumn("Add Ons");
        model.addColumn("Total");
        model.addColumn("Status");
        model.addColumn("Cabang");
        model.addColumn("Masuk");
        model.addColumn("Selesai");

        table.setModel(model);

        // ================= ADD =================
        add(title);

        add(btnTambah);

        add(btnHapus);
        add(btnCetak);

        add(lblSearch);

        add(txtSearch);

        add(scroll);

        tampilData();

        // ================= ACTION =================
        btnTambah.addActionListener(e -> {

            formTransaksi();

        });

        btnHapus.addActionListener(e -> {

            hapusData();

        });

        btnCetak.addActionListener(e -> {

            cetakStruk();

        });
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

                int row =
                        table.getSelectedRow();

                selectedId =
                        Integer.parseInt(
                                table.getValueAt(row, 0)
                                        .toString()
                        );
            }

        });
    }

    // ================= FORM TRANSAKSI =================
    private void formTransaksi() {

        JDialog dialog = new JDialog();

        dialog.setTitle("Tambah Transaksi");

        dialog.setSize(580, 900);

        dialog.setLayout(null);

        dialog.setLocationRelativeTo(null);

        dialog.setModal(true);

        dialog.getContentPane().setBackground(Color.WHITE);

        // ================= LABEL =================
        JLabel lblPelanggan =
                new JLabel("Pelanggan");

        JLabel lblKategori =
                new JLabel("Layanan Kiloan");

        JLabel lblLayanan =
                new JLabel("Layanan");

        JLabel lblAddons =
                new JLabel("Add Ons");

        JLabel lblQty =
                new JLabel("Qty / Kg");

        JLabel lblHargaCustom =
                new JLabel("Harga");

        JLabel lblCabang =
                new JLabel("Cabang");

        JLabel lblStatus =
                new JLabel("Status");

        // ================= RADIO =================
        JRadioButton rbKiloan =
                new JRadioButton("Kiloan");

        ButtonGroup bgKategori =
                new ButtonGroup();

        bgKategori.add(rbKiloan);

        // ================= CHECKBOX =================
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

        // ================= QTY ADDONS =================
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

        // ================= COMPONENT =================
        JComboBox<String> cbPelanggan =
                new JComboBox<>();

        JComboBox<String> cbLayanan =
                new JComboBox<>();

        JComboBox<String> cbCabang =
                new JComboBox<>();

        JTextField txtQty =
                new JTextField();

        JTextField txtHargaCustom =
                new JTextField();

        String[] statusList = {
            "proses",
            "dicuci",
            "disetrika",
            "selesai",
            "diambil"
        };

        JComboBox<String> cbStatus =
                new JComboBox<>(statusList);

        JLabel lblTotal =
                new JLabel("Total : Rp 0");

        JButton btnHitung =
                new JButton("Hitung");

        JButton btnSimpan =
                new JButton("Simpan");

        // ================= STYLE =================
        btnHitung.setBackground(
                new Color(22,163,74)
        );

        btnHitung.setForeground(Color.BLACK);

        btnHitung.setFocusPainted(false);

        btnSimpan.setBackground(
                new Color(37,99,235)
        );

        btnSimpan.setForeground(Color.BLACK);

        btnSimpan.setFocusPainted(false);

        lblTotal.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        lblTotal.setForeground(
                new Color(22,163,74)
        );

        // ================= POSITION =================
        lblPelanggan.setBounds(40, 40, 120, 30);

        cbPelanggan.setBounds(170, 40, 280, 35);

        lblKategori.setBounds(40, 100, 150, 30);

        rbKiloan.setBounds(170, 100, 120, 30);

        lblLayanan.setBounds(40, 150, 120, 30);

        cbLayanan.setBounds(170, 150, 280, 35);

        lblAddons.setBounds(40, 210, 120, 30);

        cbSeprei.setBounds(170,210,100,30);
        qtySeprei.setBounds(280,210,50,30);

        cbSelimut.setBounds(340,210,100,30);
        qtySelimut.setBounds(450,210,50,30);

        cbBedCover.setBounds(170,250,120,30);
        qtyBedCover.setBounds(300,250,50,30);

        cbBoneka.setBounds(360,250,100,30);
        qtyBoneka.setBounds(470,250,50,30);

        cbTas.setBounds(170,290,100,30);
        qtyTas.setBounds(280,290,50,30);

        cbKarpet.setBounds(340,290,100,30);
        qtyKarpet.setBounds(450,290,50,30);

        lblQty.setBounds(40, 360, 120, 30);

        txtQty.setBounds(170, 360, 280, 35);

        lblHargaCustom.setBounds(40, 420, 120, 30);

        txtHargaCustom.setBounds(170, 420, 280, 35);

        btnHitung.setBounds(170, 490, 120, 40);

        lblTotal.setBounds(170, 550, 300, 40);

        lblCabang.setBounds(40, 620, 120, 30);

        cbCabang.setBounds(170, 620, 280, 35);

        lblStatus.setBounds(40, 680, 120, 30);

        cbStatus.setBounds(170, 680, 280, 35);

        btnSimpan.setBounds(170, 760, 160, 45);

        // ================= LOAD PELANGGAN =================
        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql;

            PreparedStatement pst;

            if (Session.role.equals("admin")
                    || Session.role.equals("owner")) {

                sql =
                        "SELECT * FROM master_pelanggan";

                pst =
                        conn.prepareStatement(sql);

            } else {

                sql =
                        "SELECT * FROM master_pelanggan "
                        + "WHERE id_cabang=?";

                pst =
                        conn.prepareStatement(sql);

                pst.setInt(1,
                        Session.idCabang);
            }

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                cbPelanggan.addItem(
                        rs.getInt("id_pelanggan")
                        + " - "
                        + rs.getString(
                                "nama_pelanggan"
                        )
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(dialog, e);

        }

        // ================= LOAD CABANG =================
        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT * FROM master_cabang";

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

            for (int i = 0;
                    i < cbCabang.getItemCount();
                    i++) {

                String item =
                        cbCabang.getItemAt(i);

                if (item.startsWith(
                        Session.idCabang + " -"
                )) {

                    cbCabang.setSelectedIndex(i);

                    break;
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(dialog, e);

        }

        // ================= LOAD LAYANAN =================
        rbKiloan.setSelected(true);

        loadLayanan(cbLayanan, "kiloan");

        // ================= AUTO LOAD HARGA =================
        cbLayanan.addActionListener(e -> {

            try {

                if (cbLayanan.getSelectedItem() == null) {

                    return;
                }

                String layanan =
                        cbLayanan.getSelectedItem()
                                .toString();

                int idLayanan =
                        Integer.parseInt(
                                layanan.split(" - ")[0]
                        );

                Connection conn =
                        Koneksi.getKoneksi();

                String sql =
                        "SELECT * FROM master_layanan "
                        + "WHERE id_layanan=?";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setInt(1, idLayanan);

                ResultSet rs =
                        pst.executeQuery();

                if (rs.next()) {

                    double min =
                            rs.getDouble("harga_min");

                    double max =
                            rs.getDouble("harga_max");

                    txtHargaCustom.setText(
                            String.valueOf(min)
                    );

                    if (min != max) {

                        txtHargaCustom.setEnabled(true);

                    } else {

                        txtHargaCustom.setEnabled(false);
                    }
                }

            } catch (Exception ex) {

            }

        });

        // ================= HITUNG =================
        btnHitung.addActionListener(e -> {

            try {

                double harga =
                        Double.parseDouble(
                                txtHargaCustom.getText()
                        );

                double qty =
                        Double.parseDouble(
                                txtQty.getText()
                        );

                double total =
                        harga * qty;

                double addonsTotal = 0;

                if (cbSeprei.isSelected()) {
                    addonsTotal +=
                            10000
                            * Double.parseDouble(
                                    qtySeprei.getText()
                            );
                }

                if (cbSelimut.isSelected()) {
                    addonsTotal +=
                            10000
                            * Double.parseDouble(
                                    qtySelimut.getText()
                            );
                }

                if (cbBedCover.isSelected()) {
                    addonsTotal +=
                            20000
                            * Double.parseDouble(
                                    qtyBedCover.getText()
                            );
                }

                if (cbBoneka.isSelected()) {
                    addonsTotal +=
                            10000
                            * Double.parseDouble(
                                    qtyBoneka.getText()
                            );
                }

                if (cbTas.isSelected()) {
                    addonsTotal +=
                            20000
                            * Double.parseDouble(
                                    qtyTas.getText()
                            );
                }

                if (cbKarpet.isSelected()) {
                    addonsTotal +=
                            30000
                            * Double.parseDouble(
                                    qtyKarpet.getText()
                            );
                }

                total += addonsTotal;

                NumberFormat rupiah =
                        NumberFormat.getCurrencyInstance(
                                new Locale("id", "ID")
                        );

                lblTotal.setText(
                        "Total : "
                        + rupiah.format(total)
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Perhitungan gagal"
                );

            }

        });

        // ================= SIMPAN =================
        btnSimpan.addActionListener(e -> {

            try {

                String pelanggan =
                        cbPelanggan.getSelectedItem()
                                .toString();

                int idPelanggan =
                        Integer.parseInt(
                                pelanggan.split(" - ")[0]
                        );

                String layanan =
                        cbLayanan.getSelectedItem()
                                .toString();

                int idLayanan =
                        Integer.parseInt(
                                layanan.split(" - ")[0]
                        );

                String cabang =
                        cbCabang.getSelectedItem()
                                .toString();

                int idCabang =
                        Integer.parseInt(
                                cabang.split(" - ")[0]
                        );

                double harga =
                        Double.parseDouble(
                                txtHargaCustom.getText()
                        );

                double qty =
                        Double.parseDouble(
                                txtQty.getText()
                        );

                double total =
                        harga * qty;

                String addons = "";

                if (cbSeprei.isSelected()) {

                    addons +=
                            "Seprei x"
                            + qtySeprei.getText()
                            + ", ";

                    total +=
                            10000
                            * Double.parseDouble(
                                    qtySeprei.getText()
                            );
                }

                if (cbSelimut.isSelected()) {

                    addons +=
                            "Selimut x"
                            + qtySelimut.getText()
                            + ", ";

                    total +=
                            10000
                            * Double.parseDouble(
                                    qtySelimut.getText()
                            );
                }

                if (cbBedCover.isSelected()) {

                    addons +=
                            "Bed Cover x"
                            + qtyBedCover.getText()
                            + ", ";

                    total +=
                            20000
                            * Double.parseDouble(
                                    qtyBedCover.getText()
                            );
                }

                if (cbBoneka.isSelected()) {

                    addons +=
                            "Boneka x"
                            + qtyBoneka.getText()
                            + ", ";

                    total +=
                            10000
                            * Double.parseDouble(
                                    qtyBoneka.getText()
                            );
                }

                if (cbTas.isSelected()) {

                    addons +=
                            "Tas x"
                            + qtyTas.getText()
                            + ", ";

                    total +=
                            20000
                            * Double.parseDouble(
                                    qtyTas.getText()
                            );
                }

                if (cbKarpet.isSelected()) {

                    addons +=
                            "Karpet x"
                            + qtyKarpet.getText()
                            + ", ";

                    total +=
                            30000
                            * Double.parseDouble(
                                    qtyKarpet.getText()
                            );
                }

                String invoice =
                        "INV"
                        + System.currentTimeMillis();

                Connection conn =
                        Koneksi.getKoneksi();

                String sql =
                        "INSERT INTO transaksi_laundry "
                        + "(kode_invoice,"
                        + "tanggal_masuk,"
                        + "tanggal_selesai,"
                        + "id_pelanggan,"
                        + "id_layanan,"
                        + "berat,"
                        + "total_harga,"
                        + "status,"
                        + "id_cabang,"
                        + "addons)"
                        + " VALUES(?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                pst.setString(1, invoice);

                pst.setDate(2,
                        new java.sql.Date(
                                System.currentTimeMillis()
                        )
                );

                pst.setDate(3,
                        new java.sql.Date(
                                System.currentTimeMillis()
                                + (3L * 24 * 60 * 60 * 1000)
                        )
                );

                pst.setInt(4, idPelanggan);

                pst.setInt(5, idLayanan);

                pst.setDouble(6, qty);

                pst.setDouble(7, total);

                pst.setString(
                        8,
                        cbStatus.getSelectedItem()
                                .toString()
                );

                pst.setInt(9, idCabang);

                pst.setString(10, addons);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Transaksi berhasil disimpan"
                );

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
        dialog.add(lblPelanggan);
        dialog.add(cbPelanggan);

        dialog.add(lblKategori);
        dialog.add(rbKiloan);

        dialog.add(lblLayanan);
        dialog.add(cbLayanan);

        dialog.add(lblAddons);

        dialog.add(cbSeprei);
        dialog.add(qtySeprei);

        dialog.add(cbSelimut);
        dialog.add(qtySelimut);

        dialog.add(cbBedCover);
        dialog.add(qtyBedCover);

        dialog.add(cbBoneka);
        dialog.add(qtyBoneka);

        dialog.add(cbTas);
        dialog.add(qtyTas);

        dialog.add(cbKarpet);
        dialog.add(qtyKarpet);

        dialog.add(lblQty);
        dialog.add(txtQty);

        dialog.add(lblHargaCustom);
        dialog.add(txtHargaCustom);

        dialog.add(btnHitung);

        dialog.add(lblTotal);

        dialog.add(lblCabang);
        dialog.add(cbCabang);

        dialog.add(lblStatus);
        dialog.add(cbStatus);

        dialog.add(btnSimpan);

        dialog.setVisible(true);
    }

    // ================= LOAD LAYANAN =================
    private void loadLayanan(
            JComboBox<String> cbLayanan,
            String kategori) {

        try {

            cbLayanan.removeAllItems();

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT * FROM master_layanan "
                    + "WHERE kategori=?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setString(1, kategori);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                cbLayanan.addItem(
                        rs.getInt("id_layanan")
                        + " - "
                        + rs.getString("nama_layanan")
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }
    // ================= CETAK STRUK =================
    private void cetakStruk() {

    if (selectedId == 0) {

        JOptionPane.showMessageDialog(
                this,
                "Pilih transaksi terlebih dahulu"
        );

        return;
    }

    try {

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
                + "WHERE t.id_transaksi=?";

        PreparedStatement pst =
                conn.prepareStatement(sql);

        pst.setInt(1, selectedId);

        ResultSet rs =
                pst.executeQuery();

        if (rs.next()) {

            String fileName =
                    "struk_"
                    + rs.getString("kode_invoice")
                    + ".pdf";

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

    // ================= TAMPIL DATA =================
    private void tampilData() {

        model.setRowCount(0);

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT t.*, "
                    + "p.nama_pelanggan, "
                    + "l.nama_layanan, "
                    + "c.nama_cabang "
                    + "FROM transaksi_laundry t "
                    + "JOIN master_pelanggan p "
                    + "ON t.id_pelanggan = p.id_pelanggan "
                    + "JOIN master_layanan l "
                    + "ON t.id_layanan = l.id_layanan "
                    + "JOIN master_cabang c "
                    + "ON t.id_cabang = c.id_cabang";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{

                    rs.getInt("id_transaksi"),

                    rs.getString("kode_invoice"),

                    rs.getString("nama_pelanggan"),

                    rs.getString("nama_layanan"),

                    rs.getString("berat"),

                    rs.getString("addons"),

                    rs.getString("total_harga"),

                    rs.getString("status"),

                    rs.getString("nama_cabang"),

                    rs.getString("tanggal_masuk"),

                    rs.getString("tanggal_selesai")

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

            String keyword =
                    "%" + txtSearch.getText() + "%";

            String sql =
                    "SELECT t.*, "
                    + "p.nama_pelanggan, "
                    + "l.nama_layanan, "
                    + "c.nama_cabang "
                    + "FROM transaksi_laundry t "
                    + "JOIN master_pelanggan p "
                    + "ON t.id_pelanggan = p.id_pelanggan "
                    + "JOIN master_layanan l "
                    + "ON t.id_layanan = l.id_layanan "
                    + "JOIN master_cabang c "
                    + "ON t.id_cabang = c.id_cabang "
                    + "WHERE "
                    + "t.kode_invoice ILIKE ? "
                    + "OR p.nama_pelanggan ILIKE ? "
                    + "OR l.nama_layanan ILIKE ?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setString(1, keyword);

            pst.setString(2, keyword);

            pst.setString(3, keyword);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{

                    rs.getInt("id_transaksi"),

                    rs.getString("kode_invoice"),

                    rs.getString("nama_pelanggan"),

                    rs.getString("nama_layanan"),

                    rs.getString("berat"),

                    rs.getString("addons"),

                    rs.getString("total_harga"),

                    rs.getString("status"),

                    rs.getString("nama_cabang"),

                    rs.getString("tanggal_masuk"),

                    rs.getString("tanggal_selesai")

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
                        "Yakin ingin menghapus transaksi?",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm == JOptionPane.YES_OPTION) {

            try {

                Connection conn =
                        Koneksi.getKoneksi();

                String sql =
                        "DELETE FROM transaksi_laundry "
                        + "WHERE id_transaksi=?";

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