package form;

import com.formdev.flatlaf.FlatClientProperties;

import koneksi.Koneksi;
import koneksi.KoneksiException;
import koneksi.Session;

import ui.Async;
import ui.Icons;
import ui.UIFactory;
import ui.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Halaman login BOZZ Laundry. Dirancang profesional, responsif, dan tidak
 * memblokir UI saat verifikasi kredensial (query dijalankan via SwingWorker).
 */
public class LoginForm extends JFrame {

    private final JTextField txtUsername = UIFactory.textField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JButton btnLogin = UIFactory.primaryButton("MASUK");
    private final JLabel lblError = new JLabel(" ");

    public LoginForm() {
        UITheme.init();

        setTitle("BOSS Laundry - Masuk");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 500));

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildBrandPanel(), BorderLayout.WEST);
        root.add(buildFormPanel(), BorderLayout.CENTER);
        setContentPane(root);

        setSize(960, 580);
        setLocationRelativeTo(null);

        getRootPane().setDefaultButton(btnLogin);
        btnLogin.addActionListener(e -> login());

        setVisible(true);
        txtUsername.requestFocusInWindow();
    }

    // ================= PANEL KIRI (BRAND) =================
    private JComponent buildBrandPanel() {
        JPanel brand = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, UITheme.SIDEBAR_BG,
                        0, getHeight(), new Color(0x1E3A8A)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        brand.setPreferredSize(new Dimension(410, 10));

        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new javax.swing.BoxLayout(col, javax.swing.BoxLayout.Y_AXIS));

        JLabel logo = new JLabel();
        logo.setAlignmentX(CENTER_ALIGNMENT);
        ImageIcon icon = loadLogo(96);
        if (icon != null) {
            logo.setIcon(icon);
        }

        JLabel name = new JLabel("BOSS LAUNDRY");
        name.setFont(UITheme.font(java.awt.Font.BOLD, 30));
        name.setForeground(Color.WHITE);
        name.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Sistem Manajemen Laundry");
        tagline.setFont(UITheme.font(java.awt.Font.PLAIN, 15));
        tagline.setForeground(UITheme.SIDEBAR_TEXT);
        tagline.setAlignmentX(CENTER_ALIGNMENT);

        col.add(logo);
        col.add(UIFactory.vgap(18));
        col.add(name);
        col.add(UIFactory.vgap(8));
        col.add(tagline);
        col.add(UIFactory.vgap(28));
        col.add(feature("Transaksi kasir cepat dan akurat"));
        col.add(UIFactory.vgap(12));
        col.add(feature("Manajemen pelanggan & layanan"));
        col.add(UIFactory.vgap(12));
        col.add(feature("Laporan pendapatan multi-cabang"));

        brand.add(col);
        return brand;
    }

    private JComponent feature(String text) {
        JPanel row = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(CENTER_ALIGNMENT);
        JLabel dot = new JLabel(Icons.of(Icons.CHECK, 16, new Color(0x6EE7B7)));
        JLabel l = new JLabel(text);
        l.setForeground(UITheme.SIDEBAR_TEXT);
        l.setFont(UITheme.body());
        row.add(dot);
        row.add(l);
        return row;
    }

    // ================= PANEL KANAN (FORM) =================
    private JComponent buildFormPanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(UITheme.SURFACE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        form.setPreferredSize(new Dimension(360, 420));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;

        JLabel welcome = UIFactory.h1("Selamat Datang");
        JLabel sub = UIFactory.muted("Masuk untuk melanjutkan ke aplikasi.");

        form.add(welcome, gc);
        gc.gridy++;
        gc.insets = new Insets(4, 0, 24, 0);
        form.add(sub, gc);

        gc.insets = new Insets(0, 0, 6, 0);
        gc.gridy++;
        form.add(UIFactory.fieldLabel("Username"), gc);

        txtUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                Icons.of(Icons.USER, 16, UITheme.TEXT_MUTED));
        UITheme.setPlaceholder(txtUsername, "Masukkan username");
        txtUsername.setPreferredSize(new Dimension(10, 42));
        gc.gridy++;
        gc.insets = new Insets(0, 0, 16, 0);
        form.add(txtUsername, gc);

        gc.gridy++;
        gc.insets = new Insets(0, 0, 6, 0);
        form.add(UIFactory.fieldLabel("Password"), gc);

        txtPassword.setFont(UITheme.body());
        txtPassword.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                Icons.of(Icons.USER, 16, UITheme.TEXT_MUTED));
        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton:true");
        UITheme.setPlaceholder(txtPassword, "Masukkan password");
        txtPassword.setPreferredSize(new Dimension(10, 42));
        gc.gridy++;
        gc.insets = new Insets(0, 0, 6, 0);
        form.add(txtPassword, gc);

        lblError.setFont(UITheme.small());
        lblError.setForeground(UITheme.DANGER);
        gc.gridy++;
        gc.insets = new Insets(0, 0, 14, 0);
        form.add(lblError, gc);

        btnLogin.setPreferredSize(new Dimension(10, 46));
        gc.gridy++;
        gc.insets = new Insets(0, 0, 0, 0);
        form.add(btnLogin, gc);

        wrap.add(form);
        return wrap;
    }

    private ImageIcon loadLogo(int size) {
        try {
            java.net.URL url = getClass().getResource("/image/logo.png");
            if (url == null) {
                return null;
            }
            Image img = new ImageIcon(url).getImage()
                    .getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ex) {
            return null;
        }
    }

    // ================= AKSI LOGIN =================
    private void login() {
        lblError.setText(" ");

        final String username = txtUsername.getText().trim();
        final String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password wajib diisi.");
            return;
        }

        setLoading(true);

        Async.run(
                () -> authenticate(username, password),
                user -> {
                    setLoading(false);
                    if (user != null) {
                        openDashboard(user);
                    } else {
                        showError("Username atau password salah.");
                        txtPassword.setText("");
                        txtPassword.requestFocusInWindow();
                    }
                },
                err -> {
                    setLoading(false);
                    if (err instanceof KoneksiException) {
                        showError("Tidak dapat terhubung ke database. "
                                + "Periksa koneksi internet Anda.");
                    } else {
                        showError("Terjadi kesalahan saat memproses login.");
                    }
                });
    }

    /** Verifikasi kredensial (dipanggil di thread latar). */
    private UserSession authenticate(String username, String password)
            throws Exception {
        Connection conn = Koneksi.getKoneksi();
        String sql = "SELECT id_user, username, role, id_cabang "
                + "FROM master_user WHERE username = ? AND password = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    UserSession u = new UserSession();
                    u.idUser = rs.getInt("id_user");
                    u.username = rs.getString("username");
                    u.role = rs.getString("role");
                    u.idCabang = rs.getInt("id_cabang");
                    return u;
                }
                return null;
            }
        }
    }

    private void openDashboard(UserSession u) {
        Session.idUser = u.idUser;
        Session.username = u.username;
        Session.role = u.role;
        Session.idCabang = u.idCabang;

        dispose();
        if ("kasir".equalsIgnoreCase(u.role)) {
            new KasirDashboard();
        } else {
            new MenuDashboard();
        }
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        txtUsername.setEnabled(!loading);
        txtPassword.setEnabled(!loading);
        btnLogin.setText(loading ? "Memproses..." : "MASUK");
    }

    private void showError(String msg) {
        lblError.setText(msg);
    }

    private static final class UserSession {
        int idUser;
        String username;
        String role;
        int idCabang;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(LoginForm::new);
    }
}
