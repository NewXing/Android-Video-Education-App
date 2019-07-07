package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class UserLoginBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserLoginBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "token='" + token + '\'' +
                    '}';
        }
    }
}
