import java.sql.Connection;
import java.sql.PreparedStatement;

public class StudentService {

    public void registerStudent(Student student) {

        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "INSERT INTO students(name,email,password,teaching_skill,learning_skill) VALUES(?,?,?,?,?)";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, student.getName());
            pst.setString(2, student.getEmail());
            pst.setString(3, student.getPassword());
            pst.setString(4, student.getTeachingSkill());
            pst.setString(5, student.getLearningSkill());

            pst.executeUpdate();

            System.out.println("Student Registered Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
