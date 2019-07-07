package education.juxin.com.educationpro.download;

import java.io.Serializable;

public class VideoCacheInfoData implements Serializable {

    private String courseCoverImg;
    private String title;
    private String mainTeacherName;
    private String courseEndDate;
    private String currentLessonNum;
    private String cacheFileName;
    private boolean isCheck;

    public String getCacheFileName() {
        return cacheFileName;
    }

    public void setCacheFileName(String cacheFileName) {
        this.cacheFileName = cacheFileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseCoverImg() {
        return courseCoverImg;
    }

    public void setCourseCoverImg(String courseCoverImg) {
        this.courseCoverImg = courseCoverImg;
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

    public String getMainTeacherName() {
        return mainTeacherName;
    }

    public void setMainTeacherName(String mainTeacherName) {
        this.mainTeacherName = mainTeacherName;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "VideoCacheInfoData{" +
                "courseCoverImg='" + courseCoverImg + '\'' +
                ", title='" + title + '\'' +
                ", mainTeacherName='" + mainTeacherName + '\'' +
                ", courseEndDate='" + courseEndDate + '\'' +
                ", currentLessonNum='" + currentLessonNum + '\'' +
                ", cacheFileName='" + cacheFileName + '\'' +
                ", isCheck='" + isCheck + '\'' +
                '}';
    }
}
