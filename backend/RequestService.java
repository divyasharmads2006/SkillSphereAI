import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RequestService {

    public void sendRequest(int senderId, int receiverId, String skill) {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "INSERT INTO requests(sender_id, receiver_id, skill, status) VALUES(?,?,?,?)";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, senderId);
            pst.setInt(2, receiverId);
            pst.setString(3, skill);
            pst.setString(4, "Pending");

            pst.executeUpdate();

            System.out.println("Request sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptRequest(int requestId) {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "UPDATE requests SET status='Accepted' WHERE id=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, requestId);

            pst.executeUpdate();

            System.out.println("Request accepted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rejectRequest(int requestId) {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "DELETE FROM requests WHERE id=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, requestId);

            pst.executeUpdate();

            System.out.println("Request rejected successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewRequests() {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM requests";

            PreparedStatement pst = con.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            System.out.println("\n===== ALL REQUESTS =====");

            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("id") +
                    " | Sender: " + rs.getInt("sender_id") +
                    " | Receiver: " + rs.getInt("receiver_id") +
                    " | Skill: " + rs.getString("skill") +
                    " | Status: " + rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
