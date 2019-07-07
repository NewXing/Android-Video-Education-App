package education.juxin.com.educationpro.bean;

import java.util.ArrayList;

import education.juxin.com.educationpro.base.BaseBean;

public class AbsBean extends BaseBean {

    private ArrayList<AbsData> data;

    public ArrayList<AbsData> getData() {
        return data;
    }

    public void setData(ArrayList<AbsData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AbsBean{" +
                "data=" + data +
                '}';
    }

    public class AbsData {

        private String fkId;
        private String id;
        private String name;
        private String thumbnails;
        private String type;
        private String courseId;

        public String getFkId() {
            return fkId;
        }

        public void setFkId(String fkId) {
            this.fkId = fkId;
        }

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

        public String getThumbnails() {
            return thumbnails;
        }

        public void setThumbnails(String thumbnails) {
            this.thumbnails = thumbnails;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        @Override
        public String toString() {
            return "AbsData{" +
                    "fkId=" + fkId +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", thumbnails='" + thumbnails + '\'' +
                    ", type='" + type + '\'' +
                    ", courseId='" + courseId + '\'' +
                    '}';
        }
    }
}
