package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Kartu statistik dashboard: ikon beraksen, judul, nilai besar, dan sub-teks
 * opsional (mis. keterangan periode). Nilai dapat diperbarui setelah data
 * dimuat dari database.
 */
public class StatCard extends CardPanel {

    private final JLabel valueLabel;
    private final JLabel captionLabel;
    private final Color accent;
    private final String iconName;

    public StatCard(String title, String iconName, Color accent) {
        super(new BorderLayout(14, 0));
        this.accent = accent;
        this.iconName = iconName;
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setPreferredSize(new Dimension(220, 104));

        add(new IconBadge(), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(UITheme.smallBold());
        titleLabel.setForeground(UITheme.TEXT_MUTED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel = new JLabel("-");
        valueLabel.setFont(UITheme.font(java.awt.Font.BOLD, 26));
        valueLabel.setForeground(UITheme.TEXT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        captionLabel = new JLabel(" ");
        captionLabel.setFont(UITheme.small());
        captionLabel.setForeground(UITheme.TEXT_MUTED);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(titleLabel);
        text.add(javax.swing.Box.createVerticalStrut(6));
        text.add(valueLabel);
        text.add(javax.swing.Box.createVerticalStrut(2));
        text.add(captionLabel);

        add(text, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption == null || caption.isEmpty()
                ? " " : caption);
    }

    public StatCard caption(String caption) {
        setCaption(caption);
        return this;
    }

    private final class IconBadge extends JPanel {
        IconBadge() {
            setOpaque(false);
            setPreferredSize(new Dimension(52, 52));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int size = 48;
            int y = (getHeight() - size) / 2;
            g2.setColor(softAccent());
            g2.fillRoundRect(0, y, size, size, 14, 14);
            Icons.of(iconName, 24, accent)
                    .paintIcon(this, g2, 12, y + 12);
            g2.dispose();
        }

        private Color softAccent() {
            return new Color(accent.getRed(), accent.getGreen(),
                    accent.getBlue(), 32);
        }
    }
}
