package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class WxParamsBean extends BaseBean {

    private WxParamsData data;

    public WxParamsData getData() {
        return data;
    }

    public void setData(WxParamsData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "WxParamsBean{" +
                "data=" + data +
                '}';
    }

    public class WxParamsData {

        private String appid;
        private String secret;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        @Override
        public String toString() {
            return "WxParamsBean{" +
                    "appid='" + appid + '\'' +
                    ", secret='" + secret + '\'' +
                    '}';
        }
    }
}
