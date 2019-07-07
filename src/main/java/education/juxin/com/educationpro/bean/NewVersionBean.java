package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class NewVersionBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NewVersionBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String id;
        private String versionNum;
        private String dowUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersionNum() {
            return versionNum;
        }

        public void setVersionNum(String versionNum) {
            this.versionNum = versionNum;
        }

        public String getDowUrl() {
            return dowUrl;
        }

        public void setDowUrl(String dowUrl) {
            this.dowUrl = dowUrl;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", versionNum='" + versionNum + '\'' +
                    ", dowUrl='" + dowUrl + '\'' +
                    '}';
        }
    }
}
