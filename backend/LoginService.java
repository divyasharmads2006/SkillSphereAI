import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {

    public boolean loginStudent(String email, String password) {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM students WHERE email=? AND password=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("Login Successful!");
                System.out.println("Welcome " + rs.getString("name"));
                return true;
            } else {
                System.out.println("Invalid Email or Password!");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}