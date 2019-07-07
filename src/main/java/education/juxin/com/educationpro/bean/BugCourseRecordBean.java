package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class BugCourseRecordBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BugCourseRecordBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String id;
        private String name;
        private String coverImgUrl;
        private String classNum;
        private String mainTeacherName;
        private Object originalPrice;
        private Object currentPrice;
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

        public Object getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(Object originalPrice) {
            this.originalPrice = originalPrice;
        }

        public Object getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(Object currentPrice) {
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
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", coverImgUrl='" + coverImgUrl + '\'' +
                    ", classNum='" + classNum + '\'' +
                    ", mainTeacherName='" + mainTeacherName + '\'' +
                    ", originalPrice=" + originalPrice +
                    ", currentPrice=" + currentPrice +
                    ", endTime='" + endTime + '\'' +
                    '}';
        }
    }
}
