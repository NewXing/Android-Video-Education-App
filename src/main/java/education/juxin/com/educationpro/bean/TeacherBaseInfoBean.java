package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class TeacherBaseInfoBean extends BaseBean {

    private TeacherBaseInfoData data;

    public TeacherBaseInfoData getData() {
        return data;
    }

    public void setData(TeacherBaseInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TeacherBaseInfoBean{" +
                "data=" + data +
                '}';
    }

    public class TeacherBaseInfoData {
        private String id;
        private String name;
        private String headImgUrl;

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

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        @Override
        public String toString() {
            return "TeacherBaseInfoData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", headImgUrl='" + headImgUrl + '\'' +
                    '}';
        }
    }
}
