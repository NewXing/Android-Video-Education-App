package education.juxin.com.educationpro.bean;

import java.util.ArrayList;

import education.juxin.com.educationpro.base.BaseBean;

public class RecommendTeacherBean extends BaseBean {

    private ArrayList<RecommendTeacherData> data;

    public ArrayList<RecommendTeacherData> getData() {
        return data;
    }

    public void setData(ArrayList<RecommendTeacherData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RecommendTeacherBean{" +
                "data=" + data +
                '}';
    }

    public class RecommendTeacherData {

        private String id;
        private String headImgUrl;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "RecommendTeacherData{" +
                    "id='" + id + '\'' +
                    ", headImgUrl='" + headImgUrl + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
