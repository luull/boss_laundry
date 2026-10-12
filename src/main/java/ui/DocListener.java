package ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * {@link DocumentListener} ringkas yang menjalankan satu aksi pada setiap
 * perubahan teks. Berguna untuk pencarian "ketik langsung cari".
 */
public final class DocListener implements DocumentListener {

    private final Runnable action;

    public DocListener(Runnable action) {
        this.action = action;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        action.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        action.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        action.run();
    }
}
