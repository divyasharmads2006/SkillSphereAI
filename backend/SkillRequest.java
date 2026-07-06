public class SkillRequest {

    private int requestId;
    private int senderId;
    private int receiverId;
    private String skill;
    private String status;

    public SkillRequest(int requestId, int senderId, int receiverId,
                        String skill, String status) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.skill = skill;
        this.status = status;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getSkill() {
        return skill;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
