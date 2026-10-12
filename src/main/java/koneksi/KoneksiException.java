package koneksi;

/**
 * Dilempar ketika koneksi database gagal dibuat atau konfigurasinya tidak
 * lengkap. Dibuat unchecked agar pola pemanggilan {@code Koneksi.getKoneksi()}
 * yang sudah ada tidak perlu menambahkan {@code throws}, namun tetap tertangkap
 * oleh blok {@code catch (Exception e)} pada form sehingga pesan yang jelas
 * dapat ditampilkan ke pengguna.
 *
 * <p>Pesan pada exception ini sengaja TIDAK memuat password / kredensial.
 */
public class KoneksiException extends RuntimeException {

    public KoneksiException(String message) {
        super(message);
    }

    public KoneksiException(String message, Throwable cause) {
        super(message, cause);
    }
}
