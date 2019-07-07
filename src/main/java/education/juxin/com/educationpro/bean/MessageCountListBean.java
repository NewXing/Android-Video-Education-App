package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class MessageCountListBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MessageCountListBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String id;
        private String name;
        private String courseCoverImgUrl;
        private String teacherId;
        private String teacherName;
        private String teacherHeadImgUrl;
        private String messageType;
        private String userNickName;
        private String returnPrice;
        private String state;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(String teacherId) {
            this.teacherId = teacherId;
        }

        public String getReturnPrice() {
            return returnPrice;
        }

        public void setReturnPrice(String returnPrice) {
            this.returnPrice = returnPrice;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCourseCoverImgUrl() {
            return courseCoverImgUrl;
        }

        public void setCourseCoverImgUrl(String courseCoverImgUrl) {
            this.courseCoverImgUrl = courseCoverImgUrl;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getTeacherHeadImgUrl() {
            return teacherHeadImgUrl;
        }

        public void setTeacherHeadImgUrl(String teacherHeadImgUrl) {
            this.teacherHeadImgUrl = teacherHeadImgUrl;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getUserNickName() {
            return userNickName;
        }

        public void setUserNickName(String userNickName) {
            this.userNickName = userNickName;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", courseCoverImgUrl='" + courseCoverImgUrl + '\'' +
                    ", teacherId='" + teacherId + '\'' +
                    ", teacherName='" + teacherName + '\'' +
                    ", teacherHeadImgUrl='" + teacherHeadImgUrl + '\'' +
                    ", messageType='" + messageType + '\'' +
                    ", userNickName='" + userNickName + '\'' +
                    ", returnPrice='" + returnPrice + '\'' +
                    ", state='" + state + '\'' +
                    '}';
        }
    }
}
