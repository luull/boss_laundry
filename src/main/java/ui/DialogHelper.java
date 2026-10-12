package ui;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Pembungkus dialog standar agar pesan ke pengguna konsisten, ramah, dan
 * tidak pernah membocorkan detail teknis (stack trace, connection string).
 */
public final class DialogHelper {

    private DialogHelper() {
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void success(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Berhasil",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Perhatian",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Terjadi Kesalahan",
                JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message,
                                  String title) {
        int r = JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }

    /**
     * Konfirmasi untuk tindakan berbahaya (hapus/nonaktif). Mengembalikan true
     * bila pengguna menyetujui.
     */
    public static boolean confirmDanger(Component parent, String message) {
        int r = JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }
}
