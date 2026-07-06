
public class Student extends User {

    private String teachingSkill;
    private String learningSkill;

    public Student() {
    }

    public Student(int id, String name, String email, String password,
                   String teachingSkill, String learningSkill) {

        super(id, name, email, password);

        this.teachingSkill = teachingSkill;
        this.learningSkill = learningSkill;
    }

    @Override
    public void showDashboard() {
        System.out.println("Welcome to Skill Swap Dashboard");
    }

    public String getTeachingSkill() {
        return teachingSkill;
    }

    public void setTeachingSkill(String teachingSkill) {
        this.teachingSkill = teachingSkill;
    }

    public String getLearningSkill() {
        return learningSkill;
    }

    public void setLearningSkill(String learningSkill) {
        this.learningSkill = learningSkill;
    }
}
