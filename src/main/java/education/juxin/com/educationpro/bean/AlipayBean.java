package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class AlipayBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AlipayBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String id;
        private String account;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", account='" + account + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
