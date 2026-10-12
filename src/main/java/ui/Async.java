package ui;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

/**
 * Pembungkus ringkas {@link SwingWorker} untuk menjalankan operasi database di
 * thread latar belakang, lalu mengirim hasil (atau error) kembali ke Event
 * Dispatch Thread. Mencegah UI freeze saat query berlangsung.
 */
public final class Async {

    private static final Logger LOGGER =
            Logger.getLogger(Async.class.getName());

    private Async() {
    }

    /**
     * Jalankan {@code background} di luar EDT. Bila sukses, {@code onSuccess}
     * dipanggil di EDT dengan hasilnya. Bila gagal, {@code onError} dipanggil
     * di EDT dengan penyebabnya.
     */
    public static <T> void run(Callable<T> background,
                               Consumer<T> onSuccess,
                               Consumer<Throwable> onError) {

        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() throws Exception {
                return background.call();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    if (onSuccess != null) {
                        onSuccess.accept(result);
                    }
                } catch (Exception ex) {
                    Throwable cause =
                            ex.getCause() != null ? ex.getCause() : ex;
                    LOGGER.log(Level.WARNING,
                            "Operasi latar belakang gagal.", cause);
                    if (onError != null) {
                        onError.accept(cause);
                    }
                }
            }
        };
        worker.execute();
    }
}
