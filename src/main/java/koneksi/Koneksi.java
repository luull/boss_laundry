package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {

    private static Connection koneksi;

    public static Connection getKoneksi() {

        try {

            String url = "jdbc:mysql://localhost:3306/bozz_laundry";
            String user = "root";
            String password = "";

            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

            koneksi = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {

            System.out.println("Koneksi gagal");
            System.out.println(e);

        }

        return koneksi;
    }
}