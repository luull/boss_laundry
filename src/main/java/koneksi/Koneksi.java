package koneksi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pengelola koneksi database PostgreSQL (Supabase) untuk aplikasi BOZZ Laundry.
 *
 * <p>Perubahan utama dibanding versi MySQL lama:
 * <ul>
 *   <li>Menggunakan driver JDBC PostgreSQL dan SSL (wajib untuk Supabase).</li>
 *   <li>Konfigurasi diambil dari environment variable atau file lokal
 *       ({@code .env} / {@code db.properties}) yang TIDAK ikut masuk Git.
 *       Tidak ada kredensial yang ditulis di dalam source code.</li>
 *   <li>Koneksi tunggal dipakai ulang dan divalidasi; dibuat ulang hanya bila
 *       sudah tertutup / tidak valid. Tidak membuka koneksi baru berulang kali
 *       setiap pemanggilan.</li>
 *   <li>Jika koneksi gagal, melempar {@link KoneksiException} dengan pesan yang
 *       jelas (tanpa membocorkan password), BUKAN mengembalikan {@code null}
 *       secara diam-diam. Ini menghentikan error
 *       "Cannot invoke ... because conn is null".</li>
 * </ul>
 *
 * <p>Format konfigurasi yang didukung (prioritas: environment variable &gt;
 * file {@code .env} &gt; file {@code db.properties}):
 * <pre>
 *   # Opsi A - URL penuh (boleh sudah memuat user &amp; password):
 *   url=jdbc:postgresql://HOST:5432/postgres?user=USER&amp;password=PASS
 *
 *   # Opsi B - bagian terpisah:
 *   host=aws-0-xxx.pooler.supabase.com
 *   port=5432
 *   database=postgres
 *   user=postgres.xxxx
 *   password=xxxx
 *   sslmode=require
 * </pre>
 * Prefiks {@code db.} (mis. {@code db.host}) dan nama environment variable
 * {@code SUPABASE_DB_*} juga dikenali.
 */
public final class Koneksi {

    private static final Logger LOGGER =
            Logger.getLogger(Koneksi.class.getName());

    /** Timeout (detik) untuk validasi dan pembuatan koneksi. */
    private static final int TIMEOUT_DETIK = 10;

    /** Jumlah percobaan koneksi saat handshake awal gagal (SSL reset pooler). */
    private static final int MAX_PERCOBAAN = 3;

    /** Jeda antar percobaan koneksi (milidetik). */
    private static final long JEDA_PERCOBAAN_MS = 700L;

    /** Koneksi tunggal yang dipakai ulang oleh seluruh aplikasi. */
    private static Connection koneksi;

    private Koneksi() {
        // utility class, tidak untuk diinstansiasi
    }

    /**
     * Mengembalikan koneksi database yang valid dan siap pakai.
     *
     * @return koneksi aktif (tidak pernah {@code null})
     * @throws KoneksiException bila konfigurasi tidak lengkap atau koneksi gagal
     */
    public static synchronized Connection getKoneksi() {

        try {
            if (koneksi != null && koneksi.isValid(TIMEOUT_DETIK)) {
                return koneksi;
            }
        } catch (SQLException e) {
            // koneksi lama bermasalah -> buat ulang di bawah
            LOGGER.log(Level.FINE, "Validasi koneksi lama gagal, membuat ulang.", e);
        }

        koneksi = buatKoneksiBaru();
        return koneksi;
    }

    /** Menutup koneksi aktif (dipanggil mis. saat aplikasi keluar). */
    public static synchronized void tutup() {
        if (koneksi != null) {
            try {
                koneksi.close();
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "Gagal menutup koneksi.", e);
            } finally {
                koneksi = null;
            }
        }
    }

    // ------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------

    private static Connection buatKoneksiBaru() {

        DbConfig cfg = DbConfig.muat();

        String url = cfg.jdbcUrl();

        try {
            // Pastikan driver PostgreSQL ter-load (eksplisit, aman untuk shaded JAR).
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new KoneksiException(
                    "Driver PostgreSQL tidak ditemukan. Pastikan dependency "
                    + "org.postgresql:postgresql tersedia pada classpath.", e);
        }

        DriverManager.setLoginTimeout(TIMEOUT_DETIK);

        // Pooler Supabase kadang me-reset handshake SSL pada percobaan pertama
        // (terutama setelah idle). Lakukan beberapa kali percobaan singkat
        // agar tidak muncul kegagalan koneksi palsu pada aksi pertama pengguna.
        SQLException terakhir = null;
        for (int percobaan = 1; percobaan <= MAX_PERCOBAAN; percobaan++) {
            try {
                LOGGER.log(Level.INFO,
                        "Menghubungkan ke database {0} (SSL: {1}, percobaan {2}/{3}).",
                        new Object[]{cfg.ringkasanAman(), cfg.sslmode,
                                percobaan, MAX_PERCOBAAN});

                Connection c;
                if (cfg.urlSudahAdaKredensial()) {
                    // user & password sudah ada di dalam URL (query string).
                    c = DriverManager.getConnection(url);
                } else {
                    c = DriverManager.getConnection(url, cfg.user, cfg.password);
                }

                LOGGER.info("Koneksi ke Supabase berhasil.");
                return c;

            } catch (SQLException e) {
                terakhir = e;
                LOGGER.log(Level.WARNING,
                        "Percobaan koneksi {0} gagal ke {1}: {2}",
                        new Object[]{percobaan, cfg.ringkasanAman(),
                                e.getMessage()});
                if (percobaan < MAX_PERCOBAAN) {
                    try {
                        Thread.sleep(JEDA_PERCOBAAN_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // Semua percobaan gagal. Jangan pernah menyertakan password pada pesan.
        LOGGER.log(Level.SEVERE,
                "Gagal membuka koneksi ke " + cfg.ringkasanAman(), terakhir);
        throw new KoneksiException(
                "Koneksi ke database gagal. Periksa konfigurasi Supabase "
                + "(host, port, database, user, password, SSL) serta "
                + "koneksi internet Anda.\nDetail: "
                + (terakhir == null ? "tidak diketahui" : terakhir.getMessage()),
                terakhir);
    }

    // ==================================================================
    // Konfigurasi database (url atau host/port/db/user/password/sslmode).
    // Diambil dari environment variable atau file .env / db.properties lokal.
    // ==================================================================
    private static final class DbConfig {

        final String urlOverride;
        final String host;
        final String port;
        final String database;
        final String user;
        final String password;
        final String sslmode;

        private DbConfig(String urlOverride, String host, String port,
                         String database, String user, String password,
                         String sslmode) {
            this.urlOverride = urlOverride;
            this.host = host;
            this.port = port;
            this.database = database;
            this.user = user;
            this.password = password;
            this.sslmode = sslmode;
        }

        static DbConfig muat() {

            Map<String, String> f = muatFileKonfigurasi();

            String urlOverride = ambil(f, "SUPABASE_DB_URL",      "url");
            String host        = ambil(f, "SUPABASE_DB_HOST",     "host");
            String port        = pilih(ambil(f, "SUPABASE_DB_PORT",     "port"),     "5432");
            String database    = pilih(ambil(f, "SUPABASE_DB_NAME",     "database"), "postgres");
            String user        = ambil(f, "SUPABASE_DB_USER",     "user");
            String password    = ambil(f, "SUPABASE_DB_PASSWORD", "password");
            String sslmode     = pilih(ambil(f, "SUPABASE_DB_SSLMODE",  "sslmode"),  "require");

            boolean adaUrl = !isBlank(urlOverride);
            boolean urlPunyaKredensial = adaUrl
                    && urlOverride.contains("user=")
                    && urlOverride.contains("password=");

            // Validasi: kombinasi minimal yang bisa dipakai untuk connect.
            if (!adaUrl && isBlank(host)) {
                throw new KoneksiException(
                        "Host database Supabase belum diisi. Set 'host' (atau "
                        + "'url') pada file .env / db.properties, atau environment "
                        + "variable SUPABASE_DB_HOST. Lihat db.properties.example.");
            }

            if (!urlPunyaKredensial && (isBlank(user) || isBlank(password))) {
                throw new KoneksiException(
                        "Konfigurasi database belum lengkap. Diperlukan user & "
                        + "password Supabase, baik di dalam 'url' (?user=...&"
                        + "password=...) maupun sebagai key 'user' dan 'password' "
                        + "terpisah pada file .env / db.properties (atau environment "
                        + "variable SUPABASE_DB_USER & SUPABASE_DB_PASSWORD). "
                        + "Jangan menuliskan kredensial di dalam source code.");
            }

            return new DbConfig(urlOverride, host, port, database, user,
                    password, sslmode);
        }

        boolean urlSudahAdaKredensial() {
            return !isBlank(urlOverride)
                    && urlOverride.contains("user=")
                    && urlOverride.contains("password=");
        }

        String jdbcUrl() {
            String url;
            if (!isBlank(urlOverride)) {
                url = urlOverride;
            } else {
                url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            }
            // Pastikan koneksi terenkripsi (Supabase mewajibkan SSL).
            if (!url.toLowerCase().contains("sslmode=")) {
                url += (url.contains("?") ? "&" : "?") + "sslmode=" + sslmode;
            }
            return url;
        }

        /** Ringkasan koneksi untuk log/pesan error, TANPA password. */
        String ringkasanAman() {
            if (!isBlank(urlOverride)) {
                return urlOverride.replaceAll("(?i)password=[^&\"]*", "password=***");
            }
            return host + ":" + port + "/" + database
                    + (isBlank(user) ? "" : " (user=" + user + ")");
        }

        // --- pembacaan file konfigurasi (.env / db.properties) ---------

        private static Map<String, String> muatFileKonfigurasi() {

            Map<String, String> hasil = new LinkedHashMap<>();

            // Dari prioritas RENDAH ke TINGGI (yang belakangan menimpa).
            // 1) classpath (opsional, paling rendah)
            serapClasspath(hasil, "/db.properties");
            serapClasspath(hasil, "/.env");

            // 2) ~/.bozzlaundry/*
            String home = System.getProperty("user.home");
            if (home != null) {
                serapFile(hasil, Path.of(home, ".bozzlaundry", "db.properties"));
                serapFile(hasil, Path.of(home, ".bozzlaundry", ".env"));
            }

            // 3) folder kerja aplikasi
            serapFile(hasil, Path.of("db.properties"));
            serapFile(hasil, Path.of(".env"));

            // 4) path eksplisit dari -Dbozz.db.config=... (paling tinggi)
            String sysPath = System.getProperty("bozz.db.config");
            if (sysPath != null) {
                serapFile(hasil, Path.of(sysPath));
            }

            return hasil;
        }

        private static void serapFile(Map<String, String> target, Path path) {
            if (path == null || !Files.isRegularFile(path)) {
                return;
            }
            try (InputStream in = Files.newInputStream(path)) {
                parse(in, target);
                LOGGER.log(Level.INFO, "Konfigurasi database dimuat dari {0}", path);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING,
                        "Gagal membaca " + path + ", mencoba sumber lain.", e);
            }
        }

        private static void serapClasspath(Map<String, String> target, String res) {
            try (InputStream in = Koneksi.class.getResourceAsStream(res)) {
                if (in != null) {
                    parse(in, target);
                    LOGGER.log(Level.INFO,
                            "Konfigurasi database dimuat dari classpath {0}", res);
                }
            } catch (IOException e) {
                LOGGER.log(Level.FINE, "Tidak ada " + res + " pada classpath.", e);
            }
        }

        /**
         * Parser sederhana untuk format KEY=VALUE (mendukung .env & .properties).
         * Mengabaikan baris kosong dan komentar ({@code #} / {@code !}), membuang
         * prefiks {@code export }, serta menghapus tanda kutip pembungkus nilai.
         */
        private static void parse(InputStream in, Map<String, String> target)
                throws IOException {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String s = line.trim();
                    if (s.isEmpty() || s.startsWith("#") || s.startsWith("!")) {
                        continue;
                    }
                    if (s.startsWith("export ")) {
                        s = s.substring("export ".length()).trim();
                    }
                    int eq = s.indexOf('=');
                    if (eq <= 0) {
                        continue;
                    }
                    String key = s.substring(0, eq).trim();
                    String val = s.substring(eq + 1).trim();
                    val = lepasKutip(val);
                    // Normalisasi: buang prefiks "db." agar seragam.
                    if (key.startsWith("db.")) {
                        key = key.substring(3);
                    }
                    target.put(key.toLowerCase(), val);
                }
            }
        }

        private static String lepasKutip(String v) {
            if (v.length() >= 2
                    && ((v.startsWith("\"") && v.endsWith("\""))
                        || (v.startsWith("'") && v.endsWith("'")))) {
                return v.substring(1, v.length() - 1);
            }
            return v;
        }

        // --- resolusi nilai: env var dulu, lalu file konfigurasi ----------

        private static String ambil(Map<String, String> f, String envKey,
                                    String fileKey) {
            String v = System.getenv(envKey);
            if (isBlank(v)) {
                v = f.get(fileKey.toLowerCase());
            }
            return isBlank(v) ? null : v.trim();
        }

        private static String pilih(String v, String def) {
            return isBlank(v) ? def : v;
        }

        private static boolean isBlank(String s) {
            return s == null || s.trim().isEmpty();
        }
    }
}
