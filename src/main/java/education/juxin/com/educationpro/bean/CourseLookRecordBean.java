package education.juxin.com.educationpro.bean;

import java.util.ArrayList;

import education.juxin.com.educationpro.base.BaseBean;

public class CourseLookRecordBean extends BaseBean {

    private ArrayList<CourseLookRecordData> data;

    public ArrayList<CourseLookRecordData> getData() {
        return data;
    }

    public void setData(ArrayList<CourseLookRecordData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CourseLookRecordBean{" +
                "data=" + data +
                '}';
    }

    public class CourseLookRecordData {

        private String courseId;
        private String courseName;
        private String coverImgUrl;
        private String currentNum;
        private String endtime;
        private String id;
        private String lessonId;
        private String mainTeacherName;
        private String classNum;

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

        public String getCurrentNum() {
            return currentNum;
        }

        public void setCurrentNum(String currentNum) {
            this.currentNum = currentNum;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }

        public String getMainTeacherName() {
            return mainTeacherName;
        }

        public void setMainTeacherName(String mainTeacherName) {
            this.mainTeacherName = mainTeacherName;
        }

        public String getClassNum() {
            return classNum;
        }

        public void setClassNum(String classNum) {
            this.classNum = classNum;
        }

        @Override
        public String toString() {
            return "CourseLookRecordData{" +
                    "courseId='" + courseId + '\'' +
                    ", courseName='" + courseName + '\'' +
                    ", coverImgUrl='" + coverImgUrl + '\'' +
                    ", currentNum='" + currentNum + '\'' +
                    ", endtime='" + endtime + '\'' +
                    ", id='" + id + '\'' +
                    ", lessonId='" + lessonId + '\'' +
                    ", mainTeacherName='" + mainTeacherName + '\'' +
                    ", classNum='" + classNum + '\'' +
                    '}';
        }
    }
}
