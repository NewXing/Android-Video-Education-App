package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class MyRewardBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MyRewardBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String id;
        private String payType;
        private String paytime;
        private String totalAmount;
        private String teacherName;
        private String courseId;
        private String teacherAccount;
        private String mainTeacherName;
        private String courseName;
        private String coverImgUrl;
        private String giftNum;

        public String getTeacherAccount() {
            return teacherAccount;
        }

        public void setTeacherAccount(String teacherAccount) {
            this.teacherAccount = teacherAccount;
        }

        public String getMainTeacherName() {
            return mainTeacherName;
        }

        public void setMainTeacherName(String mainTeacherName) {
            this.mainTeacherName = mainTeacherName;
        }

        public String getGiftNum() {
            return giftNum;
        }

        public void setGiftNum(String giftNum) {
            this.giftNum = giftNum;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getPaytime() {
            return paytime;
        }

        public void setPaytime(String paytime) {
            this.paytime = paytime;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", payType='" + payType + '\'' +
                    ", paytime='" + paytime + '\'' +
                    ", totalAmount='" + totalAmount + '\'' +
                    ", teacherName='" + teacherName + '\'' +
                    ", courseId='" + courseId + '\'' +
                    ", teacherAccount='" + teacherAccount + '\'' +
                    ", mainTeacherName='" + mainTeacherName + '\'' +
                    ", courseName='" + courseName + '\'' +
                    ", coverImgUrl='" + coverImgUrl + '\'' +
                    ", giftNum='" + giftNum + '\'' +
                    '}';
        }
    }
}
