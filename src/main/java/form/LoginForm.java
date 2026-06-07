package form;

import koneksi.Koneksi;
import koneksi.Session;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        try {

            Connection conn =
                    Koneksi.getKoneksi();

            String sql =
                    "SELECT * FROM master_user "
                    + "WHERE username=? "
                    + "AND password=?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setString(
                    1,
                    txtUsername.getText()
            );

            pst.setString(
                    2,
                    txtPassword.getText()
            );

            ResultSet rs =
                    pst.executeQuery();

            if (rs.next()) {

                Session.idUser =
                        rs.getInt("id_user");

                Session.username =
                        rs.getString("username");

                Session.role =
                        rs.getString("role");

                Session.idCabang =
                        rs.getInt("id_cabang");

                JOptionPane.showMessageDialog(
                        null,
                        "Login Berhasil"
                );

                dispose();

                if (Session.role.equals("kasir")) {

                    new KasirDashboard();

                } else {

                    new MenuDashboard();

                }

            } else {

                JOptionPane.showMessageDialog(
                        null,
                        "Username atau Password Salah"
                );

            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    e
            );

        }

    }

    // ================= MAIN =================
    public static void main(String[] args) {

        new LoginForm();

    }

}