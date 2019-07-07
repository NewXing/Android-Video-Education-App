package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class DynamicBean extends BaseBean {

    private List<DynamicData> data;

    public List<DynamicData> getData() {
        return data;
    }

    public void setData(List<DynamicData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DynamicBean{" +
                "data=" + data +
                '}';
    }

    public class DynamicData {

        private String id;
        private String teacherId;
        private String lessonId;
        private String courseId;
        private String currentLesson;
        private String courseName;
        private String courseCoverImgUrl;
        private String teacherName;
        private String courseMainTeacherName;
        private String teacherHeadImg;
        private String createtime;
        private boolean newCourse;

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

        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCurrentLesson() {
            return currentLesson;
        }

        public void setCurrentLesson(String currentLesson) {
            this.currentLesson = currentLesson;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getCourseMainTeacherName() {
            return courseMainTeacherName;
        }

        public void setCourseMainTeacherName(String courseMainTeacherName) {
            this.courseMainTeacherName = courseMainTeacherName;
        }

        public String getTeacherHeadImg() {
            return teacherHeadImg;
        }

        public void setTeacherHeadImg(String teacherHeadImg) {
            this.teacherHeadImg = teacherHeadImg;
        }

        public String getCourseCoverImgUrl() {
            return courseCoverImgUrl;
        }

        public void setCourseCoverImgUrl(String courseCoverImgUrl) {
            this.courseCoverImgUrl = courseCoverImgUrl;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public boolean isNewCourse() {
            return newCourse;
        }

        public void setNewCourse(boolean newCourse) {
            this.newCourse = newCourse;
        }

        @Override
        public String toString() {
            return "DynamicData{" +
                    "id='" + id + '\'' +
                    ", teacherId='" + teacherId + '\'' +
                    ", lessonId='" + lessonId + '\'' +
                    ", courseId='" + courseId + '\'' +
                    ", currentLesson='" + currentLesson + '\'' +
                    ", courseName='" + courseName + '\'' +
                    ", courseCoverImgUrl='" + courseCoverImgUrl + '\'' +
                    ", teacherName='" + teacherName + '\'' +
                    ", courseMainTeacherName='" + courseMainTeacherName + '\'' +
                    ", teacherHeadImg='" + teacherHeadImg + '\'' +
                    ", createtime='" + createtime + '\'' +
                    ", newCourse=" + newCourse +
                    '}';
        }
    }
}
