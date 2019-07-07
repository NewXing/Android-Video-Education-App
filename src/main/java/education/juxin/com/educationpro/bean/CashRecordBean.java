package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class CashRecordBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CashRecordBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String id;
        private String mainUserNickname;
        private String mainUserHeadImgUrl;
        private String courseId;
        private String courseName;
        private String courseCoverImgUrl;
        private String returnPrice;
        private String buyTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMainUserNickname() {
            return mainUserNickname;
        }

        public void setMainUserNickname(String mainUserNickname) {
            this.mainUserNickname = mainUserNickname;
        }

        public String getMainUserHeadImgUrl() {
            return mainUserHeadImgUrl;
        }

        public void setMainUserHeadImgUrl(String mainUserHeadImgUrl) {
            this.mainUserHeadImgUrl = mainUserHeadImgUrl;
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

        public String getCourseCoverImgUrl() {
            return courseCoverImgUrl;
        }

        public void setCourseCoverImgUrl(String courseCoverImgUrl) {
            this.courseCoverImgUrl = courseCoverImgUrl;
        }

        public String getReturnPrice() {
            return returnPrice;
        }

        public void setReturnPrice(String returnPrice) {
            this.returnPrice = returnPrice;
        }

        public String getBuyTime() {
            return buyTime;
        }

        public void setBuyTime(String buyTime) {
            this.buyTime = buyTime;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", mainUserNickname='" + mainUserNickname + '\'' +
                    ", mainUserHeadImgUrl='" + mainUserHeadImgUrl + '\'' +
                    ", courseId='" + courseId + '\'' +
                    ", courseName='" + courseName + '\'' +
                    ", courseCoverImgUrl='" + courseCoverImgUrl + '\'' +
                    ", returnPrice='" + returnPrice + '\'' +
                    ", buyTime='" + buyTime + '\'' +
                    '}';
        }
    }
}
