package education.juxin.com.educationpro.bean;

public class ProOrderParam {

    private String courseId; // 课程id
    private String giftId; // 礼物id(如果打赏此id必填)
    private String giftNum; // 礼物的数量(默认为1,如果是1可以不传,如果打赏此字段必填)
    private String lessonId; // 课节id(不必填,如果是送礼物需要填写此id)
    private String orderType; // 订单类型：1课程支付，2打赏支付
    private String passiveInvitationCode; // 邀请码(不必填)
    private String totalAmount; // 订单总价格

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPassiveInvitationCode() {
        return passiveInvitationCode;
    }

    public void setPassiveInvitationCode(String passiveInvitationCode) {
        this.passiveInvitationCode = passiveInvitationCode;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(String giftNum) {
        this.giftNum = giftNum;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    @Override
    public String toString() {
        return "ProOrderParam{" +
                "courseId='" + courseId + '\'' +
                ", giftId='" + giftId + '\'' +
                ", giftNum='" + giftNum + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", orderType='" + orderType + '\'' +
                ", passiveInvitationCode='" + passiveInvitationCode + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                '}';
    }
}
