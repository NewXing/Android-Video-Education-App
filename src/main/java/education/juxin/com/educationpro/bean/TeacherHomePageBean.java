package education.juxin.com.educationpro.bean;

import java.util.ArrayList;

import education.juxin.com.educationpro.base.BaseBean;

public class TeacherHomePageBean extends BaseBean {

    private TeacherHomePageData data;

    public TeacherHomePageData getData() {
        return data;
    }

    public void setData(TeacherHomePageData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TeacherHomePageBean{" +
                "data=" + data +
                '}';
    }

    public class TeacherHomePageData {

        private String id;
        private String name;
        private String headImg;
        private ArrayList<TeacherHomePageItemData> courseList;

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

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public ArrayList<TeacherHomePageItemData> getCourseList() {
            return courseList;
        }

        public void setCourseList(ArrayList<TeacherHomePageItemData> courseList) {
            this.courseList = courseList;
        }

        @Override
        public String toString() {
            return "TeacherHomePageData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", headImg='" + headImg + '\'' +
                    ", courseList=" + courseList +
                    '}';
        }

        public class TeacherHomePageItemData {

            private String id;
            private String name;
            private String classNum;
            private String classificationId;
            private String courseName;
            private String coverImgUrl;
            private String endTime;
            private String mainTeacherName;

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

            public String getClassNum() {
                return classNum;
            }

            public void setClassNum(String classNum) {
                this.classNum = classNum;
            }

            public String getClassificationId() {
                return classificationId;
            }

            public void setClassificationId(String classificationId) {
                this.classificationId = classificationId;
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

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getMainTeacherName() {
                return mainTeacherName;
            }

            public void setMainTeacherName(String mainTeacherName) {
                this.mainTeacherName = mainTeacherName;
            }

            @Override
            public String toString() {
                return "TeacherHomePageData{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", classNum='" + classNum + '\'' +
                        ", classificationId='" + classificationId + '\'' +
                        ", courseName='" + courseName + '\'' +
                        ", coverImgUrl='" + coverImgUrl + '\'' +
                        ", endTime='" + endTime + '\'' +
                        ", mainTeacherName='" + mainTeacherName + '\'' +
                        '}';
            }
        }
    }

}
