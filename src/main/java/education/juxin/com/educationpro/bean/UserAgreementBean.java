package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class UserAgreementBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserAgreementBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String name;
        private String content;
        private int id;
        private int page;
        private int rows;
        private String createtime;
        private String updatetime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "name='" + name + '\'' +
                    ", content='" + content + '\'' +
                    ", id=" + id +
                    ", page=" + page +
                    ", rows=" + rows +
                    ", createtime='" + createtime + '\'' +
                    ", updatetime='" + updatetime + '\'' +
                    '}';
        }
    }
}
