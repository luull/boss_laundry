package form;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import koneksi.Session;

public class MenuDashboard extends JFrame {

    JPanel sidebar = new JPanel();
    JPanel header = new JPanel();
    JPanel content = new JPanel();

    JLabel title = new JLabel();
    JLabel welcome = new JLabel();

    JButton btnDashboard = new JButton("Dashboard");
    JButton btnCabang = new JButton("Master Cabang");
    JButton btnPelanggan = new JButton("Master Pelanggan");
    JButton btnLayanan = new JButton("Master Layanan");
    JButton btnUser = new JButton("Master User");
    JButton btnTransaksi = new JButton("Transaksi Laundry");
    JButton btnLaporan = new JButton("Laporan");
    JButton btnLogout = new JButton("Logout");

    public MenuDashboard() {

        setTitle("Dashboard Boss Laundry");
        setSize(1000, 600);
        setBackground(new Color(240, 248, 255));
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================= SIDEBAR =================
        sidebar.setBounds(0, 0, 220, 600);
        sidebar.setLayout(null);
        sidebar.setBackground(new Color(30, 41, 59));

        JLabel logo = new JLabel("BOSS LAUNDRY");
        logo.setBounds(25, 20, 200, 40);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 22));

        btnDashboard.setBounds(20, 90, 180, 35);
        btnCabang.setBounds(20, 140, 180, 35);
        btnPelanggan.setBounds(20, 190, 180, 35);
        btnLayanan.setBounds(20, 240, 180, 35);
        btnUser.setBounds(20, 290, 180, 35);
        btnTransaksi.setBounds(20, 340, 180, 35);
        btnLaporan.setBounds(20, 390, 180, 35);
        btnLogout.setBounds(20, 470, 180, 35);

        sidebar.add(logo);
        sidebar.add(btnDashboard);
        sidebar.add(btnCabang);
        sidebar.add(btnPelanggan);
        sidebar.add(btnLayanan);
        sidebar.add(btnUser);
        sidebar.add(btnTransaksi);
        sidebar.add(btnLaporan);
        sidebar.add(btnLogout);

        // ================= HEADER =================
        header.setBounds(220, 0, 780, 70);
        header.setLayout(null);
        header.setBackground(new Color(59, 130, 246));
        title.setText(
                "BOSS LAUNDRY - "
                + getNamaCabang().toUpperCase()
        );
        welcome.setText(
                "Selamat Datang "
                + Session.username
                + " ("
                + Session.role.toUpperCase()
                + ")"
        );
        title.setBounds(20, 15, 400, 40);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        header.add(title);

        // ================= CONTENT =================
        content.setBounds(220, 70, 780, 530);
        content.setLayout(null);
        content.setBackground(new Color(240, 248, 255));

        // ================= DASHBOARD DEFAULT =================
        showDashboard();

        // ================= ADD =================
        add(sidebar);
        add(header);
        add(content);

        // ================= ROLE =================

        // KASIR
        if (Session.role.equals("kasir")) {

            btnUser.setVisible(false);
            btnCabang.setVisible(false);
            btnLaporan.setVisible(false);

        }

        // OWNER
        if (Session.role.equals("owner")) {

            btnCabang.setVisible(false);
            btnUser.setVisible(false);

        }
        // ================= ACTION =================

        btnDashboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showDashboard();

            }
        });

        btnCabang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showPanel(new MasterCabang());

            }
        });
        btnPelanggan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showPanel(new MasterPelanggan());

            }
        });
        btnLayanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showPanel(new MasterLayanan());

            }
        });
        btnUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showPanel(new MasterUser());

            }
        });
        btnTransaksi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showPanel(new TransaksiLaundry());

            }
        });
        btnLaporan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showPanel(new LaporanLaundry());

            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
                new LoginForm();

            }
        });

        setVisible(true);
    }

    // ================= METHOD SHOW PANEL =================
    private void showPanel(JPanel panel) {

        content.removeAll();
        content.repaint();
        content.revalidate();

        panel.setBounds(0, 0, 780, 530);

        content.add(panel);
        content.repaint();
        content.revalidate();
    }

    // ================= METHOD DASHBOARD =================
    private void showDashboard() {

        content.removeAll();
        content.repaint();
        content.revalidate();

        welcome.setBounds(120, 50, 600, 40);
        welcome.setFont(new Font("Arial", Font.BOLD, 22));

        content.add(welcome);

        content.repaint();
        content.revalidate();
    }
    private String getNamaCabang() {

    String namaCabang = "";

    try {

        java.sql.Connection conn =
                koneksi.Koneksi.getKoneksi();

        String sql =
                "SELECT * FROM master_cabang "
                + "WHERE id_cabang=?";

        java.sql.PreparedStatement pst =
                conn.prepareStatement(sql);

        pst.setInt(1, Session.idCabang);

        java.sql.ResultSet rs =
                pst.executeQuery();

        if (rs.next()) {

            namaCabang =
                    rs.getString("nama_cabang");

        }

    } catch (Exception e) {

        System.out.println(e);

    }

    return namaCabang;
}
    
}