package koneksi;

/**
 * Menyimpan identitas pengguna yang sedang login beserta helper otorisasi.
 *
 * <p>Aturan akses data per cabang:
 * <ul>
 *   <li><b>admin</b> dapat mengakses data seluruh cabang.</li>
 *   <li><b>owner</b> dan <b>kasir</b> hanya dapat mengakses data pada cabang
 *       miliknya ({@link #idCabang}).</li>
 * </ul>
 */
public class Session {

    public static int idUser;
    public static String username;
    public static String role;
    public static int idCabang;

    private Session() {
    }

    public static boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public static boolean isOwner() {
        return "owner".equalsIgnoreCase(role);
    }

    public static boolean isKasir() {
        return "kasir".equalsIgnoreCase(role);
    }

    /** Hanya admin yang boleh melihat/memilih seluruh cabang. */
    public static boolean canAccessAllCabang() {
        return isAdmin();
    }

    /**
     * Cabang yang boleh diakses pengguna saat ini. Mengembalikan {@code null}
     * bila pengguna boleh mengakses semua cabang (admin), atau id cabang
     * pengguna untuk owner/kasir. Dipakai sebagai filter query.
     */
    public static Integer scopedCabang() {
        return canAccessAllCabang() ? null : idCabang;
    }

    public static void clear() {
        idUser = 0;
        username = null;
        role = null;
        idCabang = 0;
    }
}
