package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class CourseDetailBean extends BaseBean {

    private CourseDetailData data;

    public CourseDetailData getData() {
        return data;
    }

    public void setData(CourseDetailData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CourseDetailBean{" +
                "data=" + data +
                '}';
    }

    public class CourseDetailData {

        private String id;
        private String countLesson;
        private String courseCoverImg;
        private String courseDetail;
        private String courseEndDate;
        private String currentLessonNum;
        private String ifcharges;
        private String mainTeacherName;
        private String price;
        private String teacherId;
        private String title;
        private String videoUrl;
        private String lessonId;
        private String whetherBuy; // 是否购买了当前课程(1 未购买 2已购买)
        private String whetherLook; // 是否可以观看 1不可以 2可以
        private String returnPrice;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCountLesson() {
            return countLesson;
        }

        public void setCountLesson(String countLesson) {
            this.countLesson = countLesson;
        }

        public String getCourseCoverImg() {
            return courseCoverImg;
        }

        public void setCourseCoverImg(String courseCoverImg) {
            this.courseCoverImg = courseCoverImg;
        }

        public String getCourseDetail() {
            return courseDetail;
        }

        public void setCourseDetail(String courseDetail) {
            this.courseDetail = courseDetail;
        }

        public String getCourseEndDate() {
            return courseEndDate;
        }

        public void setCourseEndDate(String courseEndDate) {
            this.courseEndDate = courseEndDate;
        }

        public String getCurrentLessonNum() {
            return currentLessonNum;
        }

        public void setCurrentLessonNum(String currentLessonNum) {
            this.currentLessonNum = currentLessonNum;
        }

        public String getIfcharges() {
            return ifcharges;
        }

        public void setIfcharges(String ifcharges) {
            this.ifcharges = ifcharges;
        }

        public String getMainTeacherName() {
            return mainTeacherName;
        }

        public void setMainTeacherName(String mainTeacherName) {
            this.mainTeacherName = mainTeacherName;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(String teacherId) {
            this.teacherId = teacherId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }

        public String getWhetherBuy() {
            return whetherBuy;
        }

        public void setWhetherBuy(String whetherBuy) {
            this.whetherBuy = whetherBuy;
        }

        public String getWhetherLook() {
            return whetherLook;
        }

        public void setWhetherLook(String whetherLook) {
            this.whetherLook = whetherLook;
        }

        public String getReturnPrice() {
            return returnPrice;
        }

        public void setReturnPrice(String returnPrice) {
            this.returnPrice = returnPrice;
        }

        @Override
        public String toString() {
            return "CourseDetailData{" +
                    "id='" + id + '\'' +
                    ", countLesson='" + countLesson + '\'' +
                    ", courseCoverImg='" + courseCoverImg + '\'' +
                    ", courseDetail='" + courseDetail + '\'' +
                    ", courseEndDate='" + courseEndDate + '\'' +
                    ", currentLessonNum='" + currentLessonNum + '\'' +
                    ", ifcharges='" + ifcharges + '\'' +
                    ", mainTeacherName='" + mainTeacherName + '\'' +
                    ", price='" + price + '\'' +
                    ", teacherId='" + teacherId + '\'' +
                    ", title='" + title + '\'' +
                    ", videoUrl='" + videoUrl + '\'' +
                    ", lessonId='" + lessonId + '\'' +
                    ", whetherBuy='" + whetherBuy + '\'' +
                    ", whetherLook='" + whetherLook + '\'' +
                    ", returnPrice='" + returnPrice + '\'' +
                    '}';
        }
    }
}
