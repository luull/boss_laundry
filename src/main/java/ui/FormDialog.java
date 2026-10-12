package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Dialog form modal dengan gaya konsisten: header (judul + subjudul), banner
 * error, body berbasis {@link GridBagLayout} untuk pasangan label + input, dan
 * footer berisi tombol Batal &amp; Simpan. Dipakai ulang oleh seluruh form
 * tambah/edit agar seragam dan mudah dirawat.
 */
public class FormDialog extends JDialog {

    private final JPanel body = new JPanel(new GridBagLayout());
    private final JLabel errorBanner = new JLabel(" ");
    private final JButton btnSave = UIFactory.primaryButton("Simpan");
    private final JButton btnCancel = UIFactory.secondaryButton("Batal");
    private final GridBagConstraints gc = new GridBagConstraints();
    private int row = 0;

    public FormDialog(Window owner, String title, String subtitle) {
        super(owner, title, ModalityType.APPLICATION_MODAL);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel t = UIFactory.h2(title);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(t);
        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel s = UIFactory.muted(subtitle);
            s.setFont(UITheme.small());
            s.setAlignmentX(Component.LEFT_ALIGNMENT);
            header.add(Box.createVerticalStrut(2));
            header.add(s);
        }
        errorBanner.setFont(UITheme.small());
        errorBanner.setForeground(UITheme.DANGER);
        errorBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(Box.createVerticalStrut(10));
        header.add(errorBanner);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        root.add(header, BorderLayout.NORTH);

        // Body
        body.setOpaque(false);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        root.add(body, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new java.awt.FlowLayout(
                java.awt.FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        btnCancel.addActionListener(e -> dispose());
        footer.add(btnCancel);
        footer.add(btnSave);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(btnSave);
    }

    /** Tambah satu baris label + input yang mengisi lebar penuh. */
    public FormDialog addRow(String label, JComponent field) {
        return addRow(label, field, null);
    }

    public FormDialog addRow(String label, JComponent field, String hint) {
        JLabel l = UIFactory.fieldLabel(label);
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 6, 0);
        body.add(l, gc);
        row++;

        if (field.getPreferredSize().height < 38
                && !(field instanceof javax.swing.JScrollPane)) {
            field.setPreferredSize(new Dimension(
                    Math.max(260, field.getPreferredSize().width), 40));
        }
        gc.gridy = row;
        gc.insets = new Insets(0, 0, hint == null ? 14 : 2, 0);
        body.add(field, gc);
        row++;

        if (hint != null) {
            JLabel h = UIFactory.muted(hint);
            h.setFont(UITheme.small());
            gc.gridy = row;
            gc.insets = new Insets(0, 0, 14, 0);
            body.add(h, gc);
            row++;
        }
        return this;
    }

    public void showError(String message) {
        errorBanner.setText(message);
    }

    public void clearError() {
        errorBanner.setText(" ");
    }

    public void onSave(Runnable action) {
        btnSave.addActionListener(e -> action.run());
    }

    public void setSaving(boolean saving) {
        btnSave.setEnabled(!saving);
        btnCancel.setEnabled(!saving);
        btnSave.setText(saving ? "Menyimpan..." : "Simpan");
    }

    public JButton saveButton() {
        return btnSave;
    }

    /** Tampilkan dialog, dengan ukuran menyesuaikan isi, di tengah owner. */
    public void display() {
        pack();
        setMinimumSize(new Dimension(440, getHeight()));
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
