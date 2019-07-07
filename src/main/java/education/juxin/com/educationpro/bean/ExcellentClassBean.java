package education.juxin.com.educationpro.bean;

import java.util.ArrayList;

import education.juxin.com.educationpro.base.BaseBean;

/**
 * Created on 2018/3/16.
 */

public class ExcellentClassBean extends BaseBean {

    private ArrayList<ExcellentClassData> data;

    public ArrayList<ExcellentClassData> getData() {
        return data;
    }

    public void setData(ArrayList<ExcellentClassData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ExcellentClassBean{" +
                "data=" + data +
                '}';
    }

    public class ExcellentClassData {

        private String id;
        private String courseId;
        private String coverImgUrl;
        private String name;
        private String currentNum;
        private String currentPrice;

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        @Override
        public String toString() {
            return "ExcellentClassData{" +
                    "id='" + id + '\'' +
                    ", courseId='" + courseId + '\'' +
                    ", coverImgUrl='" + coverImgUrl + '\'' +
                    ", name='" + name + '\'' +
                    ", currentNum='" + currentNum + '\'' +
                    ", currentPrice='" + currentPrice + '\'' +
                    '}';
        }
    }
}
