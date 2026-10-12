package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * Panel kartu dengan sudut membulat, latar putih, dan garis tepi tipis.
 * Dipakai untuk mengelompokkan konten (statistik, toolbar, tabel) agar
 * tampilan rapi dan konsisten di seluruh aplikasi.
 */
public class CardPanel extends JPanel {

    private int arc = UITheme.RADIUS + 4;
    private Color borderColor = UITheme.BORDER;
    private Color fill = UITheme.SURFACE;
    private boolean shadow = true;

    public CardPanel() {
        setOpaque(false);
    }

    public CardPanel(java.awt.LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public CardPanel arc(int arc) {
        this.arc = arc;
        return this;
    }

    public CardPanel fill(Color c) {
        this.fill = c;
        return this;
    }

    public CardPanel border(Color c) {
        this.borderColor = c;
        return this;
    }

    public CardPanel shadow(boolean b) {
        this.shadow = b;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int off = shadow ? 3 : 0;

        if (shadow) {
            g2.setColor(new Color(15, 23, 42, 18));
            g2.fillRoundRect(2, 3, w - 4, h - 4, arc, arc);
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 1, h - 1 - off, arc, arc);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, w - 1, h - 1 - off, arc, arc);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
