import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BackendServer {

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
             HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
             server.createContext("/", exchange -> {
    try {
        java.nio.file.Path path = java.nio.file.Paths.get("../frontend/index.html");
        byte[] bytes = java.nio.file.Files.readAllBytes(path);

        exchange.getResponseHeaders().add("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    } catch (Exception e) {
        e.printStackTrace();
        try {
    sendResponse(exchange, "Frontend not found");
} catch (Exception ex) {
    ex.printStackTrace();
}
    }
});
            server.createContext("/login", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();
                    sendResponse(exchange, loginStudent(getParam(q, "email"), getParam(q, "password")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            server.createContext("/register", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();
                    sendResponse(exchange, registerStudent(
                            getParam(q, "name"),
                            getParam(q, "email"),
                            getParam(q, "password"),
                            getParam(q, "teachingSkill"),
                            getParam(q, "learningSkill")
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            server.createContext("/updateProfile", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();

                    int userId = Integer.parseInt(getParam(q, "userId"));
                    String name = getParam(q, "name");
                    String teachingSkill = getParam(q, "teachingSkill");
                    String learningSkill = getParam(q, "learningSkill");

                    sendResponse(exchange, updateProfile(userId, name, teachingSkill, learningSkill));

                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.createContext("/skills", exchange -> {
                try {
                    sendResponse(exchange, getAllSkills());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            server.createContext("/userStats", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();
                    int userId = Integer.parseInt(getParam(q, "userId"));
                    sendResponse(exchange, getUserStats(userId));
                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.createContext("/sendRequest", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();

                    int senderId = Integer.parseInt(getParam(q, "senderId"));
                    int receiverId = Integer.parseInt(getParam(q, "receiverId"));
                    String skill = getParam(q, "skill");

                    sendResponse(exchange, sendSkillRequest(senderId, receiverId, skill));
                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.createContext("/myRequests", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();
                    int userId = Integer.parseInt(getParam(q, "userId"));

                    sendResponse(exchange, getMyRequests(userId));
                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.createContext("/acceptRequest", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();
                    int id = Integer.parseInt(getParam(q, "id"));

                    sendResponse(exchange, acceptRequest(id));
                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.createContext("/rejectRequest", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();
                    int id = Integer.parseInt(getParam(q, "id"));

                    sendResponse(exchange, rejectRequest(id));
                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.createContext("/notifications", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();
                    int userId = Integer.parseInt(getParam(q, "userId"));

                    sendResponse(exchange, getNotifications(userId));
                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.createContext("/scheduleMeeting", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();

                    int requestId = Integer.parseInt(getParam(q, "requestId"));
                    String meetLink = getParam(q, "meetLink");
                    String meetingDate = getParam(q, "meetingDate");
                    String meetingTime = getParam(q, "meetingTime");

                    sendResponse(exchange, scheduleMeeting(requestId, meetLink, meetingDate, meetingTime));

                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.createContext("/addRating", exchange -> {
                try {
                    String q = exchange.getRequestURI().getQuery();

                    int requestId = Integer.parseInt(getParam(q, "requestId"));
                    int reviewerId = Integer.parseInt(getParam(q, "reviewerId"));
                    int mentorId = Integer.parseInt(getParam(q, "mentorId"));
                    int rating = Integer.parseInt(getParam(q, "rating"));
                    String review = getParam(q, "review");

                    sendResponse(exchange, addRating(requestId, reviewerId, mentorId, rating, review));

                } catch (Exception e) {
                    e.printStackTrace();
                    safeError(exchange);
                }
            });

            server.start();
            System.out.println("Server started on port " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loginStudent(String email, String password) {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM students WHERE email=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return "success|" +
                        rs.getInt("id") + "|" +
                        rs.getString("name") + "|" +
                        rs.getString("email") + "|" +
                        rs.getString("teaching_skill") + "|" +
                        rs.getString("learning_skill") + "|" +
                        rs.getString("department") + "|" +
                        rs.getString("role") + "|" +
                        rs.getString("badge") + "|" +
                        rs.getInt("reputation");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "failed";
    }

    public static String registerStudent(String name, String email, String password,
                                         String teachingSkill, String learningSkill) {
        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement checkPst =
                    con.prepareStatement("SELECT * FROM students WHERE email=?");
            checkPst.setString(1, email);

            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) {
                return "email_exists";
            }

            String sql = "INSERT INTO students(name,email,password,teaching_skill,learning_skill) VALUES(?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, password);
            pst.setString(4, teachingSkill);
            pst.setString(5, learningSkill);

            pst.executeUpdate();

            return "registered";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    public static String updateProfile(int userId, String name, String teachingSkill, String learningSkill) {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "UPDATE students SET name=?, teaching_skill=?, learning_skill=? WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, name);
            pst.setString(2, teachingSkill);
            pst.setString(3, learningSkill);
            pst.setInt(4, userId);

            pst.executeUpdate();

            return "updated";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    public static String getUserStats(int userId) {
        try {
            Connection con = DatabaseConnection.getConnection();

            int total = 0;
            int pending = 0;
            int accepted = 0;

            PreparedStatement pst1 = con.prepareStatement(
                    "SELECT COUNT(*) FROM requests WHERE sender_id=? OR receiver_id=?"
            );
            pst1.setInt(1, userId);
            pst1.setInt(2, userId);
            ResultSet rs1 = pst1.executeQuery();
            if (rs1.next()) total = rs1.getInt(1);

            PreparedStatement pst2 = con.prepareStatement(
                    "SELECT COUNT(*) FROM requests WHERE (sender_id=? OR receiver_id=?) AND status='Pending'"
            );
            pst2.setInt(1, userId);
            pst2.setInt(2, userId);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) pending = rs2.getInt(1);

            PreparedStatement pst3 = con.prepareStatement(
                    "SELECT COUNT(*) FROM requests WHERE (sender_id=? OR receiver_id=?) AND status='Accepted'"
            );
            pst3.setInt(1, userId);
            pst3.setInt(2, userId);
            ResultSet rs3 = pst3.executeQuery();
            if (rs3.next()) accepted = rs3.getInt(1);

            return total + "|" + pending + "|" + accepted;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0|0|0";
    }

    public static String getAllSkills() {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "SELECT id, name, email, teaching_skill, average_rating, total_reviews FROM students";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            StringBuilder data = new StringBuilder();

            while (rs.next()) {
                data.append(rs.getInt("id")).append("|")
                        .append(rs.getString("name")).append("|")
                        .append(rs.getString("email")).append("|")
                        .append(rs.getString("teaching_skill")).append("|")
                        .append(rs.getDouble("average_rating")).append("|")
                        .append(rs.getInt("total_reviews")).append("\n");
            }

            return data.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String sendSkillRequest(int senderId, int receiverId, String skill) {
        try {
            if (senderId == receiverId) return "self_request";

            Connection con = DatabaseConnection.getConnection();

            String checkSql =
                    "SELECT * FROM requests WHERE sender_id=? AND receiver_id=? AND skill=? AND status='Pending'";

            PreparedStatement checkPst = con.prepareStatement(checkSql);
            checkPst.setInt(1, senderId);
            checkPst.setInt(2, receiverId);
            checkPst.setString(3, skill);

            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) return "already_sent";

            String sql = "INSERT INTO requests(sender_id, receiver_id, skill, status) VALUES(?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, senderId);
            pst.setInt(2, receiverId);
            pst.setString(3, skill);
            pst.setString(4, "Pending");

            pst.executeUpdate();

            return "request_sent";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    public static String getMyRequests(int userId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            StringBuilder data = new StringBuilder();

            String incomingSql =
                    "SELECT r.id, r.skill, r.status, r.meet_link, r.meeting_date, r.meeting_time, s.id AS other_id, s.name " +
                    "FROM requests r JOIN students s ON r.sender_id = s.id " +
                    "WHERE r.receiver_id=?";

            PreparedStatement incomingPst = con.prepareStatement(incomingSql);
            incomingPst.setInt(1, userId);

            ResultSet incomingRs = incomingPst.executeQuery();

            while (incomingRs.next()) {
                data.append(incomingRs.getInt("id")).append("|")
                        .append("Incoming").append("|")
                        .append(incomingRs.getString("skill")).append("|")
                        .append(incomingRs.getString("name")).append("|")
                        .append(incomingRs.getString("status")).append("|")
                        .append(incomingRs.getString("meet_link")).append("|")
                        .append(incomingRs.getString("meeting_date")).append("|")
                        .append(incomingRs.getString("meeting_time")).append("|")
                        .append(incomingRs.getInt("other_id")).append("\n");
            }

            String outgoingSql =
                    "SELECT r.id, r.skill, r.status, r.meet_link, r.meeting_date, r.meeting_time, s.id AS other_id, s.name " +
                    "FROM requests r JOIN students s ON r.receiver_id = s.id " +
                    "WHERE r.sender_id=?";

            PreparedStatement outgoingPst = con.prepareStatement(outgoingSql);
            outgoingPst.setInt(1, userId);

            ResultSet outgoingRs = outgoingPst.executeQuery();

            while (outgoingRs.next()) {
                data.append(outgoingRs.getInt("id")).append("|")
                        .append("Outgoing").append("|")
                        .append(outgoingRs.getString("skill")).append("|")
                        .append(outgoingRs.getString("name")).append("|")
                        .append(outgoingRs.getString("status")).append("|")
                        .append(outgoingRs.getString("meet_link")).append("|")
                        .append(outgoingRs.getString("meeting_date")).append("|")
                        .append(outgoingRs.getString("meeting_time")).append("|")
                        .append(outgoingRs.getInt("other_id")).append("\n");
            }

            return data.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String acceptRequest(int id) {
        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement pst =
                    con.prepareStatement("UPDATE requests SET status='Accepted' WHERE id=?");

            pst.setInt(1, id);
            pst.executeUpdate();

            return "accepted";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    public static String rejectRequest(int id) {
        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement pst =
                    con.prepareStatement("DELETE FROM requests WHERE id=?");

            pst.setInt(1, id);
            pst.executeUpdate();

            return "rejected";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    public static String scheduleMeeting(int requestId, String meetLink, String meetingDate, String meetingTime) {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql =
                    "UPDATE requests SET meet_link=?, meeting_date=?, meeting_time=? WHERE id=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, meetLink);
            pst.setString(2, meetingDate);
            pst.setString(3, meetingTime);
            pst.setInt(4, requestId);

            pst.executeUpdate();

            return "meeting_scheduled";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    public static String addRating(int requestId, int reviewerId, int mentorId, int rating, String review) {
        try {
            if (rating < 1 || rating > 5) {
                return "invalid_rating";
            }

            Connection con = DatabaseConnection.getConnection();

            String checkSql = "SELECT * FROM ratings WHERE request_id=? AND reviewer_id=?";
            PreparedStatement checkPst = con.prepareStatement(checkSql);
            checkPst.setInt(1, requestId);
            checkPst.setInt(2, reviewerId);
            ResultSet checkRs = checkPst.executeQuery();

            if (checkRs.next()) {
                return "already_rated";
            }

            String insertSql = "INSERT INTO ratings(request_id, reviewer_id, mentor_id, rating, review) VALUES(?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(insertSql);

            pst.setInt(1, requestId);
            pst.setInt(2, reviewerId);
            pst.setInt(3, mentorId);
            pst.setInt(4, rating);
            pst.setString(5, review);

            pst.executeUpdate();

            String avgSql = "SELECT AVG(rating) AS avg_rating, COUNT(*) AS total FROM ratings WHERE mentor_id=?";
            PreparedStatement avgPst = con.prepareStatement(avgSql);
            avgPst.setInt(1, mentorId);
            ResultSet avgRs = avgPst.executeQuery();

            double avgRating = 0;
            int totalReviews = 0;

            if (avgRs.next()) {
                avgRating = avgRs.getDouble("avg_rating");
                totalReviews = avgRs.getInt("total");
            }

            String updateSql = "UPDATE students SET average_rating=?, total_reviews=?, reputation=reputation+10 WHERE id=?";
            PreparedStatement updatePst = con.prepareStatement(updateSql);

            updatePst.setDouble(1, avgRating);
            updatePst.setInt(2, totalReviews);
            updatePst.setInt(3, mentorId);

            updatePst.executeUpdate();

            return "rating_added";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    public static String getNotifications(int userId) {
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql =
                    "SELECT r.skill, s.name " +
                    "FROM requests r JOIN students s ON r.sender_id = s.id " +
                    "WHERE r.receiver_id=? AND r.status='Pending'";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();

            StringBuilder data = new StringBuilder();

            while (rs.next()) {
                data.append(rs.getString("name"))
                        .append(" sent you a request for ")
                        .append(rs.getString("skill"))
                        .append("\n");
            }

            return data.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getParam(String query, String key) {
        if (query == null) return "";

        String[] params = query.split("&");

        for (String param : params) {
            String[] pair = param.split("=", 2);

            if (pair.length == 2 && pair[0].equals(key)) {
                return URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            }
        }

        return "";
    }

    public static void sendResponse(HttpExchange exchange, String response) throws Exception {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, response.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static void safeError(HttpExchange exchange) {
        try {
            sendResponse(exchange, "error");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}