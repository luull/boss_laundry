package form;

import koneksi.Session;

import ui.CardPanel;
import ui.DialogHelper;
import ui.Icons;
import ui.SidebarButton;
import ui.UIFactory;
import ui.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Jendela utama aplikasi (admin / owner). Shell profesional yang terdiri dari
 * sidebar navigasi, topbar, dan area konten yang berpindah tanpa membuka frame
 * baru. Terbuka dalam kondisi maximized dan responsif terhadap ukuran layar.
 */
public class MenuDashboard extends JFrame {

    private final JPanel contentHolder = new JPanel(new BorderLayout());
    private final JLabel pageTitle = UIFactory.h2("Dashboard");
    private final JLabel breadcrumb = UIFactory.muted("Beranda / Dashboard");
    private final Map<String, SidebarButton> navButtons = new LinkedHashMap<>();
    private final Map<String, Supplier<JComponent>> pageFactories =
            new LinkedHashMap<>();
    private final Map<String, String> pageTitles = new LinkedHashMap<>();

    private Timer clockTimer;
    private JLabel clockLabel;
    private String currentKey;

    public MenuDashboard() {
        UITheme.init();

        setTitle("BOSS Laundry - " + safeRole());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 680));

        registerPages();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(), BorderLayout.CENTER);
        setContentPane(root);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setVisible(true);

        navigate("dashboard");
    }

    // ================= REGISTRASI HALAMAN (BERBASIS ROLE) =================
    private void registerPages() {
        addPage("dashboard", "Dashboard", DashboardPanel::new);

        boolean admin = "admin".equalsIgnoreCase(Session.role);

        if (admin) {
            addPage("cabang", "Master Cabang", MasterCabang::new);
        }
        addPage("pelanggan", "Master Pelanggan", MasterPelanggan::new);
        addPage("layanan", "Master Layanan", MasterLayanan::new);
        if (admin) {
            addPage("user", "Master User", MasterUser::new);
        }
        addPage("transaksi", "Transaksi Laundry", TransaksiLaundry::new);
        addPage("laporan", "Laporan", LaporanLaundry::new);
    }

    private void addPage(String key, String title, Supplier<JComponent> factory) {
        pageFactories.put(key, factory);
        pageTitles.put(key, title);
    }

    // ================= SIDEBAR =================
    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 10));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 0, 18, 0));

        // --- Brand ---
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        brand.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        JLabel logo = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/image/logo.png");
        if (logoUrl != null) {
            logo.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon(logoUrl)
                    .getImage().getScaledInstance(34, 34, java.awt.Image.SCALE_SMOOTH)));
        }
        JLabel brandName = new JLabel("BOSS LAUNDRY");
        brandName.setForeground(Color.WHITE);
        brandName.setFont(UITheme.font(java.awt.Font.BOLD, 17));
        brand.add(logo);
        brand.add(brandName);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(leftStrut(20));

        // --- Kartu pengguna ---
        sidebar.add(buildUserCard());
        sidebar.add(leftStrut(16));

        JLabel navHeader = new JLabel("  MENU UTAMA");
        navHeader.setForeground(UITheme.SIDEBAR_TEXT_DIM);
        navHeader.setFont(UITheme.font(java.awt.Font.BOLD, 11));
        navHeader.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 20));
        navHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navHeader);

        // --- Navigasi ---
        for (String key : pageFactories.keySet()) {
            SidebarButton btn = new SidebarButton(pageTitles.get(key),
                    iconFor(key));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setOnClick(() -> navigate(key));
            navButtons.put(key, btn);
            sidebar.add(btn);
            sidebar.add(leftStrut(4));
        }

        sidebar.add(leftGlue());

        // --- Logout ---
        SidebarButton logout = new SidebarButton("Logout", Icons.LOGOUT);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setOnClick(this::logout);
        sidebar.add(logout);

        return sidebar;
    }

    private JComponent buildUserCard() {
        CardPanel card = new CardPanel(new BorderLayout(12, 0));
        card.fill(UITheme.SIDEBAR_BG_ALT).border(UITheme.SIDEBAR_HOVER)
                .shadow(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        outer.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        outer.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(new Avatar(initial()), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel uname = new JLabel(Session.username == null ? "-" : Session.username);
        uname.setForeground(Color.WHITE);
        uname.setFont(UITheme.font(java.awt.Font.BOLD, 14));
        JLabel role = new JLabel(safeRole());
        role.setForeground(new Color(0x93C5FD));
        role.setFont(UITheme.font(java.awt.Font.BOLD, 11));
        info.add(uname);
        info.add(Box.createVerticalStrut(2));
        info.add(role);
        card.add(info, BorderLayout.CENTER);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ================= MAIN (TOPBAR + CONTENT) =================
    private JComponent buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG);
        main.add(buildTopbar(), BorderLayout.NORTH);

        contentHolder.setBackground(UITheme.BG);
        contentHolder.setBorder(BorderFactory.createEmptyBorder(
                UITheme.PAD_LG, UITheme.PAD_LG, UITheme.PAD_LG, UITheme.PAD_LG));
        main.add(contentHolder, BorderLayout.CENTER);
        return main;
    }

    private JComponent buildTopbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        breadcrumb.setFont(UITheme.small());
        breadcrumb.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(pageTitle);
        left.add(Box.createVerticalStrut(2));
        left.add(breadcrumb);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        clockLabel = new JLabel();
        clockLabel.setFont(UITheme.body());
        clockLabel.setForeground(UITheme.TEXT_SECONDARY);
        clockLabel.setIcon(Icons.of(Icons.CLOCK, 16, UITheme.TEXT_MUTED));
        clockLabel.setIconTextGap(8);
        updateClock();
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        right.add(clockLabel);

        JButton refresh = UIFactory.secondaryButton("Refresh");
        UIFactory.withIcon(refresh, Icons.REFRESH);
        refresh.addActionListener(e -> {
            if (currentKey != null) {
                navigate(currentKey);
            }
        });
        right.add(refresh);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        clockLabel.setText(now.format(DateTimeFormatter.ofPattern(
                "EEE, dd MMM yyyy  HH:mm:ss",
                new java.util.Locale("id", "ID"))));
    }

    // ================= NAVIGASI =================
    private void navigate(String key) {
        if (!pageFactories.containsKey(key)) {
            return;
        }
        currentKey = key;

        navButtons.forEach((k, b) -> b.setActive(k.equals(key)));
        pageTitle.setText(pageTitles.get(key));
        breadcrumb.setText("Beranda / " + pageTitles.get(key));

        contentHolder.removeAll();
        JComponent page;
        try {
            page = pageFactories.get(key).get();
        } catch (Exception ex) {
            page = errorPage();
        }
        contentHolder.add(page, BorderLayout.CENTER);
        contentHolder.revalidate();
        contentHolder.repaint();
    }

    private JComponent errorPage() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG);
        p.add(UIFactory.muted("Gagal memuat halaman. Coba lagi."));
        return p;
    }

    private void logout() {
        if (!DialogHelper.confirm(this, "Keluar dari aplikasi sekarang?",
                "Logout")) {
            return;
        }
        if (clockTimer != null) {
            clockTimer.stop();
        }
        Session.idUser = 0;
        Session.username = null;
        Session.role = null;
        Session.idCabang = 0;
        dispose();
        new LoginForm();
    }

    // ================= UTIL =================
    private String iconFor(String key) {
        return switch (key) {
            case "dashboard" -> Icons.DASHBOARD;
            case "cabang" -> Icons.BRANCH;
            case "pelanggan" -> Icons.CUSTOMER;
            case "layanan" -> Icons.SERVICE;
            case "user" -> Icons.USER;
            case "transaksi" -> Icons.TRX;
            case "laporan" -> Icons.REPORT;
            default -> Icons.BOX;
        };
    }

    private String safeRole() {
        return Session.role == null ? "" : Session.role.toUpperCase();
    }

    /** Strut vertikal yang rata-kiri agar BoxLayout tidak menyusutkan lebar. */
    private Component leftStrut(int h) {
        Box.Filler f = (Box.Filler) Box.createVerticalStrut(h);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private Component leftGlue() {
        Box.Filler f = (Box.Filler) Box.createVerticalGlue();
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private String initial() {
        if (Session.username == null || Session.username.isEmpty()) {
            return "?";
        }
        return Session.username.substring(0, 1).toUpperCase();
    }

    /** Avatar bundar dengan inisial pengguna. */
    private static final class Avatar extends JPanel {
        private final String text;

        Avatar(String text) {
            this.text = text;
            setOpaque(false);
            setPreferredSize(new Dimension(44, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int d = Math.min(getWidth(), getHeight());
            g2.setColor(UITheme.PRIMARY);
            g2.fillOval(0, (getHeight() - d) / 2, d, d);
            g2.setColor(Color.WHITE);
            g2.setFont(UITheme.font(java.awt.Font.BOLD, 18));
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(text)) / 2;
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
            g2.dispose();
        }
    }
}
