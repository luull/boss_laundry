package form;

import koneksi.Koneksi;
import koneksi.KoneksiException;
import koneksi.Session;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginForm extends JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(LoginForm.class.getName());

    JPanel mainPanel = new JPanel();

    JPanel cardPanel = new JPanel();

    JLabel lblLogo = new JLabel();

    JLabel lblSubtitle =
            new JLabel("Login Sistem Laundry");

    JLabel lblUsername =
            new JLabel("Username");

    JLabel lblPassword =
            new JLabel("Password");

    JTextField txtUsername =
            new JTextField();

    JPasswordField txtPassword =
            new JPasswordField();

    JButton btnLogin =
            new JButton("LOGIN");

    public LoginForm() {

        setTitle("Login");

        setSize(1000, 600);

        setLayout(null);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ================= MAIN PANEL =================
        mainPanel.setBounds(0, 0, 1000, 600);

        mainPanel.setLayout(null);

        mainPanel.setBackground(new Color(240, 248, 255));

        add(mainPanel);

        // ================= CARD PANEL =================
        cardPanel.setBounds(325, 70, 350, 430);

        cardPanel.setLayout(null);

        cardPanel.setBackground(Color.WHITE);

        cardPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 220, 220),
                        1
                )
        );

        mainPanel.add(cardPanel);

        // ================= LOGO =================
        ImageIcon logoIcon =
                new ImageIcon(
                        getClass().getResource(
                                "/image/logo.png"
                        )
                );

        Image image =
                logoIcon.getImage().getScaledInstance(
                        100,
                        100,
                        Image.SCALE_SMOOTH
                );

        lblLogo.setIcon(
                new ImageIcon(image)
        );

        lblLogo.setBounds(125, 20, 100, 100);

        cardPanel.add(lblLogo);

        // ================= SUBTITLE =================
        lblSubtitle.setBounds(40, 160, 270, 25);

        lblSubtitle.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        lblSubtitle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblSubtitle.setForeground(
                Color.GRAY
        );

        cardPanel.add(lblSubtitle);

        // ================= USERNAME =================
        lblUsername.setBounds(40, 210, 100, 25);

        lblUsername.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        cardPanel.add(lblUsername);

        txtUsername.setBounds(40, 235, 270, 40);

        txtUsername.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        15
                )
        );

        txtUsername.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(200, 200, 200)
                        ),
                        BorderFactory.createEmptyBorder(
                                5,
                                10,
                                5,
                                10
                        )
                )
        );

        cardPanel.add(txtUsername);

        // ================= PASSWORD =================
        lblPassword.setBounds(40, 290, 100, 25);

        lblPassword.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        cardPanel.add(lblPassword);

        txtPassword.setBounds(40, 315, 270, 40);

        txtPassword.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        15
                )
        );

        txtPassword.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(200, 200, 200)
                        ),
                        BorderFactory.createEmptyBorder(
                                5,
                                10,
                                5,
                                10
                        )
                )
        );

        cardPanel.add(txtPassword);

        // ================= BUTTON LOGIN =================
        btnLogin.setBounds(40, 370, 270, 45);

        btnLogin.setBackground(
                new Color(37, 99, 235)
        );

        btnLogin.setForeground(Color.BLACK);

        btnLogin.setFocusPainted(false);

        btnLogin.setBorderPainted(false);

        btnLogin.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        cardPanel.add(btnLogin);

        // ================= ACTION LOGIN =================
        btnLogin.addActionListener(e -> {

            login();

        });

        // ================= ENTER SHORTCUT =================
        getRootPane().setDefaultButton(btnLogin);

        setVisible(true);
    }

    // ================= METHOD LOGIN =================
    private void login() {

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Username dan password wajib diisi.",
                    "Login",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // 1) Verifikasi koneksi database SEBELUM query login dijalankan.
        //    Bila gagal, tampilkan pesan koneksi -- JANGAN menyatakan
        //    "username/password salah" karena penyebabnya bukan kredensial.
        Connection conn;

        try {

            conn = Koneksi.getKoneksi();

        } catch (KoneksiException ex) {

            LOGGER.log(Level.SEVERE,
                    "Login dibatalkan: koneksi database tidak tersedia.", ex);

            JOptionPane.showMessageDialog(
                    this,
                    "Tidak dapat terhubung ke database.\n"
                    + "Login belum dapat diproses. Periksa koneksi internet "
                    + "atau konfigurasi Supabase, lalu coba lagi.",
                    "Koneksi Database Gagal",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // 2) Jalankan query login dengan PreparedStatement (try-with-resources
        //    untuk statement & result set; koneksi tetap dipakai ulang).
        String sql =
                "SELECT id_user, username, role, id_cabang "
                + "FROM master_user "
                + "WHERE username = ? AND password = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    Session.idUser = rs.getInt("id_user");
                    Session.username = rs.getString("username");
                    Session.role = rs.getString("role");
                    Session.idCabang = rs.getInt("id_cabang");

                    JOptionPane.showMessageDialog(
                            this,
                            "Login Berhasil"
                    );

                    dispose();

                    if ("kasir".equals(Session.role)) {

                        new KasirDashboard();

                    } else {

                        new MenuDashboard();

                    }

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Username atau Password Salah",
                            "Login Gagal",
                            JOptionPane.WARNING_MESSAGE
                    );

                }
            }

        } catch (SQLException ex) {

            // Kegagalan di sini murni teknis (mis. koneksi terputus saat query).
            LOGGER.log(Level.SEVERE,
                    "Login gagal saat menjalankan query.", ex);

            JOptionPane.showMessageDialog(
                    this,
                    "Terjadi kesalahan saat memproses login pada database.\n"
                    + "Silakan coba lagi.",
                    "Login Gagal",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        new LoginForm();

    }

}