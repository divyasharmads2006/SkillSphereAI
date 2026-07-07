import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:mysql://hayabusa.proxy.rlwy.net:10381/railway?useSSL=true&requireSSL=true";
        
    private static final String USER = "root";

    private static final String PASSWORD =
            "hQzTbmmgqdjOSAGzmjMXcEkPHzyVchBE";

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Railway Database connected successfully!");
        } catch (Exception e) {
            System.out.println("Railway Database connection failed!");
            e.printStackTrace();
        }

        return con;
    }
}
