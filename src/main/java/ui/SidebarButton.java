package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Tombol navigasi sidebar dengan indikator aktif (bar aksen di kiri + latar
 * tersorot) dan efek hover. Konsisten untuk seluruh menu navigasi.
 */
public class SidebarButton extends JPanel {

    private final JLabel label;
    private final String iconName;
    private boolean active;
    private boolean hover;
    private Runnable onClick;

    public SidebarButton(String text, String iconName) {
        super(new BorderLayout());
        this.iconName = iconName;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        setPreferredSize(new Dimension(10, 44));
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 14));

        label = new JLabel(text);
        label.setFont(UITheme.font(java.awt.Font.PLAIN, 14));
        label.setForeground(UITheme.SIDEBAR_TEXT);
        label.setIconTextGap(14);
        label.setIcon(Icons.of(iconName, 18, UITheme.SIDEBAR_TEXT));
        add(label, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    public void setOnClick(Runnable r) {
        this.onClick = r;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            label.setForeground(Color.WHITE);
            label.setFont(UITheme.font(java.awt.Font.BOLD, 14));
            label.setIcon(Icons.of(iconName, 18, Color.WHITE));
        } else {
            label.setForeground(UITheme.SIDEBAR_TEXT);
            label.setFont(UITheme.font(java.awt.Font.PLAIN, 14));
            label.setIcon(Icons.of(iconName, 18, UITheme.SIDEBAR_TEXT));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        if (active) {
            // Sorotan selebar penuh sidebar + bar aksen di tepi kiri.
            g2.setColor(UITheme.SIDEBAR_ACTIVE);
            g2.fillRect(0, 0, w, h);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, 4, h);
        } else if (hover) {
            g2.setColor(UITheme.SIDEBAR_HOVER);
            g2.fillRect(0, 0, w, h);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
