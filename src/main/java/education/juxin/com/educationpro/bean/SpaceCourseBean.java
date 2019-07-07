package education.juxin.com.educationpro.bean;

import java.util.ArrayList;

import education.juxin.com.educationpro.base.BaseBean;

public class SpaceCourseBean extends BaseBean {

    private ArrayList<SpaceCourseData> data;

    public ArrayList<SpaceCourseData> getData() {
        return data;
    }

    public void setData(ArrayList<SpaceCourseData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SpaceCourseBean{" +
                "data=" + data +
                '}';
    }

    public class SpaceCourseData {

        private String id;
        private String name;
        private String coverImgUrl;
        private String classNum;
        private String mainTeacherName;
        private String originalPrice;
        private String currentPrice;
        private String endTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        public String getClassNum() {
            return classNum;
        }

        public void setClassNum(String classNum) {
            this.classNum = classNum;
        }

        public String getMainTeacherName() {
            return mainTeacherName;
        }

        public void setMainTeacherName(String mainTeacherName) {
            this.mainTeacherName = mainTeacherName;
        }

        public String getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(String originalPrice) {
            this.originalPrice = originalPrice;
        }

        public String getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(String currentPrice) {
            this.currentPrice = currentPrice;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        @Override
        public String toString() {
            return "SpaceCourseData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", coverImgUrl='" + coverImgUrl + '\'' +
                    ", classNum='" + classNum + '\'' +
                    ", mainTeacherName='" + mainTeacherName + '\'' +
                    ", originalPrice='" + originalPrice + '\'' +
                    ", currentPrice='" + currentPrice + '\'' +
                    ", endTime='" + endTime + '\'' +
                    '}';
        }
    }
}
