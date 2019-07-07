package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class UserCateListBean extends BaseBean {

    private List<CateData> data;

    public List<CateData> getData() {
        return data;
    }

    public void setData(List<CateData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserCateListBean{" +
                "data=" + data +
                '}';
    }

    public class CateData {

        private String id;
        private String teacherId;
        private String headImgUrl;
        private String name;

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
            return "CateData{" +
                    "id=" + id +
                    ", teacherId=" + teacherId +
                    ", headImgUrl='" + headImgUrl + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
