package education.juxin.com.educationpro.bean;

import java.util.ArrayList;

import education.juxin.com.educationpro.base.BaseBean;

public class ReviewBean extends BaseBean {

    ArrayList<ReviewData> data;

    public ArrayList<ReviewData> getData() {
        return data;
    }

    public void setData(ArrayList<ReviewData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReviewBean{" +
                "data=" + data +
                '}';
    }

    public class ReviewData {

        private String id; // 课节id
        private String courseId; // 课程id
        private String currentNum; // 当前第几课节
        private String currentPrice;
        private String coverImgUrl;
        private String name;
        private String mainTeacherName;
        private String countLessonNum; // 总课时
        private String uploadingTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCurrentNum() {
            return currentNum;
        }

        public void setCurrentNum(String currentNum) {
            this.currentNum = currentNum;
        }

        public String getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(String currentPrice) {
            this.currentPrice = currentPrice;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMainTeacherName() {
            return mainTeacherName;
        }

        public void setMainTeacherName(String mainTeacherName) {
            this.mainTeacherName = mainTeacherName;
        }

        public String getCountLessonNum() {
            return countLessonNum;
        }

        public void setCountLessonNum(String countLessonNum) {
            this.countLessonNum = countLessonNum;
        }

        public String getUploadingTime() {
            return uploadingTime;
        }

        public void setUploadingTime(String uploadingTime) {
            this.uploadingTime = uploadingTime;
        }

        @Override
        public String toString() {
            return "ReviewData{" +
                    "id='" + id + '\'' +
                    ", courseId='" + courseId + '\'' +
                    ", currentNum='" + currentNum + '\'' +
                    ", currentPrice='" + currentPrice + '\'' +
                    ", coverImgUrl='" + coverImgUrl + '\'' +
                    ", name='" + name + '\'' +
                    ", mainTeacherName='" + mainTeacherName + '\'' +
                    ", countLessonNum='" + countLessonNum + '\'' +
                    ", uploadingTime='" + uploadingTime + '\'' +
                    '}';
        }
    }
}