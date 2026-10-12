package ui;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Pabrik komponen Swing dengan gaya konsisten (tombol, label, input, tabel).
 * Menghindari duplikasi style dan event handler di seluruh form.
 */
public final class UIFactory {

    private static final Locale ID = new Locale("id", "ID");

    private UIFactory() {
    }

    // ==================================================================
    // TOMBOL
    // ==================================================================

    public static JButton primaryButton(String text) {
        return solidButton(text, UITheme.PRIMARY, UITheme.PRIMARY_HOVER,
                UITheme.PRIMARY_PRESSED, Color.WHITE);
    }

    public static JButton successButton(String text) {
        return solidButton(text, UITheme.SUCCESS, UITheme.SUCCESS_HOVER,
                UITheme.SUCCESS_HOVER, Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return solidButton(text, UITheme.DANGER, UITheme.DANGER_HOVER,
                UITheme.DANGER_HOVER, Color.WHITE);
    }

    public static JButton warningButton(String text) {
        return solidButton(text, UITheme.WARNING, UITheme.WARNING_HOVER,
                UITheme.WARNING_HOVER, Color.WHITE);
    }

    public static JButton infoButton(String text) {
        return solidButton(text, UITheme.INFO, new Color(0x0284C7),
                new Color(0x0369A1), Color.WHITE);
    }

    /**
     * Tombol sekunder/netral: latar abu-abu muda yang jelas terlihat (bukan
     * putih polos), teks gelap, dan garis tepi tipis. Dipakai untuk aksi
     * sekunder seperti Reset, Refresh, Batal.
     */
    public static JButton secondaryButton(String text) {
        Color bg = new Color(0xE8EDF3);
        Color hover = new Color(0xD8E0EA);
        Color pressed = new Color(0xC7D3E0);
        JButton b = baseButton(text);
        b.setForeground(UITheme.TEXT);
        b.setBackground(bg);
        b.putClientProperty(FlatClientProperties.STYLE,
                "background:" + hex(bg) + ";"
                + "foreground:" + hex(UITheme.TEXT) + ";"
                + "hoverBackground:" + hex(hover) + ";"
                + "pressedBackground:" + hex(pressed) + ";"
                + "borderColor:" + hex(UITheme.BORDER_STRONG) + ";"
                + "borderWidth:1;"
                + "focusWidth:0");
        return b;
    }

    private static JButton solidButton(String text, Color bg, Color hover,
                                       Color pressed, Color fg) {
        JButton b = baseButton(text);
        b.setForeground(fg);
        b.setBackground(bg);
        b.putClientProperty(FlatClientProperties.STYLE,
                "background:" + hex(bg) + ";"
                + "foreground:" + hex(fg) + ";"
                + "hoverBackground:" + hex(hover) + ";"
                + "pressedBackground:" + hex(pressed) + ";"
                + "focusWidth:0;"
                + "borderWidth:0");
        return b;
    }

    private static JButton baseButton(String text) {
        JButton b = new JButton(text);
        b.setFont(UITheme.buttonFont());
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMargin(new java.awt.Insets(8, 16, 8, 16));
        b.setIconTextGap(8);
        return b;
    }

    /** Tempelkan ikon kiri pada tombol dengan warna otomatis mengikuti teks. */
    public static JButton withIcon(JButton b, String iconName) {
        Color c = b.getForeground();
        b.setIcon(Icons.of(iconName, 16, c));
        return b;
    }

    // ==================================================================
    // LABEL
    // ==================================================================

    public static JLabel h1(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.h1());
        l.setForeground(UITheme.TEXT);
        return l;
    }

    public static JLabel h2(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.h2());
        l.setForeground(UITheme.TEXT);
        return l;
    }

    public static JLabel h3(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.h3());
        l.setForeground(UITheme.TEXT);
        return l;
    }

    /** Label nilai besar (mis. total/kembalian pada kasir). */
    public static JLabel bigValue(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.font(Font.BOLD, 26));
        l.setForeground(UITheme.TEXT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.body());
        l.setForeground(UITheme.TEXT_MUTED);
        return l;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.label());
        l.setForeground(UITheme.TEXT_SECONDARY);
        return l;
    }

    // ==================================================================
    // INPUT
    // ==================================================================

    public static JTextField textField() {
        JTextField t = new JTextField();
        t.setFont(UITheme.body());
        return t;
    }

    public static JTextField searchField(String placeholder) {
        JTextField t = new JTextField();
        t.setFont(UITheme.body());
        t.setColumns(18);
        UITheme.setPlaceholder(t, placeholder);
        t.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                Icons.of(Icons.SEARCH, 16, UITheme.TEXT_MUTED));
        t.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON,
                true);
        return t;
    }

    // ==================================================================
    // TABEL
    // ==================================================================

    /** Terapkan gaya tabel enterprise yang bersih dan nyaman dibaca. */
    public static void styleTable(JTable table) {
        table.setFont(UITheme.body());
        table.setRowHeight(36);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(UITheme.TABLE_GRID);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(UITheme.TABLE_SELECTION);
        table.setSelectionForeground(UITheme.TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setDefaultEditor(Object.class, null);
        table.setRowSelectionAllowed(true);
        table.putClientProperty(FlatClientProperties.STYLE,
                "showTrailingVerticalLine:false");

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.smallBold());
        header.setBackground(UITheme.TABLE_HEADER_BG);
        header.setForeground(UITheme.TABLE_HEADER_TEXT);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        // Padding kiri default pada sel.
        DefaultTableCellRenderer base = new PaddedRenderer(SwingConstants.LEFT);
        table.setDefaultRenderer(Object.class, base);
    }

    public static JScrollPane tableScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        sp.getViewport().setBackground(UITheme.SURFACE);
        sp.setBackground(UITheme.SURFACE);
        return sp;
    }

    public static DefaultTableCellRenderer centerRenderer() {
        return new PaddedRenderer(SwingConstants.CENTER);
    }

    public static DefaultTableCellRenderer rightRenderer() {
        return new PaddedRenderer(SwingConstants.RIGHT);
    }

    /** Renderer mata uang Rupiah rata kanan. */
    public static DefaultTableCellRenderer currencyRenderer() {
        return new PaddedRenderer(SwingConstants.RIGHT) {
            @Override
            protected void setValue(Object value) {
                if (value == null) {
                    setText("");
                    return;
                }
                try {
                    double d = (value instanceof Number)
                            ? ((Number) value).doubleValue()
                            : Double.parseDouble(value.toString());
                    setText(rupiah(d));
                } catch (NumberFormatException ex) {
                    setText(value.toString());
                }
            }
        };
    }

    /** Renderer badge status berwarna. */
    public static DefaultTableCellRenderer statusRenderer() {
        return new StatusRenderer();
    }

    // ==================================================================
    // UTIL
    // ==================================================================

    public static String rupiah(double value) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(ID);
        nf.setMaximumFractionDigits(0);
        return nf.format(value);
    }

    public static String hex(Color c) {
        return String.format("#%02x%02x%02x",
                c.getRed(), c.getGreen(), c.getBlue());
    }

    public static Component vgap(int h) {
        return javax.swing.Box.createVerticalStrut(h);
    }

    public static Component hgap(int w) {
        return javax.swing.Box.createHorizontalStrut(w);
    }

    // ------------------------------------------------------------------

    private static class PaddedRenderer extends DefaultTableCellRenderer {
        PaddedRenderer(int align) {
            setHorizontalAlignment(align);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            if (!isSelected) {
                c.setBackground(row % 2 == 0
                        ? UITheme.SURFACE : UITheme.TABLE_STRIPE);
                c.setForeground(UITheme.TEXT);
            }
            return c;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        StatusRenderer() {
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            String s = value == null ? "" : value.toString().toLowerCase();
            Color fg;
            switch (s) {
                case "selesai", "diambil", "aktif", "lunas" ->
                        fg = UITheme.SUCCESS_HOVER;
                case "proses", "dicuci", "disetrika" ->
                        fg = UITheme.WARNING_HOVER;
                case "nonaktif", "batal" -> fg = UITheme.DANGER;
                default -> fg = UITheme.TEXT_SECONDARY;
            }
            setFont(UITheme.smallBold());
            if (!isSelected) {
                setBackground(row % 2 == 0
                        ? UITheme.SURFACE : UITheme.TABLE_STRIPE);
            }
            setForeground(isSelected ? UITheme.TEXT : fg);
            setText(value == null ? "" : capitalize(value.toString()));
            return this;
        }

        private String capitalize(String v) {
            if (v.isEmpty()) {
                return v;
            }
            return Character.toUpperCase(v.charAt(0)) + v.substring(1);
        }
    }
}
