package ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Design system terpusat BOZZ Laundry.
 *
 * <p>Berisi seluruh token desain (warna, tipografi, radius, spacing) serta
 * inisialisasi Look &amp; Feel modern (FlatLaf). Dipakai ulang oleh seluruh
 * form agar tampilan aplikasi konsisten dan terasa seperti software komersial.
 *
 * <p>Arah desain: enterprise desktop, dominan putih / abu-abu sangat muda,
 * aksen biru profesional, hijau untuk sukses, oranye untuk peringatan, merah
 * untuk tindakan berbahaya.
 */
public final class UITheme {

    private static final Logger LOGGER =
            Logger.getLogger(UITheme.class.getName());

    private UITheme() {
    }

    // ==================================================================
    // PALET WARNA
    // ==================================================================

    /** Warna utama (biru profesional). */
    public static final Color PRIMARY        = new Color(0x2563EB);
    public static final Color PRIMARY_HOVER  = new Color(0x1D4ED8);
    public static final Color PRIMARY_PRESSED = new Color(0x1E40AF);
    public static final Color PRIMARY_SOFT   = new Color(0xDBEAFE);

    /** Sidebar / navigasi (navy gelap). */
    public static final Color SIDEBAR_BG       = new Color(0x0F172A);
    public static final Color SIDEBAR_BG_ALT   = new Color(0x111C30);
    public static final Color SIDEBAR_HOVER    = new Color(0x1E293B);
    public static final Color SIDEBAR_ACTIVE   = new Color(0x2563EB);
    public static final Color SIDEBAR_TEXT     = new Color(0xCBD5E1);
    public static final Color SIDEBAR_TEXT_DIM = new Color(0x94A3B8);

    /** Status. */
    public static final Color SUCCESS       = new Color(0x16A34A);
    public static final Color SUCCESS_HOVER = new Color(0x15803D);
    public static final Color SUCCESS_SOFT  = new Color(0xDCFCE7);

    public static final Color WARNING       = new Color(0xF59E0B);
    public static final Color WARNING_HOVER = new Color(0xD97706);
    public static final Color WARNING_SOFT  = new Color(0xFEF3C7);

    public static final Color DANGER        = new Color(0xDC2626);
    public static final Color DANGER_HOVER  = new Color(0xB91C1C);
    public static final Color DANGER_SOFT   = new Color(0xFEE2E2);

    public static final Color INFO          = new Color(0x0EA5E9);
    public static final Color INFO_SOFT     = new Color(0xE0F2FE);

    /** Permukaan & teks. */
    public static final Color BG            = new Color(0xF1F5F9);
    public static final Color SURFACE       = Color.WHITE;
    public static final Color SURFACE_ALT   = new Color(0xF8FAFC);
    public static final Color BORDER        = new Color(0xE2E8F0);
    public static final Color BORDER_STRONG = new Color(0xCBD5E1);

    public static final Color TEXT          = new Color(0x0F172A);
    public static final Color TEXT_SECONDARY = new Color(0x475569);
    public static final Color TEXT_MUTED    = new Color(0x94A3B8);

    /** Tabel. */
    public static final Color TABLE_HEADER_BG   = new Color(0xF8FAFC);
    public static final Color TABLE_HEADER_TEXT = new Color(0x475569);
    public static final Color TABLE_GRID        = new Color(0xEEF2F7);
    public static final Color TABLE_STRIPE      = new Color(0xF8FAFC);
    public static final Color TABLE_SELECTION   = new Color(0xDBEAFE);

    // ==================================================================
    // TIPOGRAFI
    // ==================================================================

    private static String baseFamily = "SansSerif";

    public static Font font(int style, int size) {
        return new Font(baseFamily, style, size);
    }

    public static Font h1()      { return font(Font.BOLD, 24); }
    public static Font h2()      { return font(Font.BOLD, 19); }
    public static Font h3()      { return font(Font.BOLD, 15); }
    public static Font body()    { return font(Font.PLAIN, 14); }
    public static Font bodyBold() { return font(Font.BOLD, 14); }
    public static Font label()   { return font(Font.BOLD, 13); }
    public static Font small()   { return font(Font.PLAIN, 12); }
    public static Font smallBold() { return font(Font.BOLD, 12); }
    public static Font buttonFont() { return font(Font.BOLD, 14); }

    // ==================================================================
    // SPACING & RADIUS
    // ==================================================================

    public static final int RADIUS   = 10;
    public static final int PAD      = 16;
    public static final int PAD_LG   = 24;
    public static final int GAP      = 12;
    public static final int SIDEBAR_WIDTH = 248;

    // ==================================================================
    // INISIALISASI LOOK & FEEL
    // ==================================================================

    private static boolean initialized = false;

    /**
     * Pasang FlatLaf dan seluruh default UIManager. Aman dipanggil berkali-kali
     * (hanya dieksekusi sekali). Bila FlatLaf gagal, aplikasi tetap berjalan
     * dengan Look &amp; Feel sistem.
     */
    public static synchronized void init() {

        if (initialized) {
            return;
        }
        initialized = true;

        // JasperReports: bila sebuah font tidak tersedia di JVM, pakai font
        // pengganti alih-alih melempar JRFontNotFoundException (membuat semua
        // export PDF gagal). Jaring pengaman selain penggunaan font logical.
        System.setProperty(
                "net.sf.jasperreports.awt.ignore.missing.font", "true");

        try {
            FlatLightLaf.setup();

            // Font dasar aplikasi.
            baseFamily = resolveFontFamily();
            UIManager.put("defaultFont",
                    new FontUIResource(font(Font.PLAIN, 14)));

            // Sudut membulat yang konsisten.
            UIManager.put("Button.arc", RADIUS);
            UIManager.put("Component.arc", RADIUS);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("CheckBox.arc", 6);
            UIManager.put("ProgressBar.arc", 8);

            // Aksen warna utama.
            UIManager.put("Component.accentColor", PRIMARY);
            UIManager.put("Component.focusColor", PRIMARY_SOFT);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.borderColor", BORDER);
            UIManager.put("Component.innerFocusWidth", 1);

            UIManager.put("Panel.background", BG);
            UIManager.put("ScrollPane.background", SURFACE);

            // Tabel.
            UIManager.put("Table.rowHeight", 34);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing",
                    new java.awt.Dimension(0, 1));
            UIManager.put("Table.gridColor", TABLE_GRID);
            UIManager.put("Table.selectionBackground", TABLE_SELECTION);
            UIManager.put("Table.selectionForeground", TEXT);
            UIManager.put("Table.background", SURFACE);
            UIManager.put("TableHeader.background", TABLE_HEADER_BG);
            UIManager.put("TableHeader.foreground", TABLE_HEADER_TEXT);
            UIManager.put("TableHeader.height", 38);
            UIManager.put("TableHeader.separatorColor", BORDER);
            UIManager.put("TableHeader.bottomSeparatorColor", BORDER);

            // Scrollbar ramping modern.
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets",
                    new java.awt.Insets(2, 2, 2, 2));
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("ScrollBar.track", BG);

            // Dialog / popup.
            UIManager.put("PopupMenu.borderColor", BORDER);
            UIManager.put("MenuItem.selectionBackground", PRIMARY_SOFT);
            UIManager.put("MenuItem.selectionForeground", TEXT);
            UIManager.put("TabbedPane.selectedBackground", SURFACE);

            // ComboBox.
            UIManager.put("ComboBox.selectionBackground", PRIMARY_SOFT);
            UIManager.put("ComboBox.selectionForeground", TEXT);

        } catch (Exception ex) {
            LOGGER.log(Level.WARNING,
                    "Gagal memuat FlatLaf, memakai Look & Feel bawaan.", ex);
        }
    }

    private static String resolveFontFamily() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String[] candidates;
        if (os.contains("mac")) {
            candidates = new String[]{"SF Pro Text", "Helvetica Neue",
                    "Lucida Grande", "SansSerif"};
        } else if (os.contains("win")) {
            candidates = new String[]{"Segoe UI", "Tahoma", "SansSerif"};
        } else {
            candidates = new String[]{"Inter", "Ubuntu", "DejaVu Sans",
                    "SansSerif"};
        }
        java.util.Set<String> available = new java.util.HashSet<>(
                java.util.Arrays.asList(
                        java.awt.GraphicsEnvironment
                                .getLocalGraphicsEnvironment()
                                .getAvailableFontFamilyNames()));
        for (String c : candidates) {
            if (available.contains(c)) {
                return c;
            }
        }
        return "SansSerif";
    }

    /** Helper: pasang placeholder pada text field (fitur FlatLaf). */
    public static void setPlaceholder(javax.swing.JComponent field,
                                      String text) {
        field.putClientProperty(
                FlatClientProperties.PLACEHOLDER_TEXT, text);
    }
}
