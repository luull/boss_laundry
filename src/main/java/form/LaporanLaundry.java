package form;

import koneksi.Koneksi;
import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.util.HashMap;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class LaporanLaundry extends JPanel {

    JPanel topPanel = new JPanel();

    JLabel title = new JLabel("LAPORAN LAUNDRY");

    JLabel lblCabang = new JLabel("Cabang");

    JComboBox<String> cbCabang =
            new JComboBox<>();

    JButton btnFilter =
            new JButton("FILTER");

    JButton btnPdf =
            new JButton("EXPORT PDF");

    JTable table = new JTable();

    JScrollPane scroll =
            new JScrollPane(table);

    JLabel lblTotal =
            new JLabel("Total Pendapatan : Rp 0");

    DefaultTableModel model;

    public LaporanLaundry() {

        setLayout(null);

 setBackground(new Color(240, 248, 255));

        // ================= TOP PANEL =================
        topPanel.setBounds(20, 20, 1140, 120);

        topPanel.setLayout(null);

        topPanel.setBackground(Color.WHITE);

        topPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(226,232,240),
                        1
                )
        );

        // ================= TITLE =================
        title.setBounds(25, 15, 400, 40);

        title.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        title.setForeground(
                new Color(15,23,42)
        );

        // ================= FILTER =================
        lblCabang.setBounds(25, 70, 80, 30);

        lblCabang.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        cbCabang.setBounds(95, 68, 220, 35);

        cbCabang.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        // ================= BUTTON FILTER =================
        btnFilter.setBounds(335, 68, 120, 35);

        btnFilter.setBackground(
                new Color(37,99,235)
        );

        btnFilter.setForeground(Color.BLACK);

        btnFilter.setFocusPainted(false);

        btnFilter.setBorderPainted(false);

        btnFilter.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        // ================= BUTTON PDF =================
        btnPdf.setBounds(470, 68, 140, 35);

        btnPdf.setBackground(
                new Color(220,38,38)
        );

        btnPdf.setForeground(Color.BLACK);

        btnPdf.setFocusPainted(false);

        btnPdf.setBorderPainted(false);

        btnPdf.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        // ================= TOTAL =================
        lblTotal.setBounds(760, 30, 330, 50);

        lblTotal.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        lblTotal.setForeground(
                new Color(22,163,74)
        );

        // ================= TABLE =================
        scroll.setBounds(20, 160, 1140, 520);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        new Color(226,232,240)
                )
        );

        table.setRowHeight(26);

        table.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        table.getTableHeader().setForeground(Color.BLACK);

        // ================= MODEL =================
        model = new DefaultTableModel();

        model.addColumn("Invoice");
        model.addColumn("Pelanggan");
        model.addColumn("Layanan");
        model.addColumn("Berat");
        model.addColumn("Total");
        model.addColumn("Status");
        model.addColumn("Tanggal");

        table.setModel(model);

        // ================= ADD =================
        topPanel.add(title);

        topPanel.add(lblCabang);

        topPanel.add(cbCabang);

        topPanel.add(btnFilter);

        topPanel.add(btnPdf);

        topPanel.add(lblTotal);

        add(topPanel);

        add(scroll);

        // ================= LOAD =================
        loadCabang();

        tampilData();

        // ================= ACTION =================
        btnFilter.addActionListener(e -> {

            filterData();

        });

        btnPdf.addActionListener(e -> {

            exportPDF();

        });
    }

    // ================= EXPORT PDF =================
private void exportPDF() {

    try {

        Connection conn =
                Koneksi.getKoneksi();

        // Muat report sebagai stream dari classpath agar tetap bekerja
        // ketika aplikasi dijalankan dari dalam shaded JAR
        // (getResource(...).getPath() tidak valid di dalam JAR).
        InputStream reportStream =
                getClass().getResourceAsStream(
                        "/report/Transactions_report.jasper"
                );

        if (reportStream == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "File report Transactions_report.jasper tidak ditemukan "
                    + "pada classpath."
            );

            return;
        }

        HashMap<String, Object> parameter =
                new HashMap<>();

        // ================= FILTER CABANG =================
        if (cbCabang.getSelectedIndex() == 0) {

            parameter.put(
                    "id_cabang",
                    null
            );

        } else {

            String cabang =
                    cbCabang.getSelectedItem()
                            .toString();

            int idCabang =
                    Integer.parseInt(
                            cabang.split(" - ")[0]
                    );

            parameter.put(
                    "id_cabang",
                    idCabang
            );
        }

        JasperPrint jp =
                JasperFillManager.fillReport(
                        reportStream,
                        parameter,
                        conn
                );

        JasperViewer.viewReport(
                jp,
                false
        );

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e
        );

    }
}
    // ================= LOAD CABANG =================
    private void loadCabang() {

        cbCabang.addItem("Semua Cabang");

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

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }

    // ================= TAMPIL DATA =================
    private void tampilData() {

        model.setRowCount(0);

        double totalPendapatan = 0;

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT t.*, "
                    + "p.nama_pelanggan, "
                    + "l.nama_layanan "
                    + "FROM transaksi_laundry t "
                    + "JOIN master_pelanggan p "
                    + "ON t.id_pelanggan = p.id_pelanggan "
                    + "JOIN master_layanan l "
                    + "ON t.id_layanan = l.id_layanan";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                double total =
                        rs.getDouble("total_harga");

                totalPendapatan += total;

                model.addRow(new Object[]{

                    rs.getString("kode_invoice"),

                    rs.getString("nama_pelanggan"),

                    rs.getString("nama_layanan"),

                    ((int) rs.getDouble("berat"))
                    + " Kg",

                    formatRupiah(total),

                    rs.getString("status"),

                    rs.getString("tanggal_masuk")

                });
            }

            lblTotal.setText(
                    "Total Pendapatan : "
                    + formatRupiah(totalPendapatan)
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }

    // ================= FILTER DATA =================
    private void filterData() {

        model.setRowCount(0);

        double totalPendapatan = 0;

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql;

            PreparedStatement pst;

            if (cbCabang.getSelectedIndex() == 0) {

                sql =
                        "SELECT t.*, "
                        + "p.nama_pelanggan, "
                        + "l.nama_layanan "
                        + "FROM transaksi_laundry t "
                        + "JOIN master_pelanggan p "
                        + "ON t.id_pelanggan = p.id_pelanggan "
                        + "JOIN master_layanan l "
                        + "ON t.id_layanan = l.id_layanan";

                pst =
                        conn.prepareStatement(sql);

            } else {

                String cabang =
                        cbCabang.getSelectedItem()
                                .toString();

                int idCabang =
                        Integer.parseInt(
                                cabang.split(" - ")[0]
                        );

                sql =
                        "SELECT t.*, "
                        + "p.nama_pelanggan, "
                        + "l.nama_layanan "
                        + "FROM transaksi_laundry t "
                        + "JOIN master_pelanggan p "
                        + "ON t.id_pelanggan = p.id_pelanggan "
                        + "JOIN master_layanan l "
                        + "ON t.id_layanan = l.id_layanan "
                        + "WHERE t.id_cabang=?";

                pst =
                        conn.prepareStatement(sql);

                pst.setInt(1, idCabang);
            }

            ResultSet rs =
                    pst.executeQuery();

            while (rs.next()) {

                double total =
                        rs.getDouble("total_harga");

                totalPendapatan += total;

                model.addRow(new Object[]{

                    rs.getString("kode_invoice"),

                    rs.getString("nama_pelanggan"),

                    rs.getString("nama_layanan"),

                    ((int) rs.getDouble("berat"))
                    + " Kg",

                    formatRupiah(total),

                    rs.getString("status"),

                    rs.getString("tanggal_masuk")

                });
            }

            lblTotal.setText(
                    "Total Pendapatan : "
                    + formatRupiah(totalPendapatan)
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e);

        }
    }

    // ================= FORMAT RUPIAH =================
    private String formatRupiah(double angka) {

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        return rupiah.format(angka);
    }
}